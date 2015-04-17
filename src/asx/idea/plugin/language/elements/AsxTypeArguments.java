package asx.idea.plugin.language.elements;

import asx.idea.plugin.language.AsxElement;
import com.intellij.lang.ASTNode;

/**
 * Created by Sergey on 4/10/15.
 */
public interface AsxTypeArguments {
    ElementType TYPE = new ElementType();
    class ElementType extends AsxElement.CompositeType {
        ElementType(){
            super("TYPE_ARGUMENTS");
        }
        @Override
        public AsxElement construct(ASTNode node) {
            return new ElementImpl(node);
        }
    }
    class ElementImpl extends AsxElement.ElementImpl implements AsxTypeArguments {
        public ElementImpl(ASTNode node) {
            super(node);
        }
    }
}
