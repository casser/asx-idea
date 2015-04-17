package asx.idea.plugin.actions;

import asx.idea.plugin.utils.AsxIcons;
import asx.idea.plugin.project.AsxModuleType;
import asx.idea.plugin.project.AsxSourceRootType;
import asx.idea.plugin.utils.AsxFileTemplateUtil;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.IdeView;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.CreateTemplateInPackageAction;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.impl.DirectoryIndex;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Sergey on 4/4/15.
 */
public class AsxCreateClassAction extends CreateTemplateInPackageAction<PsiFile> {
    private final static Set<? extends JpsModuleSourceRootType<?>> ROOTS = Collections.singleton(AsxSourceRootType.SOURCE);
    public AsxCreateClassAction() {
        super("New Class","New Class",AsxIcons.CLASS,ROOTS);
    }
    protected boolean isSuperAvailable(DataContext var1) {
        Project var2 = (Project) CommonDataKeys.PROJECT.getData(var1);
        IdeView var3 = (IdeView)LangDataKeys.IDE_VIEW.getData(var1);
        if(var2 != null && var3 != null && var3.getDirectories().length != 0) {
            if(ROOTS == null) {
                return true;
            } else {
                ProjectFileIndex var4 = ProjectRootManager.getInstance(var2).getFileIndex();
                PsiDirectory[] var5 = var3.getDirectories();
                int var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    PsiDirectory var8 = var5[var7];


                    if(var4.isUnderSourceRootOfType(var8.getVirtualFile(), ROOTS) && this.checkPackageExists(var8)) {
                        return true;
                    }
                }

                return false;
            }
        } else {
            return false;
        }
    }
    @Override
    protected boolean isAvailable(DataContext dataContext) {
        final Module module = LangDataKeys.MODULE.getData(dataContext);
        return isSuperAvailable(dataContext) && module != null && ModuleType.get(module) == AsxModuleType.getInstance();
    }

    @Override
    protected PsiElement getNavigationElement(@NotNull PsiFile createdElement) {
        return createdElement.getNavigationElement();
    }

    @Override
    protected boolean checkPackageExists(PsiDirectory directory) {
        return DirectoryIndex.getInstance(directory.getProject()).getPackageName(directory.getVirtualFile()) != null;
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return newName;
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle(IdeBundle.message("action.create.new.class"));
        for (FileTemplate fileTemplate : AsxFileTemplateUtil.getApplicableTemplates()) {
            final String templateName = fileTemplate.getName();
            final String shortName = AsxFileTemplateUtil.getTemplateShortName(templateName);
            builder.addKind(shortName, AsxFileTemplateUtil.getTemplateIcon(templateName), templateName);
        }
    }

    @Override
    protected PsiFile doCreate(@NotNull PsiDirectory dir, String className, String templateName) throws IncorrectOperationException {
        String packageName = DirectoryIndex.getInstance(dir.getProject()).getPackageName(dir.getVirtualFile());
        try {
            return createClass(className, packageName, dir, templateName).getContainingFile();
        }
        catch (Exception e) {
            throw new IncorrectOperationException(e.getMessage(), e);
        }
    }

    private static PsiElement createClass(String className, String packageName, @NotNull PsiDirectory directory, final String templateName) throws Exception {
        final Properties props = new Properties(FileTemplateManager.getDefaultInstance().getDefaultProperties());
        props.setProperty(FileTemplateManager.PROJECT_NAME_VARIABLE, directory.getProject().getName());
        props.setProperty(FileTemplate.ATTRIBUTE_NAME, className);
        props.setProperty(FileTemplate.ATTRIBUTE_PACKAGE_NAME, packageName);

        final FileTemplate template = FileTemplateManager.getDefaultInstance().getInternalTemplate(templateName);

        return FileTemplateUtil.createFromTemplate(template, className, props, directory, AsxCreateClassAction.class.getClassLoader());
    }
}
