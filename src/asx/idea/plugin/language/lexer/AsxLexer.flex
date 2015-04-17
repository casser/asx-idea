/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package asx.idea.plugin.language.lexer;

import asx.idea.plugin.language.AsxTokenTypes;
import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;

@SuppressWarnings({"ALL"})
%%

%{
  public AsxLexer() {
    this((java.io.Reader)null);
  }

  public void goTo(int offset) {
    zzCurrentPos = zzMarkedPos = zzStartRead = offset;
    zzPushbackPos = 0;
    zzAtEOF = offset < zzEndRead;
  }
%}

%unicode
%class AsxLexer
%implements FlexLexer
%function advance
%type IElementType
%eof{  return;
%eof}

WHITE_SPACE_CHAR=[\ \n\r\t\f]

IDENTIFIER=[:jletter:] [:jletterdigit:]*

BLOCK_COMMENT=("/*"[^"*"]{COMMENT_TAIL})|"/*"
DOC_COMMENT="/*""*"+("/"|([^"/""*"]{COMMENT_TAIL}))?
COMMENT_TAIL=([^"*"]*("*"+[^"*""/"])?)*("*"+"/")?
LINE_COMMENT="/""/"[^\r\n]*

DIGIT = [0-9]
DIGIT_OR_UNDERSCORE = [_0-9]
DIGITS = {DIGIT} | {DIGIT} {DIGIT_OR_UNDERSCORE}*
HEX_DIGIT_OR_UNDERSCORE = [_0-9A-Fa-f]

INTEGER_LITERAL = {DIGITS} | {HEX_INTEGER_LITERAL} | {BIN_INTEGER_LITERAL}
LONG_LITERAL = {INTEGER_LITERAL} [Ll]
HEX_INTEGER_LITERAL = 0 [Xx] {HEX_DIGIT_OR_UNDERSCORE}*
BIN_INTEGER_LITERAL = 0 [Bb] {DIGIT_OR_UNDERSCORE}*

FLOAT_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Ff] | {DIGITS} [Ff]
DOUBLE_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Dd]? | {DIGITS} [Dd]
DEC_FP_LITERAL = {DIGITS} {DEC_EXPONENT} | {DEC_SIGNIFICAND} {DEC_EXPONENT}?
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT_OR_UNDERSCORE}*
DEC_EXPONENT = [Ee] [+-]? {DIGIT_OR_UNDERSCORE}*
HEX_FP_LITERAL = {HEX_SIGNIFICAND} {HEX_EXPONENT}
HEX_SIGNIFICAND = 0 [Xx] ({HEX_DIGIT_OR_UNDERSCORE}+ "."? | {HEX_DIGIT_OR_UNDERSCORE}* "." {HEX_DIGIT_OR_UNDERSCORE}+)
HEX_EXPONENT = [Pp] [+-]? {DIGIT_OR_UNDERSCORE}*

SINGLE_STRING="'"([^\\\'\r\n]|{ESCAPE_SEQUENCE})*("'"|\\)?
STRING_LITERAL=\"([^\\\"\r\n]|{ESCAPE_SEQUENCE})*(\"|\\)?
ESCAPE_SEQUENCE=\\[^\r\n]

%%

<YYINITIAL> {WHITE_SPACE_CHAR}+ { return AsxTokenTypes.WHITE_SPACE; }
<YYINITIAL> {BLOCK_COMMENT}     { return AsxTokenTypes.BLOCK_COMMENT; }
<YYINITIAL> {LINE_COMMENT}      { return AsxTokenTypes.LINE_COMMENT; }
<YYINITIAL> {DOC_COMMENT}       { return AsxTokenTypes.DOC_COMMENT; }

<YYINITIAL> {LONG_LITERAL}      { return AsxTokenTypes.LONG_LITERAL; }
<YYINITIAL> {INTEGER_LITERAL}   { return AsxTokenTypes.INTEGER_LITERAL; }
<YYINITIAL> {FLOAT_LITERAL}     { return AsxTokenTypes.FLOAT_LITERAL; }
<YYINITIAL> {DOUBLE_LITERAL}    { return AsxTokenTypes.DOUBLE_LITERAL; }

<YYINITIAL> {SINGLE_STRING} { return AsxTokenTypes.CHARACTER_LITERAL; }
<YYINITIAL> {STRING_LITERAL}    { return AsxTokenTypes.STRING_LITERAL; }

<YYINITIAL> "true"              { return AsxTokenTypes.TRUE_KEYWORD; }
<YYINITIAL> "false"             { return AsxTokenTypes.FALSE_KEYWORD; }
<YYINITIAL> "null"              { return AsxTokenTypes.NULL_KEYWORD; }
<YYINITIAL> "undefined"         { return AsxTokenTypes.UNDEFINED_KEYWORD; }

<YYINITIAL> "long"              { return AsxTokenTypes.LONG_KEYWORD; }

<YYINITIAL> "abstract"          { return AsxTokenTypes.ABSTRACT_KEYWORD; }
<YYINITIAL> "assert"            { return AsxTokenTypes.ASSERT_KEYWORD ; }
<YYINITIAL> "boolean"           { return AsxTokenTypes.BOOLEAN_KEYWORD; }
<YYINITIAL> "break"             { return AsxTokenTypes.BREAK_KEYWORD; }
<YYINITIAL> "byte"              { return AsxTokenTypes.BYTE_KEYWORD; }
<YYINITIAL> "case"              { return AsxTokenTypes.CASE_KEYWORD; }
<YYINITIAL> "catch"             { return AsxTokenTypes.CATCH_KEYWORD; }
<YYINITIAL> "char"              { return AsxTokenTypes.CHAR_KEYWORD; }
<YYINITIAL> "class"             { return AsxTokenTypes.CLASS_KEYWORD; }
<YYINITIAL> "const"             { return AsxTokenTypes.CONST_KEYWORD; }
<YYINITIAL> "continue"          { return AsxTokenTypes.CONTINUE_KEYWORD; }
<YYINITIAL> "default"           { return AsxTokenTypes.DEFAULT_KEYWORD; }
<YYINITIAL> "do"                { return AsxTokenTypes.DO_KEYWORD; }
<YYINITIAL> "double"            { return AsxTokenTypes.DOUBLE_KEYWORD; }
<YYINITIAL> "else"              { return AsxTokenTypes.ELSE_KEYWORD; }
<YYINITIAL> "enum"              { return AsxTokenTypes.ENUM_KEYWORD; }
<YYINITIAL> "extends"           { return AsxTokenTypes.EXTENDS_KEYWORD; }
<YYINITIAL> "final"             { return AsxTokenTypes.FINAL_KEYWORD; }
<YYINITIAL> "finally"           { return AsxTokenTypes.FINALLY_KEYWORD; }
<YYINITIAL> "float"             { return AsxTokenTypes.FLOAT_KEYWORD; }
<YYINITIAL> "for"               { return AsxTokenTypes.FOR_KEYWORD; }
<YYINITIAL> "if"                { return AsxTokenTypes.IF_KEYWORD; }
<YYINITIAL> "implements"        { return AsxTokenTypes.IMPLEMENTS_KEYWORD; }
<YYINITIAL> "import"            { return AsxTokenTypes.IMPORT_KEYWORD; }
<YYINITIAL> "instanceof"        { return AsxTokenTypes.INSTANCEOF_KEYWORD; }
<YYINITIAL> "int"               { return AsxTokenTypes.INT_KEYWORD; }
<YYINITIAL> "interface"         { return AsxTokenTypes.INTERFACE_KEYWORD; }
<YYINITIAL> "native"            { return AsxTokenTypes.NATIVE_KEYWORD; }
<YYINITIAL> "new"               { return AsxTokenTypes.NEW_KEYWORD; }
<YYINITIAL> "package"           { return AsxTokenTypes.PACKAGE_KEYWORD; }
<YYINITIAL> "private"           { return AsxTokenTypes.PRIVATE_KEYWORD; }
<YYINITIAL> "public"            { return AsxTokenTypes.PUBLIC_KEYWORD; }
<YYINITIAL> "short"             { return AsxTokenTypes.SHORT_KEYWORD; }
<YYINITIAL> "super"             { return AsxTokenTypes.SUPER_KEYWORD; }
<YYINITIAL> "switch"            { return AsxTokenTypes.SWITCH_KEYWORD; }
<YYINITIAL> "synchronized"      { return AsxTokenTypes.SYNCHRONIZED_KEYWORD; }
<YYINITIAL> "this"              { return AsxTokenTypes.THIS_KEYWORD; }
<YYINITIAL> "throw"             { return AsxTokenTypes.THROW_KEYWORD; }
<YYINITIAL> "protected"         { return AsxTokenTypes.PROTECTED_KEYWORD; }
<YYINITIAL> "transient"         { return AsxTokenTypes.TRANSIENT_KEYWORD; }
<YYINITIAL> "return"            { return AsxTokenTypes.RETURN_KEYWORD; }
<YYINITIAL> "void"              { return AsxTokenTypes.VOID_KEYWORD; }
<YYINITIAL> "static"            { return AsxTokenTypes.STATIC_KEYWORD; }
<YYINITIAL> "strictfp"          { return AsxTokenTypes.STRICTFP_KEYWORD; }
<YYINITIAL> "while"             { return AsxTokenTypes.WHILE_KEYWORD; }
<YYINITIAL> "try"               { return AsxTokenTypes.TRY_KEYWORD; }
<YYINITIAL> "volatile"          { return AsxTokenTypes.VOLATILE_KEYWORD; }
<YYINITIAL> "throws"            { return AsxTokenTypes.THROWS_KEYWORD; }

<YYINITIAL> {IDENTIFIER}        { return AsxTokenTypes.IDENTIFIER; }

<YYINITIAL> "=="                { return AsxTokenTypes.EQEQ; }
<YYINITIAL> "!="                { return AsxTokenTypes.NE; }
<YYINITIAL> "||"                { return AsxTokenTypes.OROR; }
<YYINITIAL> "++"                { return AsxTokenTypes.PLUSPLUS; }
<YYINITIAL> "--"                { return AsxTokenTypes.MINUSMINUS; }

<YYINITIAL> "<"                 { return AsxTokenTypes.LT; }
<YYINITIAL> "<="                { return AsxTokenTypes.LE; }
<YYINITIAL> "<<="               { return AsxTokenTypes.LTLTEQ; }
<YYINITIAL> "<<"                { return AsxTokenTypes.LTLT; }
<YYINITIAL> ">"                 { return AsxTokenTypes.GT; }
<YYINITIAL> "&"                 { return AsxTokenTypes.AND; }
<YYINITIAL> "&&"                { return AsxTokenTypes.ANDAND; }

<YYINITIAL> "+="                { return AsxTokenTypes.PLUSEQ; }
<YYINITIAL> "-="                { return AsxTokenTypes.MINUSEQ; }
<YYINITIAL> "*="                { return AsxTokenTypes.ASTERISKEQ; }
<YYINITIAL> "/="                { return AsxTokenTypes.DIVEQ; }
<YYINITIAL> "&="                { return AsxTokenTypes.ANDEQ; }
<YYINITIAL> "|="                { return AsxTokenTypes.OREQ; }
<YYINITIAL> "^="                { return AsxTokenTypes.XOREQ; }
<YYINITIAL> "%="                { return AsxTokenTypes.PERCEQ; }

<YYINITIAL> "("                 { return AsxTokenTypes.LPARENTH; }
<YYINITIAL> ")"                 { return AsxTokenTypes.RPARENTH; }
<YYINITIAL> "{"                 { return AsxTokenTypes.LBRACE; }
<YYINITIAL> "}"                 { return AsxTokenTypes.RBRACE; }
<YYINITIAL> "["                 { return AsxTokenTypes.LBRACKET; }
<YYINITIAL> "]"                 { return AsxTokenTypes.RBRACKET; }
<YYINITIAL> ";"                 { return AsxTokenTypes.SEMICOLON; }
<YYINITIAL> ","                 { return AsxTokenTypes.COMMA; }
<YYINITIAL> "..."               { return AsxTokenTypes.ELLIPSIS; }
<YYINITIAL> "."                 { return AsxTokenTypes.DOT; }

<YYINITIAL> "="                 { return AsxTokenTypes.EQ; }
<YYINITIAL> "!"                 { return AsxTokenTypes.EXCL; }
<YYINITIAL> "~"                 { return AsxTokenTypes.TILDE; }
<YYINITIAL> "?"                 { return AsxTokenTypes.QUEST; }
<YYINITIAL> ":"                 { return AsxTokenTypes.COLON; }
<YYINITIAL> "+"                 { return AsxTokenTypes.PLUS; }
<YYINITIAL> "-"                 { return AsxTokenTypes.MINUS; }
<YYINITIAL> "*"                 { return AsxTokenTypes.ASTERISK; }
<YYINITIAL> "/"                 { return AsxTokenTypes.DIV; }
<YYINITIAL> "|"                 { return AsxTokenTypes.OR; }
<YYINITIAL> "^"                 { return AsxTokenTypes.XOR; }
<YYINITIAL> "%"                 { return AsxTokenTypes.PERC; }
<YYINITIAL> "@"                 { return AsxTokenTypes.AT; }

<YYINITIAL> "::"                { return AsxTokenTypes.DOUBLE_COLON; }
<YYINITIAL> "->"                { return AsxTokenTypes.ARROW; }

<YYINITIAL> .                   { return AsxTokenTypes.BAD_CHARACTER; }