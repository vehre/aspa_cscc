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
package gr.omadak.leviathan.asp.objects;

import antlr.collections.AST;
import gr.omadak.leviathan.asp.TreeVbsTokenTypes;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

public class VbUserDefinedMethod extends UserDefinedMethod {
    private boolean privateFunction;
    private boolean defaultFunction;

    private BitSet argMethod;

    static {
        NULL = TreeVbsTokenTypes.NULL;
        METHOD_CALL = TreeVbsTokenTypes.METHOD_CALL;
        DOT = TreeVbsTokenTypes.DOT;
        ARGLIST_VALUES = TreeVbsTokenTypes.ARGLIST_VALUES;
        NEW = TreeVbsTokenTypes.NEW;
    }

    private void setByRef(int index) {
        if (argMethod == null) {
            argMethod = new BitSet(index + 1);
        }
        argMethod.set(index);
    }


    public VbUserDefinedMethod(String name, List args, boolean isPrivate,
    boolean isDefault) {
        super(name, args);
        if (!args.isEmpty()) {
            int index = 0;
            for (Iterator it = args.iterator(); it.hasNext();) {
                AST arg = (AST) it.next();
                if (arg.getType() == TreeVbsTokenTypes.BYREF) {
                    setByRef(index);
                }
                index++;
            }
        }
        privateFunction = isPrivate;
        defaultFunction = isDefault;
    }


    public void setByRef(String argName) {
        int index = getArgTypes().isEmpty() 
        ?  -1 : getArgTypes().indexOf(argName);
        if (index > -1) {
            setByRef(index);
        }
    }


    public void setByVal(String argName) {
        int index = getArgTypes().isEmpty() || argMethod == null ?  -1
        : getArgTypes().indexOf(argName);
        if (index > -1) {
            argMethod.clear(index);
        }
    }


    public BitSet getArgsMethod() {
        return argMethod;
    }


    public void setDefault(boolean def) {
    //    defaultFunction = def;
    }


    public void setPrivate(boolean priv) {
    //    privateFunction = priv;
    }


    public String toString() {
		return getName();
	}
}
