package asx.idea.plugin.language;

import asx.idea.plugin.language.elements.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;

/**
 * Created by Sergey on 4/5/15.
 */
public interface AsxElementTypes {
    IStubFileElementType              ASX_FILE                     = new IStubFileElementType(AsxLanguage.INSTANCE);
    IElementType                      LITERAL_EXPRESSION           = AsxLiteralExpression.TYPE;
    IElementType                      REFERENCE_EXPRESSION         = AsxReferenceExpression.TYPE;
    IElementType                      EXPRESSION_STATEMENT         = AsxExpressionStatement.TYPE;
    IElementType                      VARIABLE_STATEMENT           = AsxVariableStatement.TYPE;
    IElementType                      VARIABLE_DECLARATION         = AsxVariableDeclaration.TYPE;
    IElementType                      FUNCTION_DECLARATION         = AsxFunctionDeclaration.TYPE;
    IElementType                      FUNCTION_EXPRESSION          = AsxFunctionExpression.TYPE;
    IElementType                      FUNCTION_PARAMETER           = AsxFunctionParameter.TYPE;
    IElementType                      FUNCTION_PARAMETERS          = AsxFunctionParameters.TYPE;
    IElementType                      TYPE_REFERENCE               = AsxTypeReference.TYPE;
    IElementType                      TYPE_ARGUMENTS               = AsxTypeArguments.TYPE;
    IElementType                      OBJECT_EXPRESSION            = AsxObjectExpression.TYPE;
    IElementType                      OBJECT_PROPERTY              = AsxObjectProperty.TYPE;
    /*
    AsxVariableElementType            ASX_VARIABLE                 = AsxVariableElementType.INSTANCE;
    AsxSingleTypeElementType          ASX_SINGLE_TYPE              = AsxSingleTypeElementType.INSTANCE;

    AsxModuleReferenceElementType     EXTERNAL_MODULE_REFERENCE    = new AsxModuleReferenceElementType();
    AsxEntityNameElementType          ENTITY_NAME                  = new AsxEntityNameElementType();
    AsxParameterElementType           TYPESCRIPT_PARAMETER         = new AsxParameterElementType();
    AsxImportStatementElementType     IMPORT_STATEMENT             = new AsxImportStatementElementType();
    AsxObjectTypeElementType          OBJECT_TYPE                  = new AsxObjectTypeElementType();
    AsxTupleTypeElementType           TUPLE_TYPE                   = new AsxTupleTypeElementType();
    AsxUnionTypeElementType           UNION_TYPE                   = new AsxUnionTypeElementType();
    AsxPropertySignatureElementType   PROPERTY_SIGNATURE           = new AsxPropertySignatureElementType();
    AsxTypeParameterElementType       TYPE_PARAMETER               = new AsxTypeParameterElementType();
    AsxTypeParameterListElementType   TYPE_PARAMETER_LIST          = new AsxTypeParameterListElementType();

    AsxTypeArgumentListElementType    TYPE_ARGUMENTS           = new AsxTypeArgumentListElementType();
    AsxImplicitModuleElementType      IMPLICIT_MODULE              = new AsxImplicitModuleElementType();
    AsxExportAssignmentElementType    EXPORT_ASSIGNMENT            = new AsxExportAssignmentElementType();
    AsxIndexSignatureElementType      INDEX_SIGNATURE              = new AsxIndexSignatureElementType();
    */
}
