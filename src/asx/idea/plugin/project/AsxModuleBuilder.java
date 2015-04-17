package asx.idea.plugin.project;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Sergey on 4/4/15.
 */
public class AsxModuleBuilder extends ModuleBuilder implements SourcePathsBuilder {
    private String myCompilerOutputPath;
    private List<Pair<String, String>> mySourcePaths;
    private final List<Pair<String, String>> myModuleLibraries = new ArrayList<Pair<String, String>>();

    public AsxModuleBuilder() {
    }

    public final void setCompilerOutputPath(String compilerOutputPath) {
        this.myCompilerOutputPath = this.acceptParameter(compilerOutputPath);
    }

    public List<Pair<String, String>> getSourcePaths() {
        if(this.mySourcePaths == null) {
            ArrayList paths = new ArrayList();
            String path = this.getContentEntryPath() + File.separator + "src";
            new File(path).mkdirs();
            paths.add(Pair.create(path, ""));
            return paths;
        } else {
            return this.mySourcePaths;
        }
    }

    public void setSourcePaths(List<Pair<String, String>> sourcePaths) {
        this.mySourcePaths = sourcePaths != null?new ArrayList<Pair<String, String>>(sourcePaths):null;
    }

    public void addSourcePath(Pair<String, String> sourcePathInfo) {
        if(this.mySourcePaths == null) {
            this.mySourcePaths = new ArrayList<Pair<String, String>>();
        }

        this.mySourcePaths.add(sourcePathInfo);
    }

    public ModuleType getModuleType() {
        return AsxModuleType.getInstance();
    }

    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return sdkType instanceof AsxSdkType;
    }

    @Nullable
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        return getModuleType().modifySettingsStep(settingsStep, this);
    }

    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        CompilerModuleExtension compilerModuleExtension = rootModel.getModuleExtension(CompilerModuleExtension.class);
        compilerModuleExtension.setExcludeOutput(true);
        if(this.myJdk != null) {
            rootModel.setSdk(this.myJdk);
        } else {
            rootModel.inheritSdk();
        }
        ContentEntry contentEntry = this.doAddContentEntry(rootModel);
        Iterator i$;
        Pair libInfo;
        String moduleLibraryPath;
        if(contentEntry != null) {
            List libraryTable = this.getSourcePaths();
            if(libraryTable != null) {
                i$ = libraryTable.iterator();
                while(i$.hasNext()) {
                    libInfo = (Pair)i$.next();
                    moduleLibraryPath = (String)libInfo.first;
                    (new File(moduleLibraryPath)).mkdirs();
                    VirtualFile sourceLibraryPath = LocalFileSystem.getInstance().refreshAndFindFileByPath(FileUtil.toSystemIndependentName(moduleLibraryPath));
                    if(sourceLibraryPath != null) {
                        contentEntry.addSourceFolder(sourceLibraryPath, AsxSourceRootType.SOURCE);
                    }
                }
            }
        }

        if(this.myCompilerOutputPath != null) {
            String libraryTable1;
            try {
                libraryTable1 = FileUtil.resolveShortWindowsName(this.myCompilerOutputPath);
            } catch (IOException var11) {
                libraryTable1 = this.myCompilerOutputPath;
            }

            compilerModuleExtension.setCompilerOutputPath(VfsUtilCore.pathToUrl(FileUtil.toSystemIndependentName(libraryTable1)));
        } else {
            compilerModuleExtension.inheritCompilerOutputPath(true);
        }

        LibraryTable libraryTable2 = rootModel.getModuleLibraryTable();

        Library.ModifiableModel modifiableModel;
        for(i$ = this.myModuleLibraries.iterator(); i$.hasNext(); modifiableModel.commit()) {
            libInfo = (Pair)i$.next();
            moduleLibraryPath = (String)libInfo.first;
            String sourceLibraryPath1 = (String)libInfo.second;
            Library library = libraryTable2.createLibrary();
            modifiableModel = library.getModifiableModel();
            modifiableModel.addRoot(getUrlByPath(moduleLibraryPath), OrderRootType.CLASSES);
            if(sourceLibraryPath1 != null) {
                modifiableModel.addRoot(getUrlByPath(sourceLibraryPath1), OrderRootType.SOURCES);
            }
        }

    }

    @Nullable
    public List<Module> commit(@NotNull Project project, ModifiableModuleModel model, ModulesProvider modulesProvider) {
        LanguageLevelProjectExtension extension = LanguageLevelProjectExtension.getInstance(ProjectManager.getInstance().getDefaultProject());
        Boolean aDefault = extension.getDefault();
        LanguageLevelProjectExtension instance = LanguageLevelProjectExtension.getInstance(project);
        if(aDefault != null && !aDefault.booleanValue()) {
            instance.setLanguageLevel(extension.getLanguageLevel());
            instance.setDefault(Boolean.valueOf(false));
        } else {
            instance.setDefault(Boolean.valueOf(true));
        }

        return super.commit(project, model, modulesProvider);
    }

    private static String getUrlByPath(String path) {
        return VfsUtil.getUrlForLibraryRoot(new File(path));
    }

    public void addModuleLibrary(String moduleLibraryPath, String sourcePath) {
        this.myModuleLibraries.add(Pair.create(moduleLibraryPath, sourcePath));
    }

    @Nullable
    protected static String getPathForOutputPathStep() {
        return null;
    }

    public int getWeight() {
        return 200;
    }
}
