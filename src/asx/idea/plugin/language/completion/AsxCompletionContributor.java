package asx.idea.plugin.language.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.javascript.JSModuleReference;
import com.intellij.lang.javascript.TypeScriptFileType;
import com.intellij.lang.javascript.psi.ecma6.TypeScriptImportStatement;
import com.intellij.lang.typescript.completion.TypeScriptExternalModuleCompletionContributor;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxCompletionContributor extends TypeScriptExternalModuleCompletionContributor {

    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiReference ref = parameters.getPosition().getContainingFile().findReferenceAt(parameters.getOffset());
        if(ref instanceof JSModuleReference && ref.getElement().getParent() instanceof TypeScriptImportStatement) {
            Object[] variants = ref.getVariants();
            Object[] arr$ = variants;
            int len$ = variants.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Object variant = arr$[i$];
                if(variant instanceof LookupElementBuilder) {
                    LookupElementBuilder builder = (LookupElementBuilder)variant;
                    String fileName = builder.getLookupString();
                    if(fileName.endsWith(".asx")) {
                        String nameWithoutExtension = fileName.substring(0, fileName.length() - 3);
                        LookupElementBuilder newBuilder = LookupElementBuilder.create(builder.getObject(), nameWithoutExtension);
                        newBuilder = newBuilder.withIcon(TypeScriptFileType.INSTANCE.getIcon());
                        result.addElement(newBuilder);
                    }
                }
            }

            result.stopHere();
        }

    }
}
