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
import java.util.Map;
import java.util.TreeMap;


public final class JsConstants implements JsTokenTypes{
    public static final Map KEYWORDS;

    static {
        KEYWORDS = new TreeMap();
    //----- ENTER KEYWORDS -----//
         KEYWORDS.put("Array", new Integer(ARRAY));
         KEYWORDS.put("Boolean", new Integer(BOOLEAN));
         KEYWORDS.put("Date", new Integer(DATE));
         KEYWORDS.put("Number", new Integer(NUMBER));
         KEYWORDS.put("Object", new Integer(OBJECT));
         KEYWORDS.put("String", new Integer(STRING));
         KEYWORDS.put("NaN", new Integer(NAN));
         KEYWORDS.put("break", new Integer(BREAK));
         KEYWORDS.put("case", new Integer(CASE));
         KEYWORDS.put("catch", new Integer(CATCH));
         KEYWORDS.put("continue", new Integer(CONTINUE));
         KEYWORDS.put("default", new Integer(DEFAULT));
         KEYWORDS.put("delete", new Integer(DELETE));
         KEYWORDS.put("else", new Integer(ELSE));
         KEYWORDS.put("false", new Integer(FALSE));
         KEYWORDS.put("finally", new Integer(FINALLY));
         KEYWORDS.put("for", new Integer(FOR));
         KEYWORDS.put("function", new Integer(FUNCTION));
         KEYWORDS.put("if", new Integer(IF));
         KEYWORDS.put("in", new Integer(IN));
         KEYWORDS.put("instanceof", new Integer(INSTANCEOF));
         KEYWORDS.put("new", new Integer(NEW));
         KEYWORDS.put("null", new Integer(NULL));
         KEYWORDS.put("return", new Integer(RETURN));
         KEYWORDS.put("switch", new Integer(SWITCH));
         KEYWORDS.put("this", new Integer(THIS));
         KEYWORDS.put("throw", new Integer(THROW));
         KEYWORDS.put("true", new Integer(TRUE));
         KEYWORDS.put("try", new Integer(TRY));
         KEYWORDS.put("typeof", new Integer(TYPEOF));
         KEYWORDS.put("undefined", new Integer(UNDEFINED));
         KEYWORDS.put("var", new Integer(VAR));
         KEYWORDS.put("void", new Integer(VOID));
         KEYWORDS.put("while", new Integer(WHILE));
         KEYWORDS.put("with", new Integer(WITH));
    //----- FINISHED KEYWORDS -----//
    }


    private JsConstants(){}
}


