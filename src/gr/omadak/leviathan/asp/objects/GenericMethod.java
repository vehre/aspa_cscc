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
import java.util.Collections;
import java.util.List;

public class GenericMethod extends BaseMember implements Method {
    private List argTypes;
    private AST template;
    private boolean constructor;

    public GenericMethod(String methodName, int retType,
    List argTypes, AST template) {
        this.argTypes = argTypes;
        this.template = template;
        this.retType = retType;
        this.name = methodName;
    }


    public AST translate(AST instance) {
        addParentArgs();
        AST result = template == null
        ? null : cloneAST(template, args, instance);
        resetState();
        return result;
    }


    public List getArgTypes() {
        return argTypes == null ? Collections.EMPTY_LIST : argTypes;
    }


    public int getArgCount() {
        return argTypes == null ? 0 : argTypes.size();
    }


    public boolean isConstructor() {
        return constructor;
    }


    public void setConstructor(boolean val) {
        constructor = val;
    }
}
