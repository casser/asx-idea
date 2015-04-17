package asx.idea.plugin.language.completion;

import asx.idea.plugin.project.AsxFileType;
import com.intellij.lang.javascript.ecmascript6.TypeScriptResolveScopeProvider;
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Sergey on 4/6/15.
 */
public class AsxResolveScopeProvider extends TypeScriptResolveScopeProvider {
    @Nullable
    public GlobalSearchScope getResolveScope(@NotNull VirtualFile file, Project project) {
        if (file.getFileType() == AsxFileType.INSTANCE) {
            Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(file);
            if (module != null) {
                return GlobalSearchScope.getScopeRestrictedByFileTypes(
                    JSResolveUtil.ourScopeCache.get(module, null).getValue(),
                    AsxFileType.INSTANCE
                );
            }
        }
        return null;
    }
}
