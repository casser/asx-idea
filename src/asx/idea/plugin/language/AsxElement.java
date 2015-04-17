package asx.idea.plugin.language;

import com.intellij.lang.ASTNode;
import com.intellij.lang.javascript.psi.impl.JSStubElementImpl;
import com.intellij.lang.javascript.psi.stubs.JSStubElement;
import com.intellij.lang.javascript.types.PsiGenerator;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Sergey on 4/10/15.
 */
public interface AsxElement extends NavigatablePsiElement {
    abstract
    class CompositeType extends ElementType implements PsiGenerator<AsxElement> {
        public CompositeType(@NonNls @NotNull String debugName) {
            super(debugName);
        }
        abstract public AsxElement construct(ASTNode node);
    }
    class ElementType extends IElementType {
        public ElementType(@NonNls @NotNull String debugName) {
            super(debugName, AsxLanguage.INSTANCE, true);
        }
        public String toString() {
            return "ASX:" + super.toString();
        }
    }
    class ElementImpl extends JSStubElementImpl<JSStubElement> implements AsxElement {
        public ElementImpl(ASTNode node) {
            super(node);
        }
        public String toString() {
            String classname = this.getClass().getName();
            if(classname.endsWith("$ElementImpl")) {
                classname = classname.substring(0, classname.length() - "$ElementImpl".length());
            }
            classname = classname.substring(classname.lastIndexOf(".") + 1);
            return classname;
        }
    }
}
