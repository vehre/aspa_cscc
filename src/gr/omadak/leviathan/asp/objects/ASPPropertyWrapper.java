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

public class ASPPropertyWrapper extends BaseMemberWrapper implements Property {
    public ASPPropertyWrapper(ASPClass wrapped) {
        setEvaluatedClass(wrapped);
    }


    public AST read(AST instance) {
		Property prop = wrapped.getDefaultProperty();
        AST result = prop.read(instance);
        resetState();
        return result;
    }


    public AST write(AST instance) {
		Property prop = wrapped.getDefaultProperty();
        setArgList(prop);
        AST result;
        result = prop.write(instance);
        resetState();
        return result;
    }


	public void setArgType(int type) {}


    public void setIndicatedMethod(Method method, int index) {
        wrapped.getDefaultProperty().setIndicatedMethod(method, index);
    }


    public boolean canRead() {
        return wrapped.getDefaultProperty().canRead();
    }


    public boolean canWrite() {
        return wrapped.getDefaultProperty().canWrite();
    }
}
