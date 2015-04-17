package asx.idea.plugin.language.lexer;

import asx.idea.plugin.language.AsxTokenTypes;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxLexerAdapter extends MergingLexerAdapterBase {
    private static final int NESTED_BLOCKS_COUNT_SHIFT = 5;
    private static final int BASE_STATE_MASK = 31;
    private final MergeFunction myMergeFunction;

    public AsxLexerAdapter() {
        this(new AsxLexerOld(false, AsxLexerOptions.ASX));
    }

    public AsxLexerAdapter(FlexLexer lexer) {
        super(new FlexAdapter(lexer));
        this.myMergeFunction = new AsxLexerAdapter.MyMergeFunction();
        assert lexer instanceof AsxLexerOld;
    }

    private AsxLexerOld getFlex() {
        return (AsxLexerOld)((FlexAdapter)this.getOriginal()).getFlex();
    }

    public MergeFunction getMergeFunction() {
        return this.myMergeFunction;
    }

    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        super.start(buffer, startOffset, endOffset, initialState & BASE_STATE_MASK);
        this.getFlex().setNestedBlocksCount(initialState >> NESTED_BLOCKS_COUNT_SHIFT);
    }

    public int getState() {
        return super.getState() + (this.getFlex().getNestedBlocksCount() << NESTED_BLOCKS_COUNT_SHIFT);
    }

    private static class MyMergeFunction implements MergeFunction {
        public IElementType merge(IElementType type, Lexer originalLexer) {
            if(type != AsxTokenTypes.XML_JS_SCRIPT) {
                if(type == AsxTokenTypes.STRING_LITERAL_PART) {
                    if(originalLexer.getTokenType() == type) {
                        originalLexer.advance();
                    }
                    return AsxTokenTypes.STRING_LITERAL;
                } else {
                    if(type == AsxTokenTypes.HEREDOC_BODY || type == AsxTokenTypes.XML_TAG_CONTENT) {
                        while(originalLexer.getTokenType() == type) {
                            originalLexer.advance();
                        }
                    }
                    return type;
                }
            } else {
                int braceBalance = 1;

                while(true) {
                    IElementType tokenType = originalLexer.getTokenType();
                    String text = originalLexer.getTokenText();
                    if(tokenType != AsxTokenTypes.XML_JS_SCRIPT) {
                        break;
                    }

                    originalLexer.advance();
                    if(braceBalance == 0) {
                        break;
                    }

                    if("{".equals(text)) {
                        ++braceBalance;
                    }

                    if("}".equals(text)) {
                        --braceBalance;
                    }
                }

                return type;
            }
        }
    }
}
