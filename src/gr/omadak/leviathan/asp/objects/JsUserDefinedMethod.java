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
import gr.omadak.leviathan.asp.TreeJsTokenTypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;

public class JsUserDefinedMethod extends UserDefinedMethod {

    /**
     keeps the variables declared in the parent method which where
     alterered in this method
    */
    private Map parentAlteredVars;
    /** The parent method */
    private JsUserDefinedMethod outerMethod;
    /** Functions declared inside this function */
    private Set localFunctions;
    /** True if the function defined in a context like var id = function() {} */
    private boolean definedInVar;
    /**
    True if the function is anonymous and is assigned on a variable like
    id = function() {}
    or
    var id = function() {}
    */
    private boolean definedAsVar;
    /** Counts the number of calls made */
    private int callCount;
    /** True if the definition of the function is complete */
    private boolean definitionFinished;
    /** True if the expression parsed is precedented with a var statement */
    private boolean inVar;
    /**
    Keeps the expressions declared inside of a var statement
    inside function scope
    */
    private List var_expressions;
    /** The Root node of the function in the AST tree */
    private AST rootNode;
    private Set usedVars = new TreeSet();

   private void addLocalFunction(JsUserDefinedMethod child) {
        if (localFunctions == null) {
            localFunctions = new HashSet();
        }
        localFunctions.add(child);
    }


    private void removeVarFromChildren(String name) {
        if (localFunctions != null) {
            for (Iterator it = localFunctions.iterator(); it.hasNext();) {
                JsUserDefinedMethod method = (JsUserDefinedMethod) it.next();
                if (method.globalObjects != null) {
                    method.globalObjects.remove(name);
                }
            }
        }
    }


    private void addObject(String name, ASPObjectInstance instance,
    boolean local) {
        if (local) {
            localObjects = addObject(name, instance, localObjects);
            removeVarFromChildren(name);
        } else {
            globalObjects = addObject(name, instance, globalObjects);
        }
    }


    private Map addObject(String name, ASPObjectInstance instance, Map to) {
        if (to == null) {
            to = new TreeMap();
        }
        to.put(name, instance);
        usedVars.add(name);
        return to;
    }


    private void cleanUp() {}


    private void addToParent() {
        outerMethod.addLocalFunction(this);
    }


    private void handleParentVars() {
        for (Iterator it = parentAlteredVars.keySet().iterator();
        it.hasNext();) {
            JsUserDefinedMethod method = (JsUserDefinedMethod) it.next();
            Map[] vars = (Map[]) parentAlteredVars.get(method);
            Map addedObjects = vars[0];
            Map removedObjects = vars[1];
            if (addedObjects != null) {
                if (method.localObjects == null) {
                    method.localObjects = addedObjects;
                } else {
                    method.localObjects.putAll(addedObjects);
                }
            }
            if (removedObjects != null) {
                for (Iterator varIt = removedObjects.keySet().iterator();
                varIt.hasNext();) {
                    method.localObjects.remove(varIt.next());
                }
            }
        }
    }


    private void handleGlobalObjects() {
        if (globalObjects != null && outerMethod != null
        && !outerMethod.definitionFinished) {
            /*
            The method is called inside it's parent so the global
            objects of this method should be exposed to it's parent as
            well
            */
            if (outerMethod.globalObjects == null) {
                outerMethod.globalObjects = new TreeMap(globalObjects);
            } else {
                outerMethod.globalObjects.putAll(globalObjects);
            }
        }
    }


    public JsUserDefinedMethod(String name, List args, boolean inVar) {
        super(name, args);
        this.definedInVar = inVar;
        NULL = TreeJsTokenTypes.NULL;
        METHOD_CALL = TreeJsTokenTypes.METHOD_CALL;
        DOT = TreeJsTokenTypes.DOT;
        ARGLIST_VALUES = TreeJsTokenTypes.ELIST;
        NEW = TreeJsTokenTypes.NEW;
    }


    public void setParentFunction(JsUserDefinedMethod papa) {
        outerMethod = papa;
        if (name != null) {
            addToParent();
        }
    }


    public void setName(String name) {
        boolean wasNull = this.name == null;
        super.setName(name);
        if (wasNull && outerMethod != null) {
            addToParent();
        }
    }


    public JsUserDefinedMethod getParentFunction() {
        return outerMethod;
    }


    public List getUsedGlobals() {
        List result = super.getUsedGlobals();
        for (ListIterator li = result.listIterator(); li.hasNext();) {
            String varName = (String) li.next();
            if (!usedVars.contains(varName)
            || hasVar(varName)) {
                li.remove();
            }
        }
        return result;
    }


    /**
    * Checks if the method or it's parent contains an initilized
    * variable which contains an Object reference
    * @param name is the name of the variable
    * @return true if the method or any of it's parents contains a variable
    * with this name which holds an Object reference
    */
    public boolean objectExists(String name) {
        return super.objectExists(name)
        || (outerMethod != null && outerMethod.objectExists(name));
    }


    /**
    * Checks if the method or it's parent has a variable with
    * this name.
    * @param name is the name of the variable
    * @return true if the method or any of it's parents knows
    * the variable.
    */
    public boolean hasVar(String name) {
        return super.hasVar(name)
        || (outerMethod != null && outerMethod.hasVar(name));
    }


    /**
    * Initilizes or places a new variable initilized with instance.
    * @param name is the name of the variable
    * @param instance is the Object reference
    */
    public void placeObject(String name, ASPObjectInstance instance,
    boolean local) {
        if (local) {
            addObject(name, instance, true);
        } else {
            boolean papaHasIt = false;
            JsUserDefinedMethod method = outerMethod;
            while (method != null) {
                if (method.localObjects != null
                && method.localObjects.containsKey(name)) {
                    papaHasIt = true;
                    Map altered;
                    if (parentAlteredVars == null
                    || !parentAlteredVars.containsKey(method)) {
                        if (parentAlteredVars == null) {
                            parentAlteredVars = new HashMap();
                        }
                        altered = new TreeMap();
                        parentAlteredVars.put(method,
                        new Map[] {altered, null});
                    } else {
                        Map[] vars = (Map[]) parentAlteredVars.get(method);
                        if (vars[0] == null) {
                            altered = new TreeMap();
                            vars[0] = altered;
                        } else {
                            altered = vars[0];
                        }
                    }
                    altered.put(name, instance);
                    break;
                }
                method = method.outerMethod;
            }
            if (!papaHasIt) {
                addObject(name, instance,
                localObjects != null && localObjects.containsKey(name));
            }
        }
    }


    public ASPObjectInstance removeObject(String name) {
        ASPObjectInstance result;
        if (localObjects != null && localObjects.containsKey(name)) {
            result = (ASPObjectInstance) localObjects.remove(name);
        } else {
            JsUserDefinedMethod method = outerMethod;
            result = null;
            while (method != null) {
                if (method.localObjects != null
                && method.localObjects.get(name) != null) {
                    result = (ASPObjectInstance) method.localObjects.get(name);
                    Map altered;
                    if (parentAlteredVars == null
                    || !parentAlteredVars.containsKey(method)) {
                        if (parentAlteredVars == null) {
                            parentAlteredVars = new HashMap();
                        }
                        altered = new TreeMap();
                        parentAlteredVars.put(method,
                        new Map[] {null, altered});
                    } else {
                        Map[] vars = (Map[]) parentAlteredVars.get(method);
                        if (vars[1] == null) {
                            altered = new TreeMap();
                            vars[1] = altered;
                        } else {
                            altered = vars[1];
                        }
                    }
                    altered.put(name, result);
                    break;
                }
                method = method.outerMethod;
            }
        }
        return result;
    }


    public ASPObjectInstance getObject(String name) {
        JsUserDefinedMethod method = this;
        ASPObjectInstance result = null;
        while (method != null) {
            if (method.localObjects != null
            && method.localObjects.containsKey(name)) {
                result = (ASPObjectInstance) method.localObjects.get(name);
                break;
            } else {
                method = method.outerMethod;
            }
        }
        return result;
    }


    public void placeVar(String name, int type, boolean local) {
        local = local || (localObjects != null
        && localObjects.containsKey(name));
        if (local) {
            usedVars.add(name);
			if (!hasVar(name)) {
				super.placeVar(name, type, local);
				removeVarFromChildren(name);
			}
        } else if (globalObjects == null || !globalObjects.containsKey(name)) {
            usedVars.add(name);
            super.placeVar(name, type, false);
        }
    }


    public void removeVariable(String name) {
        if (localObjects != null && localObjects.containsKey(name)
        && localObjects.get(name) == null) {
            localObjects.remove(name);
            usedVars.remove(name);
        }
    }


    public AST translate(AST ast) {
        callCount++;
        if (callCount == 1) {
            if (parentAlteredVars != null) {
                handleParentVars();
            }
            handleGlobalObjects();
        }
        return super.translate(ast);
    }


    public Iterator getLocalFunctions() {
        return localFunctions == null
        ? IteratorUtils.EMPTY_ITERATOR
        : IteratorUtils.filteredIterator(localFunctions.iterator(),
        new Predicate() {
            public boolean evaluate(Object ob) {
                JsUserDefinedMethod method = (JsUserDefinedMethod) ob;
                return method.definedAsVar && !method.definedInVar;
            }
        });
    }


    public JsUserDefinedMethod getLocalFunction(String name) {
        JsUserDefinedMethod result;
        if (localFunctions != null) {
            result = null;
            for (Iterator it = localFunctions.iterator(); it.hasNext();) {
                JsUserDefinedMethod method = (JsUserDefinedMethod) it.next();
                if (name.equals(method.getName())) {
                    result = method;
                    break;
                }
            }
        } else {
            result = null;
        }
        return result;
    }


    public boolean isAccessibleOutsideParent() {
        return (!definedInVar && definedAsVar) || outerMethod == null;
    }


    public void setDefinedAsVar(boolean varDefined) {
        definedAsVar = varDefined;
    }


    public void setDefinitionFinished(boolean definitionFinished) {
        this.definitionFinished = definitionFinished;
        if (definitionFinished) {
            cleanUp();
        }
    }


    public void setDefinedInVar(boolean var) {
		definedInVar = var;
	}


	public void setInVar(boolean inVar) {
        this.inVar = inVar;
    }


    public boolean isInVar() {
        return inVar;
    }


    public void addVarExpression(AST expr) {
        if (var_expressions == null) {
            var_expressions = new ArrayList();
        }
        var_expressions.add(expr);
    }


    public void setClass(ASPClass clazz) {
        if (this.clazz == null
        || !this.clazz.getName().equals(clazz.getName())) {
            setConstructor(false);
            this.clazz = clazz;
        }
    }


    public ASPClass createClass() {
        if (clazz == null) {
            if (name != null) {
                clazz = new XmlASPClass(name);
            } else {
                clazz = new XmlASPClass();
            }
            clazz.addConstructor(this);
            clazz.setCaseSensitive(true);
            setConstructor(true);
        }
        return clazz;
    }


    public ASPClass getASPClass() {
        return outerMethod != null ? outerMethod.getASPClass() : clazz;
    }


    public List getVarExpressions() {
        return var_expressions;
    }


    public boolean isFirstCall() {
        return callCount == 1;
    }


    public AST getRootAST() {
        return rootNode;
    }


    public void setRootAST(AST root) {
        rootNode = root;
    }


    public String toString() {
		return getName();
	}
}
