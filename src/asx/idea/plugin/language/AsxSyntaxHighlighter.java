package asx.idea.plugin.language;

import asx.idea.plugin.language.lexer.AsxLexerAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey on 4/10/15.
 */
public class AsxSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>() {{
        fillMap(this, AsxSyntaxHighlighterColors.BAD_CHARACTER,             AsxTokenSets.BAD_CHARACTER);
        fillMap(this, AsxSyntaxHighlighterColors.KEYWORD,                   AsxTokenSets.KEYWORDS.getTypes());
        fillMap(this, AsxSyntaxHighlighterColors.KEYWORD,                   AsxTokenSets.VALUE_LITERALS.getTypes());
        fillMap(this, AsxSyntaxHighlighterColors.BOOLEAN,                   AsxTokenSets.BOOLEAN_LITERALS.getTypes());
        fillMap(this, AsxSyntaxHighlighterColors.STRING,                    AsxTokenSets.STRING_LITERALS.getTypes());
        fillMap(this, AsxSyntaxHighlighterColors.STRING,                    AsxTokenSets.TEMPLATE_PARTS.getTypes());
        fillMap(this, AsxSyntaxHighlighterColors.NUMBER,                    AsxTokenSets.NUMERIC_LITERALS.getTypes());
        fillMap(this, AsxSyntaxHighlighterColors.REGEXP,                    AsxTokenSets.REGEXP_LITERALS.getTypes());

        fillMap(this, AsxSyntaxHighlighterColors.LINE_COMMENT,              AsxTokenSets.LINE_COMMENT);
        fillMap(this, AsxSyntaxHighlighterColors.BLOCK_COMMENT,             AsxTokenSets.BLOCK_COMMENT);
        fillMap(this, AsxSyntaxHighlighterColors.DOC_COMMENT,               AsxTokenSets.DOC_COMMENT_TOKEN);
        fillMap(this, AsxSyntaxHighlighterColors.OPERATION_SIGN,            AsxTokenSets.OPERATIONS.getTypes());
        fillMap(this, AsxSyntaxHighlighterColors.PARENTHESES,               AsxTokenSets.PARENTHESES.getTypes());
        fillMap(this, AsxSyntaxHighlighterColors.BRACKETS,                  AsxTokenSets.BRACKETS.getTypes());
        fillMap(this, AsxSyntaxHighlighterColors.BRACES,                    AsxTokenSets.BRACES.getTypes());
        fillMap(this, AsxSyntaxHighlighterColors.COMMA,                     AsxTokenSets.COMMA);
        fillMap(this, AsxSyntaxHighlighterColors.DOT,                       AsxTokenSets.DOT);
        fillMap(this, AsxSyntaxHighlighterColors.SEMICOLON,                 AsxTokenSets.SEMICOLON);
    }};

    @NotNull
    public Lexer getHighlightingLexer() {
        return new AsxLexerAdapter();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(ATTRIBUTES.get(tokenType));
    }
}

