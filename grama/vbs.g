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


class VbsParser extends Parser;

options {
    exportVocab = Vbs;
    buildAST=true;
    defaultErrorHandler=false;
    k = 2;
}



tokens {
    <tokens>
}

<init>


start_rule
  :
  (statement)*
  EOF!
  <end>
  ;


onError
   :
  ON! ERROR^ (
      RESUME NEXT!
      | GOTO! d:DINT <goto_int>
  )
  statement_term
  ;


s_decl
  :
  id:IDENTIFIER^ (LPAREN! <add_array> (DINT (COMMA! DINT)*)? RPAREN!)?
  ;


expression_statement
  :!
  e1:expression <expr_statement>
  (el:expressionList <expr_statement_list>)?
  ;


set_statement
  :
  <tset> SET lset:left_set ASSIGN expr:expression <set>
  ;


left_set
  :
  IDENTIFIER (DOT^ IDENTIFIER)*
  ;

const_statement
  :
  CONST^ IDENTIFIER ASSIGN! relationalImpExpression
  ;

sub_func
  :
  PUBLIC! (
      DEFAULT! <default_matched> (
          dsub:sub_statement <sub_def>
          | dfunc:function_statement <func_def>
      )
      | sub_statement
      | function_statement
  )
  | PRIVATE^ (
      psub:sub_statement
      | pfunc:function_statement
  )
  | sub_statement
  | function_statement
  ;


block_statement
  :
  onError
  | sub_func
  | class_statement
  | if_then_else_statement
  | do_statement
  | while_wend_statement
  | select_case_statement
  | for_statement
  | with_statement
  | (!PRIVATE | PUBLIC) const_statement
  ;


simple_statement
  :
  (DIM^ | REDIM^) s_decl (COMMA! s_decl)*
  |! OPTION EXPLICIT
  | ERASE^ IDENTIFIER
  | CALL! im:IDENTIFIER <call_sub>
      (DOT^ in:IDENTIFIER <call_obj>)*
      LPAREN! (expressionList)? RPAREN!
  | set_statement
  | RANDOMIZE^ (expression)?
  | expression_statement
  | EQ_HTML^ expression
  | const_statement
  | EXIT^ (DO | SUB | FUNCTION | FOR)
  ;


non_term
  :
  !h:HTML <html> //palced here because html does not need an statement_term
  | INCLUDE
  ;


statement
  :
  simple_statement statement_term
  | non_term
  | block_statement
  | statement_term
  ;


pp
  :
  PUBLIC! ({currentClass != null}? (DEFAULT)? |) | PRIVATE
  ;

dim_stm
  :
  DIM^ s_decl (COMMA! s_decl)*
  ;

class_statement
    <class_init>
    :
    CLASS! IDENTIFIER^ <class_start> statement_term
    (
        ((PUBLIC | PRIVATE) IDENTIFIER)=> pp_var
        |! (pp PROPERTY)=> p1:pp p2:property <add_prop>
        |! p3:property <append_prop>
        | DIM! sd:s_decl <add_dim> (COMMA! sd2:s_decl <add_dim2>)*
        | sub_func
        //| set_statement Not sure if allowed
        | cid:IDENTIFIER <add_id>
        | statement_term
    )*
    (!END CLASS) <class_end> statement_term
    ;


exit_prop
    :
    ex:EXIT !PROPERTY <exit_prop>
    ;


property
    :
    PROPERTY! pdecl:prop_decl statement_term
    (statement | exit_prop)*
    (!END PROPERTY) statement_term <property>
    ;


prop_decl
	:
    GET! IDENTIFIER^ ((LPAREN)=> argList |) <prop_get>
    | LET! IDENTIFIER^ argList <prop_let>
    | SET! IDENTIFIER^ argList <prop_set>
	;


pp_var
    :
    (PUBLIC! | PRIVATE^) sd:s_decl <add_dim> (COMMA sd2:s_decl <add_dim2>)*
    statement_term
    ;


do_statement
  <do_init>
  :
  d:DO^
  (
    (
        WHILE! <while_found>
        | UNTIL! <until_found>
    ) logicalExpression <found_while> statement_term
    | statement_term
  )
  (statement)* LOOP!
  ({!whileFound && (LA(1) == WHILE || LA(1) == UNTIL)}? do_while
      | statement_term
  )
  ;


do_while
  :
  (WHILE^ | UNTIL^)  logicalExpression
  ;


for_statement
  :
  FOR^  (for_each | for_step)
  ;


for_each
  :
  each_in (statement)* (!NEXT (IDENTIFIER | statement_term))
  ;


each_in
  :
  EACH! IDENTIFIER IN^ expression
  ;


for_step
  :
  for_identifier (statement)* NEXT!
  ;


for_identifier
  :
  expression for_to (for_stepexpr)?
  <for_identifier>
  ;


for_to
  :
  TO^ expression
  ;


for_stepexpr
  :
  STEP^ expression
  ;


else_single_line
  :
  ELSE^  (simple_statement | non_term) (COLON! (simple_statement | non_term))*
  ;


if_then_else_statement
  :
  kif:IF^ logicalExpression THEN!
  (
      statement_term
      (statement)*
      (elseif)*
      (else_rule)?
      (!END IF statement_term)
      |
      (simple_statement | non_term) (COLON! (simple_statement))*
      (else_single_line)?
      (!(END IF)? STATEMENT_END)
  )
  <if_then_else_statement>
  ;


if_rule
  :
  IF^ logicalExpression THEN! (statement)*
  ;


elseif
  :
  ELSEIF^ logicalExpression THEN! (statement)*
  ;


else_rule
  :
  ELSE^  (statement)*
  ;


select_case_statement
  :
  SELECT^ CASE! expression statement_term
  (case_rule)*
  (case_else)?
  (!END SELECT)
  ;


case_rule
  :
  CASE^ case_list (statement)*
  ;


case_list
  :
  //(DINT | DFLOAT | DSTRING) (COMMA! (DINT | DFLOAT | DSTRING))*
  expression (COMMA! expression)*
  <case_list>
  ;


case_else
  :
  CASE^ ELSE! <case_else> (statement)*
  ;


sub_statement
  :
  SUB! IDENTIFIER^ ((LPAREN)=> argList |) <sub>
  (statement)* <erase_func>
  (!END SUB)
  ;


function_statement
  :
  FUNCTION! IDENTIFIER^ ((LPAREN)=> argList |) <function>
  (statement)* <erase_func>
  (!END FUNCTION)
  ;


while_wend_statement
  :
  WHILE^ logicalExpression (statement)* WEND!
  <while_wend>
  ;


with_statement
  :
  WITH^ expression <with_expr> (
      statement
  )*
  <with_count>
  (!END WITH)
  ;


argList!
  <arg_init>
  :
  LPAREN! (method_arg (COMMA! method_arg)*)? RPAREN!
  ;


method_arg!
  <method_arg_init>
  :
  (BYREF! <byref>| BYVAL!)? id:IDENTIFIER <method_arg>
  ;


expressionList
  :
  assignementExpression (COMMA! assignementExpression)*
  <expression_list>
  ;


expression
  :
  assignementExpression <expression>
  ;


assignementExpression
  :
  left:relationalImpNEQxpression
  (ASSIGN^ relationalImpExpression <check_id>)?
  ;


logicalExpression
  :
  relationalImpExpression <logical_expression>
  ;


relationalImpNEQxpression
  :
  relationalEqvNEQxpression (IMP^  relationalEqvNEQxpression)*
  ;


relationalImpExpression
  :
  relationalEqvExpression (IMP^  relationalEqvExpression)*
  ;


relationalEqvNEQxpression
    :
    relationalXorNEQxpression (EQV^ relationalXorNEQxpression)*
    ;


relationalEqvExpression
  :
  relationalXORExpression (EQV^ relationalXORExpression)*
  ;


relationalXorNEQxpression
    :
    relationalORNEQxpression (XOR^ relationalORNEQxpression)*
    ;


relationalXORExpression
  :
  relationalORExpression  (XOR^ relationalORExpression)*
  ;


relationalORNEQxpression
    :
    relationlANDNEQxpression (OR^ relationlANDNEQxpression)*
    ;


relationalORExpression
  :
  relationalANDExpression   (OR^  relationalANDExpression)*
  ;


relationlANDNEQxpression
    :
    comparassionNEQxpression (AND^ comparassionNEQxpression)*
    ;


relationalANDExpression
  :
  comparassionExpression (AND^ comparassionExpression)*
  ;


comparassionNEQxpression
  :
  stringConcatanateExpression
  (
      (
          LT^
          | GT^
          | LE^
          | GE^
          | NEQ^
          | IS^
      ) stringConcatanateExpression
  )*
  ;


comparassionExpression
  :
  stringConcatanateExpression
  (
      (
          LT^
          | GT^
          | LE^
          | GE^
          | NEQ^
          | IS^
          | ASSIGN^ {#ASSIGN.setType(EQ);}
      ) stringConcatanateExpression
  )*
  ;


stringConcatanateExpression
  :
  addSubExpression (CONCAT^ addSubExpression)*
  ;


addSubExpression
  :
  modExpression (
    /*
    Ambiguety is created because '+' or '-' can be the sign
    of a numeric expression or an arithmetic expression
    Example:
    a + -3
    */
    options {
        warnWhenFollowAmbig = false;
    }
    :
      (PLUS^ | MINUS^) modExpression
  )*
  ;


modExpression
    :
    intDivideExpression (MOD^ intDivideExpression)*
    ;


intDivideExpression
    :
    multiplicativeExpression (INT_DIVIDE^ multiplicativeExpression)*
    ;


multiplicativeExpression
  :
  expExpression
  ((ASTERISK^ | DIVIDE^) expExpression)*
  ;


expExpression
  :
  unaryExpression (POW^  unaryExpression)*
  ;


unaryExpression
  :
  MINUS^ <unary_minus> unaryExpression
  | PLUS^ <unary_plus> unaryExpression
  | NOT^ unaryExpression
  | withExpression
  ;


withExpression
    :
    {inWith && LA(1) == DOT}?
    DOT^ <with_dot> postFixExpression
    | postFixExpression
    ;


postFixExpression
  <array_init>
  :
  <array_lastid>
  primaryExpression
  (
    /*
    We have 2 non deterministic cases in these rules
    1)When inside "with" command expressions can start with '.'
    like
    with obj
        .height = 30
        .width = 40
        obj.height = 40
        a = .items.count
    end with
    On the other hand, expressions can have dot as well like "obj.height"
    It seems to work well.
    2)LPAREN can apper inside expression like obj.method()
    or as part of a nested expression like a * (b + c).
    This is handled correctly as well.
    */
    options {
        warnWhenFollowAmbig = false;
    }
    :
    DOT^ IDENTIFIER
    | LPAREN^ <array_lparen>
    ({isArray}? indexList | (expressionList)?)
    RPAREN!
  )*
  ;


indexList
  :
  relationalImpExpression (COMMA! relationalImpExpression)*
  <index_list>
  ;


primaryExpression
  :
  DINT
  | DFLOAT
  | DSTRING
  | DDATE
  | TRUE
  | FALSE
  | EMPTY
  | NULL
  | NOTHING
  | id:IDENTIFIER <set_lastid>
  | NEW^ IDENTIFIER
  | LPAREN^ relationalImpExpression RPAREN!
  ;


statement_term!
    :
    STATEMENT_END | COLON
    ;

/*
  Start of VbsLexer implementation
*/

class VbsLexer extends Lexer;

options {
    exportVocab = Vbs;
    testLiterals = false;
    k = 3;
    caseSensitive = false;
    caseSensitiveLiterals = false;
    filter = false;
    charVocabulary = '\u0003'..'\u7FFF';
}

<lexer_init>

protected EXPONENT
	:
  ('e') ('+'|'-')? ('0'..'9')+
	;


protected FLT_SUFFIX
	:
	'f'|'d'
	;


INT_CONST
  :
  (
      '.' <lexer_typedot>
      (
          ('0'..'9')+ (EXPONENT)? (FLT_SUFFIX)? <lexer_typefloat>
      )?
      | (DIGIT)+ <lexer_typeint> ('.' (DIGIT)+ (EXPONENT)? (FLT_SUFFIX)? <lexer_typefloat>)?
      | '&' <lexer_typeconcat> (
        'h' (HEX_DIGIT)+ <lexer_typeint>
        | 'o' ('0' .. '7')+ <lexer_typeint>
      )?
  )
  <lexer_lasttoken>
  ;


DSTRING
  :
  '"'!
  (
      {LA(2) == '"'}? '"'! '"' | ~('"' | '\n' | '\r')
  )*
  '"'!
  ;


DDATE
  :
  '#'! (~('#' | '\n' | '\f'))* '#'!
  ;


COMMA : ',' ;
LPAREN : '(' ;
RPAREN : ')' ;


/* Operators */

ASTERISK : '*' ;
PLUS : '+' ;
MINUS : '-' ;
DIVIDE : '/' ;
INT_DIVIDE: '\\';
ASSIGN : '=' <lexer_lasttoken_assign>;
GT: '>' ;
LT : '<' ;
GE: ">=" ;
LE : "<="  ;
NEQ : "<>" ;
POW : '^' ;
COLON : ':' <lexer_lasttoken_none>;

CONTINUE_STAT : '_' (WS!)? <lexer_lasttoken_stat>;


IDENTIFIER_TYPES : id:IDENTIFIER <lexer_identifier>;


ASP_END : "%>" <lexer_aspend>;


VBS_END : "</script>" <lexer_vbsend>;


LANGUAGE
  <lexer_langinit>
  :
  '@' (IGNORED!)* "language" (IGNORED!)* '=' (IGNORED!)*
  (
      '"' i:IDENTIFIER '"' <lexer_langi>
      | j:IDENTIFIER <lexer_langj>
  )
  (IGNORED!)* <lexer_langend>
  ;


WS : ( '\r' '\n' | '\n' | '\r') <lexer_ws>;


protected DIGIT
  :
  '0' .. '9'
  ;


protected
HEX_DIGIT
	:	'0'..'9' | 'a'..'f'
	;


protected LETTER
  :
    'a' .. 'z'
  ;


protected IDENTIFIER
  :
  LETTER ( LETTER | DIGIT | '_' )*
  ;


protected LINE : (~'\n')* '\n' <lexer_line> ;


COMMENT
  :
  '\''
  LINE <lexer_comment>
  ;


IGNORED
  :
  (' ' | '\t') <lexer_ignored>
  ;
