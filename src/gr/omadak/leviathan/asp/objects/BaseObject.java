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
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import antlr.collections.AST;

public abstract class BaseObject implements ASPObject {
    protected String name;
    protected Set dependencies;

    protected static final ASPFactory FACTORY = new ASPFactory();
    private static final Logger LOG = Logger.getLogger(BaseObject.class);


    protected AST cloneAST(AST toClone, List arguments) {
        if (LOG.isDebugEnabled()) {
            LOG.debug((toClone == null ? "null" : toClone.toStringList())
            + ", " + arguments);
        }
        FACTORY.setArgs(arguments);
        AST result = FACTORY.dupList(toClone);
        if (LOG.isDebugEnabled()) {
            LOG.debug("result:" + (result == null ? " is null"
            : result.toStringList()));
        }
        resetState();
        if (FACTORY.hadErrors()) {
            LOG.error("Errors found when translating:" + toClone
            + " with arguments:" + arguments);
        }
        return result;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void addDependency(String depend) {
        if (depend != null && depend.length() > 0) {
            depend = depend.trim();
            StringTokenizer tokenizer = new StringTokenizer(depend, ",");
            if (dependencies == null) {
                dependencies = new HashSet();
            }
            while (tokenizer.hasMoreTokens()) {
                String include = tokenizer.nextToken();
                dependencies.add(include);
            }
        }
    }


    public List getDependencies() {
        return dependencies == null
        ? Collections.EMPTY_LIST
        : new ArrayList(dependencies);
    }
}
