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

public interface Property extends Member {
    public AST read(AST instance);
    public AST write(AST instance);
    public void setArgType(int type);
    /**
    * Sets the name of the method which it's arguments
    * are affected by an assignment to this property
    *@param method is the method indicated by the property
    *@param index is the argument's index in the method
    */
    public void setIndicatedMethod(Method method, int index);
    
    /**
    * Returns true if the Property supports the read process
    */
    public boolean canRead();
    /**
    * Returns true if the Property supports the write process
    */
    public boolean canWrite();
}

