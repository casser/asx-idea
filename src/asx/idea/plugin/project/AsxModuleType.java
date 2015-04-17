package asx.idea.plugin.project;

import asx.idea.plugin.utils.AsxIcons;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by Sergey on 4/4/15.
 */
public class AsxModuleType extends ModuleType<AsxModuleBuilder>{

    private static final String ID              = "AsxModuleType";
    private static final String NAME            = "ASX";
    private static final String DESCRIPTION     = "ASX Module";

    public static AsxModuleType getInstance(){
        return (AsxModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    public AsxModuleType(){
        super(ID);
    }

    @NotNull
    @Override
    public AsxModuleBuilder createModuleBuilder() {
        return new AsxModuleBuilder();
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

    @Override
    public Icon getBigIcon() {
        return AsxIcons.FOLDER;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return AsxIcons.FILE;
    }
}
