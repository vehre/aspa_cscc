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

public interface ASPObject {
    /**
    * Resets the internal state of the ASPObject in the initial
    * state.
    */
    public void resetState();
    /**
    * Returns the name of the ASPObject.
    */
    public String getName();
    /**
    * Adds a dependency for this ASPObject.
    * Dependencies are files which should be included
    * when the services the ASPObject provides are used.
    * @param depend is the file name which should be included.
    * If more then one file is needed then file names should be
    * separated by commas.
    */
    public void addDependency(String depend);
    /**
    * Returns the files the object depends on.
    * If no dependencies exist then an empty List is returned.
    */
    public List getDependencies();
}
