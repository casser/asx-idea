package asx.idea.plugin.language.completion;

import com.intellij.lang.javascript.ecmascript6.TypeScriptQualifiedItemProcessor;
import com.intellij.lang.javascript.nashorn.resolve.NashornJSQualifiedItemProcessor;
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSType;
import com.intellij.lang.javascript.psi.JSTypeUtils;
import com.intellij.lang.javascript.psi.ecma6.TypeScriptObjectType;
import com.intellij.lang.javascript.psi.ecmal4.JSClass;
import com.intellij.lang.javascript.psi.ecmal4.JSQualifiedNamedElement;
import com.intellij.lang.javascript.psi.ecmal4.JSReferenceList;
import com.intellij.lang.javascript.psi.ecmal4.XmlBackedJSClass;
import com.intellij.lang.javascript.psi.resolve.*;
import com.intellij.lang.javascript.psi.stubs.JSImplicitElement;
import com.intellij.lang.javascript.psi.stubs.impl.JSImplicitElementImpl;
import com.intellij.lang.javascript.psi.types.JSAnyType;
import com.intellij.lang.javascript.psi.types.JSRecordTypeImpl;
import com.intellij.lang.javascript.psi.types.primitives.JSObjectType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxQualifiedItemProcessor<T extends ResultSink> extends QualifiedItemProcessor<T> {
    public AsxQualifiedItemProcessor(T sink, PsiFile containingFile) {
        super(sink, containingFile);
    }
    public void process(@NotNull JSType type, @NotNull BaseJSSymbolProcessor.EvaluateContext evaluateContext, PsiElement source) {
        super.process(type, evaluateContext, source);
    }
}
