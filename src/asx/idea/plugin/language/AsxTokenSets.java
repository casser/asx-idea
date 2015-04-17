package asx.idea.plugin.language;

import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.psi.tree.TokenSet;

public interface AsxTokenSets extends AsxTokenTypes {
    TokenSet XML_TOKENS = TokenSet.create(
            XML_START_TAG_START,
            XML_START_TAG_LIST,
            XML_END_TAG_LIST,
            XML_END_TAG_START,
            XML_EMPTY_TAG_END,
            XML_NAME,
            XML_TAG_NAME,
            XML_ATTR_EQUAL,
            XML_ATTR_VALUE,
            XML_ATTR_VALUE_START,
            XML_ATTR_VALUE_END,
            XML_TAG_END,
            XML_JS_SCRIPT,
            XML_TAG_CONTENT,
            XML_STYLE_COMMENT,
            XML_ENTITY_REF,
            XML_TAG_WHITE_SPACE
    );
    TokenSet PARSER_WHITE_SPACE_TOKENS = TokenSet.create(
            WHITE_SPACE
    );
    TokenSet PARENTHESES = TokenSet.create(RPAR,LPAR);
    TokenSet BRACKETS = TokenSet.create(RBRACKET,LBRACKET);
    TokenSet BRACES = TokenSet.create(RBRACE,LBRACE);
    TokenSet XML_JS_TEXT_SET = TokenSet.create(
            XML_JS_TEXT
    );

    TokenSet OPERATIONS = TokenSet.create(
            LT,
            GT,
            LE,
            GE,
            EQEQ,
            NE,
            EQEQEQ,
            NEQEQ,
            PLUS,
            MINUS,
            MULT,
            PERC,
            PLUSPLUS,
            MINUSMINUS,
            LTLT,
            GTGT,
            GTGTGT,
            AND,
            OR,
            XOR,
            EXCL,
            TILDE,
            ANDAND,
            OROR,
            QUEST,
            COLON,
            EQ,
            PLUSEQ,
            MINUSEQ,
            MULTEQ,
            PERCEQ,
            LTLTEQ,
            GTGTEQ,
            GTGTGTEQ,
            ANDEQ,
            OREQ,
            XOREQ,
            DIV,
            DIVEQ,
            COMMA,
            AND_AND_EQ,
            OR_OR_EQ
    );
    TokenSet ASSOC_OPERATIONS = TokenSet.create(PLUS,MULT,AND,OR,XOR,OROR,ANDAND);
    TokenSet EQUALITY_OPERATIONS = TokenSet.create(EQEQ,NE,EQEQEQ,NEQEQ);
    TokenSet RELATIONAL_OPERATIONS = TokenSet.create(LT,GT,LE,GE,INSTANCEOF_KEYWORD,IN_KEYWORD);
    TokenSet ADDITIVE_OPERATIONS = TokenSet.create(PLUS, MINUS);
    TokenSet MULTIPLICATIVE_OPERATIONS = TokenSet.create(MULT,DIV,PERC);
    TokenSet SHIFT_OPERATIONS = TokenSet.create(LTLT, GTGT, GTGTGT);
    TokenSet UNARY_OPERATIONS = TokenSet.create(PLUS, MINUS, PLUSPLUS, MINUSMINUS, TILDE, EXCL, TYPEOF_KEYWORD, VOID_KEYWORD, DELETE_KEYWORD, TYPEOF_KEYWORD, AWAIT_KEYWORD);
    TokenSet COMMENTS = TokenSet.create(LINE_COMMENT, DOC_COMMENT, BLOCK_COMMENT, XML_STYLE_COMMENT, CDATA_START, CDATA_END, XML_STYLE_COMMENT_START, XML_TAG_WHITE_SPACE, JSP_TEXT, JSDOC_TAG_DATA, DOC_COMMENT_TOKEN);
    TokenSet COMMENTS_AND_WHITESPACES = TokenSet.orSet(COMMENTS, PARSER_WHITE_SPACE_TOKENS);
    TokenSet MODIFIERS = TokenSet.create(PUBLIC_KEYWORD, STATIC_KEYWORD, OVERRIDE_KEYWORD, PROTECTED_KEYWORD, PRIVATE_KEYWORD, INTERNAL_KEYWORD, DYNAMIC_KEYWORD, FINAL_KEYWORD, NATIVE_KEYWORD, VIRTUAL_KEYWORD, EXPORT_KEYWORD, DECLARE_KEYWORD);
    TokenSet ACCESS_MODIFIERS = TokenSet.create(PUBLIC_KEYWORD, PROTECTED_KEYWORD, PRIVATE_KEYWORD, INTERNAL_KEYWORD);
    TokenSet VAR_MODIFIERS = TokenSet.create(VAR_KEYWORD, LET_KEYWORD, CONST_KEYWORD);

    TokenSet TEMPLATE_PARTS        = TokenSet.create(STRING_TEMPLATE_PART, BACKQUOTE);
    TokenSet BOOLEAN_LITERALS      = TokenSet.create(TRUE_KEYWORD, FALSE_KEYWORD);
    TokenSet VALUE_LITERALS        = TokenSet.create(NULL_KEYWORD,UNDEFINED_KEYWORD);
    TokenSet NUMERIC_LITERALS      = TokenSet.create(NUMERIC_LITERAL);
    TokenSet REGEXP_LITERALS       = TokenSet.create(REGEXP_LITERAL);
    TokenSet STRING_LITERALS       = TokenSet.create(STRING_LITERAL, SINGLE_QUOTE_STRING_LITERAL);
    TokenSet PRIMARY_LITERALS      = TokenSet.orSet(BOOLEAN_LITERALS, VALUE_LITERALS, STRING_LITERALS, NUMERIC_LITERALS, REGEXP_LITERALS);

    TokenSet ARROWS = TokenSet.create(EQGT, MINUSGT);


    TokenSet KEYWORDS = TokenSet.create(
            ANY_KEYWORD,
            AS_KEYWORD,
            ASYNC_KEYWORD,
            AWAIT_KEYWORD,
            BOOLEAN_KEYWORD,
            BREAK_KEYWORD,
            CASE_KEYWORD,
            CATCH_KEYWORD,
            CLASS_KEYWORD,
            CONST_KEYWORD,
            CONTINUE_KEYWORD,
            DEBUGGER_KEYWORD,
            DECLARE_KEYWORD,
            DEFAULT_KEYWORD,
            DELETE_KEYWORD,
            DO_KEYWORD,
            DYNAMIC_KEYWORD,
            EACH_KEYWORD,
            ELSE_KEYWORD,
            ENUM_KEYWORD,
            EXPORT_KEYWORD,
            EXTENDS_KEYWORD,
            FINAL_KEYWORD,
            FINALLY_KEYWORD,
            FOR_KEYWORD,
            FROM_KEYWORD,
            FUNCTION_KEYWORD,
            GET_KEYWORD,
            IF_KEYWORD,
            IMPLEMENTS_KEYWORD,
            IMPORT_KEYWORD,
            IN_KEYWORD,
            INCLUDE_KEYWORD,
            INSTANCEOF_KEYWORD,
            INT_KEYWORD,
            INTERFACE_KEYWORD,
            INTERNAL_KEYWORD,
            IS_KEYWORD,
            LET_KEYWORD,
            MIXIN_KEYWORD,
            MODULE_KEYWORD,
            NAMESPACE_KEYWORD,
            NATIVE_KEYWORD,
            NEW_KEYWORD,
            NUMBER_KEYWORD,
            OF_KEYWORD,
            OVERRIDE_KEYWORD,
            PACKAGE_KEYWORD,
            PRIVATE_KEYWORD,
            PROTECTED_KEYWORD,
            PUBLIC_KEYWORD,
            REQUIRE_KEYWORD,
            REQUIRES_KEYWORD,
            RETURN_KEYWORD,
            SET_KEYWORD,
            STATIC_KEYWORD,
            STRING_KEYWORD,
            SUPER_KEYWORD,
            SWITCH_KEYWORD,
            THIS_KEYWORD,
            THROW_KEYWORD,
            TRY_KEYWORD,
            TYPE_KEYWORD,
            TYPEOF_KEYWORD,
            UINT_KEYWORD,
            USE_KEYWORD,
            VAR_KEYWORD,
            VIRTUAL_KEYWORD,
            VOID_KEYWORD,
            WHILE_KEYWORD,
            WITH_KEYWORD,
            YIELD_KEYWORD
    );
    TokenSet ACCESSORS = TokenSet.create (
        SET_KEYWORD,
        GET_KEYWORD
    );
    TokenSet IDENTIFIERS = TokenSet.create ( // Fill with allowed identifier names
        IDENTIFIER,
        STATIC_KEYWORD,
        PUBLIC_KEYWORD,
        PROTECTED_KEYWORD
    );
    TokenSet PROPERTY_NAMES = TokenSet.orSet(IDENTIFIERS, TokenSet.create(
        JSTokenTypes.NUMERIC_LITERAL,
        JSTokenTypes.STRING_LITERAL
    ));
}
