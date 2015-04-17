package asx.idea.plugin.language.completion;

import com.intellij.lang.javascript.completion.JSSmartCompletionContributor;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * Created by Sergey on 4/6/15.
 */
public class AsxSmartCompletionContributor extends JSSmartCompletionContributor {
    private static boolean DEBUG = true;
    private static List<Object> debug(List<Object> results,String target){
        if(DEBUG && results!=null && results.size()>0){
            System.out.println("Resolved "+target);
            for(Object result:results){
                System.out.println("  "+result.toString());
            }
        }
        return results;
    }
    @Nullable
    public List<Object> getSmartCompletionVariants(@NotNull PsiElement location) {
        if(location instanceof JSReferenceExpression && ((JSReferenceExpression)location).getQualifier() == null && !JSResolveUtil.isExprInStrictTypeContext((JSReferenceExpression) location)) {
            List<Object> variants = this.addVariantsForUnqualifiedReference((JSReferenceExpression)location);
            if(!variants.isEmpty()) {
                return variants;
            }
        }
        return debug(super.getSmartCompletionVariants(location), location.toString());
    }

    protected boolean needToQualify(int qualifiedStaticVariantsStart, int i) {
        return true;
    }
}