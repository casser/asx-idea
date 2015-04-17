package asx.idea.plugin.language;

import asx.idea.plugin.language.completion.*;
import com.intellij.lang.javascript.completion.JSCompletionKeywordsContributor;
import com.intellij.lang.javascript.dialects.JSDialectSpecificHandlersFactory;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.*;
import com.intellij.lang.javascript.psi.stubs.impl.JSFileCachedData;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class AsxHandlersFactory extends JSDialectSpecificHandlersFactory {
    @NotNull
    @Override
    public JSTypeEvaluator newTypeEvaluator(BaseJSSymbolProcessor.EvaluateContext context, BaseJSSymbolProcessor.TypeProcessor processor, boolean ecma) {
        return new AsxTypeEvaluator(context, processor, ecma);
    }

    @NotNull
    @Override
    public JSCompletionKeywordsContributor newCompletionKeywordsContributor() {
        return new AsxCompletionKeywordsContributor();
    }

    @NotNull
    @Override
    public PsiElementVisitor newFileCachedDataEvaluator(JSFileCachedData outCachedData) {
        return new AsxFileCachedDataEvaluator(outCachedData);
    }

    @NotNull
    @Override
    public JSResolveUtil.Resolver<JSReferenceExpressionImpl> createReferenceExpressionResolver(JSReferenceExpressionImpl referenceExpression, PsiFile containingFile) {
        return new AsxReferenceExpressionResolver(referenceExpression, containingFile);
    }

    @NotNull
    @Override
    public JSClassResolver getClassResolver() {
        return AsxClassResolver.INSTANCE;
    }

    @NotNull
    @Override
    public JSImportHandler getImportHandler() {
        return AsxImportHandler.INSTANCE;
    }

    @NotNull
    @Override
    public AsxQualifiedItemProcessor<? extends ResultSink> createCompletionItemProcessor(ResultSink sink, PsiFile file) {
        return new AsxQualifiedItemProcessor<ResultSink>(sink, file);
    }

    @NotNull
    @Override
    public JSTypeHelper getTypeHelper() {
        return AsxTypeHelper.INSTANCE;
    }
}
