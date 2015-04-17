package asx.idea.plugin.language.elements;

import asx.idea.plugin.language.AsxElement;
import com.intellij.lang.ASTNode;

/**
 * Created by Sergey on 4/10/15.
 */
public interface AsxReferenceExpression {
    ElementType TYPE = new ElementType();
    class ElementType extends AsxElement.CompositeType {
        ElementType(){
            super("REFERENCE_EXPRESSION");
        }
        @Override
        public AsxElement construct(ASTNode node) {
            return new ElementImpl(node);
        }
    }
    class ElementImpl extends AsxAbstractExpression.ElementImpl implements AsxReferenceExpression {
        public ElementImpl(ASTNode node) {
            super(node);
        }
    }
}
