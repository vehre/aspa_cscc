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
package gr.omadak.leviathan.asp;

import java.util.List;
import java.util.Map;

public interface SymbolTableExposer {
    /**
    * Provides access to variables which where discovered
    * during parsing.Each key is the variable name.
    * If the value is not null then the value is an ASPObjectInstance
    * object.If is null then it is either a primitive, or it's type
    * is uknown.
    * If no variable is found then an empty Map is returned.
    */
    public Map getVariables();
    /**
    * Provides access to the functions found during parsing.
    * Each element is either a JsUserDefinedMethod or VbUserDefinedMethod
    * instance.
    * If no function was found, returns an empty List.
    */
    public List getFunctions();
    /**
    * Returns the classes found.
    * If no classes where found then an empty List is returned.
    */
    public List getClasses();
    /**
    * Returns the files which should be included
    * in the generated code.
    */
    public List getDependencies();
    /**
    * Sets the AspParser instance which is used for parsing of
    * included files.
    */
    public void setAspParser(AspParser parser);
    /**
    * Sets the variables discoverd by an other SymbolTableExposer
    * in this instance.
    */
    public void appendVariables(Map variables);
    /**
    * Sets the functions discoverd by an other SymbolTableExposer
    * in this instance.
    */
    public void appendFunctions(List funcs);
    /**
    * Sets the classes discoverd by an other SymbolTableExposer
    * in this instance.
    */
    public void appendClasses(List classes);
}
