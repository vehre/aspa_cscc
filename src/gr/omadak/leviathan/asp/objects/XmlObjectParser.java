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

import antlr.ASTFactory;
import antlr.collections.AST;
import gr.omadak.leviathan.asp.CommonConstants;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlObjectParser {
    private static ASTFactory factory = new ASTFactory();
    private static Logger LOG = Logger.getLogger(XmlObjectParser.class);

    private Map types;
    private Map registeredObjects;
    private int lastArgType;
    private int objectType;
    private int astArgType;
    private int translateRoot;
    private int lastIndex;
    private String url;
    private Map idMethods;
    private String lastState;

    public XmlObjectParser(Map types) {
        this.types = types;
        try {
            objectType = getType("OBJECT");
            astArgType = getType("AST_ARG");
            translateRoot = getType("TRANSLATE_ROOT");
        } catch (DocumentException doc) {
            throw new RuntimeException("Failed to get basic types"
            + ":OBJECT, AST_ARG, TRANSLATE_ROOT");
        }
    }


    public XmlObjectParser(Map types, Map objects) {
        this(types);
        registeredObjects = objects;
    }


    public void configure(GenericClass clazz, URL url)
    throws DocumentException {
        Document doc = loadDocument(url);
        this.url = url.toString();
        Element root = doc.getRootElement();
        if (root != null) {
            configureClass(clazz, root);
            if (idMethods != null) {
                for (Iterator it = idMethods.keySet().iterator();
                it.hasNext();) {
                    Object key = it.next();
                    Object[] value = (Object[]) idMethods.get(key);
                    Method indicated = (Method) value[0];
                    Property prop = (Property) value[1];
                    if (indicated != null && prop != null && value[2] != null) {
                        int index = ((Integer) value[2]).intValue();
                        prop.setIndicatedMethod(indicated, index);
                    } else {
                        LOG.warn("Failed to configure indicated method:"
                        + (indicated == null ? "null" : indicated.getName())
                        + " indicated by property:"
                        + (prop == null ? "null" : prop.getName())
                        + " with index:" + value[2]);
                    }
                }
                idMethods.clear();
                idMethods = null;
            }
        }
    }


    public void configureMethods(Map placeIn, URL url)
    throws DocumentException {
        Document doc = loadDocument(url);
        Element root = doc.getRootElement();
        List classes = doc.selectNodes("class");
        Map classMap = classes.isEmpty()
        ? Collections.EMPTY_MAP : collectInnerClasses(classes);
        List methods = root.selectNodes("method");
        StringBuffer errors = new StringBuffer();
        for (Iterator it = methods.iterator(); it.hasNext();) {
            Element methodDef = (Element) it.next();
            try {
                Method method = configureMethod(methodDef, classMap);
                String name = method.getName().toUpperCase();
                if (placeIn.containsKey(name)) {
                    Object obj = placeIn.get(name);
                    if (obj instanceof List) {
                        ((List) obj).add(method);
                    } else {
                        List item = new ArrayList();
                        item.add(obj);
                        item.add(method);
                        placeIn.put(name, item);
                    }
                } else {
                    placeIn.put(name, method);
                }
            } catch (DocumentException docex) {
                errors.append(docex).append('\n');
            }
        }
        if (errors.length() > 0) {
            throw new DocumentException(url + "\n" + errors.toString());
        }
    }


    private void configureClass(GenericClass clazz, Element element)
    throws DocumentException {
        String clazzName = element.attributeValue("name");
        if (clazzName != null) {
            clazz.setName(clazzName);
        }
        boolean standalone =
        !Boolean.valueOf(element.attributeValue("dependant")).booleanValue()
        && element.attributeValue("standalone") != null
        && Boolean.valueOf(element.attributeValue("standalone")).booleanValue();
        clazz.setStandalone(standalone);
        List innerClasses = element.selectNodes("class");
        Map innerMap = innerClasses.isEmpty()
        ? Collections.EMPTY_MAP : collectInnerClasses(innerClasses);
        List properties = element.selectNodes("property");
        for (Iterator it = properties.iterator(); it.hasNext();) {
            configureProperty(clazz, (Element) it.next(), innerMap);
        }
        List methods = element.selectNodes("method");
        for (Iterator it = methods.iterator(); it.hasNext();) {
            configureMethod(clazz, (Element) it.next(), innerMap);
        }
        List constructors = element.selectNodes("constructor");
        for (Iterator it = constructors.iterator(); it.hasNext();) {
            configureConstructor(clazz, (Element) it.next(), innerMap);
        }
        if (clazz instanceof XmlASPClass)  {
            XmlASPClass xmlClass = (XmlASPClass) clazz;
            List delete = element.selectNodes("delete");
            if (!delete.isEmpty()) {
                if (delete.size() > 1) {
                    LOG.warn("Only first delete node is used");
                }
                Element elDelete = (Element) delete.get(0);
                AST deleteAST = translate(elDelete, null, false, 0);
                xmlClass.setDeleteAST(deleteAST);
            }
        }
        configureInclude(clazz, element);
    }


    private Map collectInnerClasses(List classes) throws DocumentException {
        Map result = new HashMap();
        for (Iterator it = classes.iterator(); it.hasNext();) {
            Element classElement = (Element) it.next();
            XmlDependantASPClass clazz = new XmlDependantASPClass();
            configureClass(clazz, classElement);
            result.put(clazz.getName().toUpperCase(), clazz);
        }
        return result;
    }


    private Document loadDocument(URL url) throws DocumentException {
        return new SAXReader().read(url);
    }


    private ASPClass getUseClass(String use, Map innerClasses) {
        use = use.trim().toUpperCase();
        Object val = registeredObjects.get(use);
        if (val == null) {
            LOG.debug("Class:" + use
            + " not found among the registered onjects");
            val = innerClasses.get(use);
            if (val == null) {
                LOG.error("use attribute:" + use
                + " does not define an ASPClass");
            }
        }
        ASPClass result;
        if (val instanceof ASPClass) {
            result = (ASPClass) val;
        } else {
            result = null;
        }
        return result;
    }


    private void configureConstructor(GenericClass clazz, Element elem,
    Map innerClasses) throws DocumentException {
        elem.addAttribute("name", clazz.getName());
        elem.addAttribute("type", "CONSTRUCTOR");
        Method constr = configureMethod(elem, innerClasses);
        clazz.addConstructor(constr);
    }


    private Method configureMethod(Element methodEl, Map innerClasses)
    throws DocumentException {
        String methodName = methodEl.attributeValue("name");
        assertValue(methodName, "Name not specified for method");
        String use = methodEl.attributeValue("use");
        Method method = null;
        String type = methodEl.attributeValue("type");
        assertValue(type, "Method type not specified");
        int dType;
        ASPClass retClass = null;
        try {
            dType = getType(type);
        } catch (DocumentException de) {
            dType = objectType;
            retClass = getUseClass(type, innerClasses);
            if (retClass == null) {
                throw new DocumentException("type:" + type
                + " is not recognized and is not a known class");
            }
        }
        if (use != null || methodEl.hasContent()) {
            String id = methodEl.attributeValue("id");
            if (use != null) {
                ASPDependantClass aspClazz =
                (ASPDependantClass) getUseClass(use, innerClasses);
                if (aspClazz != null) {
                    ASPMethodWrapper mWrapper = new ASPMethodWrapper(aspClazz);
                    method = mWrapper;
                    mWrapper.setName(methodName);
                    mWrapper.setReturnType(dType);
                }
            } else {
                List args = getArgs(methodEl);
                Element map = methodEl.element("map");
                if (map != null) {
                    method = new ASPMethodMap(methodName, dType, args);
                    configureMethodMap((ASPMethodMap) method, map,
                    innerClasses);
                } else {
                    AST translation = translate(methodEl, null, false, 0);
                    method = new GenericMethod(methodName, dType, args,
                    translation);
                    if (dType == objectType) {
                        method.setEvaluatedClass(
                        (ASPClass) registeredObjects.get(type.toUpperCase()));
                    }
                }
            }
            if (id != null) {
                if (idMethods == null) {
                    idMethods = new HashMap();
                    idMethods.put(id, new Object[] {method, null, null});
                } else {
                    Object[] mem = (Object[]) idMethods.get(id);
                    mem[0] = method;
                }
            }
            if (retClass != null) {
                method.setRetObjectClass(retClass);
            }
        }
        if (method != null) {
            configureInclude(method, methodEl);
        }
        return method;
    }


    private void configureMethod(GenericClass clazz, Element methodEl,
    Map innerClasses) throws DocumentException {
        Method method = configureMethod(methodEl, innerClasses);
        if (method != null) {
            boolean isDefault = Boolean.valueOf(
            methodEl.attributeValue("default")).booleanValue();
            if (isDefault) {
                if (clazz instanceof XmlASPClass) {
                    ((XmlASPClass) clazz).setDefaultMethod(method);
                } else {
                    LOG.error(
                    "Can not set default method for non XmlASPClass instances");
                }
            } else {
                clazz.addMember(method);
            }
        } else {
            throw new DocumentException(url + ": Failed to create method:"
            + methodEl.getText());
        }
    }


    private void configureMethodMap(ASPMethodMap mMap, Element map,
    Map innerClasses) throws DocumentException {
        List keys = map.selectNodes("key");
        for (Iterator it = keys.iterator(); it.hasNext();) {
            Element keyEl = (Element) it.next();
            String key = keyEl.attributeValue("value");
            boolean isNStr = false;
            boolean isDefault = false;
            if (key == null) {
                isNStr = Boolean.valueOf(
                keyEl.attributeValue("nstr")).booleanValue();
                isDefault = !isNStr;
            }
            if (keyEl.attributeValue("use") != null) {
                if (isNStr || isDefault) {
                    throw new DocumentException(
                    "use attribute can be defined for String cases only");
                }
                ASPClass useClass = getUseClass(keyEl.attributeValue("use"),
                innerClasses);
                if (useClass == null) {
                    throw new DocumentException("Failed to resolve class:"
                    + keyEl.attributeValue("use"));
                }
                mMap.addCase(key, useClass);
            } else {
                AST trans = translate(keyEl, null, false, 0);
                if (trans == null) {
                    throw new DocumentException(url
                    + " : Defined null ast for key:" + key);
                } else {
                    if (isDefault) {
                        mMap.setDefaultCase(trans);
                    } else if (isNStr) {
                        mMap.setNonStringCase(trans);
                    } else {
                        mMap.addCase(key, trans);
                    }
                }
            }
        }
        mMap.setCaseSensitive(Boolean.valueOf(
        map.attributeValue("casesensitive")).booleanValue());
    }


    private List getArgs(Element el) throws DocumentException {
        List result = new ArrayList();
        for (Iterator it = el.elementIterator(); it.hasNext();) {
            Element arg = (Element) it.next();
            if ("arg".equals(arg.getName())) {
                String type = arg.attributeValue("type");
                assertValue(type, "Type of arg is required");
                int objType = getType(type);
                result.add(new Integer(objType));
            } else if ("args".equals(arg.getName())) {
                result.add(new Integer(CommonConstants.ALL_ARGS));
            }
        }
        return result;
    }


    private void assertValue(String value, String msg)
    throws DocumentException {
        if (value == null) {
            throw new DocumentException(url + ":" + msg);
        }
    }


    private int getType(String name) throws DocumentException {
        Integer iType = (Integer) types.get(name);
        if (iType == null) {
            if (registeredObjects != null
            && registeredObjects.containsKey(name.toUpperCase())) {
                return objectType;
            } else {
                throw new DocumentException(url
                + " : code not found for type:" + name);
            }
        }
        return iType.intValue();
    }


    private void configureInclude(ASPObject obj, Element objDef)
    throws DocumentException {
        String dep = objDef.attributeValue("requires");
        if (dep != null) {
            obj.addDependency(dep);
        }
    }


    private void configureProperty(GenericClass clazz, Element propertyEl,
    Map innerClasses) throws DocumentException {
        lastArgType = -1;
        String propName = propertyEl.attributeValue("name");
        assertValue(propName, "Name not specified for property");
        String use = propertyEl.attributeValue("use");
        Property property = null;
        boolean isDefault = Boolean.valueOf(
        propertyEl.attributeValue("default")).booleanValue();
        lastState = null;
        if (use != null) {
            ASPDependantClass aspClazz =
            (ASPDependantClass) getUseClass(use, innerClasses);
            if (aspClazz != null) {
                property = new ASPPropertyWrapper(aspClazz);
                ((ASPPropertyWrapper) property).setName(propName);
            }
        } else {
            String type = propertyEl.attributeValue("type");
            assertValue(type, "Property type not specified");
            int dType;
            ASPClass retClass = null;
            try {
                dType = getType(type);
            } catch (DocumentException de) {
                retClass = getUseClass(type, innerClasses);
                if (retClass == null) {
                    throw new DocumentException("type:" + type
                    + " is not recognized and is not a known class");
                }
                dType = objectType;
            }
            Element read = propertyEl.element("read");
            AST readAST = null;
            AST writeAST = null;
            if (read != null) {
                readAST = translate(read, null, false, 0);
            }
            Element write = propertyEl.element("write");
            if (write != null) {
                writeAST = translate(write, null, true, 0);
            }
            property =
            new GenericASPProperty(propName, readAST, writeAST, dType);
            if (retClass != null) {
                property.setRetObjectClass(retClass);
            }
            if (lastArgType != -1) {
                property.setArgType(lastArgType);
            }
        }
        if (property != null) {
            if (lastState != null) {
                if (idMethods == null) {
                    idMethods = new HashMap();
                    idMethods.put(lastState,
                    new Object[] {null, property, new Integer(lastIndex)});
                } else {
                    Object[] obj = (Object[]) idMethods.get(lastState);
                    obj[1] = property;
                    obj[2] = new Integer(lastIndex);
                }
            }
            lastState = null;
            if (isDefault) {
                if (clazz instanceof XmlASPClass) {
                    ((XmlASPClass) clazz).setDefaultProperty(property);
                } else {
                    LOG.error(
                    "Can not set default property for"
                    + " non XmlASPClass instances");
                }
            } else {
                clazz.addMember(property);
            }
            configureInclude(property, propertyEl);
        } else {
            throw new DocumentException(url + " : Failed to create Property:"
            + propertyEl.getText());
        }
    }


    private AST createNode(int type, String name) {
        AST result = factory.create(type, name);
        if (name == null) {
            LOG.error("Passed null", new Exception("Test"));
        }
        return result;
    }


    private AST translate(Element el, AST root, boolean evalFirstLevelArgs,
    int cDepth) throws DocumentException {
        List astChildren = new ArrayList();
        boolean firstAST = cDepth > 0;
        for (Iterator it = el.elementIterator(); it.hasNext();) {
            Element ch = (Element) it.next();
            if ("ast".equals(ch.getName())) {
                firstAST = true;
                String type = ch.attributeValue("name");
                String text = ch.attributeValue("text");
                assertValue(type, "Name of ast elements is required");
                text = text == null ? type : text;
                int dType = getType(type);
                AST node = createNode(dType, text);
                if (ch.elementIterator().hasNext()) {
                    translate(ch, node, evalFirstLevelArgs, cDepth + 1);
                }
                astChildren.add(node);
            } else if ("arg".equals(ch.getName())) {
                if (root == null) {
                    if (evalFirstLevelArgs) {
                        String type = ch.attributeValue("type");
                        assertValue(type, "Type of arg is required");
                        int objType = getType(type);
                        lastArgType = objType;
                    }
                } else {
                    String index = ch.attributeValue("index");
                    assertValue(index, "Argument index should be specified");
                    try {
                        Integer.parseInt(index);
                    } catch (NumberFormatException nfe) {
                        throw new DocumentException(url + " : Invalid value:"
                        + index + " for attribute index");
                    }
                    astChildren.add(createNode(astArgType, index));
                }
            } else if ("args".equals(ch.getName())) {
                if (firstAST) {
                    astChildren.add(ch.attributeValue("mode") == null
                    ? factory.create(CommonConstants.ALL_ARGS)
                    : createNode(CommonConstants.ALL_ARGS,
                    ch.attributeValue("mode")));
                }
            } else if ("this".equals(ch.getName())) {
                astChildren.add(createNode(CommonConstants.INSTANCE, "this"));
            } else if ("state".equals(ch.getName())) {
                String id = ch.attributeValue("methodId");
                assertValue(id, "The id of the method affected is not defined");
                String index = ch.attributeValue("index");
                assertValue(index,
                "The index of the argument for the affected method "
                + "is not defined");
                try {
                    lastIndex = Integer.parseInt(index) - 1;
                } catch (NumberFormatException nfe) {
                    throw new DocumentException(
                    "The index for the affected mathod is invalid:" + index);
                }
                lastState = id;
            } else {
                System.err.println("Unexpected element:" + ch.getName());
            }
        }
        if (astChildren.isEmpty()) {
            return null;
        }
        if (root == null && astChildren.size() > 1) {
            root = createNode(translateRoot, "TRANSLATE_ROOT");
        }
        AST current = null;
        for (Iterator it = astChildren.iterator(); it.hasNext();) {
            if (root == null) {
                root = (AST) it.next();
            } else if (root.getFirstChild() == null) {
                current = (AST) it.next();
                root.setFirstChild(current);
            } else {
                if (current == null) {
                    current = root.getFirstChild();
                }
                AST next = (AST) it.next();
                current.setNextSibling(next);
                current = next;
            }
        }
        return root;
    }
}
