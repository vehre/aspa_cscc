/*
    This file is part of Aspa.

    Aspa is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    Aspa is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Aspa; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
header {
<header>
}


class JsParser extends Parser;
options {
  k = 2;
  exportVocab=Js;
  codeGenMakeSwitchThreshold = 2;
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;
  buildAST = true;
}

tokens {
<tokens>
}

<init>

start_rule
  :
  (statement)* EOF! <end>
  ;


compoundStatement
  : lc:LCURLY^ {#lc.setType(SLIST);}
      // include the (possibly-empty) list of statements
      (statement)*
    RCURLY!
  ;


var_assign
  :
  (IDENTIFIER ASSIGN)=> expression | IDENTIFIER
  ;


declaration!
  <var_init>
  :
  VAR^ v:var_assign <add_var> (COMMA! vn:var_assign <add_var2>)* <var>
  ;


ignored! : SEMI | NEW_LINE;

statement
  :
  !(FUNCTION IDENTIFIER)=> FUNCTION^ IDENTIFIER (NEW_LINE!)?
  LPAREN! args:argList RPAREN! (NEW_LINE!)? <function_def>
  stm:compoundStatement <function_declend>
  | compoundStatement
  | declaration ignored
  | expression ignored
  | INCLUDE
  | IDENTIFIER c:COLON^ {#c.setType(LABELED_STAT);} statement
  | if_rule
  | WITH^ LPAREN! expression RPAREN! statement
  | FOR^
      LPAREN!
      (
      (expression IN)=>
          in_statement
          |
          forInit SEMI!   // initializer
          forCond SEMI!   // condition test
          forIter         // updater
      )
      RPAREN!
      statement
  | WHILE^ LPAREN! expression RPAREN! statement
  | DO^ statement WHILE! LPAREN! expression RPAREN! ignored
  | BREAK^ (IDENTIFIER)? ignored
  | CONTINUE^ (IDENTIFIER)? ignored
  | RETURN^ (expression)? ignored
  | SWITCH^ LPAREN! expression RPAREN! LCURLY!  (casesGroup)* RCURLY!
  | tryBlock
  | THROW^ expression ignored
  | SEMI //preserve the empty statement
  | !NEW_LINE
  | HTML
  | EQ_HTML^ expression ignored
  ;


if_rule
  :
  IF^ LPAREN! expression RPAREN! statement
  (
      (ELSE)=> else_rule
      |
  )
  ;


else_rule
  :
  ELSE^ statement
  ;


in_statement
  :
  expression IN^ expression
  ;


casesGroup
  :
  ( // CONFLICT: to which case group do the statements bind?
      //           ANTLR generates proper code: it groups the
      //           many "case"/"default" labels together then
      //           follows them with the statements
      options {
        warnWhenFollowAmbig = false;
      }
      :
      aCase
  )+
  caseSList
  {#casesGroup = #([CASE_GROUP, "CASE_GROUP"], #casesGroup);}
  ;


aCase
  :
  (CASE^ expression | DEFAULT) COLON!
  ;


caseSList
  :
  (statement)*
  {#caseSList = #(#[SLIST,"SLIST"],#caseSList);}
  ;


// The initializer for a for loop
forInit
    // if it looks like a declaration, it is
  :
  (
    declaration
    // otherwise it could be an expression list...
    | expressionList
  )?
  {#forInit = #(#[FOR_INIT,"FOR_INIT"],#forInit);}
  ;


forCond
  :
  (expression)?
  {#forCond = #(#[FOR_CONDITION,"FOR_CONDITION"],#forCond);}
  ;


forIter
  :
  (expressionList)?
  {#forIter = #(#[FOR_ITERATOR,"FOR_ITERATOR"],#forIter);}
  ;


// an exception handler try/catch block
tryBlock
  :
  TRY^ statement
  (
      catchBlock ((FINALLY)=> finallyBlock |)
      | finallyBlock
  )
  ;


catchBlock
  :
  CATCH^ LPAREN! IDENTIFIER RPAREN! statement
  ;


finallyBlock
  :
  FINALLY^ statement
  ;


expression
  :
  a:assignmentExpression
  <expression>
  ;


// This is a list of expressions.
expressionList
  :
  expression (NEW_LINE!)? (COMMA! expression (NEW_LINE!)?)*
  {#expressionList = #(#[ELIST, "ELIST"], expressionList);}
  ;


// assignment expression (level 13)
assignmentExpression
  :
  c:conditionalExpression
    ( ( a:ASSIGN^
            |   PLUS_ASSIGN^
            |   MINUS_ASSIGN^
            |   STAR_ASSIGN^
            |   DIV_ASSIGN^
            |   MOD_ASSIGN^
            |   SR_ASSIGN^
            |   BSR_ASSIGN^
            |   SL_ASSIGN^
            |   BAND_ASSIGN^
            |   BXOR_ASSIGN^
            |   BOR_ASSIGN^
            )
      ax:assignmentExpression <assign>
    )?
  ;


// conditional test (level 12)
conditionalExpression
  :
  logicalOrExpression
  (QUESTION^ assignmentExpression COLON! conditionalExpression)?
  ;


// logical or (||)  (level 11)
logicalOrExpression
  :
  logicalAndExpression (LOR^ logicalAndExpression)*
  ;


// logical and (&&)  (level 10)
logicalAndExpression
  :
  inclusiveOrExpression (LAND^ inclusiveOrExpression)*
  ;


// bitwise or non-short-circuiting or (|)  (level 9)
inclusiveOrExpression
  :
  exclusiveOrExpression (BOR^ exclusiveOrExpression)*
  ;


// exclusive or (^)  (level 8)
exclusiveOrExpression
  :
  andExpression (BXOR^ andExpression)*
  ;


// bitwise or non-short-circuiting and (&)  (level 7)
andExpression
  :
  equalityExpression (BAND^ equalityExpression)*
  ;


// equality/inequality (==/!=/===/!===) (level 6)
equalityExpression
  :
  relationalExpression
  ((NOT_EQUAL^ | EQUAL^ | IDENTICAL^ | NOT_IDENTICAL^) relationalExpression)*
  ;


// boolean relational expressions (level 5)
relationalExpression
  :
  shiftExpression
    ( ( ( LT^
        | GT^
        | LE^
        | GE^
        )
        shiftExpression
      )*
    | INSTANCEOF^ (type | IDENTIFIER)
    )
  ;


// bit shift expressions (level 4)
shiftExpression
  :
  additiveExpression ((SL^ | SR^ | BSR^) additiveExpression)*
  ;


// binary addition/subtraction (level 3)
additiveExpression
  :
  multiplicativeExpression ((PLUS^ | MINUS^) multiplicativeExpression)*
  ;


// multiplication/division/modulo (level 2)
multiplicativeExpression
  :
  unaryExpression ((STAR^ | DIV^ | MOD^ ) unaryExpression)*
  ;


unaryExpression
  :
  INC^ unaryExpression
  | DEC^ unaryExpression
  | MINUS^ {#MINUS.setType(UNARY_MINUS);} unaryExpression
  | PLUS^  {#PLUS.setType(UNARY_PLUS);} unaryExpression
  | unaryExpressionNotPlusMinus
  ;


unaryExpressionNotPlusMinus
  :
  BNOT^ unaryExpression
  | LNOT^ unaryExpression
  | DELETE^ unaryExpression
  | TYPEOF^ (NEW_LINE!)? unaryExpression
  | postfixExpression
  ;


nl !
  :
  (
    NEW_LINE (TYPEOF | INSTANCEOF | DELETE | NEW)
  )=> NEW_LINE
  |
  ;


// qualified names, array expressions, method invocation, post inc/dec
postfixExpression
  :
  LBRACK^ argList RBRACK! //array initilizer
  |
  (
      primaryExpression // start with a primary
      (
        // qualified id (id.id.id.id...) -- build the name
        DOT^  (IDENTIFIER | newExpression) nl
        // an array indexing operation
        | lb:LBRACK^ {#lb.setType(INDEX_OP);} argList RBRACK! nl
        | lp:LPAREN^ {#lp.setType(METHOD_CALL);} argList RPAREN! nl
      )*
      // possibly add on a post-increment or post-decrement.
      // allows INC/DEC on too much, but semantics can check
      (
        in:INC^ {#in.setType(POST_INC);} nl
        | de:DEC^ {#de.setType(POST_DEC);} nl
      )?
  )
  ;


// the basic element of an expression
primaryExpression
  :
    IDENTIFIER
  | newExpression
  | constant
  | LPAREN^ assignmentExpression RPAREN!
  |! FUNCTION^ LPAREN! args:argList RPAREN! <anonym_def>
  cs:compoundStatement <function_end>
  /*
      this introduces a problem when have expressions like
      a = Array
      but is necessery for expressions like Array.prototype.something
  */
  | type
  ;


type
  :
  ARRAY | DATE | OBJECT | STRING | NUMBER | BOOLEAN
  ;


newExpression
  :
  NEW^ (NEW_LINE!)? (type | IDENTIFIER)
  (
      (NEW_LINE)=> NEW_LINE! ((LPAREN)=> LPAREN! argList RPAREN! |)
      | (LPAREN)=> LPAREN! argList RPAREN!
      |
  )
  ;


argList
  :
  (
    expressionList
    | {#argList = #[ELIST,"ELIST"];} /*nothing*/
  )
  ;


constant
  :
  NUM_INT
  | STRING_LITERAL
  | TRUE
  | FALSE
  | NULL
  | UNDEFINED
  | REG_EXP
  | VOID
  | THIS
  ;


//----------------------------------------------------------------------------
// The JavaScript scanner
//----------------------------------------------------------------------------
class JsLexer extends Lexer;

options {
  exportVocab=Js;      // call the vocabulary "Java"
  testLiterals=false;    // don't automatically test for literals
  k=4;                   // four characters of lookahead
  filter=false;
  charVocabulary = '\u0003'..'\u7FFF';
}

{
    private int lastToken = -1;
}

// OPERATORS
//Computational
PLUS      : '+'   SPACES ;
MINUS     : '-'   SPACES ;
STAR      : '*'   SPACES ;
DIV       : '/'   SPACES ;
MOD       : '%'   SPACES ;
INC       : "++"  SPACES ;
DEC       : "--"  SPACES ;

//logical
QUESTION  : '?'   SPACES ;
COLON     : ':'   SPACES ;
EQUAL     : "=="  SPACES ;
GT        : ">"   SPACES ;
GE        : ">="  SPACES ;
IDENTICAL  : "===" SPACES ;
NOT_EQUAL : "!="  SPACES ;
LT        : '<'   SPACES ;
LE        : "<="  SPACES ;
LAND      : "&&"  SPACES ;
LNOT      : '!'   SPACES ;
LOR       : "||"  SPACES ;
NOT_IDENTICAL : "!==" SPACES ;

//bitwise
BAND      : '&'   SPACES ;
SL        : "<<"  SPACES ;
BNOT      : '~'   SPACES ;
BOR       : '|'   SPACES ;
SR        : ">>"  SPACES ;
BXOR      : '^'   SPACES ;
BSR       : ">>>" SPACES ;

//assignment
ASSIGN        : '='    SPACES ;
PLUS_ASSIGN   : "+="   SPACES ;
BAND_ASSIGN   : "&="   SPACES ;
BOR_ASSIGN    : "|="   SPACES ;
BXOR_ASSIGN   : "^="   SPACES ;
DIV_ASSIGN    : "/="   SPACES ;
SL_ASSIGN     : "<<="  SPACES ;
MOD_ASSIGN    : "%="   SPACES ;
STAR_ASSIGN   : "*="   SPACES ;
SR_ASSIGN     : ">>="  SPACES ;
MINUS_ASSIGN  : "-="   SPACES ;
BSR_ASSIGN    : ">>>=" SPACES ;

LPAREN        : '('  SPACES ;
RPAREN        : ')' ;
LBRACK        : '['  SPACES ;
RBRACK        : ']' ;
LCURLY        : '{'  SPACES ;
RCURLY        : '}' ;
COMMA         : ',' SPACES ;
SEMI          : ';';


REG_EXP
  :
  '/'
  COMMON_REG
  (COMMON_REG | INNER_REG)*
  '/'
  ('g' | 'm' | 'i')*
  //this would match a /../ggimm which is invalid reg exp
  //but not interested if the regular expression is valid
  //or not
  ;


//Characters which may exist anywhere in a regular exception
protected COMMON_REG
  :
  'a' .. 'z'
  | 'A' .. 'Z'
  | '0' .. '9'
  | '\\' ~('\r' | '\n' | '\f')
  | '^' | '.' | '(' | ')' | '[' | ']' | '~' | '`' | '@' | '#' | '$'
  | '%' | '&' | '_' | '<' | '>' | ',' | ';'
  ;


//characters which can not be imidiatly after the begining of a regular
//expression: example /*aa/ is invalid since * is a quantifier
protected INNER_REG
  :
  '*'
  | '+' | '?' | ':' | '=' | '!' | '|' | '-' | '\''
  | '"' | ' ' | '\t'
  ;


IDENTIFIER_TYPES
  :
  id:IDENTIFIER {
      /*
        Since ASP uses ActiveX components
        and an ActiveX can have a method or attribute with
        name equal to some keyword, be sure that the last token
        wasn't DOT
      */
      if (lastToken != DOT) {
          String idValue = id.getText();
          Object intVal = JsConstants.KEYWORDS.get(idValue);
          if (intVal != null) {
              _ttype = ((Integer) intVal).intValue();
          } else {
              _ttype = IDENTIFIER;
          }
      } else {
          _ttype = IDENTIFIER;
      }
      lastToken = _ttype;
  }
  ;


NEW_LINE
  :
  LF (LF | WS)*
  {
      $setText("\n");
      switch (LA(1)) {
          case '=':
          case '+':
          case '-':
          case '*':
          case '%':
          case '>':
          case '<':
          case '^':
          case '|':
          case '&':
          case '!':
          case '?':
          case ':':
          case ';':
          case '.':
          $setType(Token.SKIP);
          break;
          case '/':
          if (LA(2) != '/') {
              $setType(Token.SKIP);
          }
          break;
      }
  }
  ;

protected LF
  :
  (
  options {
    generateAmbigWarnings=false;
  }
  :
  "\r\n"  // Evil DOS
  | '\r'    // Macintosh
  | '\n'    // Unix (the right way)
  )
  { newline(); }
  ;


// Whitespace -- ignored
WS
  :
  (
    ' '
    | '\t'
    | '\f'
  )
  { _ttype = Token.SKIP; }
  ;

protected SPACES
  :
  !(WS | LF)*
  ;


// Single-line comments
SL_COMMENT
  : "//"
    (~('\n'|'\r'))* ('\n'|'\r'('\n')?)
    {$setType(Token.SKIP); newline();}
  ;

// multiple-line comments
ML_COMMENT
  : "/*"
    ( /*  '\r' '\n' can be matched in one alternative or by matching
        '\r' in one iteration and '\n' in another.  I am trying to
        handle any flavor of newline that comes in, but the language
        that allows both "\r\n" and "\r" and "\n" to all be valid
        newline is ambiguous.  Consequently, the resulting grammar
        must be ambiguous.  I'm shutting this warning off.
       */
      options {
        generateAmbigWarnings=false;
      }
    :
      { LA(2)!='/' }? '*'
    | '\r' '\n'   {newline();}
    | '\r'      {newline();}
    | '\n'      {newline();}
    | ~('*'|'\n'|'\r')
    )*
    "*/"
    {$setType(Token.SKIP);}
  ;


// string literals
STRING_LITERAL
  :
  '"'! (ESC|~('"'|'\\'))* '"'!
  | '\''! ( ESC | ~'\'' )* '\''!
  ;


// escape sequence -- note that this is protected; it can only be called
//   from another lexer rule -- it will not ever directly return a token to
//   the parser
// There are various ambiguities hushed in this rule.  The optional
// '0'...'9' digit matches should be matched here rather than letting
// them go back to STRING_LITERAL to be matched.  ANTLR does the
// right thing by matching immediately; hence, it's ok to shut off
// the FOLLOW ambig warnings.
protected
ESC
  : '\\'
    ( 'n'
    | 'r'
    | 't'
    | 'b'
    | 'f'
    | '"'
    | '\''
    | '\\'
    | ('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    | ('0'..'3')
      (
        options {
          warnWhenFollowAmbig = false;
        }
      : ('0'..'7')
        (
          options {
            warnWhenFollowAmbig = false;
          }
        : '0'..'7'
        )?
      )?
    | ('4'..'7')
      (
        options {
          warnWhenFollowAmbig = false;
        }
      : ('0'..'9')
      )?
    )
  ;


// hexadecimal digit (again, note it's protected!)
protected
HEX_DIGIT
  :
  ('0'..'9'|'A'..'F'|'a'..'f')
  ;


ASPEND
  :
  "%>" { _ttype = HtmlLexerUtil.ASP_END;}
  ;


JS_END
  :
  "</" s:WORD
  {
      if (!"script".equalsIgnoreCase(s.getText())) {
          throw new RecognitionException("Expected \"script\" at line:"
          + getLine() + " at column:" + getColumn());
      }
   }
  '>'
  { _ttype = HtmlLexerUtil.JS_END;}
  ;


LANGUAGE
  {String lang = null;}
  :
  '@' ("language" '=' (
  	    	l:STRING_LITERAL {lang = l.getText();}
    	  | i:IDENTIFIER   {lang = i.getText();}
   		) {
			$setType(HtmlLexerUtil.LANGUAGE);
			$setText(lang);
		}
	| id:IDENTIFIER ({LA(2) != '>'}? ~'%')* "%>" {
		$setType(HtmlLexerUtil.UNKNOWN_CONTROL);	
	}
  )
  /*w:WORD (' ')*
  {
      if (!"language".equalsIgnoreCase(w.getText())) {
          throw new RecognitionException("Expected \"language\" at line:"
          + getLine() + " at column:" + getColumn());
      }
   }
   '=' (' ')*
   (
      l:STRING_LITERAL {lang = l.getText();}
      | i:IDENTIFIER   {lang = i.getText();}
   ) (' ')*
   {
      $setType(HtmlLexerUtil.LANGUAGE);
      $setText(lang);
   }*/
   ;


protected WORD
  :
  ('a'..'z' | 'A'..'Z')+
  ;


// an identifier.  Note that testLiterals is set to true!  This means
// that after we match the rule, we look in the literals table to see
// if it's a literal or really an identifer
protected IDENTIFIER
  :
  ('a'..'z' | 'A'..'Z' | '_' | '$')
  ('a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '$')*
  ;


// a numeric literal
NUM_INT
  {boolean isDecimal=false;}
  :
  (
  '.' {_ttype = DOT;} SPACES
      (('0'..'9')+ (EXPONENT)? (FLOAT_SUFFIX)? /*{ _ttype = NUM_FLOAT; }*/)?
  | ( '0' {isDecimal = true;} // special case for just '0'
      ( ('x'|'X')
        (                     // hex
          // the 'e'|'E' and float suffix stuff look
          // like hex digits, hence the (...)+ doesn't
          // know when to stop: ambig.  ANTLR resolves
          // it correctly by matching immediately.  It
          // is therefor ok to hush warning.
          options {
            warnWhenFollowAmbig=false;
          }
        : HEX_DIGIT
        )+
      | ('0'..'7')+                 // octal
      )?
    | ('1'..'9') ('0'..'9')*  {isDecimal=true;}   // non-zero decimal
    )
    ( ('l'|'L')

    // only check to see if it's a float if looks like decimal so far
    | {isDecimal}?
      ( '.' ('0'..'9')* (EXPONENT)? (FLOAT_SUFFIX)?
      | EXPONENT (FLOAT_SUFFIX)?
      | FLOAT_SUFFIX
      )
      /*{ _ttype = NUM_FLOAT; }*/
    )?
  )
  {
      lastToken = _ttype;
  }

  ;


// a couple protected methods to assist in matching floating point numbers
protected
EXPONENT
  :
  ('e'|'E')
  ('+'|'-')? ('0'..'9')+
  ;


protected
FLOAT_SUFFIX
  :
  'f'|'F'|'d'|'D'
  ;

