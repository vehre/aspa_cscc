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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class XmlASPClass extends GenericClass {
    private Property defaultProperty;
    private Method defaultMethod;
    private boolean standalone;
    private AST deleteAST;


    public XmlASPClass(String name) {
        setName(name);
    }


    public XmlASPClass() {}


    public XmlASPClass(ASPClass clazz) {
        this.name = clazz.getName();
        standalone = clazz.isStandalone();
        defaultProperty = clazz.getDefaultProperty();
        defaultMethod = clazz.getDefaultMethod();
        deleteAST = clazz.getDeleteAST();
        if (clazz.getConstructors() != null) {
            constructors = new ArrayList(clazz.getConstructors());
        }
        if (clazz instanceof GenericClass) {
            GenericClass gclazz = (GenericClass) clazz;
            if (gclazz.members != null) {
                members = new HashMap(gclazz.members);
            }
        }
    }


    public boolean isStandalone() {
        return standalone;
    }


    public Property getDefaultProperty() {
        return defaultProperty == null ? null : defaultProperty;
    }


    public AST getDeleteAST() {
        return deleteAST;
    }


    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }


    public void setDeleteAST(AST del) {
        deleteAST = del;
    }


    private void removeDefaultMemeber(Member memb) {
        if (memb != null && memb.getName() != null
        && memb.getName().charAt(0) != '_') {
            members.remove(memb.getName());
        }
    }


    private void addDefaultMember(Member memb) {
        if (memb != null && memb.getName() != null
        && memb.getName().charAt(0) != '_') {
            addMember(memb);
        }
    }


    public void setDefaultProperty(Property prop) {
        removeDefaultMemeber(defaultProperty);
        defaultProperty = prop;
        defaultProperty.setClass(this);
        addDefaultMember(defaultProperty);
    }


    public Method getDefaultMethod() {
        return defaultMethod == null ? null : defaultMethod;
    }


    public void setDefaultMethod(Method method) {
        removeDefaultMemeber(defaultMethod);
        defaultMethod = method;
        defaultMethod.setClass(this);
        addDefaultMember(defaultMethod);
    }


    public List getConstructors() {
        return constructors == null ? Collections.EMPTY_LIST : constructors;
    }


    private void memberToString(StringBuffer sb, Member mem) {
        sb.append("\n")
        .append(mem instanceof Property ? "property" : "method")
        .append(':').append(mem.getName());
    }


    public String toString() {
        StringBuffer sb = new StringBuffer("class:").append(name)
        .append("\ndefault property:")
        .append(defaultProperty == null ? "null" : defaultProperty.getName())
        .append("\ndefaultMethod:")
        .append(defaultMethod == null ? "null" : defaultMethod.getName());
        if (constructors != null) {
            for (Iterator it = constructors.iterator(); it.hasNext();) {
                Method constr = (Method) it.next();
                sb.append("\nconstructor:").append(constr.getName());
            }
        } else {
            sb.append("\nno constructors");
        }
        if (members != null) {
            for (Iterator it = members.keySet().iterator(); it.hasNext();) {
                String name = (String) it.next();
                Object value = members.get(name);
                if (value instanceof Member) {
                    memberToString(sb, (Member) value);
                } else {
                    for (Iterator memIt = ((List) value).iterator();
                    memIt.hasNext();) {
                        memberToString(sb, (Member) memIt.next());
                    }
                }
            }
        } else {
            sb.append("\nNo members");
        }
        return sb.toString();
    }
}
