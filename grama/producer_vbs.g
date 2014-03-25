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

class VbsGenerator extends TreeParser;
options {
    importVocab = TreeVbs;
    buildAST=false;
    defaultErrorHandler=false;
    classHeaderSuffix = "CodeGenerator";
}

<init>

generate
	:
	(HTML <html>
	| (<script_begin> statements <script_end>))*
	;

  	
statements
	:
	(statement)+
	;
	
function
  :
  #(FUNCTION <function>
      (#(ARGLIST <arglist_init> (BYREF <byref> | IDENTIFIER <arg_id>)+))?
      <arglist_end>
      (#(GLOBALS <glob_init> (IDENTIFIER <id_glob>)+))? <func_end> statements
  )
  ;


nested
  :
  #(IF_ELSE
      #(IF <if> expr <if_expr> statements)
      (#(ELSEIF <elseif> expr <elseif_expr> statements))*
      (#(ELSE <else> statements))?
  )
  | #(WHILE <while> expr <while_expr> statements)
  | function
  | #(SELECT <switch> expr <switch_end>
      (select_case)*
      (#(CASE_ELSE <default> statements <case_end>))?
    )
  | #(FOR #(FOR_INIT <finit>
          expr <fexpr1>
          expr <fexpr1>
          expr <fexpr3>)
      statements
  )
  | #(FOR_EACH <foreach> #(FOR_INIT expr <foreach_expr>
          expr <foreach_end>) statements
  )
  | #(CLASS <class> (IDENTIFIER <cid> | function <nested_end>)* <class_end>)
  ;


select_case
  :
  #(CASE <case> expr <case_expr_end>
       statements <case_end>)
  ;

statement
  :
  INCLUDE <include>
  | #(EQ_HTML <eq_html> expr)
  | #(DO <do> statements #(DO_END <do_end> expr <do_expr>))
  | nested <nested_end>
  | BREAK <break>
  | CONTINUE <continue>
  | #(ERROR (RESUME | DINT))
  | #(RETURN <return> (expr)?) <exp_end>
  | expr <exp_end>
  | #(VAR <var_decl> (<next_var> expr)+) <exp_end>
  | #(CONST <const_decl> (<next_const> expr)+) <exp_end>
  ;
  
expr
  :
  #(EXPR expression)
  ;


expression
  :
  #(XOR expression <xor> expression)
  | #(OR expression <or> expression)
  | #(AND expression <and> expression)
  | #(BOR expression <bor> expression)
  | #(BAND expression <band> expression)
  | #(BXOR expression <bxor> expression)
  | #(LT expression <lt> expression)
  | #(GT expression <gt> expression)
  | #(LE expression <le> expression)
  | #(GE expression <ge> expression)
  | #(EQ expression <eq> expression)
  | #(ASSIGN expression <assign> expression)
  | #(CONCAT_ASSIGN expression <cassign> expression)
  | #(PLUS_ASSIGN expression <passign> expression)
  | #(MINUS_ASSIGN expression <minassign> expression)
  | #(MOD_ASSIGN expression <modassign> expression)
  | #(STAR_ASSIGN expression <sassign> expression)
  | #(DIV_ASSIGN expression <dassign> expression)
  | #(NEQ expression <neq> expression)
  | #(IS expression <is> expression) //check this
  | #(CONCAT expression <concat> expression)
  | #(PLUS expression <plus> expression)
  | #(MINUS expression <minus> expression)
  | #(MOD expression <mod> expression)
  | #(ASTERISK expression <star> expression)
  | #(DIVIDE expression <div> expression)
  | #(UNARY_PLUS <uplas> expression)
  | #(UNARY_MINUS <umin> expression)
  | #(NOT <not> expression)
  | #(BNOT <bnot> expression)
  | #(METHOD_CALL <method> (arglist_values | <no_args>))
  | #(OBJECT_RET <CCobject_ret>)
  | #(OBJECT <CCobject>)
  | #(CONDITIONAL expression <quest> expression <alt> expression)
  | #(CAST <cast> expression)
  | #(INDEX_OP iex:expression 
      (
          #(INDEX_LIST (<pre_index> expression <post_index>)+)
          | (<pre_index> index:expression <post_index>)+
      )
  )
  | #(UKNOWN_METHOD <uknown_method> (expression | arglist_values | <no_args>))
  | #(POST_PLUS expression <post_plus>)
  | #(POST_MINUS expression <post_minus>)
  | #(DOT expression <dot> expression)
  | #(LPAREN <lp> expression <lp_end>)
  | #(NEW IDENTIFIER <new> (arglist_values)?)
  | DINT <int>
  | DFLOAT <float>
  | IDENTIFIER <id>
  | DSTRING <string>
  | TRUE <true>
  | FALSE <false>
  | NULL <null>
  | EMPTY <null>
  | DDATE <date>
  | NON_APPLICABLE_HEADER <nhead>
  | INVALID_OBJECT <inv_obj>
  | CONSTANT <const>
  | EMBEDDED_ASP <embedded_asp>
  ;
  exception // for rule
  catch [NoViableAltException ex] {
  	LOG.error(ex.toString());
  }

arglist_values
  :
  #(ARGLIST_VALUES <arg_init> (<arg> expression)+ <arg_end>)
  ;
