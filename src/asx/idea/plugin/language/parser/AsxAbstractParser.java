package asx.idea.plugin.language.parser;

import asx.idea.plugin.language.AsxTokenSets;
import asx.idea.plugin.language.AsxTokenTypes;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesAndCommentsBinder;
import com.intellij.lang.javascript.JSBundle;
import com.intellij.lang.javascript.JSLanguageDialect;

import com.intellij.lang.javascript.JavaScriptSupportLoader;
import com.intellij.openapi.util.Key;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.util.List;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxAbstractParser<T extends AsxSyntaxParserBase> {
    public static final Key<JSLanguageDialect> JS_DIALECT_KEY = Key.create("JS_DIALECT");
    public static final Key<AsxAbstractParser.ForceContext> FORCE_CONTEXT_KEY = Key.create("FORCE_CONTEXT");
    public static int MAX_TREE_DEPTH = 100;
    protected final PsiBuilder builder;
    protected final T myJavaScriptParser;
    protected static final WhitespacesAndCommentsBinder INCLUDE_DOC_COMMENT_AT_LEFT = new WhitespacesAndCommentsBinder() {
        public int getEdgePosition(List<IElementType> tokens, boolean atStreamEdge, TokenTextGetter getter) {
            int i = tokens.size() - 1;

            IElementType type;
            for(type = i >= 0?(IElementType)tokens.get(i):null; type == AsxTokenTypes.WHITE_SPACE || type == AsxTokenTypes.LINE_COMMENT; type = i >= 0?(IElementType)tokens.get(i):null) {
                --i;
            }

            if(type == AsxTokenTypes.DOC_COMMENT_TOKEN) {
                return i;
            } else {
                return tokens.size();
            }
        }
    };

    protected AsxAbstractParser(T parser) {
        this.builder = parser.builder;
        this.myJavaScriptParser = parser;
    }

    protected boolean is(IElementType ...types){
        for(int i=0;i<types.length;i++){
            if(!types[i].equals(builder.lookAhead(i))){
                return false;
            }
        }
        return true;
    }

    protected boolean is(TokenSet ...sets){
        for(int i=0;i<sets.length;i++){
            if(!sets[i].contains(builder.lookAhead(i))){
                return false;
            }
        }
        return true;
    }

    protected static boolean hasSemanticLinefeedBefore(PsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();
        if(tokenType != null && tokenType != AsxTokenTypes.RBRACE) {
            if(tokenType != AsxTokenTypes.ELSE_KEYWORD && tokenType != AsxTokenTypes.WHILE_KEYWORD && tokenType != AsxTokenTypes.CLASS_KEYWORD) {
                int at = -1;

                for(IElementType elementTypeBefore = builder.rawLookup(at); elementTypeBefore == AsxTokenTypes.WHITE_SPACE || elementTypeBefore == AsxTokenTypes.HEREDOC_BOUND || AsxTokenSets.COMMENTS.contains(elementTypeBefore); elementTypeBefore = builder.rawLookup(at)) {
                    int start = builder.rawTokenTypeStart(at);
                    int end = builder.getCurrentOffset();

                    for(CharSequence sequence = builder.getOriginalText(); start < end; ++start) {
                        char ch = sequence.charAt(start);
                        if(ch == 10 || ch == 8232 || ch == 8233 || ch == 13) {
                            return true;
                        }
                    }

                    --at;
                }

                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    protected static boolean checkMatches(PsiBuilder builder, IElementType token, @NonNls @PropertyKey(resourceBundle = "com.intellij.lang.javascript.JavaScriptBundle") String errorMessageKey) {
        if(builder.getTokenType() == token) {
            builder.advanceLexer();
            return true;
        } else {
            builder.error(JSBundle.message(errorMessageKey));
            return false;
        }
    }

    protected boolean isECMAL4() {
        return this.builder.getUserData(JS_DIALECT_KEY) == JavaScriptSupportLoader.ECMA_SCRIPT_L4;
    }

    protected boolean isGwt() {
        return this.builder.getUserData(JS_DIALECT_KEY) == JavaScriptSupportLoader.GWT_DIALECT;
    }

    protected boolean isECMA6() {
        JSLanguageDialect dialect = this.builder.getUserData(JS_DIALECT_KEY);
        return dialect == JavaScriptSupportLoader.ECMA_SCRIPT_6 || dialect == JavaScriptSupportLoader.JSX_HARMONY || dialect == JavaScriptSupportLoader.ATSCRIPT;
    }

    protected boolean isJavaScript() {
        JSLanguageDialect dialect = this.builder.getUserData(JS_DIALECT_KEY);
        return dialect == null || dialect.getOptionHolder().isJavaScript();
    }

    protected boolean isIdentifierToken(IElementType tokenType) {
        return tokenType == AsxTokenTypes.IDENTIFIER || this.myJavaScriptParser.isIdentifierToken(tokenType);
    }

    public static enum ForceContext {
        Type,
        Parameter;

        private ForceContext() {
        }
    }
}
