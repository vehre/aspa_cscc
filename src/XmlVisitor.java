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
import antlr.ASTVisitor;
import antlr.collections.AST;
import org.dom4j.Element;

public abstract class XmlVisitor implements ASTVisitor {
    private Element current;

    public XmlVisitor(Element root) {
        current = root;
    }


    protected abstract Element createElement(Element parent, AST node);

    public void visit(AST node) {
        AST snode;
        Element root = current;
        for (snode = node; snode != null; snode = snode.getNextSibling()) {
            Element el = createElement(root, snode);
            if (el != null && snode.getFirstChild() != null) {
                current = el;
                visit(snode.getFirstChild());
            }
        }
    }
}
