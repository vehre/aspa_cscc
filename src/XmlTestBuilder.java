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
import antlr.collections.AST;
import java.util.Map;
import org.dom4j.Element;

public class XmlTestBuilder extends XmlVisitor {
    private Map tokens;

    public XmlTestBuilder(Element el, Map tokens) {
        super(el);
        this.tokens = tokens;
    }


    protected Element createElement(Element parent, AST node) {
        Element result = parent.addElement("ast");
        int type = node.getType();
        String typeName = (String) tokens.get(new Integer(type));
        if (typeName == null) {
            result.addAttribute("type", Integer.toString(type));
        } else {
            result.addAttribute("name", typeName);
        }
        String text = node.getText();
        if (text != null) {
            if (text.indexOf('\n') == -1) {
                result.addAttribute("text", node.getText());
            } else {
                result.addCDATA(text);
            }
        }
        return result;
    }
}
