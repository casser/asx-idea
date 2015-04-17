package asx.idea.plugin.language;

import com.intellij.lang.javascript.*;
import com.intellij.lang.javascript.highlighting.JSHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Sergey on 4/10/15.
 */
public class AsxSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    @NotNull
    public SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        return new AsxSyntaxHighlighter();
    }
}
