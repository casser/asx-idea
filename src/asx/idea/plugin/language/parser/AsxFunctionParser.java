package asx.idea.plugin.language.parser;

import asx.idea.plugin.language.AsxElementTypes;
import asx.idea.plugin.language.AsxTokenSets;
import asx.idea.plugin.language.AsxTokenTypes;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesBinders;
import com.intellij.lang.atscript.AtScriptElementTypes;
import com.intellij.lang.javascript.*;
import com.intellij.lang.javascript.parsing.JSParsingResult;
import com.intellij.lang.javascript.psi.JSParameter;
import com.intellij.lang.javascript.psi.JSStubElementType;
import com.intellij.lang.javascript.psi.ecmal4.impl.JSAttributeNameValuePairImpl;
import com.intellij.lang.javascript.psi.stubs.JSParameterStub;
import com.intellij.openapi.util.Key;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxFunctionParser<T extends AsxSyntaxParserBase> extends AsxAbstractParser<T> {
    private static final Key<Boolean> ASYNC_METHOD_KEY = Key.create("js.asyncMethod");
    private static final Key<List<Boolean>> ASYNC_STACK_KEY = Key.create("js.asyncMethodsStack");

    public boolean isAsyncContext() {
        List stack = (List)this.builder.getUserData(ASYNC_STACK_KEY);
        return stack != null && !stack.isEmpty() && stack.get(stack.size() - 1) == Boolean.TRUE;
    }
    public static final Key<String> methodsEmptinessKey = Key.create("methodsEmptinessKey");
    protected static final String METHODS_EMPTINESS_ALWAYS = "a";
    public static final String METHODS_EMPTINESS_POSSIBLY = "p";

    private AsxFunctionParser<T> getFunctionParser(){
        return this.myJavaScriptParser.getFunctionParser();
    }
    private AsxExpressionParser<T> getExpressionParser(){
        return this.myJavaScriptParser.getExpressionParser();
    }
    private AsxStatementParser<T> getStatementParser(){
        return this.myJavaScriptParser.getStatementParser();
    }

    protected AsxFunctionParser(T parser) {
        super(parser);
    }
    public boolean parseFunctionExpression() {
        PsiBuilder.Marker mark = this.builder.mark();
        this.parseFunctionExpressionAttributeList();
        return this.parseFunctionNoMarker(AsxFunctionParser.Context.EXPRESSION, mark);
    }
    public void parseFunctionDeclaration() {
        PsiBuilder.Marker mark = this.builder.mark();
        this.parseAttributesList();
        this.parseFunctionNoMarker(AsxFunctionParser.Context.SOURCE_ELEMENT, mark);
    }
    protected void parseFunctionExpressionAttributeList() {
        if(this.builder.getTokenType() == AsxTokenTypes.ASYNC_KEYWORD) {
            this.parseAttributesList();
        }

    }
    public boolean parseFunctionNoMarker(AsxFunctionParser.Context context, @NotNull PsiBuilder.Marker functionMarker) {
        boolean functionKeywordWasOmitted = true;
        boolean parsedWithoutErrors = true;
        if(this.builder.getTokenType() == AsxTokenTypes.FUNCTION_KEYWORD) {
            this.builder.advanceLexer();
            functionKeywordWasOmitted = false;
            if(this.builder.getTokenType() == AsxTokenTypes.MULT) {
                this.builder.advanceLexer();
            }
        }

        if(!this.parseFunctionName(functionKeywordWasOmitted, context)) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.function.name"));
            parsedWithoutErrors = false;
        }

        JSParsingResult parameterListResult = JSParsingResult.NO_ERRORS_HAS_IMPRINT;
        this.parseParameterList(context == AsxFunctionParser.Context.EXPRESSION || context == AsxFunctionParser.Context.PROPERTY);
        parsedWithoutErrors &= !parameterListResult.hasErrors();
        this.myJavaScriptParser.getExpressionParser().tryParseType();
        String methodEmptiness = this.builder.getUserData(methodsEmptinessKey);
        if(methodEmptiness == null) {
            if(AsxTokenSets.ARROWS.contains(this.builder.getTokenType())) {
                this.builder.advanceLexer();
            }
            parsedWithoutErrors &= this.myJavaScriptParser.getStatementParser().parseFunctionBody();
        } else if("a".equals(methodEmptiness)) {
            if(this.builder.getTokenType() == AsxTokenTypes.SEMICOLON) {
                this.builder.advanceLexer();
            }

            if(this.builder.getTokenType() == AsxTokenTypes.LBRACE) {
                String key = this.builder.getUserData(AsxStatementParser.withinInterfaceKey) == null?"javascript.ambient.declaration.should.have.no.body":"interface.function.declaration.should.have.no.body";
                parsedWithoutErrors = false;
                this.builder.error(JSBundle.message(key));
            }
        } else if("p".equals(methodEmptiness)) {
            if(AsxTokenSets.ARROWS.contains(this.builder.getTokenType())) {
                this.builder.advanceLexer();
                parsedWithoutErrors &= this.myJavaScriptParser.getStatementParser().parseFunctionBody();
            } else if(this.builder.getTokenType() == AsxTokenTypes.SEMICOLON) {
                this.builder.advanceLexer();
            } else if(this.builder.getTokenType() == AsxTokenTypes.LBRACE) {
                parsedWithoutErrors &= this.myJavaScriptParser.getStatementParser().parseFunctionBody();
            }
        }

        functionMarker.done(context == AsxFunctionParser.Context.SOURCE_ELEMENT ? this.getFunctionDeclarationElementType() : this.getFunctionExpressionElementType());
        functionMarker.setCustomEdgeTokenBinders(INCLUDE_DOC_COMMENT_AT_LEFT, WhitespacesBinders.DEFAULT_RIGHT_BINDER);
        return parsedWithoutErrors;
    }
    public boolean parseFunctionName(boolean functionKeywordWasOmitted, AsxFunctionParser.Context context) {
        IElementType firstToken = this.builder.getTokenType();
        if(context != AsxFunctionParser.Context.EXPRESSION && (firstToken == AsxTokenTypes.GET_KEYWORD || firstToken == AsxTokenTypes.SET_KEYWORD) && AsxTokenSets.PROPERTY_NAMES.contains(this.builder.lookAhead(1))) {
            this.builder.advanceLexer();
        }

        IElementType tokenType = this.builder.getTokenType();
        if(this.isIdentifierToken(tokenType)) {
            this.parseFunctionIdentifier();
        } else {
            if(!functionKeywordWasOmitted || !JSKeywordSets.PROPERTY_NAMES.contains(tokenType)) {
                return context == AsxFunctionParser.Context.EXPRESSION;
            }

            this.myJavaScriptParser.getExpressionParser().buildTokenElement(AsxElementTypes.REFERENCE_EXPRESSION);
        }

        return true;
    }
    protected void parseFunctionIdentifier() {
        this.myJavaScriptParser.getExpressionParser().buildTokenElement(AsxElementTypes.REFERENCE_EXPRESSION);
    }
    protected IElementType getFunctionDeclarationElementType() {
        return AsxElementTypes.FUNCTION_DECLARATION;
    }
    public void parseAttributeWithoutBrackets() {
        PsiBuilder.Marker attribute = this.builder.mark();
        if(!this.getExpressionParser().parseQualifiedTypeName()) {
            attribute.drop();
        } else {
            if(this.builder.getTokenType() == AsxTokenTypes.LPAR) {
                this.getExpressionParser().parseArgumentList();
            }

            attribute.done(AtScriptElementTypes.ATTRIBUTE);
        }
    }
    public boolean parseAttributesList() {
        if(this.builder.getTokenType() == AsxTokenTypes.LBRACKET) {
            return false;
        } else {
            PsiBuilder.Marker modifierList = this.builder.mark();
            this.tryParseAttributesWithoutBrackets();
            if(this.builder.getTokenType() == AsxTokenTypes.EXPORT_KEYWORD) {
                this.builder.advanceLexer();
            }

            if(this.builder.getTokenType() == AsxTokenTypes.ASYNC_KEYWORD) {
                this.builder.advanceLexer();
                this.builder.putUserData(ASYNC_METHOD_KEY, Boolean.TRUE);
            }

            modifierList.done(JSStubElementTypes.ATTRIBUTE_LIST);
            return true;
        }
    }
    public void tryParseAttributesWithoutBrackets() {
        while(this.builder.getTokenType() == AsxTokenTypes.AT) {
            this.builder.advanceLexer();
            this.parseAttributeWithoutBrackets();
        }
    }


    @NotNull
    public boolean parseParameterList(boolean funExpr) {
        if(this.is(AsxTokenTypes.LPAR)) {
            PsiBuilder.Marker marker = this.builder.mark();
            this.builder.advanceLexer();
            if(!this.is(AsxTokenTypes.RPAR)){
                this.parseFunctionParameter();
                while(this.is(AsxTokenTypes.COMMA)) {
                    this.builder.advanceLexer();
                    this.parseFunctionParameter();
                }
            }
            if(!this.is(AsxTokenTypes.RPAR)){
                this.builder.error(JSBundle.message("javascript.parser.message.expected.comma.or.rparen"));
                return false;
            }else{
                this.builder.advanceLexer();
                marker.done(AsxElementTypes.FUNCTION_PARAMETERS);
                return true;
            }
        } else {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.lparen"));
            return false;
        }
    }
    public void parseFunctionParameter() {
        PsiBuilder.Marker marker = this.builder.mark();
        if(this.builder.getTokenType() == AsxTokenTypes.DOT_DOT_DOT) {
            this.builder.advanceLexer();
        }
        this.getStatementParser().parseVarDeclaration(false);
        marker.done(AsxElementTypes.FUNCTION_PARAMETER);
    }

    protected boolean parseArrowFunction() {
        PsiBuilder.Marker arrowFunction = this.builder.mark();
        this.parseFunctionExpressionAttributeList();
        IElementType firstToken = this.builder.getTokenType();
        boolean isArrowFunction = false;
        if(this.isIdentifierToken(firstToken)) {
            PsiBuilder.Marker params = this.builder.mark();
            this.parseFunctionParameter();
            params.done(AsxElementTypes.FUNCTION_PARAMETERS);
            this.myJavaScriptParser.getExpressionParser().tryParseType();
        } else {
            if(this.parseParameterList(true)) {
                isArrowFunction = true;
            } else {
                arrowFunction.rollbackTo();
                return false;
            }
            this.myJavaScriptParser.getExpressionParser().tryParseType();
        }


        if(this.is(AsxTokenSets.ARROWS)) {
            this.builder.advanceLexer();
            this.myJavaScriptParser.getStatementParser().parseBlockOrFunctionBody(AsxStatementParser.BlockType.ARROW_FUNCTION_BODY);
            isArrowFunction = true;
        } else
        if(isArrowFunction) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.eqgt"));
        }

        if(isArrowFunction) {
            arrowFunction.done(this.getFunctionExpressionElementType());
        } else {
            arrowFunction.rollbackTo();
        }

        return isArrowFunction;
    }

    protected IElementType getFunctionExpressionElementType() {
        return AsxElementTypes.FUNCTION_EXPRESSION;
    }

    public boolean tryParseTypeParameterList() {
        return true;
    }

    public enum Context {EXPRESSION,SOURCE_ELEMENT,PROPERTY}
}
