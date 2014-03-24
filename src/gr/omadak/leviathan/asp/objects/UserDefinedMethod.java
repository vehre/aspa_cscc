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
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import gr.omadak.leviathan.asp.CommonConstants;

import org.apache.log4j.Logger;

public abstract class UserDefinedMethod extends GenericMethod {
    /**keeps the local variables*/
    protected Map localObjects;
    /**keeps the global variables*/
    protected Map globalObjects;
    protected int NULL;
    protected int METHOD_CALL;
    protected int DOT;
    protected int ARGLIST_VALUES;
    protected int NEW;
	private static Logger LOG = Logger.getLogger(UserDefinedMethod.class);


    private void correctArgs() {
        //the missing parameters will be replaced by NULL
        int argCount = getArgCount();
        if (args == null && argCount == 0) {
            args = Collections.EMPTY_LIST;
        } else {
            if (args == null) {
                args = new ArrayList();
            }
            if (args.size() != getArgCount()) {
                if (args.size() < getArgCount()) {
                    args = new ArrayList(args);
                    while (args.size() < getArgCount()) {
                        args.add(FACTORY.create(NULL, "null"));
                    }
                } else {
                    args = args.subList(0, getArgCount());
                }
            }
        }
    }


    /**
	 * Creates a new <code>UserDefinedMethod</code> instance.
	 *
	 * @param name the method's name
	 * @param args the names of the arguments the method receives
	 */
	public UserDefinedMethod(String name, List args) {
        super(name, CommonConstants.UKNOWN_TYPE, args, null);
		if (!args.isEmpty()) {
            localObjects = new TreeMap();
            for (Iterator it = args.iterator(); it.hasNext();) {
                AST id = (AST) it.next();
                localObjects.put(id.getText(), null);
            }
        }
    }

    /**
    * Checks if the method contains an initialized
    * variable which contains an Object reference
    * @param name is the name of the variable
    * @return true if the method contains a variable
    * with this name which holds an Object reference
    */
    public boolean objectExists(String name) {
        return localObjects != null
			&& localObjects.get(name) instanceof ASPObjectInstance;
    }


    /**
    * Checks if the method has a variable with
    * this name.
    * @param name is the name of the variable
    * @return true if the method knows
    * the variable.
    */
    public boolean hasVar(String name) {
        return localObjects != null
        && localObjects.containsKey(name);
    }

    /**
    * Get the type of the local object. 
    * @param name is the name of the variable
    * @return The type of the variable, or unknown_type, if the variable is not present.
    */
    public int getVariableType(String name) {
    	if ( localObjects == null)
    		return CommonConstants.UKNOWN_TYPE; 
    	Object obj = localObjects.get(name);
        return obj == null || !(obj instanceof Integer)? CommonConstants.UKNOWN_TYPE : ((Integer) obj).intValue();
    }
    
	private void place(String name, Object obj, boolean local) {
		if (local) {
			if (localObjects == null) {
				localObjects = new HashMap();
			}
			localObjects.put(name, obj);
		} else {
			if (globalObjects == null) {
				globalObjects = new HashMap();
			}
			globalObjects.put(name, obj);
		}
	}


    /**
    * Initializes or places a new variable initialized with instance.
    * @param name is the name of the variable
    * @param instance is the Object reference
    * @param local indicates if it is a local object or a global one
    */
    public void placeObject(String name, ASPObjectInstance inst,
    boolean local) {
		place(name, inst, local);
    }


	/**
	 * Adds a variable to the function
	 *
	 * @param name is the name of the variable to add
	 * @param type the type of the variable
	 * @param local indicates if it is a local variable
	 */
	public void placeVar(String name, int type, boolean local) {
		place(name, new Integer(type), local);
	}


    /**
    * Removes a local object.
    */
    public ASPObjectInstance removeObject(String name) {
        return localObjects == null ? null
        : (ASPObjectInstance) localObjects.remove(name);
    }


    /**
    * Locates an object with same name as the parameter.
    * @param name the name of the object
    * @return the object or null if failed to locate an object
    * with that name.
    */
    public ASPObjectInstance getObject(String name) {
		Object obj = localObjects == null ? null : localObjects.get(name);
		return obj instanceof ASPObjectInstance ? (ASPObjectInstance) obj : null;
    }


    /**
	 * Returns the names of the global variables this method
	 * used
	 * @return a List with the names
	 */
	public List getUsedGlobals() {
        return globalObjects == null ? Collections.EMPTY_LIST
        : new ArrayList(globalObjects.keySet());
    }


    public Map getGlobalObjects() {
        return globalObjects == null ? Collections.EMPTY_MAP : globalObjects;
    }


    /**
	 * Tells if the method contains an arg with this name
	 *
	 * @param name the name of the argument
	 * @return true if it has that arg
	 */
	public boolean hasArg(String name) {
        boolean result = false;
        Iterator it = getArgTypes().iterator();
        while (it.hasNext() && !result) {
            AST arg = (AST) it.next();
            result = name.equals(arg.getText());
        }
        return result;
    }

    /**
	 * Generates the AST of the method call
	 *
	 * @param ast is the instance of the class where the method belongs.
	 * if the method does not belong to a class the param is null
	 * @return the translation of the method call
	 */
	public AST translate(AST ast) {
        AST template;
        correctArgs();
        if (args.isEmpty()) {
            template = FACTORY.create(METHOD_CALL, name);
        } else {
            template = FACTORY.make(new AST[] {
            FACTORY.create(METHOD_CALL, name),
            FACTORY.create(ARGLIST_VALUES, "ARGLIST_VALUES")
            });
        }
        if (args != null) {
            AST argList = template.getFirstChild();
            for (Iterator it = args.iterator(); it.hasNext();) {
                AST node = (AST) it.next();
                node.setNextSibling(null);
                argList.addChild(node);
            }
        }
        AST result;
        if (ast != null) {
            result = FACTORY.make(
            new AST[] {
                FACTORY.create(DOT, "DOT"),
                FACTORY.dupTree(ast),
                template
            });
        } else {
            result = template;
        }
        if (isConstructor()) {
            result = FACTORY.make(new AST[] {
                FACTORY.create(NEW, "new"),
                result
            });
        }
        resetState();
        return result;
    }
}
