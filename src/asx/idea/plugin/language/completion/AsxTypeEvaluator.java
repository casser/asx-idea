package asx.idea.plugin.language.completion;

import com.intellij.lang.javascript.psi.resolve.BaseJSSymbolProcessor;
import com.intellij.lang.javascript.psi.resolve.JSTypeEvaluator;

public class AsxTypeEvaluator extends JSTypeEvaluator {
    public AsxTypeEvaluator(BaseJSSymbolProcessor.EvaluateContext context, BaseJSSymbolProcessor.TypeProcessor processor, boolean ecma) {
        super(context, processor, ecma);
    }
}
