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
import java.util.Map;
import java.util.HashMap;
}

class JSTreeResolver extends TreeParser;
options {
    importVocab = TreeVbs;
    buildAST=true;
    defaultErrorHandler=false;
}
{
	private Map<String, Object> variables;
	
	private final int DECLARED = 1;
	private final int INIT = 2;
	private final int DECLAREDANDINIT = 3;
	
	private class Scope extends HashMap<String, Integer> {
		private Scope parent;
		public Scope() {
			super();
			parent = null;
		}
		public boolean resolveDECLARED(String id) {
			return containsKey(id) && (get(id).intValue() & DECLARED) == DECLARED;
		}
		public boolean resolveDECLINIT(String id) {
			return containsKey(id) && (get(id).intValue() & DECLAREDANDINIT) == DECLAREDANDINIT;
		}
		public String resolveToString(String id) {
			return !containsKey(id) ? " declared" : ((get(id).intValue() & INIT) != INIT ? " initialized" : " ???");			
		}
	} 
	private Scope currentScope = new Scope();
	
	/** Set the variables for this parser. */
	public void setVariables(Map<String, Object> vars) {
		variables = vars;
	}
	
	private String getPos(AST in) {
		StringBuilder str = new StringBuilder(" at (");
		if ( in != null ) 
			str.append(in.getLine()).append(", ").append(in.getColumn());
		else 
			str.append("null");
		return str.append(")").toString();
	}
	
	public static final int OBJECT = CommonConstants.OBJECT;
	public static final int OBJECT_RET = CommonConstants.OBJECT_RET;
}


/** breadth_first does a (the name tells it) breadth first search
	in the tree. It is the simplest way to filter through a tree w/o
	writing rules for all possible nodes. */
breadth_first
	:
	( 
		  #(VAR (candvar)+)
			/* (candvar)+ will walk along all children on its own. */
		| #(ASSIGN lhs:.) {
			if ( lhs.getType() == IDENTIFIER )
				if (! currentScope.resolveDECLARED(lhs.getText()) ) {
					System.out.println("Variable '" + lhs.getText() + "' assigned to w/o being" + 
						currentScope.resolveToString(lhs.getText()) + getPos(lhs));
				}
			##.setFirstChild(lhs);
		}
		| id:IDENTIFIER {
			if (! currentScope.resolveDECLINIT(id.getText()) ) {
				System.out.println("Variable '" + id.getText() + "' read w/o being" + 
					currentScope.resolveToString(id.getText()) + getPos(id));
			}	
		}
		| #(OBJECT obj_arg:.) {
			if ( obj_arg != null ) {
				breadth_first(obj_arg);
				##.setFirstChild(returnAST);
			}
		}
		| #(OBJECT_RET (obj_ret_arg:.)?) {
			if ( obj_ret_arg != null ) {
				breadth_first(obj_ret_arg);
				##.setFirstChild(returnAST);
			}
		}
		| cand:. {
			if ( cand.getFirstChild() != null ) {
				breadth_first(cand.getFirstChild());
				#cand.setFirstChild(returnAST);
			}
			## = #cand;
		} ) 
	{
		/* Now do the next sibling processing. */
		if ( breadth_first_AST_in.getNextSibling() != null ) {
			breadth_first(breadth_first_AST_in.getNextSibling());
			##.setNextSibling(returnAST);
		}
	} 
	;
	
/** All child nodes of a VAR node are EXPR, i.e. expressions. If
	a variable is already initialized, then the ASSIGN is child node
	of the EXPR. */ 
candvar
	:
	#(EXPR vdecl:.) {
		/* When the vdecl is already an ASSIGN, then getText() yields
			"=", which is not a legal name for a variable and therefore
			it will not be in the map of variables. */
		if ( variables.containsKey(vdecl.getText()) ) {
			if ( variables.get(vdecl.getText()) instanceof Integer) {
				AST initializer = null;
				switch (((Integer) variables.get(vdecl.getText())).intValue()) {
					case DINT: initializer = #([DINT, "0"]); break;
					case DFLOAT: initializer = #([DFLOAT, "0.0"]); break;
					case DSTRING: initializer = #([DSTRING, ""]); break;
					default: break; 
				}
				if ( initializer != null ) {
					// Only when the initializer is set, do a modify.
					AST tmp = #([ASSIGN, "="], #vdecl, initializer);
					/* The following line is copied from the output of antlr and correct, because
					ANTLR does not generate the EXPR in the astFactory.create() correctly. */
					## = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(EXPR,"EXPR")).add(tmp));
				}
			}
			currentScope.put(vdecl.getText(), DECLARED);
		} else if ( vdecl.getType() == ASSIGN )
			currentScope.put(vdecl.getFirstChild().getText(), DECLAREDANDINIT);
		else if ( vdecl.getType() == IDENTIFIER )
			currentScope.put(vdecl.getText(), DECLARED);
		else
			System.out.println("Decl. unknown: " + vdecl.toStringList());
	}
	;
	
