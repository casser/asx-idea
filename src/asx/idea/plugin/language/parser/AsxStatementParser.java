package asx.idea.plugin.language.parser;

import asx.idea.plugin.language.AsxElementTypes;
import asx.idea.plugin.language.AsxTokenSets;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesBinders;
import com.intellij.lang.atscript.AtScriptElementTypes;
import com.intellij.lang.ecmascript6.ES6ElementTypes;
import com.intellij.lang.javascript.*;
import com.intellij.lang.javascript.dialects.JSLanguageFeature;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxStatementParser<T extends AsxSyntaxParserBase> extends AsxAbstractParser<T> {
    public static final TokenSet CLASS_MEMBER_MODIFIERS = TokenSet.create();
    protected static final Logger LOG = Logger.getInstance("#com.intellij.lang.javascript.parsing.StatementParsing");
    static final Key<String> withinInterfaceKey = Key.create("within.interface");
    private static Key<Object> localVarsKey = Key.create("local.vars.key");
    private AsxFunctionParser<T> getFunctionParser(){
        return this.myJavaScriptParser.getFunctionParser();
    }
    private AsxExpressionParser<T> getExpressionParser(){
        return this.myJavaScriptParser.getExpressionParser();
    }
    private AsxStatementParser<T> getStatementParser(){
        return this.myJavaScriptParser.getStatementParser();
    }

    protected AsxStatementParser(T parser) {
        super(parser);
    }
    public void parseSourceElement() {
        //parseAnnotations();
        IElementType firstToken = this.builder.getTokenType();
        if(firstToken != AsxTokenSets.EXPORT_KEYWORD || this.builder.lookAhead(1) != AsxTokenSets.MULT && this.builder.lookAhead(1) != AsxTokenSets.LBRACE) {
            if(firstToken == AsxTokenSets.EXPORT_KEYWORD && this.builder.lookAhead(1) == AsxTokenSets.DEFAULT_KEYWORD) {
                this.parseExportDefaultAssignment();
            } else if(firstToken == AsxTokenSets.ASYNC_KEYWORD && this.builder.lookAhead(1) == AsxTokenSets.FUNCTION_KEYWORD) {
                this.getFunctionParser().parseFunctionDeclaration();
            } else {
                this.parseSourceElementSuper();
            }
        } else {
            this.parseExportDeclaration();
        }

    }
    public void parseSourceElementSuper() {
        IElementType tokenType = this.builder.getTokenType();
        if(tokenType == AsxTokenSets.FUNCTION_KEYWORD) {
            this.myJavaScriptParser.getFunctionParser().parseFunctionDeclaration();
        } else
        if(tokenType == AsxTokenSets.PACKAGE_KEYWORD && this.isECMAL4()) {
            this.parsePackage();
        } else
        if(tokenType == AsxTokenSets.AT && this.isECMAL4()) {
            this.builder.advanceLexer();
            this.myJavaScriptParser.getFunctionParser().parseAttributeWithoutBrackets();
        } else
        if(tokenType != AsxTokenSets.IMPORT_KEYWORD || !this.isJavaScript() && !this.isECMA6()) {
            this.doParseStatement(true);
        } else {
            this.parseES6ImportStatement();
        }

    }
    private void parseExportDeclaration() {
        PsiBuilder.Marker marker = this.builder.mark();
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.EXPORT_KEYWORD);
        this.builder.advanceLexer();
        if(this.builder.getTokenType() == AsxTokenSets.MULT) {
            this.builder.advanceLexer();
            this.parseES6FromDeclaration();
            this.forceCheckForSemicolon();
            marker.done(ES6ElementTypes.EXPORT_DECLARATION);
        } else if(this.builder.getTokenType() == AsxTokenSets.LBRACE) {
            this.builder.advanceLexer();
            this.parseES6ImportOrExportClause(true);
            if(this.builder.getTokenType() == AsxTokenSets.FROM_KEYWORD) {
                this.parseES6FromDeclaration();
            }

            this.forceCheckForSemicolon();
            marker.done(ES6ElementTypes.EXPORT_DECLARATION);
        } else {
            LOG.error("* or { expected");
            marker.drop();
        }

    }

    private void parseExportDefaultAssignment() {
        PsiBuilder.Marker marker = this.builder.mark();
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.EXPORT_KEYWORD);
        this.builder.advanceLexer();
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.DEFAULT_KEYWORD);
        this.builder.advanceLexer();
        this.getExpressionParser().parseAssignmentExpression(false, false);
        this.forceCheckForSemicolon();
        marker.done(ES6ElementTypes.EXPORT_DEFAULT_ASSIGNMENT);
    }
    private void parsePackageBodyStatement() {
        this.doParseStatement(true);
    }

    private void parsePackage() {
        PsiBuilder.Marker _package = this.builder.mark();
        this.builder.advanceLexer();
        if(this.builder.getTokenType() == AsxTokenSets.IDENTIFIER) {
            this.myJavaScriptParser.getExpressionParser().parseQualifiedTypeName();
        }

        if(this.builder.getTokenType() != AsxTokenSets.LBRACE) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.name.or.lbrace", new Object[0]));
        } else {
            this.parseBlockOrFunctionBody(AsxStatementParser.BlockType.PACKAGE_OR_CLASS_BODY);
        }

        _package.done(JSStubElementTypes.PACKAGE_STATEMENT);
    }

    public void parseStatement() {
        this.doParseStatement(false);
    }

    protected void doParseStatement(boolean canHaveClasses) {
        IElementType firstToken = this.builder.getTokenType();
        if(firstToken == null) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.statement"));
        } else if(firstToken == AsxTokenSets.LBRACE) {
            this.parseBlock();
        } else if(firstToken == AsxTokenSets.SEMICOLON) {
            this.parseEmptyStatement();
        } else if(firstToken == AsxTokenSets.IF_KEYWORD) {
            this.parseIfStatement();
        } else if(firstToken != AsxTokenSets.DO_KEYWORD && firstToken != AsxTokenSets.WHILE_KEYWORD && firstToken != AsxTokenSets.FOR_KEYWORD) {
            if(firstToken == AsxTokenSets.CONTINUE_KEYWORD) {
                this.parseContinueStatement();
            } else if(firstToken == AsxTokenSets.BREAK_KEYWORD) {
                this.parseBreakStatement();
            } else if(firstToken == AsxTokenSets.RETURN_KEYWORD) {
                this.parseReturnStatement();
            } else if(firstToken == AsxTokenSets.WITH_KEYWORD) {
                this.parseWithStatement();
            } else if(firstToken == AsxTokenSets.YIELD_KEYWORD) {
                this.parseExpressionStatement();
            } else if(firstToken == AsxTokenSets.SWITCH_KEYWORD) {
                this.parseSwitchStatement();
            } else if(firstToken == AsxTokenSets.THROW_KEYWORD) {
                this.parseThrowStatement();
            } else if(firstToken == AsxTokenSets.TRY_KEYWORD) {
                this.parseTryStatement();
            } else if(firstToken == AsxTokenSets.DEFAULT_KEYWORD && this.isECMAL4()) {
                this.parseDefaultNsStatement();
            } else if(firstToken == AsxTokenSets.IMPORT_KEYWORD && this.isECMAL4()) {
                this.parseImportStatement();
            } else if(firstToken == AsxTokenSets.USE_KEYWORD && this.isECMAL4()) {
                this.parseUseNamespaceDirective();
            } else if(firstToken == AsxTokenSets.INCLUDE_KEYWORD) {
                this.parseIncludeDirective();
            } else {
                PsiBuilder.Marker labeledStatement;
                if(firstToken == AsxTokenSets.DEBUGGER_KEYWORD) {
                    labeledStatement = this.builder.mark();
                    PsiBuilder.Marker methodEmptiness1 = this.builder.mark();
                    this.builder.advanceLexer();
                    methodEmptiness1.done(JSElementTypes.REFERENCE_EXPRESSION);
                    this.forceCheckForSemicolon();
                    labeledStatement.done(AsxElementTypes.EXPRESSION_STATEMENT);
                } else {
                    if((AsxTokenSets.IDENTIFIER == firstToken || this.isModifier(firstToken) || AsxTokenSets.LBRACKET == firstToken || AsxTokenSets.AT == firstToken && this.isECMA6()) && this.hasAttributeModifiers()) {
                        labeledStatement = this.builder.mark();
                        String methodEmptiness = this.builder.getUserData(AsxFunctionParser.methodsEmptinessKey);
                        if(!this.myJavaScriptParser.getFunctionParser().parseAttributesList()) {
                            labeledStatement.rollbackTo();
                        } else {
                            try {
                                if(this.builder.eof()) {
                                    labeledStatement.drop();
                                    return;
                                }

                                IElementType tokenType = this.builder.getTokenType();
                                if(tokenType == AsxTokenSets.FUNCTION_KEYWORD) {
                                    this.myJavaScriptParser.getFunctionParser().parseFunctionNoMarker(AsxFunctionParser.Context.SOURCE_ELEMENT, labeledStatement);
                                    return;
                                }

                                if(AsxTokenSets.VAR_MODIFIERS.contains(tokenType)) {
                                    this.parseVarStatementNoMarker(false, labeledStatement);
                                    return;
                                }

                                if(tokenType == AsxTokenSets.NAMESPACE_KEYWORD) {
                                    if(this.parseNamespaceNoMarker(labeledStatement)) {
                                        return;
                                    }

                                    this.builder.advanceLexer();
                                } else {
                                    if(this.parseDialectSpecificSourceElements(labeledStatement)) {
                                        return;
                                    }

                                    if(tokenType == AsxTokenSets.CLASS_KEYWORD || tokenType == AsxTokenSets.INTERFACE_KEYWORD) {
                                        this.parseClassOrInterfaceNoMarker(labeledStatement);
                                        return;
                                    }

                                    if(tokenType == AsxTokenSets.LBRACE) {
                                        this.parseBlockNoMarker(canHaveClasses? AsxStatementParser.BlockType.PACKAGE_OR_CLASS_BODY: AsxStatementParser.BlockType.BLOCK, labeledStatement);
                                        return;
                                    }

                                    this.builder.putUserData(AsxFunctionParser.methodsEmptinessKey, null);
                                    if(firstToken == AsxTokenSets.IDENTIFIER) {
                                        labeledStatement.rollbackTo();
                                    } else if(AsxTokenSets.COLON_COLON == this.builder.getTokenType()) {
                                        labeledStatement.rollbackTo();
                                        if(this.parseExpressionStatement()) {
                                            return;
                                        }
                                    } else {
                                        this.builder.error(JSBundle.message("javascript.parser.message.expected.function.var.class.interface.namespace", new Object[0]));
                                        labeledStatement.drop();
                                    }
                                }
                            } finally {
                                this.builder.putUserData(AsxFunctionParser.methodsEmptinessKey, methodEmptiness);
                            }
                        }
                    } else {
                        if(firstToken == AsxTokenSets.CLASS_KEYWORD || firstToken == AsxTokenSets.INTERFACE_KEYWORD) {
                            labeledStatement = this.startAttributeListOwner();
                            this.parseClassOrInterfaceNoMarker(labeledStatement);
                            return;
                        }

                        if(firstToken == AsxTokenSets.LET_KEYWORD && this.builder.lookAhead(1) == AsxTokenSets.LPAR) {
                            this.parseLetBlock();
                            return;
                        }

                        if(AsxTokenSets.VAR_MODIFIERS.contains(firstToken)) {
                            labeledStatement = this.hasAttributeModifiers()?this.startAttributeListOwner():this.builder.mark();
                            this.parseVarStatementNoMarker(false, labeledStatement);
                            return;
                        }

                        if(firstToken == AsxTokenSets.FUNCTION_KEYWORD && this.isECMAL4()) {
                            labeledStatement = this.startAttributeListOwner();
                            this.myJavaScriptParser.getFunctionParser().parseFunctionNoMarker(AsxFunctionParser.Context.SOURCE_ELEMENT, labeledStatement);
                            return;
                        }

                        if(firstToken == AsxTokenSets.NAMESPACE_KEYWORD && this.isECMAL4()) {
                            labeledStatement = this.startAttributeListOwner();
                            if(this.parseNamespaceNoMarker(labeledStatement)) {
                                return;
                            }
                        }
                    }

                    if(firstToken != AsxTokenSets.NAMESPACE_KEYWORD || !this.isECMAL4() || !this.parseNamespaceNoMarker(this.builder.mark())) {
                        if(firstToken == AsxTokenSets.FUNCTION_KEYWORD) {
                            this.myJavaScriptParser.getFunctionParser().parseFunctionDeclaration();
                        } else {
                            if(firstToken == AsxTokenSets.IDENTIFIER) {
                                labeledStatement = this.builder.mark();
                                this.builder.advanceLexer();
                                if(this.builder.getTokenType() == AsxTokenSets.COLON) {
                                    this.builder.advanceLexer();
                                    this.parseStatement();
                                    labeledStatement.done(JSElementTypes.LABELED_STATEMENT);
                                    return;
                                }

                                labeledStatement.rollbackTo();
                            }

                            if(firstToken == AsxTokenSets.LBRACE || firstToken == AsxTokenSets.FUNCTION_KEYWORD || !this.parseExpressionStatement()) {
                                this.builder.error(JSBundle.message("javascript.parser.message.expected.statement", new Object[0]));
                                this.builder.advanceLexer();
                            }
                        }
                    }
                }
            }
        } else {
            this.parseIterationStatement();
        }
    }

    protected boolean isModifier(IElementType token) {
        return AsxTokenSets.MODIFIERS.contains(token);
    }

    protected boolean hasAttributeModifiers() {
        return this.isECMAL4() || this.isECMA6();
    }

    protected boolean parseDialectSpecificSourceElements(PsiBuilder.Marker marker) {
        return false;
    }

    protected void parseClassNoMarker(PsiBuilder.Marker block) {
        if(/*!(this instanceof ES6StatementParser) && !(this instanceof TypeScriptStatementParser) && */!this.isIdentifierToken(this.builder.lookAhead(1))) {
            block.drop();
            this.builder.error(JSBundle.message("javascript.parser.message.expected.statement", new Object[0]));
            this.builder.advanceLexer();
        } else {
            LOG.assertTrue(AsxTokenSets.CLASS_KEYWORD == this.builder.getTokenType());
            this.builder.advanceLexer();
            if(!this.isIdentifierToken(this.builder.getTokenType())) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.name", new Object[0]));
                block.drop();
            } else {
                this.myJavaScriptParser.getExpressionParser().buildTokenElement(JSElementTypes.REFERENCE_EXPRESSION);
                this.myJavaScriptParser.getFunctionParser().tryParseTypeParameterList();
                if(this.builder.getTokenType() == AsxTokenSets.EXTENDS_KEYWORD) {
                    this.parseReferenceList();
                }

                if(this.builder.getTokenType() == AsxTokenSets.IMPLEMENTS_KEYWORD) {
                    this.parseReferenceList();
                }

                if(this.builder.getTokenType() != AsxTokenSets.LBRACE) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.lbrace", new Object[0]));
                } else {
                    this.builder.advanceLexer();

                    while(this.builder.getTokenType() != AsxTokenSets.RBRACE) {
                        if(this.builder.eof()) {
                            this.builder.error(JSBundle.message("javascript.parser.message.missing.rbrace", new Object[0]));
                            break;
                        }

                        this.parseClassMember();
                    }

                    this.builder.advanceLexer();
                }

                block.done(this.getClassElementType());
                block.setCustomEdgeTokenBinders(INCLUDE_DOC_COMMENT_AT_LEFT, WhitespacesBinders.DEFAULT_RIGHT_BINDER);
            }
        }
    }

    protected IElementType getClassElementType() {
        return JSStubElementTypes.CLASS;
    }
    private static boolean isPropertyNameStart(IElementType elementType) {
        return JSKeywordSets.IDENTIFIER_NAMES.contains(elementType) || elementType == AsxTokenSets.LBRACKET;
    }
    public void parseAttributeWithoutBrackets() {
        PsiBuilder.Marker attribute = this.builder.mark();
        if(!this.getExpressionParser().parseQualifiedTypeName()) {
            attribute.drop();
        } else {
            if(this.builder.getTokenType() == AsxTokenSets.LPAR) {
                this.getExpressionParser().parseArgumentList();
            }

            attribute.done(AtScriptElementTypes.ATTRIBUTE);
        }
    }

    public boolean parseAnnotation(){
        PsiBuilder.Marker attribute = this.builder.mark();
        if(this.is(AsxTokenSets.AT)){
            this.builder.advanceLexer();
            if(!this.getExpressionParser().parseQualifiedTypeName()) {
                attribute.drop();
                return false;
            } else {
                if(this.builder.getTokenType() == AsxTokenSets.LPAR) {
                    this.getExpressionParser().parseArgumentList();
                }
                attribute.done(AtScriptElementTypes.ATTRIBUTE);
                return true;
            }
        }else{
            attribute.drop();
            return false;
        }
    }

    public void parseAnnotations(){
        while(this.is(AsxTokenSets.AT)) {
            this.parseAnnotation();
        }
    }

    public void parseClassMember() {
        PsiBuilder.Marker classMember = this.builder.mark();
        int classMemberOffset = this.builder.getCurrentOffset();
        if(this.builder.getTokenType() == AsxTokenSets.SEMICOLON) {
            classMember.drop();
            this.parseEmptyStatement();
        } else {
            boolean lexerAdvanced = false;
            IElementType modifier = this.builder.getTokenType();
            PsiBuilder.Marker attrList = this.builder.mark();
            this.getFunctionParser().tryParseAttributesWithoutBrackets();
            while(isModifier(modifier)) {
                this.builder.advanceLexer();
                modifier = this.builder.getTokenType();
                lexerAdvanced = true;
            }

            attrList.done(JSStubElementTypes.ATTRIBUTE_LIST);
            IElementType nextToken = this.builder.getTokenType();
            if(nextToken == AsxTokenSets.MIXIN_KEYWORD) {
                this.builder.advanceLexer();
                if(this.builder.getTokenType() == AsxTokenSets.IDENTIFIER) {
                    this.getExpressionParser().buildTokenElement(JSElementTypes.REFERENCE_EXPRESSION);
                }

                this.forceCheckForSemicolon();
                classMember.done(JSElementTypes.MIXIN_STATEMENT);
            } else if(nextToken == AsxTokenSets.VAR_KEYWORD) {
                this.parseVarStatementNoMarker(false, classMember);
            } else if(nextToken != AsxTokenSets.GET_KEYWORD && nextToken != AsxTokenSets.SET_KEYWORD && nextToken != AsxTokenSets.MULT && nextToken != AsxTokenSets.FUNCTION_KEYWORD) {
                if(isPropertyNameStart(nextToken)) {
                    if(JSKeywordSets.IDENTIFIER_NAMES.contains(nextToken) && AsxTokenSets.LPAR == this.builder.lookAhead(1)) {
                        this.getFunctionParser().parseFunctionNoMarker(AsxFunctionParser.Context.SOURCE_ELEMENT, classMember);
                        this.forceCheckForSemicolon();
                    } else if(nextToken == AsxTokenSets.LBRACKET) {
                        this.getExpressionParser().parsePropertyName();
                        this.getFunctionParser().parseFunctionNoMarker(AsxFunctionParser.Context.EXPRESSION, classMember);
                        this.forceCheckForSemicolon();
                    } else {
                        this.parseVarList(false);
                        this.forceCheckForSemicolon();
                        classMember.done(JSStubElementTypes.VAR_STATEMENT);
                        classMember.setCustomEdgeTokenBinders(INCLUDE_DOC_COMMENT_AT_LEFT, WhitespacesBinders.DEFAULT_RIGHT_BINDER);
                    }
                } else {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.statement", new Object[0]));
                    if(!lexerAdvanced) {
                        this.builder.advanceLexer();
                    }

                    classMember.drop();
                }
            } else {
                if(nextToken == AsxTokenSets.MULT) {
                    this.builder.advanceLexer();
                }

                this.getFunctionParser().parseFunctionNoMarker(AsxFunctionParser.Context.SOURCE_ELEMENT, classMember);
            }

            assert this.builder.getCurrentOffset() > classMemberOffset;

        }
    }

    protected PsiBuilder.Marker startAttributeListOwner() {
        PsiBuilder.Marker marker = this.builder.mark();
        if(!this.isLocalVarContext()) {
            PsiBuilder.Marker modifierListMarker = this.builder.mark();
            modifierListMarker.done(JSStubElementTypes.ATTRIBUTE_LIST);
        }

        return marker;
    }

    protected boolean parseExpressionStatement() {
        PsiBuilder.Marker exprStatement = this.builder.mark();
        if(this.myJavaScriptParser.getExpressionParser().parseExpressionOptional()) {
            this.forceCheckForSemicolon();
            exprStatement.done(AsxElementTypes.EXPRESSION_STATEMENT);
            exprStatement.setCustomEdgeTokenBinders(INCLUDE_DOC_COMMENT_AT_LEFT, WhitespacesBinders.DEFAULT_RIGHT_BINDER);
            return true;
        } else {
            exprStatement.drop();
            return false;
        }
    }

    private void parseLetBlock() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.LET_KEYWORD);
        PsiBuilder.Marker marker = this.builder.mark();
        this.builder.advanceLexer();
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.LPAR);
        this.parseLetDeclarations();
        this.parseBlock();
        marker.done(JSElementTypes.LET_STATEMENT);
    }

    boolean parseLetDeclarations() {
        if(this.builder.getTokenType() != AsxTokenSets.LPAR) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.lparen", new Object[0]));
            return false;
        } else {
            this.builder.advanceLexer();
            boolean parsedSuccessfully = this.myJavaScriptParser.getExpressionParser().parseAssignmentExpression(true, false);
            if(parsedSuccessfully) {
                while(this.builder.getTokenType() == AsxTokenSets.COMMA) {
                    this.builder.advanceLexer();
                    if(!(parsedSuccessfully = this.myJavaScriptParser.getExpressionParser().parseAssignmentExpression(true, false))) {
                        break;
                    }
                }
            }

            return checkMatches(this.builder, AsxTokenSets.RPAR, "javascript.parser.message.expected.rparen") && parsedSuccessfully;
        }
    }

    private void parseDefaultNsStatement() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.DEFAULT_KEYWORD);
        PsiBuilder.Marker statementMarker = this.builder.mark();
        PsiBuilder.Marker marker = this.builder.mark();
        this.builder.advanceLexer();
        if(this.builder.getTokenType() == AsxTokenSets.IDENTIFIER && "xml".equals(this.builder.getTokenText())) {
            this.builder.advanceLexer();
            if(checkMatches(this.builder, AsxTokenSets.NAMESPACE_KEYWORD, "javascript.parser.message.expected.namespace") && checkMatches(this.builder, AsxTokenSets.EQ, "javascript.parser.message.expected.equal")) {
                this.myJavaScriptParser.getExpressionParser().parseExpression();
            }
        } else {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.xml", new Object[0]));
        }

        marker.done(JSElementTypes.ASSIGNMENT_EXPRESSION);
        this.checkForSemicolon();
        statementMarker.done(AsxElementTypes.EXPRESSION_STATEMENT);
    }

    private boolean parseNamespaceNoMarker(@NotNull PsiBuilder.Marker useNSStatement) {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.NAMESPACE_KEYWORD);
        this.builder.advanceLexer();
        if(!JSKeywordSets.IDENTIFIER_TOKENS_SET.contains(this.builder.getTokenType())) {
            useNSStatement.rollbackTo();
            return false;
        } else {
            this.myJavaScriptParser.getExpressionParser().parseQualifiedTypeName();
            if(this.builder.getTokenType() == AsxTokenSets.EQ) {
                this.builder.advanceLexer();
                IElementType tokenType = this.builder.getTokenType();
                if(tokenType == AsxTokenSets.PUBLIC_KEYWORD) {
                    this.builder.advanceLexer();
                } else if(tokenType != AsxTokenSets.STRING_LITERAL && tokenType != AsxTokenSets.IDENTIFIER) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.string.literal", new Object[0]));
                } else {
                    this.myJavaScriptParser.getExpressionParser().parseExpression();
                }
            }

            this.checkForSemicolon();
            useNSStatement.done(JSStubElementTypes.NAMESPACE_DECLARATION);
            useNSStatement.setCustomEdgeTokenBinders(INCLUDE_DOC_COMMENT_AT_LEFT, WhitespacesBinders.DEFAULT_RIGHT_BINDER);
            return true;
        }
    }

    public void parseIncludeDirective() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.INCLUDE_KEYWORD);
        PsiBuilder.Marker useNSStatement = this.builder.mark();
        this.builder.advanceLexer();
        checkMatches(this.builder, AsxTokenSets.STRING_LITERAL, "javascript.parser.message.expected.string.literal");
        this.checkForSemicolon();
        useNSStatement.done(JSStubElementTypes.INCLUDE_DIRECTIVE);
    }

    public void parseUseNamespaceDirective() {
        PsiBuilder.Marker useNSStatement = this.builder.mark();
        this.builder.advanceLexer();
        if(this.builder.getTokenType() != AsxTokenSets.NAMESPACE_KEYWORD) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.namespace", new Object[0]));
        } else {
            this.builder.advanceLexer();
            if(!this.myJavaScriptParser.getExpressionParser().parseQualifiedTypeName()) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.typename", new Object[0]));
            }

            while(this.builder.getTokenType() == AsxTokenSets.COMMA) {
                this.builder.advanceLexer();
                if(!this.myJavaScriptParser.getExpressionParser().parseQualifiedTypeName()) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.typename", new Object[0]));
                    break;
                }
            }
        }

        this.checkForSemicolon();
        useNSStatement.done(JSStubElementTypes.USE_NAMESPACE_DIRECTIVE);
    }

    private void parseImportStatement() {
        PsiBuilder.Marker importStatement = this.builder.mark();

        try {
            this.builder.advanceLexer();
            PsiBuilder.Marker nsAssignment = this.builder.mark();
            if(!this.myJavaScriptParser.getExpressionParser().parseQualifiedTypeName(true)) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.typename", new Object[0]));
                nsAssignment.drop();
                return;
            }

            if(this.builder.getTokenType() == AsxTokenSets.EQ) {
                this.builder.advanceLexer();
                if(!this.myJavaScriptParser.getExpressionParser().parseQualifiedTypeName()) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.typename", new Object[0]));
                }

                nsAssignment.done(JSElementTypes.ASSIGNMENT_EXPRESSION);
            } else {
                nsAssignment.drop();
            }

            this.checkForSemicolon();
        } finally {
            importStatement.done(JSStubElementTypes.IMPORT_STATEMENT);
        }

    }

    protected void parseClassOrInterfaceNoMarker(@NotNull PsiBuilder.Marker clazz) {
        if(!this.isECMAL4()) {
            if(this.builder.getTokenType() == AsxTokenSets.CLASS_KEYWORD) {
                this.parseClassNoMarker(clazz);
            } else {
                assert this.builder.getTokenType() == AsxTokenSets.INTERFACE_KEYWORD;

                this.parseInterfaceNoMarker(clazz);
            }

        } else {
            String methodEmptiness = this.builder.getUserData(AsxFunctionParser.methodsEmptinessKey);

            try {
                IElementType tokenType = this.builder.getTokenType();
                LOG.assertTrue(AsxTokenSets.CLASS_KEYWORD == tokenType || AsxTokenSets.INTERFACE_KEYWORD == tokenType);
                if(this.builder.getTokenType() == AsxTokenSets.INTERFACE_KEYWORD) {
                    this.builder.putUserData(AsxFunctionParser.methodsEmptinessKey, "a");
                    this.builder.putUserData(withinInterfaceKey, "");
                }

                this.builder.advanceLexer();
                if(!this.myJavaScriptParser.getExpressionParser().parseQualifiedTypeName()) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.typename", new Object[0]));
                }

                if(this.builder.getTokenType() == AsxTokenSets.EXTENDS_KEYWORD) {
                    this.parseReferenceList();
                }

                if(this.builder.getTokenType() == AsxTokenSets.IMPLEMENTS_KEYWORD) {
                    this.parseReferenceList();
                }

                this.parseBlockOrFunctionBody(AsxStatementParser.BlockType.PACKAGE_OR_CLASS_BODY);
                clazz.done(JSStubElementTypes.CLASS);
                clazz.setCustomEdgeTokenBinders(INCLUDE_DOC_COMMENT_AT_LEFT, WhitespacesBinders.DEFAULT_RIGHT_BINDER);
            } finally {
                this.builder.putUserData(AsxFunctionParser.methodsEmptinessKey, methodEmptiness);
                this.builder.putUserData(withinInterfaceKey, null);
            }

        }
    }

    protected void parseInterfaceNoMarker(@NotNull PsiBuilder.Marker clazz) {
        clazz.drop();
        if(!this.parseExpressionStatement()) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.statement", new Object[0]));
            this.builder.advanceLexer();
        }

    }

    protected void parseReferenceList() {
        IElementType tokenType = this.builder.getTokenType();
        LOG.assertTrue(tokenType == AsxTokenSets.EXTENDS_KEYWORD || tokenType == AsxTokenSets.IMPLEMENTS_KEYWORD);
        PsiBuilder.Marker referenceList = this.builder.mark();
        this.builder.advanceLexer();
        if(this.myJavaScriptParser.getExpressionParser().parseQualifiedTypeName()) {
            while(this.builder.getTokenType() == AsxTokenSets.COMMA) {
                this.builder.advanceLexer();
                if(!this.isIdentifierToken(this.builder.getTokenType())) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.type.name", new Object[0]));
                    break;
                }

                if(!this.myJavaScriptParser.getExpressionParser().parseQualifiedTypeName()) {
                    break;
                }
            }
        } else {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.type.name", new Object[0]));
        }

        referenceList.done(tokenType == AsxTokenSets.EXTENDS_KEYWORD?JSStubElementTypes.EXTENDS_LIST:JSStubElementTypes.IMPLEMENTS_LIST);
    }

    private void parseTryStatement() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.TRY_KEYWORD);
        PsiBuilder.Marker statement = this.builder.mark();
        this.builder.advanceLexer();
        this.parseBlock();

        while(this.builder.getTokenType() == AsxTokenSets.CATCH_KEYWORD) {
            this.parseCatchBlock();
        }

        if(this.builder.getTokenType() == AsxTokenSets.FINALLY_KEYWORD) {
            this.builder.advanceLexer();
            this.parseBlock();
        }

        statement.done(JSElementTypes.TRY_STATEMENT);
    }

    private void parseCatchBlock() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.CATCH_KEYWORD);
        PsiBuilder.Marker block = this.builder.mark();
        this.builder.advanceLexer();
        checkMatches(this.builder, AsxTokenSets.LPAR, "javascript.parser.message.expected.lparen");
        IElementType identifierType = this.builder.getTokenType();
        if(JSKeywordSets.IDENTIFIER_TOKENS_SET.contains(identifierType)) {
            PsiBuilder.Marker param = this.builder.mark();
            this.builder.advanceLexer();
            if(!this.myJavaScriptParser.getExpressionParser().tryParseType() && this.builder.getTokenType() == AsxTokenSets.IF_KEYWORD) {
                this.builder.advanceLexer();
                checkMatches(this.builder, identifierType, "javascript.parser.message.expected.identifier");
                checkMatches(this.builder, AsxTokenSets.INSTANCEOF_KEYWORD, "javascript.parser.message.expected.instanceof");
                checkMatches(this.builder, AsxTokenSets.IDENTIFIER, "javascript.parser.message.expected.identifier");
            }

            param.done(JSStubElementTypes.FORMAL_PARAMETER);
        } else {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.parameter.name", new Object[0]));
        }

        checkMatches(this.builder, AsxTokenSets.RPAR, "javascript.parser.message.expected.rparen");
        this.parseBlock();
        block.done(JSElementTypes.CATCH_BLOCK);
    }

    private void parseThrowStatement() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.THROW_KEYWORD);
        PsiBuilder.Marker statement = this.builder.mark();
        this.builder.advanceLexer();
        if(!hasSemanticLinefeedBefore(this.builder)) {
            this.myJavaScriptParser.getExpressionParser().parseExpression();
            this.checkForSemicolon();
        } else {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.expression", new Object[0]));
        }

        statement.done(JSElementTypes.THROW_STATEMENT);
    }

    private void parseSwitchStatement() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.SWITCH_KEYWORD);
        PsiBuilder.Marker statement = this.builder.mark();
        this.builder.advanceLexer();
        checkMatches(this.builder, AsxTokenSets.LPAR, "javascript.parser.message.expected.lparen");
        this.myJavaScriptParser.getExpressionParser().parseExpression();
        checkMatches(this.builder, AsxTokenSets.RPAR, "javascript.parser.message.expected.rparen");
        checkMatches(this.builder, AsxTokenSets.LBRACE, "javascript.parser.message.expected.lbrace");

        while(this.builder.getTokenType() != AsxTokenSets.RBRACE) {
            if(this.builder.eof()) {
                this.builder.error(JSBundle.message("javascript.parser.message.unexpected.end.of.file", new Object[0]));
                statement.done(JSElementTypes.SWITCH_STATEMENT);
                return;
            }

            this.parseCaseOrDefaultClause();
        }

        this.builder.advanceLexer();
        statement.done(JSElementTypes.SWITCH_STATEMENT);
    }

    private void parseCaseOrDefaultClause() {
        IElementType firstToken = this.builder.getTokenType();
        PsiBuilder.Marker clause = this.builder.mark();
        if(firstToken != AsxTokenSets.CASE_KEYWORD && firstToken != AsxTokenSets.DEFAULT_KEYWORD) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.catch.or.default", new Object[0]));
        }

        this.builder.advanceLexer();
        if(firstToken == AsxTokenSets.CASE_KEYWORD) {
            this.myJavaScriptParser.getExpressionParser().parseExpression();
        }

        checkMatches(this.builder, AsxTokenSets.COLON, "javascript.parser.message.expected.colon");

        while(true) {
            IElementType token = this.builder.getTokenType();
            if(token == null || token == AsxTokenSets.CASE_KEYWORD || token == AsxTokenSets.DEFAULT_KEYWORD || token == AsxTokenSets.RBRACE) {
                clause.done(JSElementTypes.CASE_CLAUSE);
                return;
            }

            this.parseStatement();
        }
    }

    private void parseWithStatement() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.WITH_KEYWORD);
        PsiBuilder.Marker statement = this.builder.mark();
        this.builder.advanceLexer();
        checkMatches(this.builder, AsxTokenSets.LPAR, "javascript.parser.message.expected.lparen");
        this.myJavaScriptParser.getExpressionParser().parseExpression();
        checkMatches(this.builder, AsxTokenSets.RPAR, "javascript.parser.message.expected.rparen");
        this.parseStatement();
        statement.done(JSElementTypes.WITH_STATEMENT);
    }

    private void parseReturnStatement() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.RETURN_KEYWORD);
        PsiBuilder.Marker statement = this.builder.mark();
        this.builder.advanceLexer();
        boolean hasNewLine = hasSemanticLinefeedBefore(this.builder);
        if(!hasNewLine) {
            this.myJavaScriptParser.getExpressionParser().parseExpressionOptional();
            this.checkForSemicolon();
        }

        statement.done(JSElementTypes.RETURN_STATEMENT);
    }

    private void parseBreakStatement() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.BREAK_KEYWORD);
        PsiBuilder.Marker statement = this.builder.mark();
        this.builder.advanceLexer();
        if(!hasSemanticLinefeedBefore(this.builder) && this.builder.getTokenType() == AsxTokenSets.IDENTIFIER) {
            this.builder.advanceLexer();
        }

        this.checkForSemicolon();
        statement.done(JSElementTypes.BREAK_STATEMENT);
    }

    private void parseContinueStatement() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.CONTINUE_KEYWORD);
        PsiBuilder.Marker statement = this.builder.mark();
        this.builder.advanceLexer();
        if(!hasSemanticLinefeedBefore(this.builder) && this.builder.getTokenType() == AsxTokenSets.IDENTIFIER) {
            this.builder.advanceLexer();
        }

        this.checkForSemicolon();
        statement.done(JSElementTypes.CONTINUE_STATEMENT);
    }

    private void parseIterationStatement() {
        IElementType tokenType = this.builder.getTokenType();
        if(tokenType == AsxTokenSets.DO_KEYWORD) {
            this.parseDoWhileStatement();
        } else if(tokenType == AsxTokenSets.WHILE_KEYWORD) {
            this.parseWhileStatement();
        } else if(tokenType == AsxTokenSets.FOR_KEYWORD) {
            this.parseForStatement();
        } else {
            LOG.error("Unknown iteration statement");
        }

    }

    private void parseForStatement() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.FOR_KEYWORD);
        PsiBuilder.Marker statement = this.builder.mark();
        boolean forin = this.parseForLoopHeader();
        this.parseStatement();
        statement.done(forin?JSElementTypes.FOR_IN_STATEMENT:JSElementTypes.FOR_STATEMENT);
    }

    public boolean parseForLoopHeader() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.FOR_KEYWORD);
        this.builder.advanceLexer();
        boolean hasEach = this.builder.getTokenType() == AsxTokenSets.EACH_KEYWORD;
        if(hasEach) {
            this.builder.advanceLexer();
        }

        checkMatches(this.builder, AsxTokenSets.LPAR, "javascript.parser.message.expected.lparen");
        boolean empty;
        if(AsxTokenSets.VAR_MODIFIERS.contains(this.builder.getTokenType())) {
            this.parseVarStatement(true);
            empty = false;
        } else {
            empty = !this.myJavaScriptParser.getExpressionParser().parseExpressionOptional(false, false);
        }

        boolean forin = false;
        if(this.builder.getTokenType() == AsxTokenSets.SEMICOLON) {
            this.builder.advanceLexer();
            this.myJavaScriptParser.getExpressionParser().parseExpressionOptional();
            if(this.builder.getTokenType() == AsxTokenSets.SEMICOLON) {
                this.builder.advanceLexer();
            } else {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.semicolon", new Object[0]));
            }

            this.myJavaScriptParser.getExpressionParser().parseExpressionOptional();
        } else if(this.builder.getTokenType() != AsxTokenSets.IN_KEYWORD && this.builder.getTokenType() != AsxTokenSets.OF_KEYWORD) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.forloop.in.or.semicolon", new Object[0]));
        } else {
            forin = true;
            if(empty) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.forloop.left.hand.side.expression.or.variable.declaration", new Object[0]));
            }

            this.builder.advanceLexer();
            this.myJavaScriptParser.getExpressionParser().parseExpression();
        }

        checkMatches(this.builder, AsxTokenSets.RPAR, "javascript.parser.message.expected.rparen");
        return forin;
    }

    private void parseWhileStatement() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.WHILE_KEYWORD);
        PsiBuilder.Marker statement = this.builder.mark();
        this.builder.advanceLexer();
        checkMatches(this.builder, AsxTokenSets.LPAR, "javascript.parser.message.expected.lparen");
        this.myJavaScriptParser.getExpressionParser().parseExpression();
        checkMatches(this.builder, AsxTokenSets.RPAR, "javascript.parser.message.expected.rparen");
        this.parseStatement();
        statement.done(JSElementTypes.WHILE_STATEMENT);
    }

    private void parseDoWhileStatement() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.DO_KEYWORD);
        PsiBuilder.Marker statement = this.builder.mark();
        this.builder.advanceLexer();
        this.parseStatement();
        checkMatches(this.builder, AsxTokenSets.WHILE_KEYWORD, "javascript.parser.message.expected.while.keyword");
        checkMatches(this.builder, AsxTokenSets.LPAR, "javascript.parser.message.expected.lparen");
        this.myJavaScriptParser.getExpressionParser().parseExpression();
        checkMatches(this.builder, AsxTokenSets.RPAR, "javascript.parser.message.expected.rparen");
        this.checkForSemicolon();
        statement.done(JSElementTypes.DOWHILE_STATEMENT);
    }

    private void parseIfStatement() {
        ArrayList ifMarkers = null;

        PsiBuilder.Marker i;
        while(true) {
            LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.IF_KEYWORD);
            i = this.builder.mark();
            this.parseIfStatementHeader();
            this.parseStatement();
            if(this.builder.getTokenType() != AsxTokenSets.ELSE_KEYWORD) {
                break;
            }

            this.builder.advanceLexer();
            if(this.builder.getTokenType() != AsxTokenSets.IF_KEYWORD) {
                this.parseStatement();
                break;
            }

            if(ifMarkers == null) {
                ifMarkers = new ArrayList();
            }

            if(ifMarkers.size() < MAX_TREE_DEPTH) {
                ifMarkers.add(i);
            } else {
                i.drop();
            }
        }

        i.done(JSElementTypes.IF_STATEMENT);
        if(ifMarkers != null) {
            for(int var3 = ifMarkers.size() - 1; var3 >= 0; --var3) {
                ((PsiBuilder.Marker)ifMarkers.get(var3)).done(JSElementTypes.IF_STATEMENT);
            }
        }

    }

    public void parseIfStatementHeader() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.IF_KEYWORD);
        this.builder.advanceLexer();
        checkMatches(this.builder, AsxTokenSets.LPAR, "javascript.parser.message.expected.lparen");
        this.myJavaScriptParser.getExpressionParser().parseExpression();

        while(this.builder.getTokenType() == AsxTokenSets.OROR || this.builder.getTokenType() == AsxTokenSets.EQEQ) {
            this.builder.advanceLexer();
        }

        checkMatches(this.builder, AsxTokenSets.RPAR, "javascript.parser.message.expected.rparen");
    }

    protected void parseEmptyStatement() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.SEMICOLON);
        PsiBuilder.Marker statement = this.builder.mark();
        this.builder.advanceLexer();
        statement.done(JSElementTypes.EMPTY_STATEMENT);
    }

    private void parseVarStatement(boolean inForInitializationContext) {
        this.parseVarStatementNoMarker(inForInitializationContext, this.builder.mark());
    }

    protected void parseVarStatementNoMarker(boolean inForInitializationContext, @NotNull PsiBuilder.Marker var) {
        IElementType declType = this.builder.getTokenType();
        LOG.assertTrue(AsxTokenSets.VAR_MODIFIERS.contains(declType));
        if(this.builder.getUserData(withinInterfaceKey) != null) {
            this.builder.error(JSBundle.message("interface.should.have.no.variable.declarations"));
        }

        this.builder.advanceLexer();
        this.parseVarList(inForInitializationContext);
        if(!inForInitializationContext) {
            this.forceCheckForSemicolon();
        }

        var.done(AsxElementTypes.VARIABLE_STATEMENT);
        var.setCustomEdgeTokenBinders(INCLUDE_DOC_COMMENT_AT_LEFT, WhitespacesBinders.DEFAULT_RIGHT_BINDER);
    }
    protected void parseVarList(boolean inForInitializationContext) {
        boolean first = true;
        do {
            if(first) {
                first = false;
            } else {
                checkMatches(this.builder, AsxTokenSets.COMMA, "javascript.parser.message.expected.comma");
            }
            this.parseVarDeclaration(!inForInitializationContext);
        } while(this.builder.getTokenType() == AsxTokenSets.COMMA);

    }
    public void parseVarDeclaration(boolean allowIn) {
        PsiBuilder.Marker var = this.builder.mark();
        if(this.parseVarName(var)) {
            this.myJavaScriptParser.getExpressionParser().tryParseType();
            if(this.builder.getTokenType() == AsxTokenSets.EQ) {
                this.parseVariableInitializer(allowIn);
            }
            var.done(this.getVariableElementType());
            var.setCustomEdgeTokenBinders(INCLUDE_DOC_COMMENT_AT_LEFT, WhitespacesBinders.DEFAULT_RIGHT_BINDER);
        }
    }
    public boolean checkForSemicolon() {
        IElementType tokenType = this.builder.getTokenType();
        if(tokenType == AsxTokenSets.SEMICOLON) {
            this.builder.advanceLexer();
            return true;
        } else {
            return false;
        }
    }

    public void forceCheckForSemicolon() {
        boolean b = this.checkForSemicolon();
        if(!b && !hasSemanticLinefeedBefore(this.builder)) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.newline.or.semicolon"));
        }
    }



    public IElementType getVariableElementType() {
        return AsxElementTypes.VARIABLE_DECLARATION;
    }

    protected boolean parseVarName(PsiBuilder.Marker var) {
        if(!this.isIdentifierToken(this.builder.getTokenType())) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.variable.name", new Object[0]));
            this.builder.advanceLexer();
            var.drop();
            return false;
        } else {
            this.builder.advanceLexer();
            return true;
        }
    }

    protected boolean isLocalVarContext() {
        return this.builder.getUserData(localVarsKey) != null;
    }

    protected void parseVariableInitializer(boolean allowIn) {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.EQ);
        this.builder.advanceLexer();
        if(allowIn) {
            if(!this.myJavaScriptParser.getExpressionParser().parseAssignmentExpression(allowIn, false)) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.expression", new Object[0]));
            }
        } else if(!this.myJavaScriptParser.getExpressionParser().parseAssignmentExpression(allowIn, false)) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.expression", new Object[0]));
        }

    }

    public void parseBlock() {
        this.parseBlockOrFunctionBody(AsxStatementParser.BlockType.BLOCK);
    }

    public boolean parseFunctionBody() {
        return this.parseBlockOrFunctionBody(AsxStatementParser.BlockType.FUNCTION_BODY);
    }

    protected boolean checkIdentifier(IElementType elementType) {
        if(this.isIdentifierToken(elementType)) {
            this.builder.advanceLexer();
            return true;
        } else {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.identifier", new Object[0]));
            return false;
        }
    }

    protected void parseES6FromDeclaration() {
        if(this.builder.getTokenType() == AsxTokenSets.FROM_KEYWORD || this.isJavaScript() && this.builder.getTokenType() == AsxTokenSets.IDENTIFIER) {
            PsiBuilder.Marker marker = this.builder.mark();
            this.builder.advanceLexer();
            if(!AsxTokenSets.STRING_LITERALS.contains(this.builder.getTokenType())) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.string.literal", new Object[0]));
                marker.done(ES6ElementTypes.FROM_CLAUSE);
            } else {
                this.builder.advanceLexer();
                marker.done(ES6ElementTypes.FROM_CLAUSE);
            }
        } else {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.from", new Object[0]));
        }
    }

    protected void parseES6ImportStatement() {
        PsiBuilder.Marker marker = this.builder.mark();
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.IMPORT_KEYWORD);
        this.builder.advanceLexer();
        boolean parsedAnything;
        if(AsxTokenSets.STRING_LITERALS.contains(this.builder.getTokenType())) {
            parsedAnything = true;
            this.builder.advanceLexer();
        } else {
            parsedAnything = this.parseES6ImportClause();
            this.parseES6FromDeclaration();
        }

        if(!parsedAnything) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.identifier.string.literal.or.lbrace", new Object[0]));
        }

        this.forceCheckForSemicolon();
        marker.done(ES6ElementTypes.IMPORT_DECLARATION);
    }

    protected boolean parseES6ImportClause() {
        boolean parsedAnything = false;
        if(this.builder.getTokenType() == AsxTokenSets.MULT) {
            parsedAnything = true;
            this.parseES6NamespaceImport();
        } else {
            boolean expectedComma = false;
            if(this.isIdentifierToken(this.builder.getTokenType())) {
                parsedAnything = true;
                PsiBuilder.Marker importedBinding = this.builder.mark();
                this.builder.advanceLexer();
                importedBinding.done(ES6ElementTypes.IMPORTED_BINDING);
                if(this.builder.getTokenType() == AsxTokenSets.COMMA) {
                    this.builder.advanceLexer();
                } else {
                    expectedComma = true;
                }
            }

            if(this.builder.getTokenType() == AsxTokenSets.LBRACE) {
                parsedAnything = true;
                if(expectedComma) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.comma", new Object[0]));
                }

                this.builder.advanceLexer();
                this.parseES6ImportOrExportClause(true);
            }
        }

        return parsedAnything;
    }

    protected void parseES6NamespaceImport() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenSets.MULT);
        this.builder.advanceLexer();
        if(checkMatches(this.builder, AsxTokenSets.AS_KEYWORD, "javascript.parser.message.expected.as") && this.isIdentifierToken(this.builder.getTokenType())) {
            PsiBuilder.Marker importedBinding = this.builder.mark();
            this.builder.advanceLexer();
            importedBinding.done(ES6ElementTypes.IMPORTED_BINDING);
        }

    }

    protected void parseES6ImportOrExportClause(boolean isImportClause) {
        while(true) {
            if(this.builder.getTokenType() == AsxTokenSets.RBRACE) {
                this.builder.advanceLexer();
            } else if(this.parseES6ImportOrExportSpecifier(isImportClause)) {
                if(this.builder.getTokenType() == AsxTokenSets.COMMA) {
                    this.builder.advanceLexer();
                }
                continue;
            }

            return;
        }
    }

    private boolean parseES6ImportOrExportSpecifier(boolean isImportSpecifier) {
        IElementType firstToken = this.builder.getTokenType();
        PsiBuilder.Marker marker;
        if((!isImportSpecifier || !JSKeywordSets.IDENTIFIER_NAMES.contains(firstToken)) && !this.isIdentifierToken(firstToken)) {
            if(JSKeywordSets.IDENTIFIER_NAMES.contains(firstToken)) {
                marker = isImportSpecifier?this.builder.mark():null;
                this.builder.advanceLexer();
                checkMatches(this.builder, AsxTokenSets.AS_KEYWORD, "javascript.parser.message.expected.as");
                this.checkIdentifier(firstToken);
                if(marker != null) {
                    marker.done(ES6ElementTypes.IMPORT_SPECIFIER);
                }

                return true;
            } else {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.name", new Object[0]));
                return false;
            }
        } else {
            marker = this.builder.mark();
            this.builder.advanceLexer();
            if(this.builder.getTokenType() == AsxTokenSets.AS_KEYWORD) {
                PsiBuilder.Marker alias = this.builder.mark();
                this.builder.advanceLexer();
                this.checkIdentifier(this.builder.getTokenType());
                alias.done(ES6ElementTypes.IMPORT_SPECIFIER_ALIAS);
            }

            marker.done(isImportSpecifier?ES6ElementTypes.IMPORT_SPECIFIER:ES6ElementTypes.EXPORT_SPECIFIER);
            return true;
        }
    }

    protected boolean parseBlockOrFunctionBody(AsxStatementParser.BlockType type) {
        PsiBuilder.Marker block = type != AsxStatementParser.BlockType.PACKAGE_OR_CLASS_BODY?this.builder.mark():null;
        return this.parseBlockNoMarker(type, block);
    }

    private boolean parseBlockNoMarker(AsxStatementParser.BlockType type, PsiBuilder.Marker block) {
        boolean isLocalVars;
        if(this.builder.getTokenType() != AsxTokenSets.LBRACE) {
            JSLanguageDialect localVarsWasSet1 = this.builder.getUserData(JS_DIALECT_KEY);
            isLocalVars = type == AsxStatementParser.BlockType.ARROW_FUNCTION_BODY || type == AsxStatementParser.BlockType.FUNCTION_BODY && localVarsWasSet1 != null && localVarsWasSet1.getOptionHolder().hasFeature(JSLanguageFeature.EXPRESSION_CLOSURES);
            if(isLocalVars && this.myJavaScriptParser.getExpressionParser().parseAssignmentExpression(true, false)) {
                if(block != null) {
                    block.drop();
                }

                return true;
            } else {
                if(block != null) {
                    block.rollbackTo();
                }

                this.builder.error(JSBundle.message("javascript.parser.message.expected.lbrace"));
                return false;
            }
        } else {
            this.builder.advanceLexer();
            boolean localVarsWasSet = false;
            if(type == AsxStatementParser.BlockType.FUNCTION_BODY || type == AsxStatementParser.BlockType.ARROW_FUNCTION_BODY) {
                isLocalVars = this.isLocalVarContext();
                if(!isLocalVars) {
                    this.builder.putUserData(localVarsKey, "");
                    localVarsWasSet = true;
                }
            }

            try {
                while(this.builder.getTokenType() != AsxTokenSets.RBRACE) {
                    if(this.builder.eof()) {
                        this.builder.error(JSBundle.message("javascript.parser.message.missing.rbrace", new Object[0]));
                        if(block != null) {
                            block.done(JSElementTypes.BLOCK_STATEMENT);
                        }

                        isLocalVars = false;
                        return isLocalVars;
                    }

                    if(type != AsxStatementParser.BlockType.FUNCTION_BODY && type != AsxStatementParser.BlockType.ARROW_FUNCTION_BODY) {
                        if(type == AsxStatementParser.BlockType.BLOCK) {
                            this.parseStatement();
                        } else {
                            this.parsePackageBodyStatement();
                        }
                    } else {
                        this.parseSourceElement();
                    }
                }
            } finally {
                if(localVarsWasSet) {
                    this.builder.putUserData(localVarsKey, (Object)null);
                }

            }

            this.builder.advanceLexer();
            if(block != null) {
                block.done(JSElementTypes.BLOCK_STATEMENT);
            }

            return true;
        }
    }

    public static enum BlockType {
        FUNCTION_BODY,
        ARROW_FUNCTION_BODY,
        BLOCK,
        PACKAGE_OR_CLASS_BODY;

        private BlockType() {
        }
    }
}
