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
import antlr.CommonAST;
import antlr.CommonToken;
import antlr.TreeParser;
import antlr.collections.AST;
import gr.omadak.leviathan.asp.*;
import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Main2 {
    private static void printApsTokens(AspStreamSelector tk) {
        try {
            while (tk.hasMoreTokens()) {
                System.out.println(tk.nextToken());
            }
            /*
            Token t;
            while ((t = tk.nextToken()) != null) {
                System.out.println(t);
                if (t.getType() == Token.EOF_TYPE) {
                    break;
                }
            }
            */
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static void putTokens(TreeParser tree, Map map) {
        String[] tokens = tree.getTokenNames();
        for (int i = 0; i < tokens.length; i++) {
            map.put(new Integer(i), tokens[i]);
        }
    }


    public static void main(String[] args) throws IOException {
        boolean printTokens = false;
        boolean showTree = false;
        boolean produceXml = false;
        boolean produceSource = false;
        for (int i = 2; i < args.length; i++) {
            if (args[i].length() == 2) {
                switch (args[i].charAt(1)) {
                    case 't': printTokens = true; break;
                    case 'v': showTree = true; break;
                    case 'x': produceXml = true; break;
                    case 's': produceSource = true; break;
                }
            }
        }
        ASTWindow window = showTree ? new ASTWindow("AST") : null;
        Document doc = null;
        if (produceXml) {
            doc = DocumentHelper.createDocument(
            DocumentHelper.createElement("asp"));
        }
        try {
            File f = new File(args[0]);
            File base = new File(args[1]);
            if (printTokens) {
                AspStreamSelector selector = new AspStreamSelector(f, base, true);
                printApsTokens(selector);
                if (!(showTree || produceXml)) {
                    return;
                }
            }
            AspParser asp = new AspParser(base, new File("out"));
            asp.setGenerateCode(produceSource);
            Map vbTokens = null;
            Map jsTokens = null;
            Map tokens = null;
            List astList = asp.parseFile(f, true);
            for (Iterator it = astList.iterator(); it.hasNext();) {
                Object[] val = (Object[]) it.next();
                boolean isVb = ((Boolean) val[0]).booleanValue();
                if (showTree) {
                    if (isVb) {
                        window.addAST("Original VB", createRoot("VB ASP",
                        (AST) val[1]));
                        if (val.length > 2) {
                            window.addAST("VB Generated", createRoot("VB",
                            (AST) val[2]));
                        }
                    } else {
                        window.addAST("ASP JS", createRoot("JS",
                        (AST) val[1]));
                        window.addAST("JS Generated", createRoot("JS",
                        (AST) val[2]));
                    }
                }
                if (produceXml) {
                    String rootName;
                    if (isVb) {
                        rootName = "vb";
                        if (vbTokens == null) {
                            vbTokens = new HashMap();
                            putTokens(new VbsPhpTree(), vbTokens);
                        }
                        tokens = vbTokens;
                    } else {
                        rootName = "js";
                        if (jsTokens == null) {
                            jsTokens = new HashMap();
                            putTokens(new JsTree(), jsTokens);
                        }
                        tokens = jsTokens;
                    }
                    Element nodeEl = doc.getRootElement().addElement(rootName);
                    Element orig = nodeEl.addElement("original");
                    new XmlTestBuilder(orig, tokens).visit((AST) val[1]);
                    Element trans = nodeEl.addElement("translated");
                    new XmlTestBuilder(trans, tokens).visit((AST) val[2]);
                }
            }
            if (produceXml) {
                OutputFormat of = OutputFormat.createPrettyPrint();
                of.setEncoding("ISO-8859-7");
                XMLWriter xwriter = new XMLWriter(new FileWriter("asp.xml"),
                of);
                xwriter.write(doc);
                xwriter.close();
            }
            if (showTree && window.hasAST()) {
                window.pack();
                window.setVisible(true);
                window.setExtendedState(Frame.MAXIMIZED_BOTH);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            System.err.println("Error");
        }
    }


    private static AST createRoot(String label, AST child) {
        CommonAST tree = new CommonAST(new CommonToken(300, label));
        tree.addChild(child);
        return tree;
    }
}
