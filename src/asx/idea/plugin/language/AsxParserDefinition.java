package asx.idea.plugin.language;

import asx.idea.plugin.language.lexer.AsxLexerAdapter;
import asx.idea.plugin.language.parser.AsxSyntaxParser;
import asx.idea.plugin.project.AsxFile;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.*;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.JavascriptParserDefinition;
import com.intellij.lang.javascript.psi.impl.JSEmbeddedContentImpl;
import com.intellij.lang.javascript.psi.jsdoc.impl.JSDocCommentImpl;
import com.intellij.lang.javascript.types.PsiGenerator;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxParserDefinition implements ParserDefinition {

    @NotNull
    public Lexer createLexer(Project project) {
        return new AsxLexerAdapter();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return AsxElementTypes.ASX_FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return AsxTokenSets.PARSER_WHITE_SPACE_TOKENS;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return AsxTokenSets.COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return AsxTokenSets.STRING_LITERALS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();
        if(type instanceof PsiGenerator) {
            PsiElement element = ((PsiGenerator)type).construct(node);
            if(element != null) {
                return element;
            }
        }
        return new ASTWrapperPsiElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new AsxFile(fileViewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        Lexer lexer = createLexer(left.getPsi().getProject());
        return LanguageUtil.canStickTokensTogetherByLexer(left, right, lexer);
    }

    @NotNull
    public PsiParser createParser(Project project) {
        return new PsiParser() {
            @NotNull
            public ASTNode parse(IElementType root, PsiBuilder builder) {
                new AsxSyntaxParser(builder).parseJS(root);
                ASTNode tree = builder.getTreeBuilt();
                return tree;
            }
        };
    }

}