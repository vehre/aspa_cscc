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

class JsTree extends TreeParser;
options {
    importVocab = Js;
    exportVocab = TreeJs;
    buildAST = true;
    defaultErrorHandler = false;
    classHeaderSuffix = "SymbolTableExposer";
}


tokens {
    <tokens>
}
<init>


start_rule
<start>
  :
  <start_rule> (statement)* <end>
  ;


statement
  :
  expr
  | HTML
  | include:INCLUDE <include>
  | #(EQ_HTML expr)
  | #(SLIST (statement)*)
  | <tfunc> #(FUNCTION <func_decl> fncstm:statement <func_end>)
  | <tmem> #(FUNCTION_MEMBER <function_member> memstm:statement <funcmem_end>)
  | var
  | <tlabel> #(LABELED_STAT label:IDENTIFIER <pre_label>
      label_stat:statement <label>)
  | <tif> #(IF if_expr:expr if_stm:statement (elsestm:else_statement)? <if>)
  | <twith> #(WITH #(EXPR with_expr:expression <with_obj>)
      with_stm:statement <with_end>)
  | #(FOR <pre_for> (
          #(IN expr expr)
          | #(finit:FOR_INIT (fvar | arglist <for_init>)?)
            #(FOR_CONDITION (expr)?)
            #(fiter:FOR_ITERATOR (arglist <for_iter>)?)
          )
          statement
    <post_for>
    )
  | #(WHILE <pre_while> expr statement <post_while>)
  | #(DO <pre_do> statement expr <post_do>)
  | <tbreak> #(BREAK (brid:IDENTIFIER)? <break>)
  | <tcontinue> #(CONTINUE (contid:IDENTIFIER)? <continue>)
  | #(RETURN (expr)?)
  | #(SWITCH <pre_switch> expr (casesGroup)* <post_switch>)
  | #(TRY statement
      (
          #(CATCH IDENTIFIER statement) (#(FINALLY statement))?
          | #(FINALLY statement)
      )
    )
  | #(THROW expr)
  | SEMI
  ;


fvar
  :
  <tfvar> #(VAR <pre_fvar>
      (fexpr:expr <fvar_expr> | fid:IDENTIFIER <fvar_decl>)+) <fvar_end>
  ;


var
  :
  <tvar> #(VAR <pre_var>
      (vexpr:expr <var_expr> | vid:IDENTIFIER <var_decl>)+ <var_end>
  )
  ;


casesGroup
  :
  #(CASE_GROUP
      (#(CASE expr) | DEFAULT)+
      #(SLIST (statement)*)
  )
  ;


else_statement
  :
  <telse>#(ELSE elsestm:statement <else>)
  ;


expr
  :
  <texpr>#(EXPR (ex:expression <expr> | SKIP_EXPR))
  ;


anonym_function
    :
    <tanon_func> #(FUNCTION <anon_decl> fncstm:statement <anon_end>)
    ;


expression
    :
    <tquestion> #(QUESTION
        eaqcond:expression <q1>
        eaqtrue:expression <q2>
        eaqfalse:expression <question>)
    |<tassign>  #(ASSIGN
        assign_expr1:expression
        (
            assign_expr2:expression
            | anonfunc:anonym_function
        )<assign>)
    |<tplus_assign> #(PLUS_ASSIGN plus_assign_expr1:expression
        plus_assign_expr2:expression <plus_assign>)
    |<tminus_assign> #(MINUS_ASSIGN minus_assign_expr1:expression
        minus_assign_expr2:expression <minus_assign>)
    |<tstar_assign> #(STAR_ASSIGN star_assign_expr1:expression
        star_assign_expr2:expression <star_assign>)
    |<tdiv_assign> #(DIV_ASSIGN div_assign_expr1:expression
        div_assign_expr2:expression <div_assign>)
    |<tmod_assign> #(MOD_ASSIGN mod_assign_expr1:expression
        mod_assign_expr2:expression <mod_assign>)
    |<tsr_assign> #(SR_ASSIGN sr_assign_expr1:expression
        sr_assign_expr2:expression <sr_assign>)
    |<tbsr_assign> #(BSR_ASSIGN bsr_assign_expr1:expression
        bsr_assign_expr2:expression <bsr_assign>)
    |<tsl_assign> #(SL_ASSIGN sl_assign_expr1:expression
        sl_assign_expr2:expression <sl_assign>)
    |<tband_assign> #(BAND_ASSIGN band_assign_expr1:expression
        band_assign_expr2:expression <band_assign>)
    |<tbxor_assign> #(BXOR_ASSIGN bxor_assign_expr1:expression
        bxor_assign_expr2:expression <bxor_assign>)
    |<tbor_assign> #(BOR_ASSIGN bor_assign_expr1:expression
        bor_assign_expr2:expression <bor_assign>)

    //=========binary
    |<tland> #(LAND land_expr1:expression <pre_land>
        land_expr2:expression <land>)
    |<tlor> #(LOR lor_expr1:expression <pre_lor>
        lor_expr2:expression <lor>)
    |<tbor> #(BOR bor_expr1:expression <pre_bor>
        bor_expr2:expression <bor>)
    |<tbxor> #(BXOR bxor_expr1:expression <pre_bxor>
        bxor_expr2:expression <bxor>)
    |<tband> #(BAND band_expr1:expression <pre_band>
        band_expr2:expression <band>)
    |<tinstanceof> #(INSTANCEOF iex:expression <pre_instanceof>
        (itype:type | idtype:IDENTIFIER) <instanceof>)

    //=========Comparasion expressions
    |<tnot_equal> #(NOT_EQUAL not_equal_expr1:expression <pre_not_equal>
        not_equal_expr2:expression <not_equal>)
    |<tequal> #(EQUAL equal_expr1:expression <pre_equal>
        equal_expr2:expression <equal>)
    |<tidentical> #(IDENTICAL identical_expr1:expression <pre_identical>
        identical_expr2:expression <identical>)
    |<tnot_identical> #(NOT_IDENTICAL not_identical_expr1:expression
        <pre_not_identical> not_identical_expr2:expression <not_identical>)
    |<tlt> #(LT lt_expr1:expression <pre_lt> lt_expr2:expression <lt>)
    |<tgt> #(GT gt_expr1:expression <pre_gt> gt_expr2:expression <gt>)
    |<tle> #(LE le_expr1:expression <pre_le> le_expr2:expression <le>)
    |<tge> #(GE ge_expr1:expression <pre_ge> ge_expr2:expression <ge>)
    |<tsl> #(SL sl_expr1:expression <pre_sl> sl_expr2:expression <sl>)
    |<tsr> #(SR sr_expr1:expression <pre_sr> sr_expr2:expression <sr>)
    |<tbsr> #(BSR bsr_expr1:expression <pre_bsr> bsr_expr2:expression <bsr>)

    //=========Numerical
    |<tplus> #(PLUS plus_expr1:expression <pre_plus>
         plus_expr2:expression <plus>)
    |<tminus> #(MINUS minus_expr1:expression <pre_minus>
         minus_expr2:expression <minus>)
    |<tstar> #(STAR star_expr1:expression <pre_star>
         star_expr2:expression <star>)
    |<tdiv> #(DIV div_expr1:expression <pre_div>
         div_expr2:expression <div>)
    |<tmod> #(MOD mod_expr1:expression <pre_mod>
         mod_expr2:expression <mod>)

    //=========unary
    |<tinc> #(INC inc:expression <inc>)
    |<tdec> #(DEC dec:expression <dec>)
    |<tunary_minus> #(UNARY_MINUS um:expression <unary_minus>)
    |<tunary_plus> #(UNARY_PLUS up:expression <unary_plus>)
    |<tbnot> #(BNOT bnot:expression <bnot>)
    |<tlnot> #(LNOT lnot:expression <lnot>)
    |<tpost_inc> #(POST_INC post_inc:expression <post_inc>)
    |<tpost_dec> #(POST_DEC post_dec:expression <post_dec>)
    |<tdelete> #(DELETE delete:expression <delete>)
    | #(TYPEOF typeof:expression)
    | #(LPAREN expression)

    //=========postfix
    |<tdot> #(DOT (d1:expression | dt:type | dthis:THIS) d2:expression <dot>)
    |<tindex_op> #(INDEX_OP (arr:expression | tarr:THIS) <pre_index_op>
        arglist <index_op>)
    |<tmethod_call> #(METHOD_CALL meth:expression arglist <method_call>)

    //primary expressions
    | IDENTIFIER <id>
    | TRUE
    | FALSE
    | NULL
    | UNDEFINED
    | REG_EXP
    | NUM_INT
    | STRING_LITERAL
    |<tnew> #(NEW (t:type | tid:IDENTIFIER) (arglist | <no_args>) <new>)
    |<tlp_back> #(LBRACK arglist <lp_back>)
    ;


type
  :
  ARRAY | DATE | OBJECT | STRING | NUMBER | BOOLEAN
  ;


arglist
  <arg_init>
  :
  <telist>#(ELIST (e:expr <arglist>)* <arglist_end>)
  ;

