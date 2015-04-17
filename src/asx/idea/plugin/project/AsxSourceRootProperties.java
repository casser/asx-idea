package asx.idea.plugin.project;

import com.intellij.openapi.util.Comparing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.ex.JpsElementBase;

/**
 * Created by Sergey on 4/4/15.
 */
public class AsxSourceRootProperties  extends JpsElementBase<AsxSourceRootProperties> implements JpsSimpleElement<AsxSourceRootProperties> {
    private String myPackagePrefix = "";
    private boolean myForGeneratedSources;


    public AsxSourceRootProperties() {
    }
    public AsxSourceRootProperties(@NotNull String packagePrefix) {
        this.myPackagePrefix = packagePrefix;
    }
    public AsxSourceRootProperties(@NotNull String packagePrefix, boolean forGeneratedSources) {
        this.myPackagePrefix = packagePrefix;
        this.myForGeneratedSources = forGeneratedSources;
    }

    @NotNull
    public String getPackagePrefix() {
        return this.myPackagePrefix;
    }

    @NotNull
    public AsxSourceRootProperties createCopy() {
        return new AsxSourceRootProperties(this.myPackagePrefix, this.myForGeneratedSources);
    }

    public boolean isForGeneratedSources() {
        return this.myForGeneratedSources;
    }

    public void setPackagePrefix(@NotNull String packagePrefix) {
        if(!Comparing.equal(this.myPackagePrefix, packagePrefix)) {
            this.myPackagePrefix = packagePrefix;
            this.fireElementChanged();
        }

    }
    public void setForGeneratedSources(boolean forGeneratedSources) {
        if(this.myForGeneratedSources != forGeneratedSources) {
            this.myForGeneratedSources = forGeneratedSources;
            this.fireElementChanged();
        }

    }

    public void applyChanges(@NotNull AsxSourceRootProperties modified) {
        this.setPackagePrefix(modified.myPackagePrefix);
        this.setForGeneratedSources(modified.myForGeneratedSources);
    }

    public void setData(@NotNull AsxSourceRootProperties data) {
        this.applyChanges(data);
    }

    @NotNull
    public AsxSourceRootProperties getData() {
        return this;
    }
}
