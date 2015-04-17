package asx.idea.plugin.project;

import asx.idea.plugin.utils.AsxIcons;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.roots.ui.configuration.ModuleSourceRootEditHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Sergey on 4/4/15.
 */

public class AsxSourceRootEditHandler extends ModuleSourceRootEditHandler<AsxSourceRootProperties>{
    public AsxSourceRootEditHandler(){
        super(AsxSourceRootType.SOURCE);
    }

    @NotNull
    @Override
    public String getRootTypeName() {
        return "Sources";
    }

    @NotNull
    @Override
    public Icon getRootIcon() {
        return AsxIcons.FOLDER;
    }

    @Nullable
    @Override
    public Icon getFolderUnderRootIcon() {
        return AsxIcons.FOLDER;
    }

    @Nullable
    @Override
    public CustomShortcutSet getMarkRootShortcutSet() {
        return null;
    }

    @NotNull
    @Override
    public String getRootsGroupTitle() {
        return "Sources";
    }

    @NotNull
    @Override
    public Color getRootsGroupColor() {
        return Color.LIGHT_GRAY;
    }

    @NotNull
    @Override
    public String getUnmarkRootButtonText() {
        return "Unmark";
    }
}
