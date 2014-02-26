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

import org.apache.log4j.Logger;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;
import java.util.HashSet;

public abstract class VbsAbstractProducer extends antlr.TreeParser {
    protected SourceBuffer out;
    protected Set ids = new HashSet();
    protected boolean inClass;

    public SourceBuffer getBuffer() {
        return out;
    }

    public abstract void setWriter(Writer writer);
}
