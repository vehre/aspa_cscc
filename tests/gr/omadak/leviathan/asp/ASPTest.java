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

import antlr.CommonAST;
import antlr.CommonToken;
import antlr.Parser;
import antlr.Parser;
import antlr.TreeParser;
import antlr.collections.AST;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ASPTest extends TestCase {
    private File sourceFile;
    private AspParser parser;
    private File xmlFile;
    private Document document;
    private boolean caseSensitive = true;
    private String[] jsTokens;
    private String[] vbTokens;

    public ASPTest(String name, File sourceDir, File sourceFile,
    File xmlFile, boolean caseSensitive) {
        super(name);
        parser = new AspParser(sourceDir, new File("out"));
        this.sourceFile = sourceFile;
        this.xmlFile = xmlFile;
        this.caseSensitive = caseSensitive;
        JsTree js = new JsTree();
        jsTokens = js.getTokenNames();
        VbsTree vb = new VbsTree();
        vbTokens = vb.getTokenNames();
    }


    protected void setUp() throws Exception {
        SAXReader reader = new SAXReader();
        document = reader.read(xmlFile);
        Logger.getLogger(ASPTest.class).info(sourceFile.getAbsolutePath());
    }


    /** @return the root element of the document */
    protected Element getRootElement() {
        Element root = document.getRootElement();
        assertTrue( "Document has root element", root != null );
        return root;
    }


    /**
    * The xml file is expected to have this structure:
    <br><pre>
    <asp>
        <vb>... Elements </vb>
        <js>... Elements </js>
        .....................
        <js>... Elements </js>
        <vb>... Elements </vb>
    </asp>
    </pre>
    * Under the vb or js elements two elements are expected to be:
    <ol>
        <li>&lt;original&gt;</li>
        <li>&lt;translated&gt;</li>
    </ol>
    * Each of those elements have ast elements containing attribute name
    * which indicates the AST type and optionaly attribute text.
    * ast Element may be nested
    */
    public void testASP() throws Exception {
        Logger.getLogger(getClass()).debug("Running test for source file:"
        + sourceFile.getName()
        + " for xml file:" + xmlFile.getName());
        Element root = getRootElement();
        Iterator rootNodes = root.elementIterator();
        Iterator astIterator = parser.parseFile(sourceFile, true).iterator();
        while (astIterator.hasNext()) {
            assertTrue("There should be a root node", rootNodes.hasNext());
            Element el = (Element) rootNodes.next();
            int chCount = el.elements().size();
            assertTrue(
            "Element expected to have at least one subelement and at most 2",
            chCount > 0 && chCount < 3);
            Object[] val = (Object[]) astIterator.next();
            boolean isVb = ((Boolean) val[0]).booleanValue();
            assertTrue("Wrong order.Expected "
            + (isVb ? "vb" : "js") + " but found:" + el.getName(),
            (isVb && "vb".equals(el.getName()))
            || (!isVb && "js".equals(el.getName())));
            AST node = (AST) val[1];
            AST translatedNode = (AST) val[2];
            Element original = el.element("original");
            assertNotNull("Original ast should be defined in the xml file",
            original);
            assertAST(original, createRoot(node), isVb ? vbTokens : jsTokens);
            Element translated = el.element("translated");
            if (translatedNode == null) {
                assertNull("translated Element should not exist",
                translatedNode);
            } else {
                assertAST(translated, createRoot(translatedNode),
                isVb ? vbTokens : jsTokens);
            }
        }
    }


    private AST createRoot(AST node) {
        CommonAST tree = new CommonAST(new CommonToken(-100, "fake"));
        tree.setFirstChild(node);
        return tree;
    }


    private int getType(String[] tokens, String tokenName) {
        int result = -1;
        if (tokens != null) {
            for (int i = 0; i < tokens.length; i++) {
                if (tokenName.equals(tokens[i])) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }


    private String getTokenName(String[] tokens, int type) {
        try {
            return tokens[type];
        } catch (IndexOutOfBoundsException ib) {
            return Integer.toString(type);
        }
    }


    protected void assertAST(Element el, AST ast, String[] tokens) {
        Iterator children = el.elementIterator();
        AST child = null;
        assertTrue("There are more elements but no siblings or viceversa",
        children.hasNext() ^ ast.getFirstChild() == null);
        while (children.hasNext()) {
            Element elem = (Element) children.next();
            child = child == null ? ast.getFirstChild() : child.getNextSibling();
            assertNotNull("There are more elements in xml then AST:"
            , child);
            assertEquals("Expected an element with name:ast", "ast", elem.getName());
            String astName = elem.attributeValue("name");
            int type;
            if (astName == null) {
                String strType  = elem.attributeValue("type");
                assertNotNull("Expected the name or type of the ast node", strType);
                type = Integer.parseInt(strType);
            } else {
                type = getType(tokens, astName);
            }
            assertFalse("Unable to locate the type for name:" + astName,
            -1 == type);
            if (astName != null) {
                assertEquals(
                "AST not of type:[" + type + ", " + astName
                + "] but of type:[" + child.getType() + ", "
                + getTokenName(tokens, child.getType()) + "]\n"
                , type, child.getType());
            }
            String astText = elem.attributeValue("text");
            if (astText == null && elem.hasContent()) {
                astText = elem.getText();
            }
            String chText = child.getText();
            if (astText != null && chText != null && !caseSensitive) {
                astText = astText.toLowerCase();
                chText = chText.toLowerCase();
            }
            assertEquals("Not same text:", astText, chText);
            assertTrue(
            "Element has sub elements but ast has no children",
            elem.elements().isEmpty() ^ child.getFirstChild() != null);
            if (child.getFirstChild() != null) {
                assertAST(elem, child, tokens);
            }
        }
        assertNull("the child should be null:" + child, child.getNextSibling());
    }


    public String getName() {
        return sourceFile.getName() + " => " + xmlFile.getName();
    }
}
