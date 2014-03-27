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

class VbsTree extends TreeParser;
options {
    importVocab = Vbs;
    exportVocab = TreeVbs;
    buildAST=true;
    defaultErrorHandler=false;
    classHeaderSuffix = "SymbolTableExposer";
}

tokens {
    <tokens>
}
<init>

start_rule
  <start>
  :
  statements
  ;

statements
  <statements_init>
  :
  !(stm:statement <statement>)*
  ;

statement
  <init_stat>
  :
  HTML
  | include:INCLUDE <include>
  | script:SCRIPT <script>
  | expr
  |<tconst> #(CONST <const_init> (ci:IDENTIFIER cex:expression <const_decl>)+ <const_end>)
  |<trand> #(RANDOMIZE (ex:expr)? <randomize>)
  |<tdim> #(DIM <dim_init> (decl:s_decl <dim_decl>)+ <dim_end>)
  |<tredim> #(REDIM (re_decl:redim_decl <re_decl>)+) <redim_end>
  |<tredimp> #(REDIM_PRESERVE (redim_decl)+)
  |<terase>#(ERASE ei:IDENTIFIER <erase>)
  | #(ERROR (RESUME | ZERO))
  |<texit> #(EXIT
      (
          DO <exit_do>
          | FOR <exit_for>
          | SUB <exit_sub>
          | FUNCTION <exit_func>
      )
  )
  |<tml> #(EQ_HTML mlexpr:expr <eq_end>)
  | sub_func
  | #(PRIVATE sub_func)
  |<tsub> #(SUB_CALL sub_name:expression <sub_call>
            (argListValues)?
      <sub_call_end>)
  | nested <nested_end>
  ;

sub_func
  :
  sub_statement | function_statement
  ;

sub_statement
  :
  #(s:SUB <sub_decl> statements <sub_end>)
  ;

function_statement
  :
  <tfunc> #(f:FUNCTION <func_decl> fst:statements <func_end>)
  ;

nested
  <nested>
  :
  #(IF_ELSE
      #(IF expr statements)
      (#(ELSEIF expr statements))*
      (#(ELSE  statements))?
  )
  |<tdw> #(DO_WHILE do_while_expr:expr dws:statements <do_while>)
  |<tdu> #(DO_UNTIL do_until_expr:expr <du_expr> dus:statements <do_until>)
  |<tdo> #(DO dost:statements
      (
          #(WHILE wh:expr <wh_condition>)
          | #(UNTIL unt:expr <u_condition>)
          | <do_plain>
      )
    )
  | #(wend:WHILE_WEND  wexpr:expr statements <while_wend_end>)
  |<twith> #(WITH with_expr:expr <with_expr_end> statements:statements
      <with_end>)
  | #(select:SELECT expr
          (case_stm)*
          (case_else)?
    ) <select_end>
  |<tfor>#(FOR <for>
      (
        #(FOR_INIT fInit:expr
            #(TO toexpr:expr)
            (#(STEP stpexpr:expr <step>))?
            <for_init>)
        | #(IN inid:IDENTIFIER inexpr:expr)
      ) fstm:statements
    )
  <for_end>
  | class_decl
  ;


priv_mem
  :
  <tprivmem> #(PRIVATE (id:IDENTIFIER | s:sub_func) <priv_mem>)
  ;


prop_stm [int propType]
  :
  EXIT_PROPERTY <exit_prop>
  | statement
  ;


prop_stms [int propType]
  :
  (prop_stm[propType])*
  ;


class_decl
  <class_init>
  :
  <tclass>#(c:CLASS <class_start>
      (
          pmem:priv_mem <pmem>
          | #(pg:PROPERTY_GET <pg_init> gst:prop_stm[0] <pget>)
          | #(pl:PROPERTY_LET <pl_init> lst:prop_stm[1] <plet>)
          | #(ps:PROPERTY_SET <ps_init> sst:prop_stm[2] <pset>)
          | id:IDENTIFIER <cid>
          | subfunc:sub_func <csub>
      )*
  <class_end>
  )
  ;


case_stm
  <case_list_init>
  :<tcsmt>#(CASE
      #(CASE_LIST (ccase:expr <case>)+)
      st:statements <case_end>
  )
  ;


case_else
  :<tcel>
  #(cel:CASE_ELSE st:statements <case_else_end>)
  ;

s_decl
  :
  #(ASSIGN sub_s_decl expr) | sub_s_decl
  ;
  
sub_s_decl
  :
  IDENTIFIER |<tarray> #(ARRAY (ex:expr <inc_dim>)*) <array_decl> 
  ;


redim_decl
  <redim_decl>
  :
  #(ARRAY (ex:expr <redim_inc>)+) <redim>
  ;


expr
  :
  <texpr> #(EXPR e:expression <expr>)
  ;


expression
  <init_expr>
  :
  //! (e1 and !e2)
  <timp> #(IMP imp1:expression <pre_imp> imp2:expression <post_imp>)
  |<teqv> #(EQV eqv1:expression <pre_eqv> eqv2:expression <post_eqv>) //not xor
  |<txor> #(XOR xor1:expression <pre_xor> xor2:expression <post_xor>)
  |<tor> #(OR  or1:expression <pre_or> or2:expression <post_or>)
  |<tand> #(AND and1:expression <pre_and> and2:expression <post_and>)
  |<tlt> #(LT  lt1:expression <pre_lt> lt2:expression <post_lt>)
  |<tgt> #(GT  gt1:expression <pre_gt> gt2:expression <post_gt>)
  |<tle> #(LE  le1:expression <pre_le> le2:expression <post_le>)
  |<tge> #(GE  ge1:expression <pre_ge> ge2:expression <post_ge>)
  |<teq_comp> #(EQ eq_comp1:expression <pre_eq_comp> eq_comp2:expression
      <post_eq_comp>)
  |<teq> #(ASSIGN eq_ex1:expression eq_ex2:expression <post_eq>)
  |<tneq> #(NEQ  neq1:expression <pre_neq> neq2:expression <post_neq>)
  | #(IS  expression expression)
  |<tconc> #(CONCAT  concat1:expression <pre_concat> concat2:expression
      <post_concat>)
  |<tplus> #(PLUS  plus1:expression <pre_plus> plus2:expression <post_plus>)
  |<tminus> #(MINUS minus1:expression <pre_minus> minus2:expression
      <post_minus>)
  |<tmod> #(MOD  mod1:expression <pre_mod> mod2:expression <post_mod>)
  |<tint_div> #(INT_DIVIDE int_div1:expression <pre_idiv> int_div2:expression
      <post_idiv>)
  |<tmul> #(ASTERISK mul1:expression <pre_mul> mul2:expression <post_mul>)
  |<tdiv> #(DIVIDE div1:expression <pre_div> div2:expression <post_div>)
  |<tpow> #(POW exp1:expression <pre_exp> exp2:expression <post_exp>)
  |<tuplas> #(UNARY_PLUS uplus:expression <unary_plus>)
  | #(INDEX_OP expression indexes)
  |<tuminus> #(UNARY_MINUS uminus:expression <unary_minus>)
  |<tnot> #(NOT not:expression <not>)
  |<tdot> #(DOT do1:expression do2:expression <dot_id_end>)
  |<tmethod> #(METHOD_CALL func:expression <func_call> (argListValues)?
      <func_call_end>)
  |! #(WITH_DOT with_dotexpr:expression <with_dot_end>)
  | #(LPAREN expression)
  |<tnew> #(NEW IDENTIFIER <new>)
  | DINT
  | DFLOAT
  | IDENTIFIER <id>
  | DSTRING
  | DDATE
  | TRUE
  | FALSE
  | EMPTY
  | NULL
  | EMBEDDED_ASP
  |! NOTHING <nothing>
  ;

indexes
  :
  #(INDEX_LIST (expression <index>)+)
  ;


argListValues
  :
  !#(ARGLIST_VALUES <arg_val> (arg:expression <arg_val_end> | EMPTYARG <arg_empty> )+)
  ;


argList
  :
  #(ARGLIST (bid:BYREF | id:IDENTIFIER)+ )
  ;

