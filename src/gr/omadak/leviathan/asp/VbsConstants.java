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


public final class VbsConstants {
    public static final Map KEYWORDS;

    static {
        KEYWORDS = new TreeMap();
    //----- ENTER KEYWORDS -----//
         KEYWORDS.put("AND", new Integer(VbsTokenTypes.AND));
         KEYWORDS.put("CALL", new Integer(VbsTokenTypes.CALL));
         KEYWORDS.put("CASE", new Integer(VbsTokenTypes.CASE));
         KEYWORDS.put("CONST", new Integer(VbsTokenTypes.CONST));
         KEYWORDS.put("CLASS", new Integer(VbsTokenTypes.CLASS));
         KEYWORDS.put("DIM", new Integer(VbsTokenTypes.DIM));
         KEYWORDS.put("DO", new Integer(VbsTokenTypes.DO));
         KEYWORDS.put("EACH", new Integer(VbsTokenTypes.EACH));
         KEYWORDS.put("ELSE", new Integer(VbsTokenTypes.ELSE));
         KEYWORDS.put("ELSEIF", new Integer(VbsTokenTypes.ELSEIF));
         KEYWORDS.put("EMPTY", new Integer(VbsTokenTypes.EMPTY));
         KEYWORDS.put("END", new Integer(VbsTokenTypes.END));
         KEYWORDS.put("EQV", new Integer(VbsTokenTypes.EQV));
         KEYWORDS.put("ERASE", new Integer(VbsTokenTypes.ERASE));
         KEYWORDS.put("ERROR", new Integer(VbsTokenTypes.ERROR));
         KEYWORDS.put("EXIT", new Integer(VbsTokenTypes.EXIT));
         KEYWORDS.put("EXPLICIT", new Integer(VbsTokenTypes.EXPLICIT));
         KEYWORDS.put("FALSE", new Integer(VbsTokenTypes.FALSE));
         KEYWORDS.put("FOR", new Integer(VbsTokenTypes.FOR));
         KEYWORDS.put("FUNCTION", new Integer(VbsTokenTypes.FUNCTION));
         KEYWORDS.put("IF", new Integer(VbsTokenTypes.IF));
         KEYWORDS.put("IS", new Integer(VbsTokenTypes.IS));
         KEYWORDS.put("IMP", new Integer(VbsTokenTypes.IMP));
         KEYWORDS.put("IN", new Integer(VbsTokenTypes.IN));
         KEYWORDS.put("LOOP", new Integer(VbsTokenTypes.LOOP));
         KEYWORDS.put("MOD", new Integer(VbsTokenTypes.MOD));
         KEYWORDS.put("NEXT", new Integer(VbsTokenTypes.NEXT));
         KEYWORDS.put("NEW", new Integer(VbsTokenTypes.NEW));
         KEYWORDS.put("NOT", new Integer(VbsTokenTypes.NOT));
         KEYWORDS.put("NOTHING", new Integer(VbsTokenTypes.NOTHING));
         KEYWORDS.put("NULL", new Integer(VbsTokenTypes.NULL));
         KEYWORDS.put("ON", new Integer(VbsTokenTypes.ON));
         KEYWORDS.put("OPTION", new Integer(VbsTokenTypes.OPTION));
         KEYWORDS.put("OR", new Integer(VbsTokenTypes.OR));
         KEYWORDS.put("REDIM", new Integer(VbsTokenTypes.REDIM));
         KEYWORDS.put("REM", new Integer(VbsTokenTypes.REM));
         KEYWORDS.put("RESUME", new Integer(VbsTokenTypes.RESUME));
         KEYWORDS.put("SELECT", new Integer(VbsTokenTypes.SELECT));
         KEYWORDS.put("SET", new Integer(VbsTokenTypes.SET));
         KEYWORDS.put("STEP", new Integer(VbsTokenTypes.STEP));
         KEYWORDS.put("SUB", new Integer(VbsTokenTypes.SUB));
         KEYWORDS.put("THEN", new Integer(VbsTokenTypes.THEN));
         KEYWORDS.put("TO", new Integer(VbsTokenTypes.TO));
         KEYWORDS.put("TRUE", new Integer(VbsTokenTypes.TRUE));
         KEYWORDS.put("WEND", new Integer(VbsTokenTypes.WEND));
         KEYWORDS.put("WHILE", new Integer(VbsTokenTypes.WHILE));
         KEYWORDS.put("WITH", new Integer(VbsTokenTypes.WITH));
         KEYWORDS.put("XOR", new Integer(VbsTokenTypes.XOR));
         KEYWORDS.put("UNTIL", new Integer(VbsTokenTypes.UNTIL));
         KEYWORDS.put("PUBLIC", new Integer(VbsTokenTypes.PUBLIC));
         KEYWORDS.put("PRIVATE", new Integer(VbsTokenTypes.PRIVATE));
         KEYWORDS.put("DEFAULT", new Integer(VbsTokenTypes.DEFAULT));
         KEYWORDS.put("PROPERTY", new Integer(VbsTokenTypes.PROPERTY));
		 KEYWORDS.put("PRESERVE", new Integer(VbsTokenTypes.PRESERVE));
         KEYWORDS.put("GET", new Integer(VbsTokenTypes.GET));
		 KEYWORDS.put("GOTO", new Integer(VbsTokenTypes.GOTO));
         KEYWORDS.put("LET", new Integer(VbsTokenTypes.LET));
         KEYWORDS.put("BYREF", new Integer(VbsTokenTypes.BYREF));
         KEYWORDS.put("BYVAL", new Integer(VbsTokenTypes.BYVAL));
         KEYWORDS.put("RANDOMIZE", new Integer(VbsTokenTypes.RANDOMIZE));
     //----- FINISHED KEYWORDS -----//
   }

    private VbsConstants() {}

}
