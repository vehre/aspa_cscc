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

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public abstract class GenericClass extends BaseObject implements ASPClass {
    protected Map members;
    protected List constructors;
    protected boolean caseSensitive;


    public void addMember(Member member) {
        member.setClass(this);
        if (members == null) {
            members = new HashMap();
        }
        String key = caseSensitive ? member.getName()
        : member.getName().toUpperCase();
        if (!members.containsKey(key)) {
            members.put(key, member);
        } else {
            Object value = members.get(key);
            if (value instanceof List) {
                List alternatives = (List) value;
                alternatives.add(member);
            } else {
                List alternatives = new ArrayList(2);
                alternatives.add(value);
                alternatives.add(member);
                members.put(key, alternatives);
            }
        }
    }


    public Member getMemberObject(String name) {
        String key = caseSensitive ? name : name.toUpperCase();
        Object obj = members != null ? members.get(key) : null;
        Member result;
        if (obj == null) {
            result = null;
        } else {
            if (obj instanceof Member) {
                result = (Member) obj;
            } else {
                List alts = (List) obj;
                result = (Member) alts.get(0);
            }
        }
        return result;
    }


    public List getMemberList(String name) {
        Object value = members == null ? null
        : members.get(caseSensitive ? name : name.toUpperCase());
        List result;
        if (value != null) {
            if (value instanceof List) {
                result = (List) value; //hope not modified outside
            } else {
                result = Collections.singletonList(value);
            }
        } else {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    public void addConstructor(Method method) {
        if (constructors == null) {
            constructors = new ArrayList();
        }
        method.setClass(this);
        method.setConstructor(true);
        constructors.add(method);
    }


    public boolean hasConstructor() {
        return constructors != null && !constructors.isEmpty();
    }


    public void resetState() {}


    public boolean isCaseSensitive() {
        return caseSensitive;
    }


    public void setCaseSensitive(boolean sense) {
        caseSensitive = sense;
    }


    public Iterator getProperties() {
        return members == null
        ? IteratorUtils.EMPTY_ITERATOR
        : IteratorUtils.filteredIterator(members.values().iterator(),
        new Predicate() {
            public boolean evaluate(Object obj) {
                return obj instanceof Property;
            }
        });
    }


    public Iterator getMethods() {
        return members == null
        ? IteratorUtils.EMPTY_ITERATOR
        : IteratorUtils.filteredIterator(members.values().iterator(),
        new Predicate() {
            public boolean evaluate(Object obj) {
                return obj instanceof Method;
            }
        });
    }


    public Iterator getMembers() {
        return members == null
        ? IteratorUtils.EMPTY_ITERATOR
        : members.values().iterator();
    }
}
