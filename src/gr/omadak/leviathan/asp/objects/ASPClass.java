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
public interface ASPClass extends ASPObject {

    /** Return a method or a property named like name */
    public Member getMemberObject(String name);
    /** Returns al methods and properties named like name */
    public List getMemberList(String name);
    /**
    Returns true if is an ASPClass which can be assigned
    in a reference.For example: a = new Array()
    */
    public boolean isStandalone();
    public void setStandalone(boolean val);
    /** Returns the default property of an ASPClass or null if none exists */
    public Property getDefaultProperty();
    /** Returns the default Method of an ASPClass.This happens in
    Collection Objects */
    public Method getDefaultMethod();
    /** Optional AST for object removal.Returns null if not applicable */
    public AST getDeleteAST();
    /** Return true if the class has a constructor */
    public boolean hasConstructor();
    /** Returns the constructors of this class */
    public List getConstructors();
    /** Adds a constructor for this class */
    public void addConstructor(Method method);
    /** Adds a member to this class */
    public void addMember(Member member);
    /** Indicates if member names should be treated as case sensitive */
    public boolean isCaseSensitive();
    /** Sets if member names should be treated as case sensitive */
    public void setCaseSensitive(boolean sense);
    /** Provides access to Properties of this class */
    public Iterator getProperties();
    /** Provides access to Methods of this class */
    public Iterator getMethods();
    /** Provides access to all members of this class */
    public Iterator getMembers();
    /** Sets the default method for the class */
    public void setDefaultMethod(Method method);
    /** Sets the default proprty for the class */
    public void setDefaultProperty(Property property);
    /** Sets the destructor translation for the class */
    public void setDeleteAST(AST ast);
}
