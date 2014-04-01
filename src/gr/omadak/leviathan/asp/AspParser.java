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

public class AspParser {
	/**
	 * Collect and store information about a file parsed.
	 * @author vehre
	 *
	 */
	public class EntityInfo {
		public class SubEntityInfo {
			/** The tree put into the tree parser. */
			public AST inputTree;
			/** The output of the transforming tree parser. */
			public AST transformedTree;
			/** Is this a vbs or a js tree. */
			public boolean vbs;
			/** Is this server side or client side code. */
			public boolean serverSide;
		};
		public List<SubEntityInfo> trees;
		/** The global variables exposed by this file's ASTs. 
		 * Map variables to their types, ASPObjects or null, if nothing is known about the var. */
		public Map<String, Object> variables;
		/** The functions exposed by this file's ASTs. */
		public List functions;
		/** The class exposed by this file's ASTs. */
		public List<XmlASPClass> classes;
		/** Fill the entity from a symbol table. */
		public void fillFromSymTable(SymbolTableExposer sTable) {
			if (variables == null) {
				variables = new HashMap<String, Object>(sTable.getVariables());
				functions = new ArrayList(sTable.getFunctions());
				classes = new ArrayList<XmlASPClass>(sTable.getClasses());
			} else {
				variables.putAll(sTable.getVariables());
				functions.addAll(sTable.getFunctions());
				classes.addAll(sTable.getClasses());
			}
		}
		/** Merge the information in this info with those of the sTable. */
		public void mergeTo(SymbolTableExposer sTable) {
			sTable.appendVariables(variables);
			sTable.appendFunctions(functions);
			sTable.appendClasses(classes);
		}
		/** Set the trees and flags according to the analysis.
		 * 
		 * @param node
		 * @param ast
		 * @param isVbs
		 * @param serverSide
		 */
		public void addTrees(AST node, AST ast, boolean isVbs, boolean serverSide) {
			if (trees == null) trees = new ArrayList<SubEntityInfo>();
			SubEntityInfo sEI = new SubEntityInfo();
			sEI.inputTree = node;
			sEI.transformedTree = ast;
			sEI.vbs = isVbs;
			sEI.serverSide = serverSide;
			trees.add(sEI);
		}
	}

	/*
	 * private static void printClass(ASPClass clazz) {
	 * LOG.debug(clazz.getName() + " standalone:" + Boolean.toString(
	 * clazz.isStandalone())); List constructors = clazz.getConstructors(); if
	 * (constructors != null && !constructors.isEmpty()) {
	 * printMembers(constructors.iterator()); } else {
	 * LOG.debug("No constructor"); } Iterator methods = clazz.getMethods(); if
	 * (methods.hasNext()) { printMembers(methods); } else {
	 * LOG.debug("No methods"); } Iterator properties = clazz.getProperties();
	 * if (properties.hasNext()) { printMembers(properties); } else {
	 * LOG.debug("No properties"); } Property defProp =
	 * clazz.getDefaultProperty(); if (defProp != null) {
	 * printProperty(defProp); } else { LOG.debug("No default property"); }
	 * Method defMethod = clazz.getDefaultMethod(); if (defMethod != null) {
	 * printMethod(defMethod); } else { LOG.debug("No default method"); }
	 * LOG.debug(""); }
	 * 
	 * 
	 * private static void printMembers(Iterator members) { while
	 * (members.hasNext()) { Member mem = (Member) members.next(); if (mem
	 * instanceof Method) { printMethod((Method) mem); } else {
	 * printProperty((Property) mem); } } }
	 * 
	 * 
	 * private static void printMember(Member mem) { LOG.debug((mem instanceof
	 * Method ? "Method" : "Property") + " name:" + mem.getName());
	 * LOG.debug("return type:" + mem.getReturnType());
	 * LOG.debug("evaluated class:" + (mem.getEvaluatedClass() == null ? "null"
	 * : mem.getEvaluatedClass().getName())); LOG.debug("return class:" +
	 * (mem.getRetObjectClass() == null ? "null" :
	 * mem.getRetObjectClass().getName())); }
	 * 
	 * 
	 * private static void printProperty(Property prop) { printMember(prop);
	 * LOG.debug("canRead:" + Boolean.toString(prop.canRead()));
	 * LOG.debug("canWrite:" + Boolean.toString(prop.canWrite())); }
	 * 
	 * 
	 * private static void printMethod(Method method) { printMember(method); try
	 * { LOG.debug("arg types:" + method.getArgTypes()); } catch
	 * (NullPointerException npe) {} LOG.debug("constructor:" +
	 * Boolean.toString(method.isConstructor())); }
	 */

	private File baseDir;
	private File baseOutDir;
	private Map<String, EntityInfo> parsedFiles;
	private boolean generateCode;
	/** If set, then ignore all server side code given by <% directives. */
	private boolean disableServerSideCode;
	private String currentFileName;

	private static Logger LOG = Logger.getLogger(AspParser.class);

	private static XmlASPClass getInstrictObject(String name, Map<String, XmlASPClass> map) {
		XmlASPClass clazz = (XmlASPClass) map.remove(name.toUpperCase());
		if (clazz == null) {
			LOG.error("Instrict object " + name + " was not loaded");
		}
		return clazz;
	}

	static {
		try {
			org.apache.log4j.PropertyConfigurator.configure(AspParser.class
					.getResource("log4j.properties"));
		} catch (Exception ex) {
			System.err.println("No logging!");
		}
		try {
			MapLoader loader = new MapLoader();
			Map<String, Integer> jsTypes = loader
					.loadMap(
							AspParser.class.getResource("tokens/js.txt"),
							new URL[] {
									AspParser.class
											.getResource("tokens/TreeJsTokenTypes.txt"),
									AspParser.class
											.getResource("tokens/common.txt") });
			Map<String, XmlASPClass> objectClasses = new HashMap<String, XmlASPClass>();
			Map<String, Object> functions = new TreeMap<String, Object>();
			XmlObjectParser xmlParser = new XmlObjectParser(jsTypes,
					objectClasses);
			loader.loadObjects(
					AspParser.class.getResource("tokens/jsobjects.txt"),
					AspParser.class, objectClasses, functions, xmlParser);
			XmlASPClass[] instr_classes = {
					getInstrictObject("Array", objectClasses),
					getInstrictObject("Date", objectClasses),
					getInstrictObject("String", objectClasses),
					getInstrictObject("Boolean", objectClasses),
					getInstrictObject("Number", objectClasses) };
			JsParser.setInstrictClasses(instr_classes);
			JsTree.setClassesAndFunctions(instr_classes, objectClasses,
					functions);
			/*
			 * for (Iterator it = objectClasses.values().iterator();
			 * it.hasNext();) { printClass((ASPClass) it.next()); }
			 */
			objectClasses = new HashMap<String, XmlASPClass>();
			functions = new TreeMap<String, Object>();
			Map<String, Integer> vbTypes = loader
					.loadMap(
							AspParser.class.getResource("tokens/vbs.txt"),
							new URL[] {
									AspParser.class
											.getResource("tokens/TreeVbsTokenTypes.txt"),
									AspParser.class
											.getResource("tokens/common.txt") });
			xmlParser = new XmlObjectParser(vbTypes, objectClasses);
			loader.loadObjects(
					AspParser.class.getResource("tokens/vbobjects.txt"),
					AspParser.class, objectClasses, functions, xmlParser);
			/*
			 * LOG.debug("\nVB classes\n"); for (Iterator it =
			 * objectClasses.values().iterator(); it.hasNext();) {
			 * printClass((ASPClass) it.next()); }
			 */
			VbsAbstractTreeParser.setClassesAndFunctions(objectClasses,
					functions);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/** Create the aspParser object.
	 * 
	 * @param {String} baseDir The input directory
	 * @param {String} baseOutDir The output directory
	 * @param {boolean} generateCode Set, if code is to generated
	 * @param {boolean} disServerSideCode Set, if server side code shall be ignored.
	 */
	public AspParser(File baseDir, File baseOutDir, boolean generateCode, boolean disServerSideCode) {
		this.baseDir = baseDir;
		this.baseOutDir = baseOutDir;
		this.generateCode = generateCode;
		this.disableServerSideCode = disServerSideCode;
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
				/*
				 * if (pElem.toLowerCase().endsWith(".asp")) { // TODO:
				 * Implement switch on using ".html" and ".php" pElem =
				 * pElem.substring(0, pElem.lastIndexOf('.')) + (true ? ".html"
				 * : ".php"); } else if (pElem.endsWith(".vbs")) { pElem =
				 * pElem.substring(0, pElem.lastIndexOf('.')) + ".js"; }
				 */
				// original filename more clear; easy to post-process with a
				// batch script
				pElem = pElem + ".converted";
			}
			out = new File(out == null ? baseOutDir : out, pElem);
			if (hasMore && !out.exists()) {
				out.mkdir();
			}
		}
		if (out == null) {
			throw new IOException("Unable to determine path from paths:"
					+ baseDir.getAbsolutePath() + " : "
					+ file.getAbsolutePath());
		} else {
			return new FileWriter(out);
		}
	}

	/**
	 * Parses an asp file and generates a List with the AST forest produced.
	 * 
	 * @param file
	 *            is the file to parse
	 * @param isVb
	 *            indicates if the default language is VbScript
	 * @param sTable
	 *            is an instance of SymbolTableExposer. If the file parsed is
	 *            part of an include statement, then the parameter is not null,
	 *            otherwise is expected to be null.
	 * @return a List which contains the AST forest if preserveAST is true.
	 */
	private EntityInfo parseFile(File file, boolean isVb,
			SymbolTableExposer sTable) throws ANTLRException {
		EntityInfo entityInfo = parsedFiles == null ? null : parsedFiles.get(file.getAbsolutePath());
		if (entityInfo != null) {
			if (sTable != null) { // is an include file
				entityInfo.mergeTo(sTable);
			}
			return null;
		} else {
			currentFileName = file.getName();
			AspStreamSelector selector = new AspStreamSelector(file, baseDir,
					sTable == null, false /*
										 * Add argument for switching non
										 * language asp directives on/off.
										 */, disableServerSideCode);
			VbsParser vbParser = null;
			JsParser jsParser = null;
			VbsAbstractTreeParser vbTreeParser = null;
			JsTree jsTreeParser = null;
			selector.setDefaultVb(isVb);
			Set<String> includes = null;
			if (generateCode) {
				includes = new HashSet<String>();
			}
			entityInfo= new EntityInfo();
			if (sTable != null) {
				entityInfo.fillFromSymTable(sTable);
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
						/*
						 * TODO: The decision, if conversion to php or to javascript
						 * should be done, should be made by examining the code in the
						 * vbParser and not by the flag.
						 */
						if (vbTreeParser == null /*
								|| !( disableServerSideCode && vbTreeParser instanceof VbsJsTree)*/) {
							if ( disableServerSideCode ) {
								vbTreeParser = new VbsJsTree();
							} else {
								vbTreeParser = new VbsPhpTree();
							}
							vbTreeParser.setAspParser(this);
						}
						vbTreeParser.setFunctions(vbParser.getFunctions());
						vbTreeParser.setClasses(vbParser.getClasses());
						vbTreeParser.setGlobalIds(vbParser.getGlobalIds());
						entityInfo.mergeTo(vbTreeParser);
						vbTreeParser.start_rule(node);
						AST transTree = vbTreeParser.getAST();
						JSTreeResolver resolver = new JSTreeResolver();
						resolver.setVariables(vbTreeParser.getVariables());
						resolver.breadth_first(transTree);
						transTree = resolver.getAST();
						if (generateCode) {
							includes.addAll(vbTreeParser.getDependencies());
						}
						entityInfo.fillFromSymTable(vbTreeParser);
						entityInfo.addTrees(node, transTree, true,
								false);
					}
				} else {
					if (jsParser == null) {
						jsParser = new JsParser(selector);
					}
					jsParser.start_rule();
					AST node = jsParser.getAST();
					if (node != null) {
						// new antlr.DumpASTVisitor().visit(node);
						if (jsTreeParser == null) {
							jsTreeParser = new JsTree();
							jsTreeParser.setAspParser(this);
							jsTreeParser.setFunctions(jsParser.getFunctions());
							jsTreeParser.setAnonymousFunctions(jsParser
									.getAnonymousFunctions());
							jsTreeParser.setParserClasses(jsParser.getClasses());
						}
						entityInfo.mergeTo(jsTreeParser);
						jsTreeParser.start_rule(node);
						if (generateCode) {
							includes.addAll(jsTreeParser.getDependencies());
						}
						entityInfo.fillFromSymTable(jsTreeParser);
						entityInfo.addTrees(node, jsTreeParser.getAST(), false, true);
					}
				}
			}
			if (parsedFiles == null) {
				parsedFiles = new HashMap<String, EntityInfo>();
			}
			parsedFiles.put(file.getAbsolutePath(), entityInfo);
			if (generateCode) {
				try {
					Writer writer = getWriter(file);
					produceCode(entityInfo, writer, includes,
							file.getAbsolutePath());
					writer.close();
				} catch (IOException ioex) {
					LOG.error("Failed to generate code", ioex);
				}
			}
			if (sTable != null) {
				entityInfo.mergeTo(sTable);
			}
		}
		return entityInfo;
	}

	private void produceCode(EntityInfo eI, Writer writer, Set<String> includes,
			String path) {
		CodeGenerator vbgenerator = null;
		JsGenerator jsgenerator = null;
		boolean isFirst = true;
		for (Iterator<EntityInfo.SubEntityInfo> it = eI.trees.iterator(); it.hasNext();) {
			EntityInfo.SubEntityInfo node = it.next();
			CodeGenerator generator;
			if (node.vbs) {
				if (vbgenerator == null
						|| vbgenerator instanceof VbsJsGenerator
						&& node.serverSide
						|| vbgenerator instanceof VbsPhpGenerator
						&& !node.serverSide) {
					vbgenerator = node.serverSide ? new VbsPhpGenerator()
							: new VbsJsGenerator();
					/*
					 * If just one ast is present, then the whole file is code,
					 * i.e., no need for script tags.
					 */
					if (eI.trees.size() <= 1)
						((VbsJsGenerator) vbgenerator).setNonInline();
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
				generator.generate(node.transformedTree);
			} catch (ANTLRException an) {
				LOG.error("Failed to produce code from "
						+ (node.vbs ? "vb" : "js") + " : " + path, an);
				try {
					generator.getBuffer().endCode();
					writer.flush();
				} catch (IOException ioex) {
					LOG.error("Failed to flush buffers" + ioex);
				}
			}
		}
	}

	private void printIncludes(CodeGenerator generator, Set<String> includes) {
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

	/**
	 * Get the current file name
	 * 
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
			private boolean disServerSideCode = disableServerSideCode;

			public boolean accept(File f) {
				boolean result = f.isDirectory();
				if (!result) {
					String name = f.getName();
					int lastDot = name.lastIndexOf('.');
					result = lastDot > 0
							&& isExtensionToProcess(name.substring(lastDot + 1));
				}
				return result;
			}

			/**
			 * Check if this is a file extension to process When server side
			 * code processing is enabled, then only .asp files are parsed (this
			 * does not apply to files included by an .asp file, those are
			 * processed like referenced). When server side code processing is
			 * disabled, then all of .asp, .htm[l], .jsp, .vbs are processed.
			 * 
			 * @param {String} extension The extension to check.
			 * @returns {boolean} True if the extension is of a file type to
			 *          process.
			 */
			private boolean isExtensionToProcess(String extension) {
				return extension.equalsIgnoreCase("asp")
						|| disServerSideCode
						&& (extension.equalsIgnoreCase("jsp")
								|| extension.toLowerCase().startsWith("htm") // Catch htm and html suffixes
								|| extension.equalsIgnoreCase("vbs"));
			}
		};
		Stack<File> stack = new Stack<File>();
		stack.push(sdir);
		while (!stack.isEmpty()) {
			File dir = (File) stack.pop();
			for (Iterator<?> it = IteratorUtils.arrayIterator(dir
					.listFiles(filter)); it.hasNext();) {
				File f = (File) it.next();
				if (f.isDirectory()) {
					stack.push(f);
				} else {
					try {
						parseFile(f, vbDefault, null);
					} catch (ANTLRException ae) {
						LOG.error(
								"Failed to parse file:" + f.getAbsolutePath(),
								ae);
					} catch (Exception ex) {
						LOG.error("Failed to parse file:" + f.getAbsolutePath()
								+ " with error", ex);
					}
				}
			}
		}
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
		boolean oDirValid = oDir != null
				&& (!oDir.exists() || oDir.isDirectory());
		boolean bDirValid = bDir != null && bDir.isDirectory();
		boolean isValid = oDirValid && bDirValid;
		if (isValid && sDir == null) {
			sDir = bDir;
		}
		if (!isValid) {
			System.err
					.println("Usage:AspParser -b <base directory> "
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
		AspParser parser = new AspParser(bDir, oDir, gSource, !enableServerSideCode);
		parser.parseDir(sDir, defaultVB);
		System.exit(0);
	}
}
