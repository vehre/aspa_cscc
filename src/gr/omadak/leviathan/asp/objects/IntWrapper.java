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

import org.apache.log4j.Logger;

public class IntWrapper {
    private static Logger LOG = Logger.getLogger(IntWrapper.class);
    private int value;

    public void incValue() {
        value++;
    }


    public void decValue() {
        if (value == 0) {
            LOG.error("value can not be less then zero");
        } else {
            value--;
        }
    }


    public int getInt() {
        return value;
    }
}
