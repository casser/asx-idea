package asx.idea.plugin.language.completion;

import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.JSReferenceExpressionResolver;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxReferenceExpressionResolver extends JSReferenceExpressionResolver {
    private static boolean DEBUG = false;
    private static ResolveResult[] debug(ResolveResult[] results,String target){
        if(DEBUG && results.length>0){
            System.out.println("Resolved "+target);
            for(ResolveResult result:results){
                System.out.println("  "+result.isValidResult() + " " + result.getElement());
            }
        }
        return results;
    }
    public AsxReferenceExpressionResolver(JSReferenceExpressionImpl expression, PsiFile file) {
        super(expression, file);
    }

    @Override
    public ResolveResult[] doResolve() {
        return debug(super.doResolve(),this.myParent+" "+this.myReferencedName);
    }
}
