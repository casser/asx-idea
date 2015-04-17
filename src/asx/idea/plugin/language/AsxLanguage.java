package asx.idea.plugin.language;

import asx.idea.plugin.project.AsxFileType;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.DialectOptionHolder;
import com.intellij.lang.javascript.JSLanguageDialect;
import com.intellij.lang.javascript.JavaScriptSupportLoader;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.lang.javascript.dialects.AtScriptLanguageDialect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Sergey on 4/4/15.
 */

public class AsxLanguage extends Language {
    public static final AsxLanguage INSTANCE = new AsxLanguage();

    private AsxLanguage() {
        super("ASX","text/asx","application/x-asx","text/x-asx");
    }

    @NotNull
    public String getDisplayName() {
        return "Asx";
    }
    public boolean isCaseSensitive() {
        return true;
    }


}
