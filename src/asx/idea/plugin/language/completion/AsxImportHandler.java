package asx.idea.plugin.language.completion;

import com.intellij.lang.javascript.psi.resolve.JSImportHandler;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxImportHandler extends JSImportHandler {
    public static final AsxImportHandler INSTANCE = new AsxImportHandler();
}
