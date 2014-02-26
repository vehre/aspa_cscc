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

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

import antlr.CommonToken;
import gr.omadak.leviathan.asp.objects.ObjectAST;
import gr.omadak.leviathan.asp.objects.ASPObject;
import gr.omadak.leviathan.asp.objects.ASPClass;
import gr.omadak.leviathan.asp.objects.ASPObjectInstance;
import gr.omadak.leviathan.asp.objects.Property;
import gr.omadak.leviathan.asp.objects.Member;
import gr.omadak.leviathan.asp.objects.Method;
import gr.omadak.leviathan.asp.objects.VbUserDefinedMethod;
import gr.omadak.leviathan.asp.objects.VbUserDefinedProperty;

import org.apache.log4j.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.collections.impl.ASTArray;
import antlr.RecognitionException;

public abstract class VBSAbstractTreeParser extends antlr.TreeParser implements TreeVbsTokenTypes, SymbolTableExposer {
	protected int lastForWhile = 1;
    protected Stack argStack = new Stack();
    protected Stack withObjects = new Stack();
    protected Map variables = new TreeMap();
    protected Map levelList = new HashMap();
    protected int cLevel = 0;
    protected AspParser parser;
    protected int lastWithVar = 1;
    protected Map localFunctions = new HashMap();
	protected Map localClasses = new HashMap();
    protected VbUserDefinedMethod currentFunction;
    protected ASPClass currentClass;
    protected Property currentProperty;
    protected Set identifiers;
    protected Set dependencies;
	protected Stack typeStack = new Stack();
	protected int lastType;
    protected Logger log = Logger.getLogger(VbsTree.class);
	//indicates if inside a redim preserve rule
	protected boolean redimp;


    protected static Map OBJECT_CLASSES;
    protected static Map FUNCTIONS;
    protected static String WITH_VAR = "WITH_VAR";
    protected static String FUNCTION_RESULT = "function_result";
    protected static Integer ALLARGS = new Integer(CommonConstants.ALL_ARGS);

    public static void setClassesAndFunctions(Map classes, Map functions) {
        OBJECT_CLASSES = classes;
        FUNCTIONS = functions;
    }

    public abstract void start_rule(AST _t) throws RecognitionException;
    
    /* Implementation of SymbolTableExposer */
    public Map getVariables() {
		/*
        HashMap variables = new HashMap(objects);
        if (identifiers != null) {
            for (Iterator it = identifiers.iterator(); it.hasNext();) {
                String id = (String) it.next();
                if (!variables.containsKey(id)) {
                    variables.put(id, null);
                }
            }
        }
        return new HashMap(objects);
		*/
		return new HashMap(variables);
    }


    public List getFunctions() {
        return new ArrayList(localFunctions.values());
    }


    public List getClasses() {
        return new ArrayList(localClasses.values());
    }


    public List getDependencies() {
        return dependencies == null ? Collections.EMPTY_LIST
        : new ArrayList(dependencies);
    }


    public void setAspParser(AspParser parser) {
        this.parser = parser;
		log = Logger.getLogger("gr.omadak.leviathan.asp.VbsTree."
							   + parser.getCurrentFileName().replace('.', '_'));
    }


    public void appendVariables(Map vars) {
        variables.putAll(vars);
    }


    public void appendFunctions(List funcs) {
        for (Iterator it = funcs.iterator(); it.hasNext();) {
            Method method = (Method) it.next();
            if (!localFunctions.containsKey(method.getName())) {
                localFunctions.put(method.getName(), method);
            }
        }
    }


    public void appendClasses(List classes) {
        for (Iterator it = classes.iterator(); it.hasNext();) {
            ASPClass clazz = (ASPClass) it.next();
            if (!localClasses.containsKey(clazz.getName())) {
                localClasses.put(clazz.getName(), clazz);
            }
        }
    }


    public void setFunctions(List functions) {
		if (functions != null) {
			for (Iterator it = functions.iterator(); it.hasNext();) {
				VbUserDefinedMethod method = (VbUserDefinedMethod) it.next();
				if (!localFunctions.containsKey(method.getName())) {
					localFunctions.put(method.getName(), method);
				}
			}
		}
	}


	public void setClasses(List classes) {
		if (classes != null) {
			for (Iterator it = classes.iterator(); it.hasNext();) {
				ASPClass clazz = (ASPClass) it.next();
				localClasses.put(clazz.getName(), clazz);
			}
		}
	}


	public void setGlobalIds(Set globals) {
        identifiers = globals;
    }


    protected ASPObjectInstance getObjectInstance(AST ast) {
        ASPObjectInstance result = null;
        if (ast instanceof ObjectAST) {
            ObjectAST oAST = (ObjectAST) ast;
            ASPObjectInstance inst = oAST.getInstance();
			ASPClass clazz = inst.getASPClass();
			if (inst.getMember() != null) {
				Member mem = inst.getMember();
				if (mem.getEvaluatedClass() != null) {
					clazz = mem.getEvaluatedClass();
				} else {
					log.error("BUG:I expected the member to be null:" + inst);
				}
			}
			result = new ASPObjectInstance(inst.getInstance(), clazz);
        } else if (ast.getType() == IDENTIFIER) {
            String className = ast.getText();
            if (OBJECT_CLASSES.containsKey(className.toUpperCase())) {
                result = new ASPObjectInstance((ASPClass) OBJECT_CLASSES.get(
                className.toUpperCase()));
            } else {
                result = getObject(className);
            }
        }
        return result;
    }


	protected void collectDependencies(Member mem) {
        List incs = mem.getDependencies();
        if (!incs.isEmpty()) {
            if (dependencies == null) {
                dependencies = new HashSet();
            }
            dependencies.addAll(incs);
        }
    }


    protected AST getObject(AST objAST, AST member) {
        ASPObjectInstance instance = getObjectInstance(objAST);
        AST result;
        if (instance != null) {
            result = getObject(instance, member);
        } else {
            log.warn("No instance found for:" + objAST.getText());
            result = null;
        }
        return result;
    }


    protected AST getObject(ASPObjectInstance instance, AST member) {
        AST result = null;
        ASPClass clazz = instance.getASPClass();
        if (clazz == null) {
            log.error("Unable to get class:" + instance.getInstance());
        } else {
            Member mem = clazz.getMemberObject(member.getText());
            if (mem == null) {
                log.error("Failed to get member:" + member.getText()
                + " from class:" + clazz.getName());
            } else {
                instance.setMember(mem);
                Token token = new CommonToken(mem.getReturnType(),
                member.getText());
                if (mem instanceof Method) {
                    List alts = clazz.getMemberList(member.getText());
                    if (alts.size() > 1) {
                        instance.setMemberList(alts);
                    }
                }
                result = new ObjectAST(token, instance);
            }
        }
        if (result == null) {
            int code = DOT;
            log.error("Failed to resolve member:" + member.getText()
            + " from class:"
            + (clazz != null ? clazz.getName() : "null"));
        }
        return result;
    }


    protected AST translateMember(Member mem, ASPObjectInstance instance) {
        AST result;
        if (mem instanceof Property) {
            Property prop  = (Property) mem;
            result = prop.read(instance == null ? null : instance.getInstance());
        } else if (mem instanceof Method) {
            Method method  = (Method) mem;
            result = method.translate(instance == null ? null : instance.getInstance());
        } else {
            result = null;
            log.error("Passed null for translateMember");
        }
        if (result != null) {
            collectDependencies(mem);
			lastType = mem.getReturnType();
        }
        return result;
    }


    protected AST translateMember(Member mem, ASPObjectInstance instance, AST value) {
        AST result;
        if (mem instanceof Property) {
            Property prop = (Property) mem;
            prop.setArgs(Collections.singletonList(value));
            result = prop.write(instance == null ? null : instance.getInstance());
            if (result != null) {
                collectDependencies(prop);
            }
			lastType = prop.getReturnType();
        } else {
            Property property;
            if (mem.getEvaluatedClass() != null) {
                property = mem.getEvaluatedClass().getDefaultProperty();
            } else {
                property = null;
            }
            if (property != null) {
                result = translateMember(property, instance, value);
            } else {
                log.error(
                "Can not assign to a method who does not have"
                + " an associated class or has a class with no "
                + " default property");
                result = null;
            }
        }
        return result;
    }


	protected int getType(AST ast) {
		int result;
		switch (ast.getType()) {
		case DINT:
		case DFLOAT:
		case DSTRING:
		case DDATE:
		case TRUE:
		case FALSE:
		case NULL:
		case NOTHING:
		case EMPTY:
			result = ast.getType();
			break;
		case IDENTIFIER:
			result = getIdentifierType(ast.getText());
			break;
		case UKNOWN_METHOD:
		case INVALID_OBJECT:
			result = CommonConstants.UKNOWN_TYPE;
			break;
		default:
			if (!typeStack.isEmpty()) {
				result = ((Integer) typeStack.pop()).intValue();
			} else {
				result = CommonConstants.UKNOWN_TYPE;
			}
			break;
		}
		return result;
	}


	protected AST getDefault(AST ast) {
        AST result = null;
        if (ast != null) {
            if (ast.getType() == IDENTIFIER && currentFunction != null
            && !FUNCTION_RESULT.equals(ast.getText())) {
                String name = ast.getText();
                boolean isGlob = identifiers.contains(name);
				currentFunction.placeVar(name, getIdentifierType(name), !isGlob);
            }
			if (ast instanceof ObjectAST || ast.getType() == IDENTIFIER) {
				result = translateObject(ast);
				//the type will be handled by translateObject(ObjectAST)
			} else {
				lastType = getType(ast);
			}
        }
		return result == null ? ast : result;
	}


    protected abstract AST createNode(int type, AST left, AST right);

    protected ASPObjectInstance setMember(Map map, String name) {
        Object obj = map.get(name);
        ASPObjectInstance result = new ASPObjectInstance();
        if (obj instanceof Method) {
            result.setMember((Method) obj);
        } else {
            result.setMemberList((List) obj);
        }
        return result;
    }


    protected ASPObjectInstance getMethod(AST ast, List args) {
        ASPObjectInstance result = null;
        if (ast instanceof ObjectAST) {
            ObjectAST oAST = (ObjectAST) ast;
            ASPObjectInstance inst = oAST.getInstance();
            AST instance = inst.getInstance();
            ASPObject obj;
             Member mem = inst.getMember();
             if (mem == null) {
                 List methods = inst.getMemberList();
                 if (methods != null) {
                     obj = getSuitableMethod(methods, args);
                 } else {
                     obj = inst.getASPClass();
                 }
            } else {
                obj = mem;
            }
            if (obj instanceof Method) {
                result = new ASPObjectInstance(instance);
                result.setMember((Method) obj);
            } else if (obj instanceof ASPClass) {
                ASPClass clazz = (ASPClass) obj;
                result = new ASPObjectInstance(instance, clazz);
                Member member = clazz.getDefaultMethod();
                if (member == null) {
                    log.error("Class:" + obj.getName()
                    + " returned [null] default method");
                } else {
                    result.setMember(member);
                }
            } else if (obj instanceof Property) {
                ASPClass clazz = ((Property) obj).getEvaluatedClass();
                if (clazz == null) {
                    log.error("Expected method but found property:"
                    + obj.getName() + " wich does not evaluate to class" );
                } else {
                    result = new ASPObjectInstance(instance, clazz);
                    Method method = clazz.getDefaultMethod();
                    if (method == null) {
                        log.warn("Class:" + clazz.getName()
                        + " evaluated from property:" + obj.getName()
                        + " has no default method");
                    } else {
                        result.setMember(method);
                    }
                }
            } else { //should be null so look for an ASPObjectInstance
                result = oAST.getInstance();
                if (result != null) {
                    if (result.getMemberList() != null) {
                        result.setMember(
                        getSuitableMethod(result.getMemberList(), args));
                    }
                }
            }
        } else if (ast.getType() == IDENTIFIER
        || ast.getType() == METHOD_CALL) {
            String fName = ast.getText();
            String uName = fName.toUpperCase();
            boolean inGlobFunctions = FUNCTIONS.containsKey(uName);
            boolean inLocalFunctions = localFunctions.containsKey(fName);
            if (inGlobFunctions || inLocalFunctions) {
                result =
                setMember(inGlobFunctions ? FUNCTIONS : localFunctions,
                inGlobFunctions ? uName : fName);
            } else if (OBJECT_CLASSES.containsKey(uName)
            || getObject(fName) != null) {
                ASPClass clazz;
                AST inst = null;
                if (OBJECT_CLASSES.containsKey(uName)) {
                    clazz = (ASPClass) OBJECT_CLASSES.get(uName);
                } else {
                    ASPObjectInstance instance = getObject(fName);
                    clazz = instance.getASPClass();
                    inst = instance.getInstance();
                }
                Method method = clazz.getDefaultMethod();
                if (method == null) {
                    log.warn("Class:" + ast.getText()
                    + " has no default method");
                } else {
                    result = new ASPObjectInstance(inst, clazz);
                    result.setMember(method);
                }
            } else {
                log.warn("Unable to locate method:" + ast.getText());
            }
        } else {
            log.error("Expected an IDENTIFIER or an ObjectAST but is:["
            + ast.getType() + ", " + ast.getText() + "]");
        }
        if (result != null && result.getMember() != null) {
            result.getMember().setArgs(args);
        }
        return result;
    }


    protected Method getSuitableMethod(List methods, List args) {
        List comply = new ArrayList();
        Method result = null;
        for (Iterator it = methods.iterator(); it.hasNext();) {
            Member mem = (Member) it.next();
            if (mem instanceof Method) {
                Method method = (Method) mem;
                List methArgs = method.getArgTypes();
                if (methArgs.size() == args.size()
                || (methArgs.size() - 1 < args.size()
                && methArgs.contains(ALLARGS))) {
                    comply.add(method);
                }
            }
        }
        switch (comply.size()) {
        case 0:
            for (Iterator it = methods.iterator(); it.hasNext();) {
                Member mem = (Member) it.next();
                if (mem instanceof Method) {
                    result = (Method) mem;
                    break;
                }
            }
            break;
        case 1:
            result = (Method) comply.get(0);
            break;
        case 2:
            Method m1 = (Method) comply.get(0);
            Method m2 = (Method) comply.get(1);
            result = m1.getArgTypes().contains(ALLARGS) ? m2 : m1;
            break;
        default:
            log.warn("More then 2 alternatives for method:"
            + ((ASPObject) comply.get(0)).getName()
            + " for args:" + args);
            result = (Method) comply.get(0);
            break;
        }
        return result;
    }


    protected AST hardTranslate(AST ast) {
        AST result = ast;
        if (ast instanceof ObjectAST) {
            ObjectAST oAST = (ObjectAST) ast;
            ASPObjectInstance instance = oAST.getInstance();
            if (instance != null) {
                result = translateObject(instance, ast.getType());
            } else {
                log.error("ast:" + ast + " has no Class or Member");
            }
        }
        return result;
    }

    protected abstract void changeType(AST ast, int type);

    protected void translateUntil(AST uExpr) {
        AST dw = uExpr.getFirstChild();
        switch (dw.getType()) {
            case LT:  changeType(dw, GE); break;
            case GT:  changeType(dw, LE); break;
            case LE:  changeType(dw, GT); break;
            case GE:  changeType(dw, LT); break;
            case NEQ: changeType(dw, EQ); break;
            case EQ: changeType(dw, NEQ); break;
            case NOT: dw = dw.getFirstChild(); break;
            default: dw = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(NOT,"not")).add(dw)); 
            	/*dw = #([NOT, "not"], dw); */ break;
        }
        uExpr.setFirstChild(dw);
    }


    protected void setAssignement() {
        Integer key = new Integer(cLevel);
        Integer val = (Integer) levelList.get(key);
        if (val == null) {
            levelList.put(key, new Integer(1));
        } else {
            int iVal = val.intValue();
            iVal |= 1;
            levelList.put(key, new Integer(iVal));
        }
    }


    protected void setExit() {
        Integer key = new Integer(cLevel);
        Integer val = (Integer) levelList.get(key);
        if (val == null) {
            levelList.put(key, new Integer(2));
        } else {
            int iVal = val.intValue();
            iVal |= 2;
            levelList.put(key, new Integer(iVal));
        }
    }


    protected boolean valueAsignedForLevel() {
        Integer key = new Integer(cLevel);
        Integer val = (Integer)  levelList.get(key);
        return val != null && (val.intValue() & 1) == 1;
    }


    protected boolean exitForLevel() {
        Integer key = new Integer(cLevel);
        Integer val = (Integer) levelList.get(key);
        return val != null && (val.intValue() & 2) == 2;
    }


    protected void placeObject(String name, ASPObjectInstance inst) {
        if (currentFunction != null) {
            currentFunction.placeObject(name, inst,
            !identifiers.contains(name) || currentFunction.hasVar(name));
        } else {
            variables.put(name, inst);
        }
    }


    protected void removeObject(String name) {
        if (currentFunction != null) {
            currentFunction.removeObject(name);
        } else {
            variables.remove(name);
        }
    }


    protected ASPObjectInstance getObject(String name) {
        ASPObjectInstance result = null;
        if (currentFunction != null) {
            result = currentFunction.getObject(name);
        }
        if (result == null) {
			Object obj = variables.get(name);
			if (obj instanceof ASPObjectInstance) {
				result = (ASPObjectInstance) variables.get(name);
			}
        }
        return result;
    }


	protected void removeVariable(String name) {
		variables.remove(name);
	}


	protected void addVariable(String name, int type) {
		if (currentFunction != null) {
			boolean local = currentFunction.hasVar(name) || !variables.containsKey(name);
			currentFunction.placeVar(name, type, local);
		} else {
			variables.put(name, new Integer(type));
		}
	}


    protected AST handleIdObject(AST id, ObjectAST obj) {
        AST result;
        ASPObjectInstance aInst = obj.getInstance();
        int retType = -1;
        if (obj.getType() == CommonConstants.OBJECT_RET && aInst.getMember() == null) {
			ASPClass clazz = aInst.getASPClass();
            result = aInst.getInstance();
			if (result == null && clazz != null && clazz.hasConstructor()) {
				result = translateMember((Method) clazz.getConstructors().get(0),
										 null);
			}
            placeObject(id.getText(), new ASPObjectInstance(
            astFactory.dup(id), aInst.getASPClass()));
            retType = 0;
        } else {
            Member member = aInst.getMember();
            if (member != null) {
                if (member.getRetObjectClass() != null) {
                    placeObject(id.getText(), new ASPObjectInstance(
                    astFactory.dup(id), member.getRetObjectClass()));
                    retType = 0;
                } else if (member instanceof Method) {
                    Method method = (Method) member;
                    if (method.isConstructor()) {
                        placeObject(id.getText(), new ASPObjectInstance(
                        astFactory.dup(id), method.getASPClass()));
                        retType = 0;
                    }
                }
                result = translateMember(member, aInst);
                retType = retType == 0 ? 0 : member.getReturnType();
            } else {
                ASPClass clazz = aInst.getASPClass();
                if (!clazz.hasConstructor()) {
                    Property prop = clazz.getDefaultProperty();
                    if (prop != null) {
                        result = translateMember(prop, aInst);
                        retType = prop.getReturnType();
                    } else {
                        log.error("handleIdObject found an ASPClass:"
                        + clazz.getName() + " with no default property");
                        result = null;
                    }
                } else {
					ASPObjectInstance instance = new ASPObjectInstance(
									astFactory.dup(id), clazz);
                    placeObject(id.getText(), instance);
                    retType = 0;
                    result = translateMember(
                    (Method) clazz.getConstructors().get(0), instance);
                }
            }
        }
		if (retType != 0) {
			if (retType == -1) {
				removeObject(id.getText());
			} else {
				addVariable(id.getText(), retType);
			}
        }
        return result;
    }


    protected AST handleWriteToObject(ObjectAST ast, AST value) {
        if (value instanceof ObjectAST || value.getType() == IDENTIFIER) {
            AST val = translateObject(value);
			if (val != null) { // if the ObjectAST can not be translated
				value = val; //keep the initial value
			}
        }
        ASPObjectInstance instance = ast.getInstance();
        AST result;
        if (instance != null) {
            if (instance.getMember() != null) {
                result = translateMember(instance.getMember(),
                instance, value);
            } else {
                ASPClass clazz = instance.getASPClass();
                Member mem = clazz.getDefaultProperty() != null
                ? (Member) clazz.getDefaultProperty()
                : (Member) clazz.getDefaultMethod();
                if (mem != null) {
                    result = translateMember(mem,
                    instance, value);
                } else {
                    log.error("class:" + clazz.getName() + " has no default"
                    + " property or method");
                    result = null;
                }
            }
        } else {
            log.error("instance was null!");
            result = null;
        }
        return result;
    }


    protected AST translateObject(ASPObjectInstance instance, int type) {
        AST result = null;
        Member mem = instance.getMember();
        if (mem != null) {
            result = translateMember(mem, instance);
        } else {
            ASPClass clazz = instance.getASPClass();
            if (clazz != null) {
                if (type == CommonConstants.OBJECT_RET) {
                    result = instance.getInstance();
                } else {
                    Property prop = clazz.getDefaultProperty();
                    if (prop != null) {
						result = translateMember(prop, instance);
                    } else {
                        log.error("class:" + clazz.getName()
                        + " has no default property");
                    }
                }
            } else {
                log.error("no member and no class set");
            }
        }
        return result;
    }


    protected AST translateObject(AST ast) {
        ASPObjectInstance instance;
        if (ast instanceof ObjectAST) {
            ObjectAST oAst = (ObjectAST) ast;
            if (oAst.getInstance() != null) {
                instance = oAst.getInstance();
            } else {
                instance = getObjectInstance(ast);
            }
        } else if (ast.getType() == IDENTIFIER) {
           instance = getObjectInstance(ast);
		   if (instance == null) {
			   lastType = getIdentifierType(ast.getText());
		   }
        } else {
            instance = null;
        }
        return instance == null ? ast : translateObject(instance, ast.getType());
    }


    protected boolean isBuiltIn(ASPClass clazz) {
        return OBJECT_CLASSES.containsKey(clazz.getName().toUpperCase());
    }


    protected AST makeFromList(List elems, AST root) {
        AST result;
        if (elems.isEmpty()) {
            result = root;
        } else {
            for (Iterator it = elems.iterator(); it.hasNext();) {
                AST element = (AST) it.next();
                element.setNextSibling(null);
            }
            elems.add(0, root);
            AST[] array = (AST[]) elems.toArray(new AST[0]);
            result = astFactory.make(array);
            elems.remove(0);
        }
        return result;
    }


    protected abstract AST handleFunctionSubEnd(AST methodAST, int lastStackSize);

	protected abstract AST translateArray(AST array, int lastStackSize);

    protected ObjectAST createObjectAST(int type, String label,
    ASPObjectInstance instance) {
        return new ObjectAST(new CommonToken(type, label), instance);
    }

    protected abstract List transform(List globals);

    //index is:0 for get, 1 for let, 2 for set
    protected void addProperty(Map props, AST root, int index) {
        String name = root.getText();
        Member mem = currentClass.getMemberObject(name);
        if (mem instanceof VbUserDefinedProperty) {
            VbUserDefinedProperty property = (VbUserDefinedProperty) mem;
            String newName;
            switch (index) {
                case 0: newName = property.getGet().getName(); break;
                case 1: newName = property.getLet().getName(); break;
                default: newName = property.getSet().getName(); break;
            }
            root.setText(newName);
            root.setType(FUNCTION);
        }
        AST[] items = (AST[]) props.get(name);
        boolean isNew = items == null;
        if (isNew) {
            items = new AST[3];
        }
        items[index] = root;
        if (isNew) {
            props.put(name, items);
        }
        currentFunction = null;
        currentProperty = null;
    }


    protected abstract void makeSub(AST subRoot);
    
    protected abstract AST makeFunction(AST funcRoot, AST statements);
    
    protected abstract AST exitFunction();

    protected VbUserDefinedMethod getProperty(String name, int type) {
        VbUserDefinedProperty prop =
        (VbUserDefinedProperty) currentClass.getMemberObject(name);
        currentProperty = prop;
        Method result;
        switch (type) {
            case 0 : result = prop.getGet(); break;
            case 1 : result = prop.getLet(); break;
            default : result = prop.getSet(); break;
        }
        return (VbUserDefinedMethod) result;
    }


	protected int getIdentifierType(String name) {
		int result;
		if (currentFunction != null && currentFunction.hasVar(name)) {
			result = currentFunction.getVariableType(name);
		} else {
			Object obj = variables.get(name);
			if (obj instanceof Integer) {
				result = ((Integer) obj).intValue();
			} else if (obj instanceof ASPObjectInstance) {
				result = CommonConstants.OBJECT;
			} else {
				result = CommonConstants.UKNOWN_TYPE;
			}
		}
		return result;
	}
}
