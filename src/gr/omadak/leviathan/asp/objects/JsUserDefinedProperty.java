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

import antlr.ASTFactory;
import antlr.collections.AST;
import gr.omadak.leviathan.asp.CommonConstants;
import gr.omadak.leviathan.asp.TreeJsTokenTypes;

public class JsUserDefinedProperty extends BaseMember implements Property {
    private static AST INSTANCE_REF;
    private static ASTFactory FACTORY;
    static {
        FACTORY = new ASTFactory();
        INSTANCE_REF = FACTORY.make(new AST[] {
        FACTORY.create(TreeJsTokenTypes.DOT, "DOT"),
        FACTORY.create(CommonConstants.INSTANCE, "inst"),
        FACTORY.create(TreeJsTokenTypes.IDENTIFIER)
        });
    }

    private AST getRefAST() {
        AST result = FACTORY.dupTree(INSTANCE_REF);
        result.getFirstChild().getNextSibling().setText(name);
        return result;
    }


    public JsUserDefinedProperty(String name) {
        this.name = name;
    }


    public JsUserDefinedProperty(String name, int type) {
        this(name);
        retType = type;
    }


    public AST read(AST instance) {
        AST result;
        if (instance != null) {
            result = cloneAST(getRefAST(), null, instance);
        } else {
            result = FACTORY.create(TreeJsTokenTypes.OBJECT_ATTRIBUTE, name);
        }
        return result;
    }


    public AST write(AST instance) {
        AST assignAST = FACTORY.make(new AST[] {
            FACTORY.create(TreeJsTokenTypes.ASSIGN, "="),
            instance == null 
            ? FACTORY.create(TreeJsTokenTypes.OBJECT_ATTRIBUTE, name)
            : getRefAST(),
            FACTORY.create(CommonConstants.TEMPLATE, "1")
        });
        return cloneAST(assignAST, args, null);
    }


    public void setArgType(int type) {}
    public void setIndicatedMethod(Method method, int index) {}
}
