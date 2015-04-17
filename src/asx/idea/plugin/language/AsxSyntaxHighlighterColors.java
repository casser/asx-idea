package asx.idea.plugin.language;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

/**
 * Created by Sergey on 4/10/15.
 */
public class AsxSyntaxHighlighterColors {
    static public final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey("ASX.BADCHARACTER", HighlighterColors.BAD_CHARACTER);
    static public final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("ASX.KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    static public final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("ASX.STRING", DefaultLanguageHighlighterColors.STRING);
    static public final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey("ASX.NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    static public final TextAttributesKey BOOLEAN = TextAttributesKey.createTextAttributesKey("ASX.BOOLEAN", DefaultLanguageHighlighterColors.KEYWORD);
    static public final TextAttributesKey REGEXP = TextAttributesKey.createTextAttributesKey("ASX.REGEXP", DefaultLanguageHighlighterColors.NUMBER);
    static public final TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey("ASX.LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    static public final TextAttributesKey BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey("ASX.BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    static public final TextAttributesKey DOC_COMMENT = TextAttributesKey.createTextAttributesKey("ASX.DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT);
    static public final TextAttributesKey OPERATION_SIGN = TextAttributesKey.createTextAttributesKey("ASX.OPERATION_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    static public final TextAttributesKey PARENTHESES = TextAttributesKey.createTextAttributesKey("ASX.PARENTHS", DefaultLanguageHighlighterColors.PARENTHESES);
    static public final TextAttributesKey BRACKETS = TextAttributesKey.createTextAttributesKey("ASX.BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
    static public final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("ASX.BRACES", DefaultLanguageHighlighterColors.BRACES);
    static public final TextAttributesKey COMMA = TextAttributesKey.createTextAttributesKey("ASX.COMMA", DefaultLanguageHighlighterColors.COMMA);
    static public final TextAttributesKey DOT = TextAttributesKey.createTextAttributesKey("ASX.DOT", DefaultLanguageHighlighterColors.DOT);
    static public final TextAttributesKey SEMICOLON = TextAttributesKey.createTextAttributesKey("ASX.SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);
    static public final TextAttributesKey METADATA = TextAttributesKey.createTextAttributesKey("ASX.SEMICOLON", DefaultLanguageHighlighterColors.METADATA);
    static public final TextAttributesKey DOC_COMMENT_TAG = TextAttributesKey.createTextAttributesKey("ASX.DOC_TAG", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);
    static public final TextAttributesKey DOC_COMMENT_MARKUP = TextAttributesKey.createTextAttributesKey("ASX.DOC_MARKUP", DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP);
    static public final TextAttributesKey VALID_STRING_ESCAPE = TextAttributesKey.createTextAttributesKey("ASX.VALID_STRING_ESCAPE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
    static public final TextAttributesKey INVALID_STRING_ESCAPE = TextAttributesKey.createTextAttributesKey("ASX.INVALID_STRING_ESCAPE", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
    static public final TextAttributesKey LOCAL_VARIABLE = TextAttributesKey.createTextAttributesKey("ASX.LOCAL_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
    static public final TextAttributesKey PARAMETER = TextAttributesKey.createTextAttributesKey("ASX.PARAMETER", DefaultLanguageHighlighterColors.PARAMETER);
    static public final TextAttributesKey INSTANCE_FIELD = TextAttributesKey.createTextAttributesKey("ASX.INSTANCE_MEMBER_VARIABLE", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    static public final TextAttributesKey STATIC_FIELD = TextAttributesKey.createTextAttributesKey("ASX.STATIC_MEMBER_VARIABLE", DefaultLanguageHighlighterColors.STATIC_FIELD);
    static public final TextAttributesKey GLOBAL_VARIABLE = TextAttributesKey.createTextAttributesKey("ASX.GLOBAL_VARIABLE", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE);
    static public final TextAttributesKey FUNCTION_DECLARATION = TextAttributesKey.createTextAttributesKey("ASX.GLOBAL_FUNCTION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    static public final TextAttributesKey STATIC_METHOD = TextAttributesKey.createTextAttributesKey("ASX.STATIC_MEMBER_FUNCTION", DefaultLanguageHighlighterColors.STATIC_METHOD);
    static public final TextAttributesKey INSTANCE_METHOD = TextAttributesKey.createTextAttributesKey("ASX.INSTANCE_MEMBER_FUNCTION", DefaultLanguageHighlighterColors.INSTANCE_METHOD);
    static public final TextAttributesKey CLASS_NAME = TextAttributesKey.createTextAttributesKey("ASX.CLASS", DefaultLanguageHighlighterColors.CLASS_NAME);
    static public final TextAttributesKey INTERFACE_NAME = TextAttributesKey.createTextAttributesKey("ASX.INTERFACE", DefaultLanguageHighlighterColors.INTERFACE_NAME);
}
