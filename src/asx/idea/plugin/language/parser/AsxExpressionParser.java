package asx.idea.plugin.language.parser;

import asx.idea.plugin.language.AsxElementTypes;
import asx.idea.plugin.language.AsxTokenSets;
import asx.idea.plugin.language.AsxTokenTypes;
import com.intellij.codeInsight.daemon.XmlErrorMessages;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesBinders;
import com.intellij.lang.ecmascript6.ES6ElementTypes;
import com.intellij.lang.javascript.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxExpressionParser<T extends AsxSyntaxParserBase> extends AsxAbstractParser<T> {

    protected static final Logger LOG = Logger.getInstance("#com.intellij.lang.javascript.parsing.ExpressionParsing");
    protected static final Key<IElementType> DESTRUCTURING_VAR_TYPE = Key.create("within.destructuring.expression");


    protected AsxExpressionParser(T parser) {
        super(parser);
    }
    private AsxFunctionParser<T> getFunctionParser(){
        return this.myJavaScriptParser.getFunctionParser();
    }
    private AsxExpressionParser<T> getExpressionParser(){
        return this.myJavaScriptParser.getExpressionParser();
    }
    private AsxStatementParser<T> getStatementParser(){
        return this.myJavaScriptParser.getStatementParser();
    }



    public boolean parsePrimaryExpression() {
        IElementType firstToken = this.builder.getTokenType();
        if(firstToken == AsxTokenTypes.THIS_KEYWORD) {
            this.buildTokenElement(JSElementTypes.THIS_EXPRESSION);
            return true;
        } else if(firstToken == AsxTokenTypes.SUPER_KEYWORD) {
            this.buildTokenElement(JSElementTypes.SUPER_EXPRESSION);
            return true;
        } else if(firstToken == AsxTokenTypes.LET_KEYWORD && this.parseLetExpression()) {
            return true;
        } else if(firstToken == AsxTokenTypes.YIELD_KEYWORD && this.parseYieldExpression()) {
            return true;
        } else {
            PsiBuilder.Marker marker;
            if(!this.isIdentifierToken(firstToken) && firstToken != AsxTokenTypes.ANY_IDENTIFIER) {
                if(!AsxTokenSets.PRIMARY_LITERALS.contains(firstToken)) {
                    if(firstToken == AsxTokenTypes.LPAR) {
                        this.parseParenthesizedExpression();
                        return true;
                    } else if(firstToken == AsxTokenTypes.LBRACKET) {
                        this.parseArrayLiteralExpression(true, false);
                        return true;
                    } else if(firstToken == AsxTokenTypes.LBRACE) {
                        this.parseObjectLiteralExpression(false);
                        return true;
                    } else if(firstToken == AsxTokenTypes.FUNCTION_KEYWORD) {
                        this.myJavaScriptParser.getFunctionParser().parseFunctionExpression();
                        return true;
                    } else if(AsxTokenSets.ACCESS_MODIFIERS.contains(firstToken)) {
                        marker = this.builder.mark();
                        this.builder.advanceLexer();
                        if(AsxTokenTypes.COLON_COLON == this.builder.getTokenType()) {
                            this.builder.advanceLexer();
                            if(this.isIdentifierToken(this.builder.getTokenType())) {
                                this.builder.advanceLexer();
                            }

                            marker.done(AsxElementTypes.REFERENCE_EXPRESSION);
                            return true;
                        } else {
                            marker.drop();
                            return false;
                        }
                    } else if(firstToken != AsxTokenTypes.XML_START_TAG_START && firstToken != AsxTokenTypes.XML_START_TAG_LIST) {
                        if(firstToken != AsxTokenTypes.AT) {
                            if(firstToken != AsxTokenTypes.INT_KEYWORD && firstToken != AsxTokenTypes.UINT_KEYWORD) {
                                return firstToken == AsxTokenTypes.BACKQUOTE?this.parseStringTemplate(this.builder.mark()):false;
                            } else {
                                marker = this.builder.mark();
                                this.builder.advanceLexer();
                                marker.done(AsxElementTypes.REFERENCE_EXPRESSION);
                                return true;
                            }
                        } else {
                            marker = this.builder.mark();
                            this.builder.advanceLexer();
                            PsiBuilder.Marker possibleNamespaceStartMarker = this.builder.mark();
                            if(!this.builder.eof()) {
                                IElementType tokenType = this.builder.getTokenType();
                                if(tokenType != AsxTokenTypes.ANY_IDENTIFIER && !this.isIdentifierToken(tokenType)) {
                                    if(tokenType == AsxTokenTypes.LBRACKET) {
                                        this.builder.advanceLexer();
                                        this.parseExpression();
                                        checkMatches(this.builder, AsxTokenTypes.RBRACKET, "javascript.parser.message.expected.rbracket");
                                    } else {
                                        this.builder.error(JSBundle.message("javascript.parser.message.expected.identifier", new Object[0]));
                                    }
                                } else {
                                    this.builder.advanceLexer();
                                    if(this.builder.getTokenType() == AsxTokenTypes.COLON_COLON) {
                                        possibleNamespaceStartMarker.done(AsxElementTypes.REFERENCE_EXPRESSION);
                                        possibleNamespaceStartMarker = possibleNamespaceStartMarker.precede();
                                        this.proceedWithNamespaceReference(possibleNamespaceStartMarker, true);
                                        possibleNamespaceStartMarker = null;
                                    }
                                }
                            }

                            if(possibleNamespaceStartMarker != null) {
                                possibleNamespaceStartMarker.drop();
                            }

                            marker.done(AsxElementTypes.REFERENCE_EXPRESSION);
                            return true;
                        }
                    } else {
                        this.parseTag();
                        return true;
                    }
                } else {
                    String marker1 = this.validateLiteral();
                    this.buildTokenElement(AsxElementTypes.LITERAL_EXPRESSION);
                    if(marker1 != null) {
                        this.builder.error(marker1);
                    }

                    return true;
                }
            } else {
                marker = this.builder.mark();
                this.buildTokenElement(AsxElementTypes.REFERENCE_EXPRESSION);
                if(this.proceedWithNamespaceReference(marker, true)) {
                    marker.precede().done(AsxElementTypes.REFERENCE_EXPRESSION);
                }

                return true;
            }
        }
    }
    private boolean parseYieldExpression() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenTypes.YIELD_KEYWORD);
        PsiBuilder.Marker marker = this.builder.mark();
        this.builder.advanceLexer();
        if(this.builder.getTokenType() == AsxTokenTypes.MULT && this.isECMA6()) {
            this.builder.advanceLexer();
        }

        if(!this.parseAssignmentExpression(true, true)) {
            marker.rollbackTo();
            return false;
        } else {
            marker.done(JSElementTypes.YIELD_EXPRESSION);
            return true;
        }
    }
    private boolean parseStringTemplate(PsiBuilder.Marker stringTemplate) {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenTypes.BACKQUOTE);
        this.builder.advanceLexer();

        while(this.builder.getTokenType() != AsxTokenTypes.BACKQUOTE) {
            if(this.builder.eof()) {
                this.builder.error(JSBundle.message("javascript.parser.message.missing.backquote"));
                if(stringTemplate != null) {
                    stringTemplate.done(JSStubElementTypes.STRING_TEMPLATE_EXPRESSION);
                }
                return false;
            }

            if(this.builder.getTokenType() == AsxTokenTypes.STRING_TEMPLATE_PART) {
                this.builder.advanceLexer();
            } else
            if(this.builder.getTokenType() == AsxTokenTypes.DOLLAR) {
                this.builder.advanceLexer();
                if(this.builder.getTokenType() == AsxTokenTypes.LBRACE) {
                    this.builder.advanceLexer();
                    if(!this.parseAssignmentExpression(true, false)) {
                        this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
                    }

                    checkMatches(this.builder, AsxTokenTypes.RBRACE, "javascript.parser.message.expected.rbrace");
                }
            } else {
                this.builder.error(JSBundle.message("javascript.parser.message.missing.backquote"));
                this.builder.advanceLexer();
            }
        }

        checkMatches(this.builder, AsxTokenTypes.BACKQUOTE, "javascript.parser.message.missing.backquote");
        stringTemplate.done(JSStubElementTypes.STRING_TEMPLATE_EXPRESSION);
        return true;
    }
    private boolean parseLetExpression() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenTypes.LET_KEYWORD);
        PsiBuilder.Marker marker = this.builder.mark();
        this.builder.advanceLexer();
        if(!this.myJavaScriptParser.getStatementParser().parseLetDeclarations()) {
            marker.rollbackTo();
            return false;
        } else {
            this.parseExpression();
            marker.done(JSElementTypes.LET_EXPRESSION);
            return true;
        }
    }
    private void parseTag() {
        IElementType tokenType = this.builder.getTokenType();
        assert AsxTokenTypes.XML_START_TAG_START == tokenType || AsxTokenTypes.XML_START_TAG_LIST == tokenType;
        PsiBuilder.Marker marker = this.builder.mark();
        this.builder.advanceLexer();
        try {
            boolean endTagStarted = false;

            for(IElementType currentTokenType = this.builder.getTokenType(); currentTokenType != null; currentTokenType = this.builder.getTokenType()) {
                if(!AsxTokenSets.XML_TOKENS.contains(currentTokenType)) {
                    PsiBuilder.Marker errorMarker = this.builder.mark();
                    this.builder.advanceLexer();
                    errorMarker.error(JSBundle.message("javascript.parser.message.expected.xml.element", new Object[0]));
                } else if(currentTokenType == AsxTokenTypes.XML_START_TAG_START) {
                    this.parseTag();
                } else {
                    if(currentTokenType == AsxTokenTypes.XML_EMPTY_TAG_END || currentTokenType == AsxTokenTypes.XML_END_TAG_LIST || currentTokenType == AsxTokenTypes.XML_TAG_END && endTagStarted) {
                        this.builder.advanceLexer();
                        return;
                    }

                    if(currentTokenType == AsxTokenTypes.XML_END_TAG_START) {
                        endTagStarted = true;
                    } else if(currentTokenType == AsxTokenTypes.XML_NAME) {
                        this.parseAttribute();
                        continue;
                    }

                    this.builder.advanceLexer();
                    if(currentTokenType != AsxTokenTypes.XML_NAME && currentTokenType != AsxTokenTypes.XML_JS_SCRIPT && this.builder.getTokenType() == AsxTokenTypes.XML_ATTR_EQUAL) {
                        this.builder.error(JSBundle.message("javascript.parser.message.missing.attribute.name", new Object[0]));
                    }
                }
            }
        } finally {
            marker.done(JSElementTypes.XML_LITERAL_EXPRESSION);
        }

    }
    private void parseAttribute() {
        assert this.builder.getTokenType() == XmlTokenType.XML_NAME;

        PsiBuilder.Marker att = this.builder.mark();
        this.builder.advanceLexer();
        if(this.builder.getTokenType() == XmlTokenType.XML_EQ) {
            this.builder.advanceLexer();
            this.parseAttributeValue();
            att.done(XmlElementType.XML_ATTRIBUTE);
        } else {
            att.done(XmlElementType.XML_ATTRIBUTE);
        }

    }
    private void parseAttributeValue() {
        PsiBuilder.Marker attValue = this.builder.mark();
        if(this.builder.getTokenType() != XmlTokenType.XML_ATTRIBUTE_VALUE_START_DELIMITER) {
            if(this.builder.getTokenType() != XmlTokenType.XML_TAG_END && this.builder.getTokenType() != XmlTokenType.XML_EMPTY_ELEMENT_END) {
                this.builder.advanceLexer();
            }
        } else {
            while(true) {
                IElementType tt = this.builder.getTokenType();
                if(tt == null || tt == XmlTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER || tt == XmlTokenType.XML_END_TAG_START || tt == XmlTokenType.XML_EMPTY_ELEMENT_END || tt == XmlTokenType.XML_START_TAG_START) {
                    if(this.builder.getTokenType() == XmlTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER) {
                        this.builder.advanceLexer();
                    } else {
                        this.builder.error(XmlErrorMessages.message("xml.parsing.unclosed.attribute.value", new Object[0]));
                    }
                    break;
                }

                this.builder.advanceLexer();
            }
        }

        attValue.done(XmlElementType.XML_ATTRIBUTE_VALUE);
    }

    @Nullable
    private String validateLiteral() {
        IElementType ttype = this.builder.getTokenType();
        if(AsxTokenSets.STRING_LITERALS.contains(ttype)) {
            String ttext = this.builder.getTokenText();
            assert ttext != null;
            return validateLiteralText(ttext);
        } else {
            return null;
        }
    }

    protected static String validateLiteralText(String text) {
        return !lastSymbolEscaped(text) && (!text.startsWith("\"") || text.endsWith("\"") && text.length() != 1) && (!text.startsWith("\'") || text.endsWith("\'") && text.length() != 1)?null:JSBundle.message("javascript.parser.message.unclosed.string.literal", new Object[0]);
    }

    private static boolean lastSymbolEscaped(String text) {
        boolean escapes = false;
        boolean escaped = true;

        for(int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if(escapes) {
                escapes = false;
                escaped = true;
            } else {
                if(c == 92) {
                    escapes = true;
                }

                escaped = false;
            }
        }

        return escapes || escaped;
    }
    protected boolean tryParseDestructuringRestElement(IElementType type) {
        if(this.builder.getTokenType() == AsxTokenTypes.DOT_DOT_DOT) {
            PsiBuilder.Marker property = this.builder.mark();
            this.builder.advanceLexer();
            if(this.isIdentifierToken(this.builder.getTokenType())) {
                this.parseDestructuringBindingIdentifier();
            } else {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.identifier"));
            }

            property.done(type);
            return true;
        } else {
            return false;
        }
    }
    protected void parseDestructuringProperty() {
        if(!this.tryParseDestructuringRestElement(JSStubElementTypes.DESTRUCTURING_PROPERTY)) {
            this.parseDestructuringPropertySuper();
        }
    }
    protected void parseDestructuringPropertySuper() {
        IElementType nameToken = this.builder.getTokenType();
        PsiBuilder.Marker property = this.builder.mark();
        if(this.isIdentifierToken(nameToken) && this.builder.lookAhead(1) != AsxTokenTypes.COLON) {
            this.parseDestructuringElement();
            property.done(JSStubElementTypes.DESTRUCTURING_PROPERTY);
        } else
        if(!this.parsePropertyName()) {
            this.builder.advanceLexer();
            property.done(JSStubElementTypes.DESTRUCTURING_PROPERTY);
        } else {
            checkMatches(this.builder, AsxTokenTypes.COLON, "javascript.parser.message.expected.colon");
            IElementType valueFirstToken = this.builder.getTokenType();
            if(valueFirstToken != AsxTokenTypes.LBRACE && valueFirstToken != AsxTokenTypes.LBRACKET && !this.isIdentifierToken(valueFirstToken)) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.identifier.lbrace.or.lbracket"));
            } else {
                this.parseDestructuringElement();
            }
            property.done(JSStubElementTypes.DESTRUCTURING_PROPERTY);
        }
    }

    protected void parseDestructuringElement(@NotNull IElementType varType) {
        IElementType savedDestructuringVarType = this.builder.getUserData(DESTRUCTURING_VAR_TYPE);
        try {
            this.builder.putUserData(DESTRUCTURING_VAR_TYPE, varType);
            this.parseDestructuringElement();
        } finally {
            this.builder.putUserData(DESTRUCTURING_VAR_TYPE, savedDestructuringVarType);
        }

    }
    protected void parseDestructuringArrayElement() {
        if(!this.tryParseDestructuringRestElement(JSStubElementTypes.DESTRUCTURING_ELEMENT)) {
            this.parseDestructuringElement();
        }
    }

    private void parseDestructuringElement() {
        PsiBuilder.Marker marker = this.builder.mark();
        IElementType firstToken = this.builder.getTokenType();
        if(AsxTokenTypes.LBRACE == firstToken) {
            this.parseObjectLiteralExpression(true);
        } else
        if(AsxTokenTypes.LBRACKET == firstToken) {
            this.parseArrayLiteralExpression(true, true);
        } else
        if(this.builder.getUserData(DESTRUCTURING_VAR_TYPE) == AsxElementTypes.REFERENCE_EXPRESSION) {
            if(!this.parseMemberExpression(false)) {
                this.builder.advanceLexer();
            }
        } else
        if(this.isIdentifierToken(firstToken)) {
            this.parseDestructuringBindingIdentifier();
        } else {
            this.builder.advanceLexer();
            this.builder.error(JSBundle.message("javascript.parser.message.expected.identifier"));
        }

        if(this.builder.getTokenType() == AsxTokenTypes.EQ) {
            this.builder.advanceLexer();
            this.parseAssignmentExpression(true, false);
            marker.done(JSStubElementTypes.DESTRUCTURING_ELEMENT);
        } else {
            marker.drop();
        }

    }

    protected void parseDestructuringBindingIdentifier() {
        PsiBuilder.Marker var = this.builder.mark();
        this.builder.advanceLexer();
        IElementType varType = this.builder.getUserData(DESTRUCTURING_VAR_TYPE);
        assert varType != null;
        if(this.builder.getTokenType() == AsxTokenTypes.COLON) {
            this.builder.advanceLexer();
            this.parseType();
        }
        if(this.builder.getTokenType() == AsxTokenTypes.EQ) {
            this.builder.advanceLexer();
            this.parseAssignmentExpression(true, false);
        }
        var.done(varType);
    }

    protected void parseObjectLiteralExpression(boolean isDestructuring) {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenTypes.LBRACE);
        PsiBuilder.Marker expr = this.builder.mark();
        this.builder.advanceLexer();
        IElementType elementType = this.builder.getTokenType();

        while(elementType != AsxTokenTypes.RBRACE && elementType != null) {

            if(!this.isPropertyStart(elementType)) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.identifier.string.literal.or.numeric.literal"));
                break;
            }

            if(isDestructuring) {
                this.parseDestructuringProperty();
            } else {
                this.parseObjectProperty();
            }

            boolean wasCommaBefore = false;
            elementType = this.builder.getTokenType();
            if(elementType == AsxTokenTypes.RBRACE) {
                break;
            }

            if(elementType == AsxTokenTypes.COMMA) {
                this.builder.advanceLexer();
                wasCommaBefore = true;
            } else {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.comma"));
            }

            elementType = this.builder.getTokenType();
            if(elementType == AsxTokenTypes.RBRACE) {
                if(wasCommaBefore) {
                    break;
                }

                this.builder.error(JSBundle.message("javascript.parser.property.expected"));
            } else if(!this.isPropertyStart(elementType)) {
                break;
            }
        }

        checkMatches(this.builder, AsxTokenTypes.RBRACE, "javascript.parser.message.expected.rbrace");
        expr.done(isDestructuring?JSStubElementTypes.DESTRUCTURING_OBJECT : AsxElementTypes.OBJECT_EXPRESSION);
    }
    protected boolean isPropertyStart(IElementType elementType) {
        return this.isPropertyStartSuper(elementType)       ||
                elementType == AsxTokenTypes.LBRACKET       ||
                elementType == AsxTokenTypes.DOT_DOT_DOT    ||
                elementType == AsxTokenTypes.GET_KEYWORD    ||
                elementType == AsxTokenTypes.SET_KEYWORD    ;
    }
    protected boolean isPropertyStartSuper(IElementType elementType) {
        return AsxTokenSets.PROPERTY_NAMES.contains(elementType);
    }
    protected boolean parsePropertyName() {
        if(this.builder.getTokenType() == AsxTokenTypes.LBRACKET) {
            this.builder.advanceLexer();
            this.parseAssignmentExpression(false, false);
            checkMatches(this.builder, AsxTokenTypes.RBRACKET, "javascript.parser.message.expected.rbracket");
            return true;
        } else {
            return this.parsePropertyNameSuper();
        }
    }
    protected boolean parsePropertyNameSuper() {
        IElementType tokenType = this.builder.getTokenType();
        if(!this.isPropertyStart(tokenType)) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.property.name"));
            return false;
        } else {
            this.builder.advanceLexer();
            return true;
        }
    }

    protected void parseObjectProperty() {
        IElementType first = this.builder.getTokenType();
        boolean isPropertyName = AsxTokenSets.KEYWORDS.contains(first);
        IElementType secondToken = this.builder.lookAhead(1);
        System.out.println(first + " " + secondToken);
        PsiBuilder.Marker marker;
        if(first == AsxTokenTypes.LBRACKET) {
            marker = this.builder.mark();
            assert this.parsePropertyName() : "must be advanced after LBRACKET";
            if(this.builder.getTokenType() == AsxTokenTypes.LPAR) {
                this.getFunctionParser().parseFunctionExpression();
            } else {
                this.parsePropertyInitializer();
            }
            marker.done(ES6ElementTypes.PROPERTY);
        } else
        if((!isPropertyName || secondToken != AsxTokenTypes.LPAR) && (
                first != AsxTokenTypes.ASYNC_KEYWORD ||
                !AsxTokenSets.KEYWORDS.contains(secondToken) ||
                this.builder.lookAhead(2) != AsxTokenTypes.LPAR
        )){
            if(!isPropertyName || secondToken != AsxTokenTypes.COMMA && secondToken != AsxTokenTypes.RBRACE) {
                if(first == AsxTokenTypes.DOT_DOT_DOT) {
                    IElementType savedDestructuringVarType1 = this.builder.getUserData(DESTRUCTURING_VAR_TYPE);

                    try {
                        this.builder.putUserData(DESTRUCTURING_VAR_TYPE, AsxElementTypes.REFERENCE_EXPRESSION);
                        this.parseDestructuringProperty();
                    } finally {
                        this.builder.putUserData(DESTRUCTURING_VAR_TYPE, savedDestructuringVarType1);
                    }

                } else {
                    this.parsePropertySuper();
                }
            } else {
                marker = this.builder.mark();
                PsiBuilder.Marker ref = this.builder.mark();
                this.builder.advanceLexer();
                ref.done(AsxElementTypes.REFERENCE_EXPRESSION);
                marker.done(ES6ElementTypes.PROPERTY);
            }
        } else {
            marker = this.builder.mark();
            this.getFunctionParser().parseFunctionExpression();
            marker.done(ES6ElementTypes.PROPERTY);
        }
    }
    protected void parsePropertySuper() {
        IElementType nameToken = this.builder.getTokenType();
        PsiBuilder.Marker property = this.builder.mark();
        if(nameToken != AsxTokenTypes.SET_KEYWORD && nameToken != AsxTokenTypes.GET_KEYWORD) {
            if(nameToken == AsxTokenTypes.LPAR) {
                this.parseParenthesizedExpression();
            } else {
                String errorMessage1 = this.validateLiteral();
                this.builder.advanceLexer();
                if(errorMessage1 != null) {
                    this.builder.error(errorMessage1);
                }
            }

            this.parsePropertyInitializer();
        } else {
            PsiBuilder.Marker errorMessage = this.builder.mark();
            this.builder.advanceLexer();
            if(this.builder.getTokenType() == AsxTokenTypes.COLON) {
                errorMessage.drop();
                this.builder.advanceLexer();
                if(!this.parseAssignmentExpression(true, false)) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
                }
            } else {
                this.myJavaScriptParser.getFunctionParser().parseFunctionNoMarker(AsxFunctionParser.Context.PROPERTY, errorMessage);
            }
        }

        property.done(AsxElementTypes.OBJECT_PROPERTY);
        property.setCustomEdgeTokenBinders(INCLUDE_DOC_COMMENT_AT_LEFT, WhitespacesBinders.DEFAULT_RIGHT_BINDER);
    }

    protected void parsePropertyInitializer() {
        if(this.builder.getTokenType() == AsxTokenTypes.COLON) {
            this.builder.advanceLexer();
            if(!this.parseAssignmentExpression(true, true)) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.expression", new Object[0]));
            }
        } else {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.colon", new Object[0]));
            if(!this.parseAssignmentExpression(true, true)) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.expression", new Object[0]));
            }
        }

    }

    private void parseArrayLiteralExpression(boolean allowSkippingLeadingElements, boolean isDestructuring) {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenTypes.LBRACKET);
        PsiBuilder.Marker marker = this.builder.mark();
        this.builder.advanceLexer();
        boolean commaExpected = false;
        boolean first = true;
        Ref elementTypeRef = Ref.create(isDestructuring?JSStubElementTypes.DESTRUCTURING_ARRAY:JSElementTypes.ARRAY_LITERAL_EXPRESSION);

        while(this.builder.getTokenType() != AsxTokenTypes.RBRACKET) {
            if(commaExpected) {
                checkMatches(this.builder, AsxTokenTypes.COMMA, "javascript.parser.message.expected.comma");
            }

            if(this.builder.getTokenType() == AsxTokenTypes.COMMA) {
                if(!allowSkippingLeadingElements) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.expression", new Object[0]));
                }

                while(this.builder.getTokenType() == AsxTokenTypes.COMMA) {
                    this.builder.advanceLexer();
                }
            }

            commaExpected = false;
            if(this.builder.getTokenType() != AsxTokenTypes.RBRACKET) {
                if(isDestructuring) {
                    this.parseDestructuringArrayElement();
                } else {
                    if(!this.parseArrayElement(first, elementTypeRef)) {
                        break;
                    }

                    first = false;
                }

                commaExpected = true;
            }
        }

        checkMatches(this.builder, AsxTokenTypes.RBRACKET, "javascript.parser.message.expected.rbracket");
        marker.done((IElementType)elementTypeRef.get());
    }

    protected boolean parseArrayElement(boolean first, Ref<IElementType> elementTypeRef) {
        boolean parseMoreElements = true;
        if(first && this.builder.getTokenType() == AsxTokenTypes.FOR_KEYWORD) {
            elementTypeRef.set(JSElementTypes.ARRAY_COMPREHENSION);
            this.getStatementParser().parseForLoopHeader();

            label25:
            while(true) {
                while(this.builder.getTokenType() != AsxTokenTypes.IF_KEYWORD) {
                    if(this.builder.getTokenType() != AsxTokenTypes.FOR_KEYWORD) {
                        parseMoreElements = false;
                        break label25;
                    }

                    this.getStatementParser().parseForLoopHeader();
                }

                this.getStatementParser().parseIfStatementHeader();
            }
        }

        if(!this.parseSpreadExpression()) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
            parseMoreElements = false;
        }

        return parseMoreElements;
    }
    protected boolean parseSpreadExpression() {
        if(this.builder.getTokenType() == AsxTokenTypes.DOT_DOT_DOT) {
            PsiBuilder.Marker marker = this.builder.mark();
            this.builder.advanceLexer();
            if(!this.parseAssignmentExpression(true, false)) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
                marker.drop();
                return false;
            }

            marker.done(JSElementTypes.SPREAD_EXPRESSION);
        } else if(!this.parseAssignmentExpression(true, false)) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
            return false;
        }

        return true;
    }

    protected void parseParenthesizedExpression() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenTypes.LPAR);
        PsiBuilder.Marker expr = this.builder.mark();
        this.builder.advanceLexer();
        if(!this.parseExpressionOptional(true, true)) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
        }

        checkMatches(this.builder, AsxTokenTypes.RPAR, "javascript.parser.message.expected.rparen");
        expr.done(JSElementTypes.PARENTHESIZED_EXPRESSION);
    }

    private boolean parseMemberExpression(boolean inNewExpression) {
        PsiBuilder.Marker expr = this.builder.mark();
        IElementType type = this.builder.getTokenType();
        boolean isNew;
        if(type == AsxTokenTypes.NEW_KEYWORD) {
            isNew = true;
            boolean requestedArgumentListType = this.parseNewExpression();
            if(requestedArgumentListType) {
                expr.done(this.getNewExpressionElementType());
                expr = expr.precede();
                isNew = false;
            }
        } else {
            isNew = false;
            if(type == AsxTokenTypes.AT && this.isGwt()) {
                this.parseGwtReferenceExpression();
                expr.done(AsxElementTypes.REFERENCE_EXPRESSION);
                expr = expr.precede();
            } else if(!this.parsePrimaryExpression()) {
                expr.drop();
                return false;
            }
        }

        IElementType requestedArgumentListType1 = null;

        while(true) {
            while(true) {
                while(true) {
                    IElementType tokenType = this.builder.getTokenType();
                    boolean parsedSuccessfully = true;
                    if(tokenType != AsxTokenTypes.DOT && tokenType != AsxTokenTypes.COLON_COLON && tokenType != AsxTokenTypes.DOT_DOT) {
                        if(tokenType == AsxTokenTypes.LBRACKET) {
                            this.builder.advanceLexer();
                            this.parseExpression();
                            checkMatches(this.builder, AsxTokenTypes.RBRACKET, "javascript.parser.message.expected.rbracket");
                            expr.done(JSElementTypes.INDEXED_PROPERTY_ACCESS_EXPRESSION);
                            expr = expr.precede();
                        } else if(!inNewExpression && tokenType == AsxTokenTypes.LPAR) {
                            if(requestedArgumentListType1 == null) {
                                this.parseArgumentList();
                            } else {
                                PsiBuilder.Marker requestedArgumentListMarker1 = this.builder.mark();
                                this.parseArgumentListNoMarker();
                                requestedArgumentListMarker1.done(requestedArgumentListType1);
                            }

                            expr.done((IElementType)(isNew?this.getNewExpressionElementType():JSStubElementTypes.CALL_EXPRESSION));
                            expr = expr.precede();
                            isNew = false;
                        } else if(tokenType == AsxTokenTypes.BACKQUOTE) {
                            this.parseStringTemplate(expr);
                            expr = expr.precede();
                        } else {
                            parsedSuccessfully = this.parseDialectSpecificMemberExpressionPart();
                        }
                    } else {
                        this.builder.advanceLexer();
                        boolean requestedArgumentListMarker = false;
                        if(this.builder.getTokenType() == AsxTokenTypes.AT) {
                            requestedArgumentListMarker = true;
                            if(this.isGwt()) {
                                this.parseGwtReferenceExpression();
                                expr.done(AsxElementTypes.REFERENCE_EXPRESSION);
                                expr = expr.precede();
                                continue;
                            }

                            this.builder.advanceLexer();
                        }

                        tokenType = this.builder.getTokenType();
                        if(tokenType == AsxTokenTypes.LBRACKET && requestedArgumentListMarker || tokenType == AsxTokenTypes.LPAR) {
                            if(tokenType == AsxTokenTypes.LPAR) {
                                requestedArgumentListType1 = JSElementTypes.E4X_FILTER_QUERY_ARGUMENT_LIST;
                            }
                            continue;
                        }

                        if(tokenType != AsxTokenTypes.ANY_IDENTIFIER && !AsxTokenSets.IDENTIFIERS.contains(tokenType)) {
                            this.builder.error(JSBundle.message("javascript.parser.message.expected.name"));
                        } else {
                            PsiBuilder.Marker identifier = this.builder.mark();
                            this.builder.advanceLexer();
                            if(this.builder.getTokenType() == AsxTokenTypes.COLON_COLON) {
                                identifier.done(AsxElementTypes.REFERENCE_EXPRESSION);
                                this.proceedWithNamespaceReference(identifier.precede(), true);
                            } else {
                                identifier.drop();
                            }
                        }
                        expr.done(AsxElementTypes.REFERENCE_EXPRESSION);
                        expr = expr.precede();
                    }

                    if(!parsedSuccessfully) {
                        if(isNew) {
                            if(tokenType == AsxTokenTypes.LT && this.isECMAL4()) {
                                this.builder.error(JSBundle.message("javascript.parser.message.expected.dot", new Object[0]));
                            }

                            expr.done(this.getNewExpressionElementType());
                        } else {
                            expr.drop();
                        }

                        return true;
                    }
                }
            }
        }
    }

    private void parseGwtReferenceExpression() {
        PsiBuilder.Marker gwtExpr = this.builder.mark();
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenTypes.AT);
        this.builder.advanceLexer();

        while(true) {
            if(this.isIdentifierToken(this.builder.getTokenType())) {
                this.builder.advanceLexer();
            } else {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.name"));
            }

            if(this.builder.getTokenType() != AsxTokenTypes.DOT) {
                if(this.builder.getTokenType() == AsxTokenTypes.COLON_COLON) {
                    this.builder.advanceLexer();
                    if(this.builder.getTokenType() == AsxTokenTypes.GWT_FIELD_OR_METHOD) {
                        this.builder.advanceLexer();
                    }
                }

                gwtExpr.done(JSElementTypes.GWT_REFERENCE_EXPRESSION);
                return;
            }

            this.builder.advanceLexer();
        }
    }

    protected IElementType getNewExpressionElementType() {
        return JSElementTypes.NEW_EXPRESSION;
    }

    protected boolean parseDialectSpecificMemberExpressionPart() {
        return false;
    }

    public boolean proceedWithNamespaceReference(PsiBuilder.Marker identifier, boolean expressionContext) {
        if(this.builder.getTokenType() != AsxTokenTypes.COLON_COLON) {
            identifier.drop();
            return false;
        } else {
            this.builder.advanceLexer();
            identifier.done(JSElementTypes.E4X_NAMESPACE_REFERENCE);
            IElementType tokenType = this.builder.getTokenType();
            if(tokenType != AsxTokenTypes.ANY_IDENTIFIER && !this.isIdentifierToken(tokenType)) {
                if(!expressionContext || tokenType != AsxTokenTypes.LBRACKET) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.name"));
                }
            } else {
                this.builder.advanceLexer();
            }

            return true;
        }
    }

    public boolean parseQualifiedTypeName() {
        return this.parseQualifiedTypeName(false);
    }

    public boolean parseQualifiedTypeName(boolean allowStar) {
        if(!this.isIdentifierToken(this.builder.getTokenType())) {
            return false;
        } else {
            PsiBuilder.Marker expr = this.builder.mark();
            this.buildTokenElement(AsxElementTypes.REFERENCE_EXPRESSION);

            while(this.builder.getTokenType() == AsxTokenTypes.DOT) {
                this.builder.advanceLexer();
                IElementType tokenType = this.builder.getTokenType();
                boolean stop = false;
                if(tokenType == AsxTokenTypes.ANY_IDENTIFIER && allowStar) {
                    this.builder.advanceLexer();
                    stop = true;
                } else if(tokenType != AsxTokenTypes.IDENTIFIER && JSKeywordSets.IDENTIFIER_NAMES.contains(tokenType)) {
                    this.builder.advanceLexer();
                } else {
                    checkMatches(this.builder, AsxTokenTypes.IDENTIFIER, "javascript.parser.message.expected.name");
                }

                expr.done(AsxElementTypes.REFERENCE_EXPRESSION);
                expr = expr.precede();
                if(stop) {
                    break;
                }
            }

            return this.parseQualifiedTypeNameTail(expr);
        }
    }

    protected boolean tryParseTypeArgumentList(boolean revert, IElementType elementType) {
        boolean result = true;
        if(this.builder.getTokenType() == AsxTokenTypes.LT) {
            PsiBuilder.Marker typeArgumentList = this.builder.mark();
            this.builder.advanceLexer();

            for(boolean first = true; this.builder.getTokenType() != AsxTokenTypes.GT && !this.builder.eof(); first = false) {
                if(!first && !checkMatches(this.builder, AsxTokenTypes.COMMA, "javascript.parser.message.expected.comma")) {
                    if(revert) {
                        typeArgumentList.rollbackTo();
                    } else {
                        typeArgumentList.drop();
                    }

                    return false;
                }

                result &= this.myJavaScriptParser.getExpressionParser().parseType();
                if(!result && revert) {
                    typeArgumentList.rollbackTo();
                    return false;
                }
            }

            this.builder.advanceLexer();
            typeArgumentList.done(elementType);
        }

        return result;
    }
    protected boolean parseQualifiedTypeNameTail(PsiBuilder.Marker expr) {
        expr.drop();
        return this.builder.getTokenType() != AsxTokenTypes.LT || this.tryParseTypeArgumentList(false, AsxElementTypes.TYPE_ARGUMENTS);
    }
    private void parseECMA4GenericSignature() {
        assert this.builder.getTokenType() == AsxTokenTypes.LT || this.builder.getTokenType() == AsxTokenTypes.GENERIC_SIGNATURE_START;
        PsiBuilder.Marker genericTypeSignature = this.builder.mark();
        this.builder.advanceLexer();
        this.parseType();
        checkMatches(this.builder, AsxTokenTypes.GT, "javascript.parser.message.expected.gt");
        genericTypeSignature.done(JSElementTypes.GENERIC_SIGNATURE);
    }

    private boolean parseNewExpression() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenTypes.NEW_KEYWORD);
        this.builder.advanceLexer();
        IElementType tokenType = this.builder.getTokenType();
        if(tokenType == AsxTokenTypes.FUNCTION_KEYWORD) {
            this.myJavaScriptParser.getFunctionParser().parseFunctionExpression();
            return true;
        } else if(tokenType == AsxTokenTypes.LT && this.isECMAL4()) {
            this.parseECMA4GenericSignature();
            if(this.builder.getTokenType() == AsxTokenTypes.LBRACKET) {
                this.parseArrayLiteralExpression(false, false);
            } else {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.lbracket", new Object[0]));
            }

            return false;
        } else {
            if(!this.parseMemberExpression(true)) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.expression", new Object[0]));
            }

            while(this.builder.getTokenType() == AsxTokenTypes.LBRACKET) {
                this.builder.advanceLexer();
                if(this.builder.getTokenType() != AsxTokenTypes.RBRACKET) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.rbracket", new Object[0]));
                    break;
                }

                this.builder.advanceLexer();
            }

            return false;
        }
    }

    public void parseArgumentList() {
        LOG.assertTrue(this.builder.getTokenType() == AsxTokenTypes.LPAR);
        PsiBuilder.Marker arglist = this.builder.mark();
        this.parseArgumentListNoMarker();
        arglist.done(JSElementTypes.ARGUMENT_LIST);
    }

    protected void parseArgumentListNoMarker() {
        this.builder.advanceLexer();
        boolean first = true;

        while(this.builder.getTokenType() != AsxTokenTypes.RPAR) {
            if(first) {
                first = false;
            } else {
                if(this.builder.getTokenType() != AsxTokenTypes.COMMA) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.comma.or.rparen", new Object[0]));
                    break;
                }

                this.builder.advanceLexer();
            }

            if(!this.parseArgument()) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.expression", new Object[0]));
            }
        }

        checkMatches(this.builder, AsxTokenTypes.RPAR, "javascript.parser.message.expected.rparen");
    }
    protected boolean parseArgument() {
        return this.builder.getTokenType() == AsxTokenTypes.DOT_DOT_DOT?this.parseSpreadExpression():this.parseGeneratorExpression();
    }


    public void parseExpression() {
        if(!this.parseExpressionOptional()) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
        }
    }

    public boolean parseGeneratorExpression() {
        PsiBuilder.Marker expr = this.builder.mark();
        if(!this.parseAssignmentExpression(true, false)) {
            expr.drop();
            return false;
        } else {
            if(this.builder.getTokenType() == AsxTokenTypes.FOR_KEYWORD) {
                this.myJavaScriptParser.getStatementParser().parseForLoopHeader();
                if(this.builder.getTokenType() == AsxTokenTypes.IF_KEYWORD) {
                    this.myJavaScriptParser.getStatementParser().parseIfStatementHeader();
                }

                expr.done(JSElementTypes.GENERATOR_EXPRESSION);
            } else {
                expr.drop();
            }

            return true;
        }
    }

    public boolean parseAssignmentExpression(boolean allowIn, boolean withinPropertyInitializer) {
        if((this.builder.getTokenType() == AsxTokenTypes.LPAR || this.isIdentifierToken(this.builder.getTokenType()))) {
            if(this.myJavaScriptParser.getFunctionParser().parseArrowFunction()){
                return true;
            }
        }
        PsiBuilder.Marker expr = this.builder.mark();
        if(JSExtendedLanguagesTokenSetProvider.ASSIGNMENT_OPERATIONS.contains(this.builder.getTokenType()) && !withinPropertyInitializer) {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
            this.builder.advanceLexer();
            if(!this.parseAssignmentExpression(allowIn, false)) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
            }

            expr.done(JSStubElementTypes.ASSIGNMENT_EXPRESSION);
            return true;
        } else {
            PsiBuilder.Marker definitionExpr = this.builder.mark();
            if(!this.parseConditionalExpression(allowIn)) {
                definitionExpr.drop();
                expr.drop();
                return false;
            } else {
                if(JSExtendedLanguagesTokenSetProvider.ASSIGNMENT_OPERATIONS.contains(this.builder.getTokenType())) {
                    LighterASTNode lhs = this.builder.getLatestDoneMarker();
                    if(lhs == null || lhs.getTokenType() != JSElementTypes.ARRAY_LITERAL_EXPRESSION && lhs.getTokenType() != JSStubElementTypes.OBJECT_LITERAL_EXPRESSION) {
                        definitionExpr.done(JSStubElementTypes.DEFINITION_EXPRESSION);
                        this.builder.advanceLexer();
                        if(!this.parseAssignmentExpression(allowIn, withinPropertyInitializer)) {
                            this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
                        }

                        expr.done(JSStubElementTypes.ASSIGNMENT_EXPRESSION);
                    } else {
                        definitionExpr.rollbackTo();
                        this.parseDestructuringElement(AsxElementTypes.REFERENCE_EXPRESSION);
                        expr.drop();
                    }
                } else {
                    definitionExpr.drop();
                    expr.drop();
                }

                return true;
            }
        }

    }

    private boolean parseConditionalExpression(boolean allowIn) {
        PsiBuilder.Marker expr = this.builder.mark();
        if(!this.parseBinaryExpression(allowIn)) {
            if(this.builder.getTokenType() != AsxTokenTypes.QUEST) {
                expr.drop();
                return false;
            }

            this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
        }

        IElementType nextTokenType = this.builder.getTokenType();
        if(nextTokenType == AsxTokenTypes.QUEST) {
            this.builder.advanceLexer();
            if(!this.parseAssignmentExpression(allowIn, false)) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
            }

            checkMatches(this.builder, AsxTokenTypes.COLON, "javascript.parser.message.expected.colon");
            if(!this.parseAssignmentExpression(allowIn, false)) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
            }

            expr.done(JSElementTypes.CONDITIONAL_EXPRESSION);
        } else {
            expr.drop();
        }

        return true;
    }

    protected boolean parseBinaryExpression(boolean allowIn) {
        PsiBuilder.Marker currentMarker = this.builder.mark();
        if(!this.parseUnaryExpression()) {
            currentMarker.drop();
            return false;
        } else if(this.getCurrentBinarySignPriority(allowIn, false) < 0) {
            currentMarker.drop();
            return true;
        } else {
            int depth = 0;

            ArrayDeque markers;
            int priority;
            for(markers = new ArrayDeque(); (priority = this.getCurrentBinarySignPriority(allowIn, false)) >= 0; ++depth) {
                boolean depthExceeded = depth >= MAX_TREE_DEPTH;
                if(!depthExceeded && !markers.isEmpty() && ((Integer)((Pair)markers.peek()).getFirst()).intValue() >= priority) {
                    currentMarker.drop();
                    PsiBuilder.Marker lastPoppedMarker = null;

                    while(!markers.isEmpty() && ((Integer)((Pair)markers.peek()).getFirst()).intValue() > priority) {
                        lastPoppedMarker = (PsiBuilder.Marker)((Pair)markers.pop()).getSecond();
                        lastPoppedMarker.done(JSElementTypes.BINARY_EXPRESSION);
                    }

                    int lastPriority = markers.isEmpty()?-1:((Integer)((Pair)markers.peek()).getFirst()).intValue();
                    PsiBuilder.Marker precede;
                    if(lastPriority == priority) {
                        precede = (PsiBuilder.Marker)((Pair)markers.pop()).getSecond();
                        precede.done(JSElementTypes.BINARY_EXPRESSION);
                        PsiBuilder.Marker precede1 = precede.precede();
                        markers.push(Pair.create(Integer.valueOf(priority), precede1));
                    } else {
                        assert lastPriority < priority;

                        assert lastPoppedMarker != null;

                        precede = lastPoppedMarker.precede();
                        markers.push(Pair.create(Integer.valueOf(priority), precede));
                    }
                } else if(!depthExceeded) {
                    markers.push(Pair.create(Integer.valueOf(priority), currentMarker));
                }

                this.getCurrentBinarySignPriority(allowIn, true);
                if(!depthExceeded) {
                    currentMarker = this.builder.mark();
                }

                if(!this.parseUnaryExpression()) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
                }
            }

            currentMarker.drop();

            while(!markers.isEmpty()) {
                ((PsiBuilder.Marker)((Pair)markers.pop()).getSecond()).done(JSElementTypes.BINARY_EXPRESSION);
            }

            return true;
        }
    }

    protected int getCurrentBinarySignPriority(boolean allowIn, boolean advance) {
        byte result = -1;
        IElementType tokenType = this.builder.getTokenType();
        if(tokenType == AsxTokenTypes.OROR) {
            result = 0;
        } else if(tokenType == AsxTokenTypes.ANDAND) {
            result = 1;
        } else if(tokenType == AsxTokenTypes.OR) {
            result = 2;
        } else if(tokenType == AsxTokenTypes.XOR) {
            result = 3;
        } else if(tokenType == AsxTokenTypes.AND) {
            result = 4;
        } else
        if(AsxTokenSets.EQUALITY_OPERATIONS.contains(tokenType)) {
            result = 5;
        } else
        if(!AsxTokenSets.RELATIONAL_OPERATIONS.contains(tokenType) || !allowIn && this.builder.getTokenType() == AsxTokenTypes.IN_KEYWORD) {
            if(AsxTokenSets.SHIFT_OPERATIONS.contains(tokenType)) {
                result = 7;
            } else if(AsxTokenSets.ADDITIVE_OPERATIONS.contains(tokenType)) {
                result = 8;
            } else if(AsxTokenSets.MULTIPLICATIVE_OPERATIONS.contains(tokenType)) {
                result = 9;
            } else if(tokenType == AsxTokenTypes.IS_KEYWORD || tokenType == AsxTokenTypes.AS_KEYWORD) {
                result = 10;
            }
        } else {
            result = 6;
        }

        if(advance && result >= 0) {
            this.builder.advanceLexer();
        }

        return result;
    }
    protected boolean parseUnaryExpression() {
        IElementType type = this.builder.getTokenType();
        return type == AsxTokenTypes.AWAIT_KEYWORD && !this.getFunctionParser().isAsyncContext()?this.parsePostfixExpression():this.parseUnaryExpressionSuper();
    }
    protected boolean parseUnaryExpressionSuper() {
        IElementType tokenType = this.builder.getTokenType();
        if(AsxTokenSets.UNARY_OPERATIONS.contains(tokenType)) {
            PsiBuilder.Marker expr = this.builder.mark();
            this.builder.advanceLexer();
            if(!this.parseUnaryExpression()) {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
            }

            expr.done(JSElementTypes.PREFIX_EXPRESSION);
            return true;
        } else {
            return this.parsePostfixExpression();
        }
    }

    protected boolean parsePostfixExpression() {
        PsiBuilder.Marker expr = this.builder.mark();
        if(!this.parseMemberExpression(false)) {
            expr.drop();
            return false;
        } else {
            IElementType tokenType = this.builder.getTokenType();
            if((tokenType == AsxTokenTypes.PLUSPLUS || tokenType == AsxTokenTypes.MINUSMINUS) && !hasSemanticLinefeedBefore(this.builder)) {
                this.builder.advanceLexer();
                expr.done(JSElementTypes.POSTFIX_EXPRESSION);
            } else {
                expr.drop();
            }

            return true;
        }
    }

    public boolean parseExpressionOptional() {
        return this.parseExpressionOptional(true, false);
    }

    public boolean parseExpressionOptional(boolean allowIn, boolean allowGenerator) {
        PsiBuilder.Marker expr = this.builder.mark();
        if(!this.parseAssignmentExpression(allowIn, false)) {
            expr.drop();
            return false;
        } else if(this.builder.getTokenType() == AsxTokenTypes.IN_KEYWORD) {
            expr.done(JSStubElementTypes.DEFINITION_EXPRESSION);
            return true;
        } else if(allowGenerator && this.builder.getTokenType() == AsxTokenTypes.FOR_KEYWORD) {
            this.myJavaScriptParser.getStatementParser().parseForLoopHeader();
            if(this.builder.getTokenType() == AsxTokenTypes.IF_KEYWORD) {
                this.myJavaScriptParser.getStatementParser().parseIfStatementHeader();
            }

            expr.done(JSElementTypes.GENERATOR_EXPRESSION);
            return true;
        } else {
            for(int nestingLevel = 0; this.builder.getTokenType() == AsxTokenTypes.COMMA; ++nestingLevel) {
                this.builder.advanceLexer();
                if(!this.parseAssignmentExpression(allowIn, false)) {
                    this.builder.error(JSBundle.message("javascript.parser.message.expected.expression"));
                }

                if(nestingLevel < MAX_TREE_DEPTH) {
                    expr.done(JSElementTypes.COMMA_EXPRESSION);
                    expr = expr.precede();
                }
            }

            expr.drop();
            return true;
        }
    }

    public void buildTokenElement(IElementType type) {
        PsiBuilder.Marker marker = this.builder.mark();
        this.builder.advanceLexer();
        if(this.builder.getTokenType() == AsxTokenTypes.GENERIC_SIGNATURE_START) {
            this.parseECMA4GenericSignature();
        }
        marker.done(type);
    }

    public boolean tryParseType() {
        if(this.builder.getTokenType() == AsxTokenTypes.COLON) {
            this.builder.advanceLexer();
            return this.parseType();
        } else {
            return false;
        }
    }

    public boolean parseType() {
        IElementType type = this.builder.getTokenType();
        boolean result;
        PsiBuilder.Marker marker;
        if(type == AsxTokenTypes.LBRACE) {
            marker = this.builder.mark();
            this.builder.advanceLexer();
            result = true;

            for(boolean first = true; this.builder.getTokenType() != AsxTokenTypes.RBRACE; first = false) {
                if(this.builder.eof()) {
                    this.builder.error(JSBundle.message("javascript.parser.message.missing.rbrace"));
                    result = false;
                    break;
                }

                if(!first) {
                    checkMatches(this.builder, AsxTokenTypes.COMMA, "javascript.parser.message.expected.comma.or.rbrace");
                }

                this.parseTypeMember();
            }

            if(result) {
                this.builder.advanceLexer();
            }

            marker.done(ES6ElementTypes.OBJECT_TYPE);
        } else if(type != AsxTokenTypes.VOID_KEYWORD && type != AsxTokenTypes.ANY_KEYWORD) {
            if(this.isIdentifierToken(type)) {
                marker = this.builder.mark();
                result = this.getExpressionParser().parseQualifiedTypeName();
                marker.done(AsxElementTypes.TYPE_REFERENCE);
            } else {
                this.builder.error(JSBundle.message("javascript.parser.message.expected.typename"));
                result = false;
            }
        } else {
            marker = this.builder.mark();
            this.builder.advanceLexer();
            marker.done(AsxElementTypes.TYPE_REFERENCE);
            result = true;
        }

        return result;
    }
    protected boolean parseTypeMember() {
        PsiBuilder.Marker typeMember = this.builder.mark();
        IElementType firstToken = this.builder.getTokenType();
        boolean result;
        if(JSKeywordSets.PROPERTY_NAMES.contains(firstToken)) {
            this.builder.advanceLexer();
            result = this.tryParseType();
            typeMember.done(ES6ElementTypes.PROPERTY_SIGNATURE);
        } else {
            this.builder.error(JSBundle.message("javascript.parser.message.expected.type.member"));
            this.builder.advanceLexer();
            typeMember.drop();
            result = false;
        }
        return result;
    }

}



