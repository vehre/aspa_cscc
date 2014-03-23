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

import antlr.ANTLRException;
import antlr.collections.AST;
import gr.omadak.leviathan.asp.objects.XmlASPClass;
import gr.omadak.leviathan.asp.objects.XmlObjectParser;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;

//import gr.omadak.leviathan.asp.objects.*;

public class AspParser {
    private static class DataHolder {
        Map variables;
        List functions;
        List classes;
        boolean isVb;
    }
/*
    private static void printClass(ASPClass clazz) {
        LOG.debug(clazz.getName() + " standalone:" + Boolean.toString(
        clazz.isStandalone()));
        List constructors = clazz.getConstructors();
        if (constructors != null && !constructors.isEmpty()) {
            printMembers(constructors.iterator());
        } else {
            LOG.debug("No constructor");
        }
        Iterator methods = clazz.getMethods();
        if (methods.hasNext()) {
            printMembers(methods);
        } else {
            LOG.debug("No methods");
        }
        Iterator properties = clazz.getProperties();
        if (properties.hasNext()) {
            printMembers(properties);
        } else {
            LOG.debug("No properties");
        }
        Property defProp = clazz.getDefaultProperty();
        if (defProp != null) {
            printProperty(defProp);
        } else {
            LOG.debug("No default property");
        }
        Method defMethod = clazz.getDefaultMethod();
        if (defMethod != null) {
            printMethod(defMethod);
        } else {
            LOG.debug("No default method");
        }
        LOG.debug("");
    }


    private static void printMembers(Iterator members) {
        while (members.hasNext()) {
            Member mem = (Member) members.next();
            if (mem instanceof Method) {
                printMethod((Method) mem);
            } else {
                printProperty((Property) mem);
            }
        }
    }


    private static void printMember(Member mem) {
        LOG.debug((mem instanceof Method ? "Method" : "Property") + " name:"
        + mem.getName());
        LOG.debug("return type:" + mem.getReturnType());
        LOG.debug("evaluated class:" + (mem.getEvaluatedClass() == null ? "null"
        : mem.getEvaluatedClass().getName()));
        LOG.debug("return class:" + (mem.getRetObjectClass() == null ? "null"
        : mem.getRetObjectClass().getName()));
    }


    private static void printProperty(Property prop) {
        printMember(prop);
        LOG.debug("canRead:" + Boolean.toString(prop.canRead()));
        LOG.debug("canWrite:" + Boolean.toString(prop.canWrite()));
    }


    private static void printMethod(Method method) {
        printMember(method);
        try {
            LOG.debug("arg types:" + method.getArgTypes());
        } catch (NullPointerException npe) {}
        LOG.debug("constructor:" + Boolean.toString(method.isConstructor()));
    }
*/

    private File baseDir;
    private File baseOutDir;
    private Map parsedFiles;
    private Map parsedAST;
    private boolean generateCode;
    private boolean preserveAST;
    /** If set, then ignore all server side code given by <% directives. */
    private boolean disableServerSideCode;
	private String currentFileName;

    private static Logger LOG = Logger.getLogger(AspParser.class);

    private static XmlASPClass getInstrictObject(String name, Map map) {
        XmlASPClass clazz = (XmlASPClass) map.remove(name.toUpperCase());
        if (clazz == null) {
            LOG.error("Instrict object " + name + " was not loaded");
        }
        return clazz;
    }

    static {
        try {
            org.apache.log4j.PropertyConfigurator.configure(
            AspParser.class.getResource("log4j.properties"));
        } catch (Exception ex) {
            System.err.println("No logging!");
        }
        try {
            MapLoader loader = new MapLoader();
            Map jsTypes = loader.loadMap(
            		AspParser.class.getResource("tokens/js.txt"), new URL[] {
            			AspParser.class.getResource("tokens/TreeJsTokenTypes.txt"),
            			AspParser.class.getResource("tokens/common.txt")});
            Map objectClasses = new HashMap();
            Map functions = new TreeMap();
            XmlObjectParser xmlParser = new XmlObjectParser(jsTypes, objectClasses);
            loader.loadObjects(
		            AspParser.class.getResource("tokens/jsobjects.txt"),
		            AspParser.class, objectClasses, functions, xmlParser);
            XmlASPClass[] instr_classes = {
		            getInstrictObject("Array", objectClasses),
		            getInstrictObject("Date", objectClasses),
		            getInstrictObject("String", objectClasses),
		            getInstrictObject("Boolean", objectClasses),
		            getInstrictObject("Number", objectClasses)
            };
            JsParser.setInstrictClasses(instr_classes);
            JsTree.setClassesAndFunctions(instr_classes, objectClasses, functions);
/*
            for (Iterator it = objectClasses.values().iterator();
            it.hasNext();) {
                printClass((ASPClass) it.next());
            }
*/
            objectClasses = new HashMap();
            functions = new TreeMap();
            Map vbTypes = loader.loadMap(
            AspParser.class.getResource("tokens/vbs.txt"), new URL[] {
            		AspParser.class.getResource("tokens/TreeVbsTokenTypes.txt"),
            		AspParser.class.getResource("tokens/common.txt")});
            xmlParser = new XmlObjectParser(vbTypes, objectClasses);
            loader.loadObjects(
            		AspParser.class.getResource("tokens/vbobjects.txt"),
            		AspParser.class, objectClasses, functions, xmlParser);
/*
            LOG.debug("\nVB classes\n");
            for (Iterator it = objectClasses.values().iterator();
            it.hasNext();) {
                printClass((ASPClass) it.next());
            }
*/
            VbsAbstractTreeParser.setClassesAndFunctions(objectClasses, functions);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public AspParser(File baseDir, File baseOutDir) {
        this.baseDir = baseDir;
        this.baseOutDir = baseOutDir;
    }


    private Writer getWriter(File file) throws IOException {
        String basePath = baseDir.getCanonicalFile().getAbsolutePath();
        String absFile = file.getCanonicalFile().getAbsolutePath();
        absFile = absFile.substring(basePath.length());
        if (absFile.charAt(0) == '/' || absFile.charAt(0) == '\\') {
            absFile = absFile.substring(1);
        }
        StringTokenizer st = new StringTokenizer(absFile, File.separator);
        File out = null;
        while (st.hasMoreTokens()) {
            String pElem = st.nextToken();
            boolean hasMore = st.hasMoreTokens();
            if (!hasMore) {
                if (pElem.endsWith(".asp")) {
                	// TODO: Implement switch on using ".html" and ".php"
                    pElem = pElem.substring(0, pElem.lastIndexOf('.')) + (true ? ".html" : ".php");
                } else if (pElem.endsWith(".vbs")) {
                	pElem = pElem.substring(0, pElem.lastIndexOf('.')) + ".js";
                }
            }
            out = new File(out == null ? baseOutDir : out, pElem);
            if (hasMore && !out.exists()) {
                out.mkdir();
            }
        }
        if (out == null) {
            throw new IOException("Unable to determine path from paths:"
            + baseDir.getAbsolutePath() + " : " + file.getAbsolutePath());
        } else {
            return new FileWriter(out);
        }
    }


    private boolean fileIsParsed(File f) {
        return parsedFiles != null
        && parsedFiles.containsKey(f.getAbsolutePath());
    }


    /**
    * Parses an asp file and generates a List with the AST forest produced.
    * @param file is the file to parse
    * @param isVb indicates if the default language is VbScript
    * @param sTable is an instance of SymbolTableExposer. If the file
    * parsed is part of an include statement, then the parameter is not null,
    * otherwise is expected to be null.
    * @return a List which contains the AST forest if preserveAST is true.
    */
    private List<Object> parseFile(File file, boolean isVb, SymbolTableExposer sTable)
    throws ANTLRException {
        List<Object> result;
        if (fileIsParsed(file)) {
			if (sTable != null) { //is an include file
				mergeSymbols((DataHolder) parsedFiles.get(file.getAbsolutePath()),
				sTable);
			}
            result = Collections.emptyList();
        } else {
			currentFileName = file.getName();
            AspStreamSelector selector = new AspStreamSelector(file, baseDir,
            		sTable == null, false /* Add argument for switching non language asp directives on/off. */,
            		disableServerSideCode);
            VbsParser vbParser = null;
            JsParser  jsParser = null;
            VbsAbstractTreeParser vbtree = null;
            JsTree jsTree = null;
            selector.setDefaultVb(isVb);
            result = new ArrayList<Object>();
            Set includes = null;
            if (generateCode) {
                includes = new HashSet();
            }
            DataHolder holder = new DataHolder();
            if (sTable != null) {
                fillHolder(sTable, holder);
            }
            while (selector.hasMoreTokens()) {
                if (selector.isVbCurrent()) {
                    if (vbParser == null) {
                        vbParser = new VbsParser(selector);
                    }
                    vbParser.start_rule();
                    AST node = vbParser.getAST();
                   // new antlr.DumpASTVisitor().visit(node);
                    if (node != null) {
                    	/* TODO: Decide, if conversion to php or to javascript should be done. */
                    	if (vbtree == null || !(true /* node.isClientSideVbs() */ && vbtree instanceof VbsJsTree)) { 
                        	/* NOTE: Currently always the client side Vbs to js converter is used. */ 
	                        if (true /* node.isClientSideVbs() */) {
	                            vbtree = new VbsJsTree();
	                            vbtree.setAspParser(this);
	                            vbtree.setFunctions(vbParser.getFunctions());
	                            vbtree.setClasses(vbParser.getClasses());
	                            vbtree.setGlobalIds(vbParser.getGlobalIds());
	                            if (sTable != null) {
	                                mergeSymbols(holder, sTable);
	                            }
	                        } else {
	                            vbtree = new VbsPhpTree();
	                            vbtree.setAspParser(this);
	                            vbtree.setFunctions(vbParser.getFunctions());
	                            vbtree.setClasses(vbParser.getClasses());
	                            vbtree.setGlobalIds(vbParser.getGlobalIds());
	                            if (sTable != null) {
	                                mergeSymbols(holder, sTable);
	                            }
	                        }
                    	} else {
                    		vbtree.setFunctions(vbParser.getFunctions());
                            vbtree.setClasses(vbParser.getClasses());
                            vbtree.setGlobalIds(vbParser.getGlobalIds());
                    	}
                        vbtree.start_rule(node);
                        if (generateCode) {
                            includes.addAll(vbtree.getDependencies());
                        }
                        fillHolder(vbtree, holder);
                        result.add(new Object[] {Boolean.TRUE, node,
                        		vbtree.getAST()});
                    }
                } else {
                    if (jsParser == null) {
                        jsParser = new JsParser(selector);
                    }
                    jsParser.start_rule();
                    AST node = jsParser.getAST();
                    if (node != null) {
                        //new antlr.DumpASTVisitor().visit(node);
                        if (jsTree == null) {
                            jsTree = new JsTree();
                            jsTree.setAspParser(this);
                            jsTree.setFunctions(jsParser.getFunctions());
                            jsTree.setAnonymousFunctions(
                            jsParser.getAnonymousFunctions());
                            jsTree.setParserClasses(jsParser.getClasses());
                            if (sTable != null) {
                                mergeSymbols(holder, jsTree);
                            }
                        }
                        jsTree.start_rule(node);
                        if (generateCode) {
                            includes.addAll(jsTree.getDependencies());
                        }
                        fillHolder(jsTree, holder);
                        result.add(new Object[] {Boolean.FALSE, node,
                        		jsTree.getAST()});
                    }
                }
            }
            if (preserveAST) {
                if (parsedAST == null) {
                    parsedAST = new HashMap();
                }
                parsedAST.put(file.getAbsolutePath(), result);
            }
            if (parsedFiles == null) {
                parsedFiles = new HashMap();
            }
            parsedFiles.put(file.getAbsolutePath(), holder);
            if (generateCode) {
                try {
                    Writer writer = getWriter(file);
                    produceCode(result, writer, includes,
                    		file.getAbsolutePath());
                    writer.close();
                } catch (IOException ioex) {
                    LOG.error("Failed to generate code", ioex);
                }
            }
            if (sTable != null) {
                mergeSymbols(holder, sTable);
            }
        }
        return result;
    }


    private void produceCode(List ast, Writer writer, Set includes,
    String path) {
        CodeGenerator vbgenerator = null;
        JsGenerator jsgenerator = null;
        boolean isFirst = true;
        for (Iterator it = ast.iterator(); it.hasNext();) {
            Object[] nodes = (Object[]) it.next();
            if (nodes.length == 3) {
                AST phpTree = (AST) nodes[2];
                boolean isVbTree = ((Boolean) nodes[0]).booleanValue();
                CodeGenerator generator;
                if (isVbTree) {
                    if (vbgenerator == null) {
                    	// TODO: Implement switching between php and js script generator depending on server / client side scripting
                        vbgenerator = new VbsJsGenerator();
                        vbgenerator.setWriter(writer);
                    }
                    generator = vbgenerator;
                } else {
                    if (jsgenerator == null) {
                        jsgenerator = new JsGenerator();
                    }
                    jsgenerator.setWriter(writer);
                    generator = jsgenerator;
                }
                try {
                    if (isFirst && !includes.isEmpty()) {
                        printIncludes(generator, includes);
                        includes.clear();
                    }
                    isFirst = false;
                    generator.generate(phpTree);
                } catch (ANTLRException an) {
                    LOG.error("Failed to produce code from "
                    + (isVbTree ? "vb" : "js") + " : " + path, an);
                    try {
                        generator.getBuffer().end();
                        writer.flush();
                    } catch (IOException ioex) {
                        LOG.error("Failed to flush buffers" + ioex);
                    }
                }
            }
        }
    }


    private void mergeSymbols(DataHolder holder, SymbolTableExposer dest) {
        dest.appendVariables(holder.variables);
        dest.appendFunctions(holder.functions);
        dest.appendClasses(holder.classes);
    }


    private void fillHolder(SymbolTableExposer vbtree, DataHolder holder) {
        if (holder.variables == null) {
            holder.variables = new HashMap(vbtree.getVariables());
            holder.functions = new ArrayList(vbtree.getFunctions());
            holder.classes = new ArrayList(vbtree.getClasses());
            holder.isVb = vbtree instanceof VbsJsTree;
        } else {
            holder.variables.putAll(vbtree.getVariables());
            holder.functions.addAll(vbtree.getFunctions());
            holder.classes.addAll(vbtree.getClasses());
        }
		/*
        LOG.debug("variables:" + holder.variables);
        LOG.debug("functions:" + holder.functions);
        LOG.debug("classes:" + holder.classes);
		*/
   }


    private void printIncludes(CodeGenerator generator, Set includes) {
        SourceBuffer buffer = generator.getBuffer();
        for (Iterator<?> it = includes.iterator(); it.hasNext();) {
            String fileName = (String) it.next();
            buffer.println("require \"" + fileName + "\";");
        }
    }


    private String getCommonPath(File dir, File file) {
        String dPath = dir.getAbsolutePath();
        String fPath = file.getAbsolutePath();
        int i = 0;
        for (i = 0; i < Math.min(dPath.length(), fPath.length()); i++) {
            if (dPath.charAt(i) != fPath.charAt(i)) {
                break;
            }
        }
        String result = fPath.substring(i);
        if (result.charAt(0) == '/' || result.charAt(0) == '\\') {
            result = result.substring(1);
        }
        return new String(result);
    }


    public List parseFile(File file, boolean isVb) throws ANTLRException {
        List result = new ArrayList(parseFile(file, isVb, null));
        if (preserveAST) {
            String absPath = file.getAbsolutePath();
            for (Iterator<?> it = parsedAST.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                if (!absPath.equals(key)) {
                    result.addAll((List) parsedFiles.get(key));
                }
            }
        }
        return result;
    }


	/**
	 * Get the current file name
	 * @return the name of the file being parsed.	 
	*/
	public String getCurrentFileName() {
		return currentFileName;
	}


    public String parseInclude(String path, SymbolTableExposer sTable,
    boolean isVb) throws ANTLRException {
        File file = new File(path);
        if (file.exists() && file.isFile() && file.canRead()) {
            parseFile(file, isVb, sTable);
        }
        return getCommonPath(baseDir, file);
    }


    public void parseDir(File sdir, boolean vbDefault) {
        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                boolean result = f.isDirectory();
                if (!result) {
                    String name = f.getName();
                    int lastDot = name.lastIndexOf('.');
                    result = lastDot > 0 && "asp".equalsIgnoreCase(
                    		name.substring(lastDot + 1));
                }
                return result;
            }
        };
        Stack<File> stack = new Stack<File>();
        stack.push(sdir);
        while (!stack.isEmpty()) {
            File dir = (File) stack.pop();
            for (Iterator<?> it = IteratorUtils.arrayIterator(
            dir.listFiles(filter)); it.hasNext();) {
                File f = (File) it.next();
                if (f.isDirectory()) {
                    stack.push(f);
                } else {
                    try {
                        parseFile(f, vbDefault);
                    } catch (ANTLRException ae) {
                        LOG.error("Failed to parse file:" + f.getAbsolutePath(),
                        ae);
                    } catch (Exception ex) {
                        LOG.error("Failed to parse file:" + f.getAbsolutePath()
                        + " with error", ex);
                    }
                }
            }
        }
    }


    /**
    * Sets the value of generateCode.
    * @param generateCode The value to assign generateCode.
    */
    public void setGenerateCode(boolean generateCode) {
        this.generateCode = generateCode;
    }

    /** Disable interpretation of server side code.  
     */
    public void disableServerSideCode() {
    	disableServerSideCode = true;
    }
    
    public static void main(String[] args) {
        File bDir = null;
        File oDir = null;
        File sDir = null;
        boolean gSource = true;
        boolean defaultVB = true;
        boolean enableServerSideCode = true;
        int i = 0;
        int argCount = args.length;
        while (i < argCount) {
            if (i < argCount - 1) {
                if (args[i].equals("-o")) {
                    oDir = new File(args[++i]);
                } else if (args[i].equals("-b")) {
                    bDir = new File(args[++i]);
                } else if (args[i].equals("-s")) {
                    sDir = new File(args[++i]);
                }
            }
            if (args[i].equals("-ng")) {
                gSource = false;
            } else if (args[i].equals("-js")) {
                defaultVB = false;
            } else if (args[i].equals("-vb")) {
                defaultVB = true;
            } else if (args[i].equals("-ds")) {
                enableServerSideCode = false;
            }
            i++;
        }
        boolean oDirValid = oDir != null && (!oDir.exists() || oDir.isDirectory());
        boolean bDirValid = bDir != null && bDir.isDirectory();
        boolean isValid = oDirValid && bDirValid;
        if (isValid && sDir == null) {
            sDir = bDir;
        }
        if (!isValid) {
            System.err.println("Usage:AspParser -b <base directory> "
            + "-o <output directory> [-s <source directory> -g -ng -vb -js -ds]\n"
            + "base directory: the directory where virtual root exists.\n"
            + "eg <!--#include virtual=\"/someFile.asp\"-->\n"
            + "output directory: the directory where generated files will be placed in\n"
            + "source directory: the root directory where sources are\n"
            + "    If not defined then the base directory is used\n"
            + "-ng disables source production\n"
            + "-js if an asp page has not defined the language assume it is js\n"
            + "-vb if an asp page has not defined the language assume it is vb\n"
            + "-ds disables interpretation of any server side code directives\n"
            + "    starting with <%");
            if (!oDirValid) {
                if (oDir == null) {
                    System.err.println("Output directory is not defined");
                } else {
                    System.err.println("Output directory:" + oDir
                    + " can not be used");
                }
            }
            if (!bDirValid) {
                if (bDir == null) {
                    System.err.println("Base directory is not defined");
                } else {
                    System.err.println("Base directory:" + bDir
                    + " does not exist or is not a directory");
                }
            }
            System.exit(1);
        }
        AspParser parser = new AspParser(bDir, oDir);
        parser.setGenerateCode(gSource);
        if ( !enableServerSideCode ) parser.disableServerSideCode();
        parser.parseDir(sDir, defaultVB);
        System.exit(0);
    }
}
