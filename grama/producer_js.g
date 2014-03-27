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
package gr.omadak.leviathan.asp;
<import>
}

class JsGenerator extends TreeParser;
options {
    importVocab = TreeJs;
    buildAST=false;
    defaultErrorHandler=false;
    classHeaderSuffix = "CodeGenerator";
}

<init>


generate
  :
  (HTML <html> |
  <script_begin> statements <script_end>)*
  ;

statements
  :
  (statement)+
  ;

statement
  :
  expr <expr_end>
  | INCLUDE <include>
  | SCRIPT <script>
  | #(EQ_HTML <eq_html> expr) <eq_html_end>
  | #(BREAK (num:NUM_INT)?) <break>
  | #(CONTINUE (cnum:NUM_INT)?) <continue>
  | #(RETURN <return> (expr)?) <return_end>
  | #(THROW expr)
  | nested
  | SEMI <semi>
  ;


/*
  This rule is declared separate from nested rule in order to keep a local
  variable.
  Do not merge it with nested rule.
*/
slist
 :
 #(SLIST <slist> (statement)* <slist_end>)
 ;


nested
  :
  slist
  | #(FUNCTION <func> (arglist)? <arg_end>
      (#(GLOBAL <global> (IDENTIFIER <global_id>)+) <glob_end>)?
      statement <func_end>)
  | #(IF_ELSE
      #(IF <if> expr <if_expr> statement)
      (#(ELSEIF <elseif> expr <elseif_expr> statement))*
      (#(ELSE <else> statement))?
      <if_end>
    )
  | #(FOR (
          #(IN <in> expr <in_expr> expr <in_end>)
          | <for> #(FOR_INIT (exprlist)? <forinit_end>)
            #(FOR_CONDITION (expr)? <for_cond_end>)
            #(FOR_ITERATOR (exprlist)? <for_it_end>)
          )
          statement
    )
  | #(WHILE <while> expr <while_end> statement)
  | #(DO <do> statement <in_while> expr <out_while>)
  | #(SWITCH <switch> expr <switch_expr> (casesGroup)*)
  | #(TRY statement
      (
          #(CATCH IDENTIFIER statement) (#(FINALLY statement))?
          | #(FINALLY statement)
      )
    )
  | #(CLASS <class> (#(VAR IDENTIFIER) <var>)* (statement)* <class_end>)
  ;


casesGroup
  :
  #(CASE_GROUP
      (#(CASE <case> expr <case_expr>) | DEFAULT <default>)+
      #(SLIST <case_stm> (statement)* <case_stmend>)
  )
  ;


arglist
  :
  #(ELIST <elist> (IDENTIFIER <elist_id>)+)
  ;


exprlist
  :
  expr (<pre_exprlist> expr)*
  ;


expr
  :
  #(EXPR expression)
  ;


expression
  :
  #(QUESTION expression <question> expression <colon> expression)
  | #(CAST (array:ARRAY
          | obj:OBJECT
          | str:STRING
          | num:NUMBER
          | bool:BOOLEAN
          | id:IDENTIFIER)
      <cast> expression)
  | #(ASSIGN expression <assign> expression)
  | #(PLUS_ASSIGN expression <plus_assign> expression)
  | #(MINUS_ASSIGN expression <min_assign> expression)
  | #(STAR_ASSIGN expression <star_assign> expression)
  | #(DIV_ASSIGN expression <div_assign> expression)
  | #(MOD_ASSIGN expression <mod_assign> expression)
  | #(SR_ASSIGN expression <sr_assign> expression)
  | #(BSR_ASSIGN expression <bsr_assign> expression)
  | #(SL_ASSIGN expression <sl_assign> expression)
  | #(BAND_ASSIGN expression <band_assign> expression)
  | #(BXOR_ASSIGN expression <bxor_assign> expression)
  | #(BOR_ASSIGN expression <bor_assign> expression)
  | #(LAND expression <land> expression)
  | #(LOR expression <lor> expression)
  | #(BOR expression <bor> expression)
  | #(BXOR expression <bxor> expression)
  | #(BAND expression <band> expression)
  | #(NOT_EQUAL expression <neq> expression)
  | #(EQUAL expression <eq> expression)
  | #(IDENTICAL expression <identical> expression)
  | #(NOT_IDENTICAL expression <nidentical> expression)
  | #(LT expression <lt> expression)
  | #(GT expression <gt> expression)
  | #(LE expression <le> expression)
  | #(GE expression <ge> expression)
  | #(SL expression <sl> expression)
  | #(SR expression <sr> expression)
  | #(BSR expression <bsr> expression)
  | #(PLUS expression <plus> expression)
  | #(MINUS expression <min> expression)
  | #(STAR expression <star> expression)
  | #(DIV expression <div> expression)
  | #(MOD expression <mod> expression)
  | #(DOT expression <dot> expression)
  | #(INC <inc> expression)
  | #(DEC <dec> expression)
  | #(BNOT <bnot> expression)
  | #(LNOT <lnot> expression)
  | #(POST_INC expression <inc>)
  | #(POST_DEC expression <dec>)
  | #(NEW <new> expression)
  | #(METHOD_CALL <method_call> (#(ELIST <argvals>
      (<arg_val> expression)*))? <method_end>)
  | #(UNRESOLVED_METHOD <unresolved_method> (#(ELIST <argvals>
      (<arg_val> expression)*))? <method_end>)
  | #(INDEX_OP expression (<pre_op> expression <post_op>)+)
  | #(LCURLY expression <precurl> expression <postcurl>)
  | #(UNARY_MINUS expression)
  | #(UNARY_PLUS expression)
  | #(LPAREN <lp> expression <lp_end>)
  | #(CONCAT expression <concat> expression)
  | IDENTIFIER <id>
  | TRUE <true>
  | FALSE <false>
  | NULL <null>
  | UNDEFINED <null>
  | REG_EXP <reg>
  | NUM_INT <number>
  | STRING_LITERAL <string>
  | OBJECT_ATTRIBUTE <attr>
  | NON_APPLICABLE_HEADER <nhead>
  | CONSTANT <const>
  | UNSUPORTED <unsup>
  ;
