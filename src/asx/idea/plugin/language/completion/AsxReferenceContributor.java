package asx.idea.plugin.language.completion;

import asx.idea.plugin.project.AsxFileType;
import com.intellij.javascript.JSModuleReference;
import com.intellij.javascript.JavaScriptReferenceContributor;
import com.intellij.lang.javascript.DialectDetector;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.TypeScriptFileType;
import com.intellij.lang.javascript.ecmascript6.TypeScriptUtil;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.impl.JSReferenceSet;
import com.intellij.lang.javascript.psi.impl.JSTextReference;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.filters.position.FilterPattern;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergey on 4/6/15.
 */
public class AsxReferenceContributor extends PsiReferenceContributor {
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(JavaScriptReferenceContributor.REQUIRE_ARG_PLACE, new PsiReferenceProvider() {
            @NotNull
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                final JSLiteralExpression literalExpression = ObjectUtils.tryCast(element, JSLiteralExpression.class);
                if(literalExpression != null) {
                    String requiredModuleName = StringUtil.replaceChar(StringUtil.stripQuotesAroundValue(literalExpression.getText()), '\\', '/');
                    final VirtualFile file = literalExpression.getContainingFile().getOriginalFile().getVirtualFile();
                    if(file != null && StringUtil.isJavaIdentifier(requiredModuleName)) {
                        JSReferenceSet referenceSet = new JSReferenceSet(literalExpression, TypeScriptUtil.unifyModuleName(literalExpression.getText()), 0, true) {
                            protected JSTextReference createTextReference(String s, int offset, boolean methodRef) {
                                return new MyDeclarationTextReference(this, s, offset, methodRef);
                            }

                            class MyDeclarationTextReference extends JSTextReference implements JSTextReference.JSDeclarationModuleReference {
                                protected MyDeclarationTextReference(JSReferenceSet set, @NotNull String s, int offset, boolean methodRef) {
                                    super(set, s, offset, methodRef);
                                }

                                protected ResolveResult[] doResolve(@NotNull PsiFile psiFile) {
                                    PsiElement element = TypeScriptUtil.findExternalModule(file, this.getCanonicalText(), psiFile.getProject());
                                    return element != null?new ResolveResult[]{new JSResolveResult(element)}:ResolveResult.EMPTY_ARRAY;
                                }
                            }
                        };
                        return referenceSet.getReferences();
                    }
                }

               return PsiReference.EMPTY_ARRAY;
            }
        });

    }

}
