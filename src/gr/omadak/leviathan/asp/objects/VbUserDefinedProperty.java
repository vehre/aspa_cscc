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

public class VbUserDefinedProperty extends BaseMember implements Property {
    private Method getMethod;
    private Method setMethod;
    private Method letMethod;
    private boolean is_private;
    private boolean is_default;


    public VbUserDefinedProperty() {}


    public AST read(AST instance) {
        AST result = null;
        if (getMethod != null) {
            getMethod.setArgs(args == null ? Collections.EMPTY_LIST : args);
            result = getMethod.translate(instance);
        }
        resetState();
        return result;
    }


    public AST write(AST instance) {
        Method method = setMethod != null ? setMethod : letMethod;
        AST result = null;
        if (method != null) {
            method.setArgs(args == null ? Collections.EMPTY_LIST : args);
            result = method.translate(instance);
        }
        resetState();
        return result;
    }


    public boolean canRead() {
        return getMethod != null;
    }


    public boolean canWrite() {
        return letMethod != null || setMethod != null;
    }


    public void setArgType(int index) {}
    public void setIndicatedMethod(Method method, int index){}


    public void setGetMethod(Method method) {
        getMethod = method;
    }


    public void setSetMethod(Method method) {
        setMethod = method;
    }


    public void setLetMethod(Method method) {
        letMethod = method;
    }


    public boolean isPrivate() {
        return is_private;
    }


    public boolean isPublic() {
        return !is_private;
    }


    public boolean isDefault() {
        return is_default;
    }


    public void setPrivate(boolean priv) {
        is_private = priv;
    }


    public void setDefault(boolean def) {
        is_default = def;
    }


    public boolean hasGet() {
        return getMethod != null;
    }


    public boolean hasSet() {
        return setMethod != null;
    }


    public boolean hasLet() {
        return letMethod != null;
    }


    public Method getSet() {
        return setMethod;
    }


    public Method getGet() {
        return getMethod;
    }


    public Method getLet() {
        return letMethod;
    }

    public String toString() {
        return new StringBuffer(getName()).append(" set:")
        .append(setMethod != null)
        .append("\nget:").append(getMethod != null)
        .append("\nlet:").append(letMethod != null)
        .toString();
    }
}
