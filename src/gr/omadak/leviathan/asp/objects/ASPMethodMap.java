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
import antlr.CommonToken;
import antlr.Token;
import antlr.collections.AST;
import gr.omadak.leviathan.asp.TokenMap;
import gr.omadak.leviathan.asp.TreeVbsTokenTypes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class ASPMethodMap extends GenericMethod {
    private Map cases = new HashMap();
    private boolean caseSensitive;
    private AST defaultCase;
    private AST nonStringCase;
    private ASPClass retClass;
    private static Logger LOG = Logger.getLogger(ASPMethodMap.class);

    public ASPMethodMap(String name, int retType, List args) {
        super(name, retType, args, null);
    }


    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }


    public void setNonStringCase(AST nonStringCase) {
        this.nonStringCase = nonStringCase;
    }


    public void addCase(String key, AST value) {
        cases.put(caseSensitive ? key : key.toUpperCase(), value);
    }


    public void addCase(String key, ASPClass clazz) {
        cases.put(caseSensitive ? key : key.toUpperCase(), clazz);
    }


    public void setDefaultCase(AST def) {
        defaultCase = def;
    }


    public void setArgs(List args) {
        super.setArgs(args);
        if (args != null && !args.isEmpty()) {
            AST arg = (AST) args.get(0);
            if (TokenMap.isSame(arg.getType(), TreeVbsTokenTypes.DSTRING,
            true)) {
                String name = arg.getText();
                name = caseSensitive ? name : name.toUpperCase();
                Object val = cases.get(name);
                if (val instanceof ASPClass) {
                    retClass = (ASPClass) val;
                }
            }
        }
    }


    public AST translate(AST instance) {
        AST template;
        LOG.debug("Have :" + (args == null ? 0 : args.size()) + " args");
        if (args != null && !args.isEmpty()) {
            AST key = (AST) args.get(0);
            if (!TokenMap.isSame(key.getType(),
            TreeVbsTokenTypes.DSTRING, true)) {
                template = nonStringCase;
            } else {
                String strKey = key.getText();
                Object val  = cases.get(caseSensitive
                ? strKey : strKey.toUpperCase());
                if (val instanceof ASPClass) {
                    template = new CommonAST(new CommonToken(Token.SKIP, null));
                } else if (val != null) {
                    template = (AST) val;
                } else {
                    template = defaultCase;
                }
            }
        } else {
            template = defaultCase;
        }
        return cloneAST(template, args, instance);
    }


    public boolean isConstructor() {
        return false;
    }


    public void setConstructor(boolean b) {}


    public ASPClass getRetObjectClass() {
        return retClass;
    }

    public void resetState() {
        super.resetState();
        retClass = null;
    }
}
