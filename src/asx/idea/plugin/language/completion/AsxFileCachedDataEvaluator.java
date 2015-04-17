package asx.idea.plugin.language.completion;

import com.intellij.lang.javascript.index.JSFileCachedDataEvaluator;
import com.intellij.lang.javascript.psi.stubs.impl.JSFileCachedData;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxFileCachedDataEvaluator extends JSFileCachedDataEvaluator {
    public AsxFileCachedDataEvaluator(JSFileCachedData outCachedData) {
        super(outCachedData);
    }
}
