package asx.idea.plugin.language;

import asx.idea.plugin.utils.AsxIcons;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey on 4/10/15.
 */
public class AsxSyntaxHighlighterSettings implements ColorSettingsPage {
    private static final AttributesDescriptor[] ATTRS = new AttributesDescriptor[]{
        new AttributesDescriptor("Single Line Comment",AsxSyntaxHighlighterColors.LINE_COMMENT),
        new AttributesDescriptor("Multi Line Comment", AsxSyntaxHighlighterColors.BLOCK_COMMENT),
        new AttributesDescriptor("Documentation Comment", AsxSyntaxHighlighterColors.DOC_COMMENT),
        new AttributesDescriptor("Keyword", AsxSyntaxHighlighterColors.KEYWORD),
        new AttributesDescriptor("Metadata", AsxSyntaxHighlighterColors.METADATA),
        new AttributesDescriptor("Number", AsxSyntaxHighlighterColors.NUMBER),
        new AttributesDescriptor("Boolean", AsxSyntaxHighlighterColors.BOOLEAN),
        new AttributesDescriptor("String", AsxSyntaxHighlighterColors.STRING),
        new AttributesDescriptor("Operator", AsxSyntaxHighlighterColors.OPERATION_SIGN),
        new AttributesDescriptor("Parenths", AsxSyntaxHighlighterColors.PARENTHESES),
        new AttributesDescriptor("Brackets", AsxSyntaxHighlighterColors.BRACKETS),
        new AttributesDescriptor("Braces", AsxSyntaxHighlighterColors.BRACES),
    };
    private static final Map<String, TextAttributesKey> ourTags = new HashMap<String, TextAttributesKey>();
    @Nullable
    @Override
    public Icon getIcon() {
        return AsxIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new AsxSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return
            "package hello {\n" +
            "\tpublic class Test{\n" +
            "\t\t// Line Comment\n" +
            "\t\t/* Block Comment */\n" +
            "\t\t/** \n" +
            "\t\t * Doc Comment \n" +
            "\t\t * @param value description \n" +
            "\t\t * @param value description \n" +
            "\t\t */\n" +
            "\t\tpublic function Test(){\n" +
            "\t\t\tdo('hello',\"hello\",`hello${param}world`,true,false,!(1.2+0.5)>9,other['one'],null,undefined,NaN);\n"+
            "\t\t}\n" +
            "\t}\n" +
            "}";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return ourTags;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "ASX";
    }
}