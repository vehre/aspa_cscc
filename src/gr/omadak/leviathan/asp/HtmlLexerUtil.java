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

public class HtmlLexerUtil {
    public final static int ASP_START = 30000;
    public final static int JS_START = 30001;
    public final static int VBS_START = 30002;
    public final static int HTML = 30003;
    public final static int INCLUDE = 30004;
    public final static Integer TYPE_FILE = new Integer(30005);
    public final static Integer TYPE_VIRTUAL = new Integer(30006);
    public final static int ASP_END = 30007;
    public final static int LANGUAGE = 30008;
    public final static int JS_END = 30009;
    public final static int VBS_END = 30010;

    private final static String KEY_SCRIPT = "script";
    private final static String KEY_LANG = "language";
    private final static String KEY_RUNAT = "runat";
    private final static String KEY_SRC = "src";
    private final static String KEY_FILE = "file";
    private final static String KEY_VIRTUAL = "virtual";

    private Object[] lastInclude;
    private Object[] lastScript;


    private static String clearQuotes(String str) {
        StringBuffer sb = new StringBuffer(str);
        int i = 0;
        while (i < sb.length()) {
            switch (sb.charAt(i)) {
                case '\'':
                case '"':
                case ' ':
                    sb.deleteCharAt(i);
                    break;
                default: i++; break;
            }
        }
        return sb.toString();
    }


    public int getType(String name, Map attributes) {
        if (!KEY_SCRIPT.equalsIgnoreCase(name)
                 || !attributes.keySet().contains(KEY_LANG)
                 || !attributes.keySet().contains(KEY_RUNAT)) {
            return HTML;
        }
        String lang = attributes.get(KEY_LANG).toString();
        String runat = attributes.get(KEY_RUNAT).toString();
        String src = null;
        if (attributes.containsKey(KEY_SRC)) {
            src = attributes.get(KEY_SRC).toString();
        }
        lang = clearQuotes(lang);
        runat = clearQuotes(runat);
        int type = HTML;
        if (runat.equalsIgnoreCase("server")) {
            type = getLangType(lang);
        }
        if (type != HTML) {
            lastScript = new Object[] {new Integer(type), src};
        }
        return type;
    }


    public static int getLangType(String lang) {
        lang = clearQuotes(lang);
        int type;
        if (lang.equalsIgnoreCase("js")
        || lang.equalsIgnoreCase("jsscript")
        || lang.toLowerCase().startsWith("javascript")
        || lang.equalsIgnoreCase("jscript")) {
            type = JS_START;
        } else if (lang.equalsIgnoreCase("vbscript")) {
            type = VBS_START;
        } else {
            throw new RuntimeException("Uknown language:" + lang);
        }
        return type;
    }


    public void addInclude(String includeType, String path) {
        path = clearQuotes(path);
        Integer type = null;
        if (KEY_FILE.equalsIgnoreCase(includeType)) {
            type = TYPE_FILE;
        } else if (KEY_VIRTUAL.equalsIgnoreCase(includeType)) {
            type = TYPE_VIRTUAL;
        }
        lastInclude = new Object[] {type, path};
    }


    public Object[] getLastInclude() {
        Object[] result = lastInclude;
        lastInclude = null;
        return result;
    }


    public Object[] getLastScript() {
        Object[] result = lastScript;
        lastScript = null;
        return result;
    }
}

