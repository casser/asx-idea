package asx.idea.plugin.project;

import asx.idea.plugin.utils.AsxIcons;
import com.intellij.openapi.projectRoots.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by Sergey on 4/4/15.
 */
public class AsxSdkType extends SdkType {

    public static final String NAME = "Asx SDK";

    public AsxSdkType(){
        super(NAME);
    }

    @Override
    public String suggestHomePath() {
        return null;
    }

    @Override
    public boolean isValidSdkHome(String s) {
        return true;
    }

    @Override
    public String suggestSdkName(String s, String s2) {
        return NAME;
    }


    @Override
    public String getVersionString(Sdk sdk) {
        return "1.0";
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData sdkAdditionalData, @NotNull org.jdom.Element element) {

    }

    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return null;
    }

    @Override
    public Icon getIcon() {
        return AsxIcons.FOLDER;
    }

    @Override
    public Icon getIconForAddAction() {
        return AsxIcons.FILE;
    }

    @Override
    public String getPresentableName() {
        return NAME;
    }

}

