package asx.idea.plugin.language.completion;

import com.intellij.lang.javascript.psi.resolve.JSTypeHelper;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxTypeHelper extends JSTypeHelper {
    public static JSTypeHelper INSTANCE = new AsxTypeHelper();
}
