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
import java.util.Iterator;
import java.util.List;

public class ASPObjectInstance {
    private AST instance;
    private ASPClass clazz;
    private Member member;
    private List memberList;

    public ASPObjectInstance() {}

    public ASPObjectInstance(ASPClass clazz) {
        setASPClass(clazz);
    }


    public ASPObjectInstance(AST instance) {
        setInstance(instance);
    }


    public ASPObjectInstance(AST instance, ASPClass object) {
        setInstance(instance);
        setASPClass(object);
    }


    public ASPObjectInstance(AST instance, ASPObjectInstance dup) {
        setInstance(instance);
        setASPClass(dup.getASPClass());
        setMember(dup.getMember());
        setMemberList(dup.getMemberList());
    }


    public void setInstance(AST instance) {
        this.instance = instance;
    }


    public void setASPClass(ASPClass object) {
        this.clazz = object;
    }


    public AST getInstance() {
        return instance;
    }


    public ASPClass getASPClass() {
        return clazz;
    }


    public void setMember(Member member) {
        memberList = null;
        this.member = member;
    }


    public Member getMember() {
        return member;
    }


    public void setMemberList(List memberList) {
        member = null;
        this.memberList = memberList;
    }


    public List getMemberList() {
        return memberList;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Instance:");
        if (instance != null) {
            sb.append(instance.toStringList());
        } else {
            sb.append("null");
        }
        sb.append("\nClass:");
        if (clazz != null) {
            sb.append(clazz.getName());
        } else {
            sb.append("null");
        }
        sb.append("\nMember:");
        if (member != null) {
            sb.append(member.getName());
        } else {
            sb.append("null");
        }
        if (memberList != null) {
            sb.append("\nList:[");
            for (Iterator it = memberList.iterator(); it.hasNext();) {
                Member mem = (Member) it.next();
                sb.append(mem.getName());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }
        return sb.toString();
    }
}

