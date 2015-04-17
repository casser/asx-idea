package asx.idea.plugin.project;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.ex.JpsElementTypeBase;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

/**
 * Created by Sergey on 4/4/15.
 */
public class AsxSourceRootType extends JpsElementTypeBase<AsxSourceRootProperties> implements JpsModuleSourceRootType<AsxSourceRootProperties> {
    public static final AsxSourceRootType SOURCE = new AsxSourceRootType();
    public static final AsxSourceRootType TEST_SOURCE = new AsxSourceRootType();
    @NotNull
    public AsxSourceRootProperties createDefaultProperties() {
        return new AsxSourceRootProperties();
    }
}
