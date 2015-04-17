package asx.idea.plugin.language.completion;

import com.intellij.lang.javascript.DialectOptionHolder;
import com.intellij.lang.javascript.index.JavaScriptIndex;
import com.intellij.lang.javascript.psi.JSPsiElementBase;
import com.intellij.lang.javascript.psi.ecmal4.JSClass;
import com.intellij.lang.javascript.psi.ecmal4.JSQualifiedNamedElement;
import com.intellij.lang.javascript.psi.resolve.JSClassResolver;
import com.intellij.lang.typescript.resolve.TypeScriptClassResolver;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxClassResolver extends JSClassResolver {
    public final static AsxClassResolver INSTANCE = new AsxClassResolver();
}

