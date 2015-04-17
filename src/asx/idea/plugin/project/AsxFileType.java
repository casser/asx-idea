package asx.idea.plugin.project;

import asx.idea.plugin.utils.AsxIcons;
import asx.idea.plugin.language.AsxLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by Sergey on 4/4/15.
 */
public class AsxFileType extends LanguageFileType {

    public static final String EXTENSION = "asx";
    public static final String NAME = "Asx File";
    public static final String DESCRIPTION = "Asx Files";

    public static final AsxFileType INSTANCE = new AsxFileType();

    protected AsxFileType() {
        super(AsxLanguage.INSTANCE);
    }


    @NotNull
    @Override
    public String getName() {
        return NAME;
    }

    @NotNull
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AsxIcons.FILE;
    }
}
