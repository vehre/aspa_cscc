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

public class ASPMethodWrapper extends BaseMemberWrapper implements Method {
    public ASPMethodWrapper(ASPDependantClass wrapped) {
        setEvaluatedClass(wrapped);
        if (wrapped.getDefaultMethod() == null
        && wrapped.getDefaultProperty() == null) {
            throw new NullPointerException(wrapped.getName() 
            + " has no default method");
        }
    }


	public AST translate(AST instance) {
		Method m = wrapped.getDefaultMethod();
        AST result;
        if (m == null) {
            Property prop = wrapped.getDefaultProperty();
            setArgList(prop);
            if ((args == null || args.isEmpty()) && prop.canRead()) {
                result = prop.read(instance);
            } else if (prop.canWrite()) {
                result = prop.write(instance);
            } else if (prop.canRead()) {
                result = prop.read(instance);
            } else {
                result = null;
            }
        } else {
            setArgList(m);
            result = m.translate(instance);
        }
        resetState();
        return result;
    }


    public List getArgTypes() {
		return wrapped.getDefaultMethod().getArgTypes();
    }


    public boolean isConstructor() {
        return false;
    }


    public void setConstructor(boolean b) {}
}
