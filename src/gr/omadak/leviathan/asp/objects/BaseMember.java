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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import antlr.collections.AST;
import org.apache.log4j.Logger;

public abstract class BaseMember extends BaseObject implements Member {
    protected ASPClass evaluatedClass;
    protected List args;
    protected int retType;
    protected ASPClass clazz;
    protected ASPClass retClass;

    private static final Logger LOG = Logger.getLogger(BaseMember.class);

    private void collectParentArgs(XmlDependantASPClass clazz, List list) {
        Member parent = clazz.getParentMember();
        if (parent != null) {
            ASPClass pclazz = parent.getASPClass();
            if (pclazz instanceof XmlDependantASPClass) {
                collectParentArgs((XmlDependantASPClass) pclazz, list);
            }
            List args = parent.getArgs();
            if (args != null && !args.isEmpty()) {
                list.addAll(args);
            }
            parent.resetState();
        }
    }


    protected void addParentArgs() {
        if (clazz instanceof XmlDependantASPClass) {
            List arguments = new ArrayList();
            collectParentArgs((XmlDependantASPClass) clazz, arguments);
            if (args != null) {
                arguments.addAll(args);
            }
            args = arguments.isEmpty() ? null : arguments;
        }
    }


    protected AST cloneAST(AST toClone, List arguments, AST instance) {
        FACTORY.setInstanceAST(instance);
        if (LOG.isDebugEnabled()) {
            LOG.debug("factory instance:" + instance);
        }
        return super.cloneAST(toClone, arguments);
    }


    public void setClass(ASPClass clazz) {
        this.clazz = clazz;
    }


    public void setEvaluatedClass(ASPClass evaluatedClass) {
        this.evaluatedClass = evaluatedClass;
    }


    public ASPClass getEvaluatedClass() {
        return evaluatedClass;
    }


	public List getArgs() {
        return args;
    }


    public void setArgs(List args) {
        if (this.args == null) {
            this.args = args;
        } else if (args != null) {
            this.args.addAll(args);
        }
	}


    public ASPClass getASPClass() {
        return clazz;
    }


    public void resetState() {
        this.args = null;
    }


    public void setRetObjectClass(ASPClass retClass) {
        this.retClass = retClass;
    }


    public ASPClass getRetObjectClass() {
        return retClass;
    }


    public int getReturnType() {
        return retType;
    }


    public void setReturnType(int type) {
        retType = type;
    }


    public List getDependencies() {
        List result;
        if (dependencies == null && clazz == null) {
            result = Collections.EMPTY_LIST;
        } else {
            Set includes = new HashSet();
            if (dependencies != null) {
                includes.addAll(dependencies);
            }
            if (clazz != null) {
                includes.addAll(clazz.getDependencies());
            }
            result = new ArrayList(includes);
        }
        return result;
    }
}
