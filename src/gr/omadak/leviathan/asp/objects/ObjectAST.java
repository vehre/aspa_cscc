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

import antlr.CommonAST;
import antlr.Token;

public class ObjectAST extends CommonAST {
    private ASPObjectInstance instance;

    public ObjectAST(Token token, ASPObjectInstance instance) {
        super(token);
        setInstance(instance);
    }


    public ASPObjectInstance getInstance() {
        return instance;
    }


    public void setInstance(ASPObjectInstance instance) {
        this.instance = instance;
    }


    public String toString() {
        return new StringBuffer("[").append(getType()).append(",")
        .append(getText()).append("]\n").append(instance).toString();
    }
}

