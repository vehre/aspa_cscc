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
import org.apache.log4j.Logger;

public class GenericASPProperty extends BaseMember implements Property {
    private AST read_AST;
    private AST write_AST;
    //private int argType = -1;
    private int methodIndex;
    private Method method;
    private static Logger LOG = Logger.getLogger(GenericASPProperty.class);

    public GenericASPProperty(String name, AST read_AST, AST write_AST,
    int ret) {
        this.name = name;
        this.read_AST = read_AST;
        this.write_AST = write_AST;
        retType = ret;
    }


    public AST read(AST instance) {
        addParentArgs();
        return cloneAST(read_AST, args, instance);
    }


    public AST write(AST instance) {
        addParentArgs();
        if (write_AST != null && method != null) {
            if (args != null && args.size() > methodIndex) {
                method.setArgs(Collections.singletonList(
                args.get(methodIndex)));
            } else {
                LOG.warn("Failed to indicate method:" + method.getName()
                + " because not enough arguments");
            }
        }
        AST result;
        if (write_AST == null) {
            resetState();
            result = null;
        } else {
            result = cloneAST(write_AST, args, instance);
        }
        return result;
    }


    public void setArgType(int type) {
        //argType = type;
    }


    public void setIndicatedMethod(Method method, int index) {
        this.method = method;
        methodIndex = index;
    }
}
