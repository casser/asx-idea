package asx.idea.plugin.language.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.javascript.JSKeywordSets;
import com.intellij.lang.javascript.JSLanguageDialect;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.psi.tree.IElementType;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxSyntaxParserBase<E extends AsxExpressionParser, S extends AsxStatementParser, F extends AsxFunctionParser> {
    protected final PsiBuilder builder;
    protected final JSLanguageDialect myDialect;
    protected E myExpressionParser;
    protected S myStatementParser;
    protected F myFunctionParser;


    public AsxSyntaxParserBase(PsiBuilder builder) {
        this.builder = builder;
        this.myDialect = null;
    }

    public void parseJS(IElementType root) {
        PsiBuilder.Marker rootMarker = this.builder.mark();
        //this.builder.putUserData(AsxAbstractParser.JS_DIALECT_KEY, this.myDialect);
        AsxAbstractParser.ForceContext forceContext = this.builder.getUserData(AsxAbstractParser.FORCE_CONTEXT_KEY);
        if (forceContext != null) {
            if (forceContext == AsxAbstractParser.ForceContext.Parameter && this.builder.getTokenType() == JSTokenTypes.DOT_DOT_DOT) {
                this.builder.advanceLexer();
            } else {
                this.myExpressionParser.parseType();
            }
            while (!this.builder.eof()) {
                this.getStatementParser().parseStatement();
            }
        } else {
            while (!this.builder.eof()) {
                this.getStatementParser().parseSourceElement();
            }
        }
        rootMarker.done(root);
    }

    public E getExpressionParser() {
        return this.myExpressionParser;
    }

    public S getStatementParser() {
        return this.myStatementParser;
    }

    public F getFunctionParser() {
        return this.myFunctionParser;
    }

    protected boolean isIdentifierToken(IElementType tokenType) {
        return this.myExpressionParser.isGwt() ? JSKeywordSets.GWT_IDENTIFIER_TOKENS_SET.contains(tokenType) : JSKeywordSets.JS_IDENTIFIER_TOKENS_SET.contains(tokenType);
    }

    public void parseAll(IElementType root) {
        PsiBuilder.Marker rootMarker = this.builder.mark();
        while (!this.builder.eof()) {
            this.builder.advanceLexer();
        }
        rootMarker.done(root);
    }
}
