package asx.idea.plugin.language.parser;

import asx.idea.plugin.language.AsxLanguage;
import com.intellij.lang.PsiBuilder;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxSyntaxParser extends AsxSyntaxParserBase<AsxExpressionParser,AsxStatementParser,AsxFunctionParser> {
    public AsxSyntaxParser(PsiBuilder builder) {
        super(builder);
        this.myStatementParser  = new AsxStatementParser<AsxSyntaxParser>(this);
        this.myFunctionParser   = new AsxFunctionParser<AsxSyntaxParser>(this);
        this.myExpressionParser = new AsxExpressionParser<AsxSyntaxParser>(this);
    }
}


/*
builder.setTokenTypeRemapper(new ITokenTypeRemapper() {
    @Override
    public IElementType filter(IElementType source, int start, int end, CharSequence text) {
        if (source == JSTokenTypes.IDENTIFIER && "native".equals(text.subSequence(start, end).toString())) {
            return JSTokenTypes.AT;
        }
        return source;
    }
});
*/