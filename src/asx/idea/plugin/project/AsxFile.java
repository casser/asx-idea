package asx.idea.plugin.project;

import asx.idea.plugin.language.AsxLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.javascript.psi.impl.JSFileImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxFile extends PsiFileBase {
    public AsxFile(FileViewProvider fileViewProvider) {
        super(fileViewProvider, AsxLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return AsxFileType.INSTANCE;
    }

}
