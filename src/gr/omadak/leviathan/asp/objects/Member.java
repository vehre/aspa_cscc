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

import java.util.List;

/**
Abstract representation of a class Member
*/
public interface Member extends ASPObject {

    /** Returns the code of the return type for the Member */
    public int getReturnType();

    /**
    * Evaluated class is the class a property or a method
    * uses to expose it's functionality.
    * Concider for example the statement Request.Cookies.
    * The Cookies is a Property which evaluates into a class.
    * Returns the evaluated class
    */
    public ASPClass getEvaluatedClass();

    public void setEvaluatedClass(ASPClass clazz);

    /**
    * Sets the arguments the method requires
    * The Member requires arguments if it is a Property and
    * the "write" part of the property is evaluated
    * or if it is a Method
    */
    public void setArgs(List args);
	
    /** Returns the arguments of the class */
    public List getArgs();

    /** Sets the class in which this method or property belongs */
    public void setClass(ASPClass clazz);

    /** Returns the class where the Member belongs to */
    public ASPClass getASPClass();

    /** Sets the class of the return value of the Member */
    public void setRetObjectClass(ASPClass clazz);

    /** Returns the class of the Object the Member returns */
    public ASPClass getRetObjectClass();

    /** Sets the code for the return type of the Member */
    public void setReturnType(int type);
}
