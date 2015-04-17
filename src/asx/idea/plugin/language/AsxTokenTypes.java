package asx.idea.plugin.language;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;

public interface AsxTokenTypes {

    IElementType LINE_COMMENT                        = new AsxElement.ElementType("LINE_COMMENT");
    IElementType BLOCK_COMMENT                       = new AsxElement.ElementType("BLOCK_COMMENT");
    IElementType DOC_COMMENT                         = new AsxElement.ElementType("DOC_COMMENT");

    IElementType  IDENTIFIER                         = new AsxElement.ElementType("IDENTIFIER");
    IElementType  ANY_IDENTIFIER                     = new AsxElement.ElementType("ANY_IDENTIFIER");
    IElementType  WHITE_SPACE                        = new AsxElement.ElementType("WHITE_SPACE");


    IElementType  XML_STYLE_COMMENT                  = new AsxElement.ElementType("XML_STYLE_COMMENT");
    IElementType  XML_STYLE_COMMENT_START            = new AsxElement.ElementType("XML_STYLE_COMMENT_START");
    IElementType  XML_JS_TEXT                        = new AsxElement.ElementType("XML_JS_TEXT");

    IElementType  DOC_COMMENT_TOKEN                  = new AsxElement.ElementType("JSDOC_COMMENT_TOKEN");
    IElementType  BREAK_KEYWORD                      = new AsxElement.ElementType("BREAK_KEYWORD");
    IElementType  CASE_KEYWORD                       = new AsxElement.ElementType("CASE_KEYWORD");
    IElementType  CATCH_KEYWORD                      = new AsxElement.ElementType("CATCH_KEYWORD");
    IElementType  CONST_KEYWORD                      = new AsxElement.ElementType("CONST_KEYWORD");
    IElementType  CONTINUE_KEYWORD                   = new AsxElement.ElementType("CONTINUE_KEYWORD");
    IElementType  DEBUGGER_KEYWORD                   = new AsxElement.ElementType("DEBUGGER_KEYWORD");
    IElementType  DELETE_KEYWORD                     = new AsxElement.ElementType("DELETE_KEYWORD");
    IElementType  DEFAULT_KEYWORD                    = new AsxElement.ElementType("DEFAULT_KEYWORD");
    IElementType  DO_KEYWORD                         = new AsxElement.ElementType("DO_KEYWORD");
    IElementType  ELSE_KEYWORD                       = new AsxElement.ElementType("ELSE_KEYWORD");
    IElementType  FINALLY_KEYWORD                    = new AsxElement.ElementType("FINALLY_KEYWORD");
    IElementType  FOR_KEYWORD                        = new AsxElement.ElementType("FOR_KEYWORD");
    IElementType  FUNCTION_KEYWORD                   = new AsxElement.ElementType("FUNCTION_KEYWORD");
    IElementType  IF_KEYWORD                         = new AsxElement.ElementType("IF_KEYWORD");
    IElementType  IN_KEYWORD                         = new AsxElement.ElementType("IN_KEYWORD");
    IElementType  INSTANCEOF_KEYWORD                 = new AsxElement.ElementType("INSTANCEOF_KEYWORD");
    IElementType  OF_KEYWORD                         = new AsxElement.ElementType("OF_KEYWORD");
    IElementType  NEW_KEYWORD                        = new AsxElement.ElementType("NEW_KEYWORD");
    IElementType  RETURN_KEYWORD                     = new AsxElement.ElementType("RETURN_KEYWORD");
    IElementType  SWITCH_KEYWORD                     = new AsxElement.ElementType("SWITCH_KEYWORD");
    IElementType  THIS_KEYWORD                       = new AsxElement.ElementType("THIS_KEYWORD");
    IElementType  THROW_KEYWORD                      = new AsxElement.ElementType("THROW_KEYWORD");
    IElementType  TRY_KEYWORD                        = new AsxElement.ElementType("TRY_KEYWORD");
    IElementType  TYPEOF_KEYWORD                     = new AsxElement.ElementType("TYPEOF_KEYWORD");
    IElementType  VAR_KEYWORD                        = new AsxElement.ElementType("VAR_KEYWORD");
    IElementType  VOID_KEYWORD                       = new AsxElement.ElementType("VOID_KEYWORD");
    IElementType  WHILE_KEYWORD                      = new AsxElement.ElementType("WHILE_KEYWORD");
    IElementType  WITH_KEYWORD                       = new AsxElement.ElementType("WITH_KEYWORD");
    IElementType  PACKAGE_KEYWORD                    = new AsxElement.ElementType("PACKAGE_KEYWORD");
    IElementType  IMPORT_KEYWORD                     = new AsxElement.ElementType("IMPORT_KEYWORD");
    IElementType  CLASS_KEYWORD                      = new AsxElement.ElementType("CLASS_KEYWORD");
    IElementType  INTERFACE_KEYWORD                  = new AsxElement.ElementType("INTERFACE_KEYWORD");
    IElementType  PUBLIC_KEYWORD                     = new AsxElement.ElementType("PUBLIC_KEYWORD");
    IElementType  STATIC_KEYWORD                     = new AsxElement.ElementType("STATIC_KEYWORD");
    IElementType  INTERNAL_KEYWORD                   = new AsxElement.ElementType("INTERNAL_KEYWORD");
    IElementType  FINAL_KEYWORD                      = new AsxElement.ElementType("FINAL_KEYWORD");
    IElementType  DYNAMIC_KEYWORD                    = new AsxElement.ElementType("DYNAMIC_KEYWORD");
    IElementType  NATIVE_KEYWORD                     = new AsxElement.ElementType("NATIVE_KEYWORD");
    IElementType  VIRTUAL_KEYWORD                    = new AsxElement.ElementType("VIRTUAL_KEYWORD");
    IElementType  REQUIRES_KEYWORD                   = new AsxElement.ElementType("REQUIRES_KEYWORD");
    IElementType  MIXIN_KEYWORD                      = new AsxElement.ElementType("MIXIN_KEYWORD");
    IElementType  AWAIT_KEYWORD                      = new AsxElement.ElementType("AWAIT_KEYWORD");
    IElementType  ENUM_KEYWORD                       = new AsxElement.ElementType("ENUM_KEYWORD");
    IElementType  TYPE_KEYWORD                       = new AsxElement.ElementType("TYPE_KEYWORD");
    IElementType  EXPORT_KEYWORD                     = new AsxElement.ElementType("EXPORT_KEYWORD");
    IElementType  MODULE_KEYWORD                     = new AsxElement.ElementType("MODULE_KEYWORD");
    IElementType  DECLARE_KEYWORD                    = new AsxElement.ElementType("DECLARE_KEYWORD");
    IElementType  REQUIRE_KEYWORD                    = new AsxElement.ElementType("REQUIRE_KEYWORD");
    IElementType  NUMBER_KEYWORD                     = new AsxElement.ElementType("NUMBER_KEYWORD");
    IElementType  STRING_KEYWORD                     = new AsxElement.ElementType("STRING_KEYWORD");
    IElementType  BOOLEAN_KEYWORD                    = new AsxElement.ElementType("BOOLEAN_KEYWORD");
    IElementType  ANY_KEYWORD                        = new AsxElement.ElementType("ANY_KEYWORD");
    IElementType  FROM_KEYWORD                       = new AsxElement.ElementType("FROM_KEYWORD");
    IElementType  NAMESPACE_KEYWORD                  = new AsxElement.ElementType("NAMESPACE_KEYWORD");
    IElementType  EXTENDS_KEYWORD                    = new AsxElement.ElementType("EXTENDS_KEYWORD");
    IElementType  IMPLEMENTS_KEYWORD                 = new AsxElement.ElementType("IMPLEMENTS_KEYWORD");
    IElementType  USE_KEYWORD                        = new AsxElement.ElementType("USE_KEYWORD");
    IElementType  PRIVATE_KEYWORD                    = new AsxElement.ElementType("PRIVATE_KEYWORD");
    IElementType  PROTECTED_KEYWORD                  = new AsxElement.ElementType("PROTECTED_KEYWORD");
    IElementType  OVERRIDE_KEYWORD                   = new AsxElement.ElementType("OVERRIDE_KEYWORD");
    IElementType  SUPER_KEYWORD                      = new AsxElement.ElementType("SUPER_KEYWORD");
    IElementType  INCLUDE_KEYWORD                    = new AsxElement.ElementType("INCLUDE_KEYWORD");
    IElementType  IS_KEYWORD                         = new AsxElement.ElementType("IS_KEYWORD");
    IElementType  AS_KEYWORD                         = new AsxElement.ElementType("AS_KEYWORD");
    IElementType  GET_KEYWORD                        = new AsxElement.ElementType("GET_KEYWORD");
    IElementType  SET_KEYWORD                        = new AsxElement.ElementType("SET_KEYWORD");
    IElementType  EACH_KEYWORD                       = new AsxElement.ElementType("EACH_KEYWORD");
    IElementType  INT_KEYWORD                        = new AsxElement.ElementType("INT_KEYWORD");
    IElementType  UINT_KEYWORD                       = new AsxElement.ElementType("UINT_KEYWORD");
    IElementType  ASYNC_KEYWORD                      = new AsxElement.ElementType("ASYNC_KEYWORD");

    IElementType  XML_START_TAG_LIST                 = new AsxElement.ElementType("XML_TAG__LIST_START");
    IElementType  XML_END_TAG_LIST                   = new AsxElement.ElementType("XML_TAG__LIST_END");
    IElementType  XML_JS_SCRIPT                      = new AsxElement.ElementType("XML_JS_SCRIPT");
    IElementType  XML_TAG_WHITE_SPACE                = new AsxElement.ElementType("XML_TAG_WHITESPACE");

    IElementType  JSP_TEXT                           = new AsxElement.ElementType("JSP_TEXT");
    IElementType  YIELD_KEYWORD                      = new AsxElement.ElementType("YIELD_KEYWORD");
    IElementType  LET_KEYWORD                        = new AsxElement.ElementType("LET_KEYWORD");
    IElementType  TRUE_KEYWORD                       = new AsxElement.ElementType("TRUE_KEYWORD");
    IElementType  FALSE_KEYWORD                      = new AsxElement.ElementType("FALSE_KEYWORD");
    IElementType  NULL_KEYWORD                       = new AsxElement.ElementType("NULL_KEYWORD");
    IElementType  UNDEFINED_KEYWORD                  = new AsxElement.ElementType("UNDEFINED_KEYWORD");
    IElementType  NUMERIC_LITERAL                    = new AsxElement.ElementType("NUMERIC_LITERAL");
    IElementType  STRING_LITERAL                     = new AsxElement.ElementType("STRING_LITERAL");
    IElementType  STRING_LITERAL_PART                = new AsxElement.ElementType("STRING_LITERAL_PART");
    IElementType  SINGLE_QUOTE_STRING_LITERAL        = new AsxElement.ElementType("SINGLE_QUOTE_STRING_LITERAL");
    IElementType  REGEXP_LITERAL                     = new AsxElement.ElementType("REGEXP_LITERAL");
    IElementType  STRING_TEMPLATE_PART               = new AsxElement.ElementType("STRING_TEMPLATE_PART");
    IElementType  LBRACE                             = new AsxElement.ElementType("LBRACE");
    IElementType  RBRACE                             = new AsxElement.ElementType("RBRACE");
    IElementType  LPAR                               = new AsxElement.ElementType("LPAR");
    IElementType  RPAR                               = new AsxElement.ElementType("RPAR");
    IElementType  LBRACKET                           = new AsxElement.ElementType("LBRACKET");
    IElementType  RBRACKET                           = new AsxElement.ElementType("RBRACKET");
    IElementType  DOT                                = new AsxElement.ElementType("DOT");
    IElementType  SEMICOLON                          = new AsxElement.ElementType("SEMICOLON");
    IElementType  COMMA                              = new AsxElement.ElementType("COMMA");
    IElementType  LT                                 = new AsxElement.ElementType("LT");
    IElementType  GT                                 = new AsxElement.ElementType("GT");
    IElementType  LE                                 = new AsxElement.ElementType("LE");
    IElementType  GE                                 = new AsxElement.ElementType("GE");
    IElementType  EQEQ                               = new AsxElement.ElementType("EQEQ");
    IElementType  NE                                 = new AsxElement.ElementType("NE");
    IElementType  EQEQEQ                             = new AsxElement.ElementType("EQEQEQ");
    IElementType  NEQEQ                              = new AsxElement.ElementType("NEQEQ");
    IElementType  PLUS                               = new AsxElement.ElementType("PLUS");
    IElementType  MINUS                              = new AsxElement.ElementType("MINUS");
    IElementType  MULT                               = new AsxElement.ElementType("MULT");
    IElementType  PERC                               = new AsxElement.ElementType("PERC");
    IElementType  PLUSPLUS                           = new AsxElement.ElementType("PLUSPLUS");
    IElementType  MINUSMINUS                         = new AsxElement.ElementType("MINUSMINUS");
    IElementType  LTLT                               = new AsxElement.ElementType("LTLT");
    IElementType  GTGT                               = new AsxElement.ElementType("GTGT");
    IElementType  GTGTGT                             = new AsxElement.ElementType("GTGTGT");
    IElementType  AND                                = new AsxElement.ElementType("AND");
    IElementType  OR                                 = new AsxElement.ElementType("OR");
    IElementType  XOR                                = new AsxElement.ElementType("XOR");
    IElementType  EXCL                               = new AsxElement.ElementType("EXCL");
    IElementType  TILDE                              = new AsxElement.ElementType("TILDE");
    IElementType  ANDAND                             = new AsxElement.ElementType("ANDAND");
    IElementType  AND_AND_EQ                         = new AsxElement.ElementType("AND_AND_EQ");
    IElementType  OROR                               = new AsxElement.ElementType("OROR");
    IElementType  OR_OR_EQ                           = new AsxElement.ElementType("OR_OR_EQ");
    IElementType  QUEST                              = new AsxElement.ElementType("QUEST");
    IElementType  COLON                              = new AsxElement.ElementType("COLON");
    IElementType  EQ                                 = new AsxElement.ElementType("EQ");
    IElementType  PLUSEQ                             = new AsxElement.ElementType("PLUSEQ");
    IElementType  MINUSEQ                            = new AsxElement.ElementType("MINUSEQ");
    IElementType  MULTEQ                             = new AsxElement.ElementType("MULTEQ");
    IElementType  PERCEQ                             = new AsxElement.ElementType("PERCEQ");
    IElementType  LTLTEQ                             = new AsxElement.ElementType("LTLTEQ");
    IElementType  GTGTEQ                             = new AsxElement.ElementType("GTGTEQ");
    IElementType  GTGTGTEQ                           = new AsxElement.ElementType("GTGTGTEQ");
    IElementType  ANDEQ                              = new AsxElement.ElementType("ANDEQ");
    IElementType  OREQ                               = new AsxElement.ElementType("OREQ");
    IElementType  XOREQ                              = new AsxElement.ElementType("XOREQ");
    IElementType  DIV                                = new AsxElement.ElementType("DIV");
    IElementType  DIVEQ                              = new AsxElement.ElementType("DIVEQ");
    IElementType  BACKQUOTE                          = new AsxElement.ElementType("BACKQUOTE");
    IElementType  EXEC_STRING_BOUND                  = new AsxElement.ElementType("EXEC_STRING_BOUND");
    IElementType  DOLLAR                             = new AsxElement.ElementType("DOLLAR");
    IElementType  EQGT                               = new AsxElement.ElementType("EQGT");
    IElementType  MINUSGT                            = new AsxElement.ElementType("MINUSGT");
    IElementType  COLON_COLON                        = new AsxElement.ElementType("COLON_COLON");
    IElementType  GWT_FIELD_OR_METHOD                = new AsxElement.ElementType("GWT_FIELD_OR_METHOD");
    IElementType  DOT_DOT                            = new AsxElement.ElementType("DOT_DOT");
    IElementType  DOT_DOT_DOT                        = new AsxElement.ElementType("DOT_DOT_DOT");
    IElementType  GENERIC_SIGNATURE_START            = new AsxElement.ElementType("GENERIC_SIGNATURE_START");
    IElementType  AT                                 = new AsxElement.ElementType("AT");
    IElementType  CDATA_START                        = new AsxElement.ElementType("CDATA_START");
    IElementType  CDATA_END                          = new AsxElement.ElementType("CDATA_END");
    IElementType  JSDOC_TAG_DATA                     = new AsxElement.ElementType("JSDOC_TAG_DATA");
    IElementType  JSDOC_MARKUP                       = new AsxElement.ElementType("JSDOC_MARKUP");
    IElementType  HEREDOC_BOUND                      = new AsxElement.ElementType("HEREDOC_BOUND");
    IElementType  HEREDOC_BODY                       = new AsxElement.ElementType("HEREDOC_BODY");
    IElementType  EXEC_BODY                          = new AsxElement.ElementType("EXEC_BODY");

    IElementType  BAD_CHARACTER                      = TokenType.BAD_CHARACTER;

    IElementType  XML_START_TAG_START                = XmlTokenType.XML_START_TAG_START;
    IElementType  XML_END_TAG_START                  = XmlTokenType.XML_END_TAG_START;
    IElementType  XML_EMPTY_TAG_END                  = XmlTokenType.XML_EMPTY_ELEMENT_END;
    IElementType  XML_NAME                           = XmlTokenType.XML_NAME;
    IElementType  XML_TAG_NAME                       = XmlTokenType.XML_TAG_NAME;
    IElementType  XML_ATTR_EQUAL                     = XmlTokenType.XML_EQ;
    IElementType  XML_TAG_END                        = XmlTokenType.XML_TAG_END;
    IElementType  XML_ATTR_VALUE                     = XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN;
    IElementType  XML_ATTR_VALUE_START               = XmlTokenType.XML_ATTRIBUTE_VALUE_START_DELIMITER;
    IElementType  XML_ATTR_VALUE_END                 = XmlTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER;
    IElementType  XML_TAG_CONTENT                    = XmlTokenType.XML_DATA_CHARACTERS;
    IElementType  XML_ENTITY_REF                     = XmlTokenType.XML_ENTITY_REF_TOKEN;







}
