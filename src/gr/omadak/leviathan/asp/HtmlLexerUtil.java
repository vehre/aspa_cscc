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
    public final static int TYPE_FILE = 30005;
    public final static int TYPE_VIRTUAL = 30006;
    public final static int ASP_END = 30007;
    public final static int LANGUAGE = 30008;
    public final static int JS_END = 30009;
    public final static int VBS_END = 30010;
    public final static int UNKNOWN_CONTROL = 30011;
    
    private final static String KEY_SCRIPT = "script";
    private final static String KEY_LANG = "language";
    private final static String KEY_RUNAT = "runat";
    private final static String KEY_SRC = "src";
    private final static String KEY_FILE = "file";
    private final static String KEY_VIRTUAL = "virtual";

    /** Store and transport information of included or "scripted" files.
     * 
     * @author vehre
     *
     */
    public class InputInfo {
    	/** The type of the data in the file given by location. 
    	 * Expected to be either JS_START or VBS_START for script tags, and
    	 * TYPE_FILE or TYPE_VIRTUAL for includes. */
    	public int type;
    	/** The location where the input is stored. */
    	public String location;
    	/** Set if this is a server side include. */
    	public boolean serverSide;
    	/** Create a new instance. */
    	public InputInfo(int type, String location, boolean serverSide) {
    		this.type = type;
    		this.location = location;
    		this.serverSide = serverSide;
    	}
    	/** Construct assuming server side to be true. */
    	public InputInfo(int type, String location) {
    		this(type, location, true);
    	}
    }
    /** The last include is the last file seen in an include tag. 
     * Either asp or html comment. */
    private InputInfo lastInclude;
    /** The last script is the last file seen in a script tag. */
    private InputInfo lastScript;

    /** Remove all quotes (",') and all spaces from the string supplied.
     * @param str String to strip of all quotes and whitespaces.
     * @return str w/o any quotes and whitespaces.
     */
    private String clearAllQuotesAndWhiteSpaces(String str) {
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
                 || !attributes.keySet().contains(KEY_LANG) ){
            return HTML;
        }
        String lang = clearAllQuotesAndWhiteSpaces(attributes.get(KEY_LANG).toString());
        boolean runatServer = attributes.containsKey(KEY_RUNAT) 
        		&& clearAllQuotesAndWhiteSpaces(attributes.get(KEY_RUNAT).toString()).equalsIgnoreCase("server");
        String src = null;
        if (attributes.containsKey(KEY_SRC)) {
            src = attributes.get(KEY_SRC).toString();
        }
        int type = HTML;
        if (runatServer|| lang.equalsIgnoreCase("vbscript")) {
            type = getLangType(lang);
        }
        if (type != HTML) {
            lastScript = new InputInfo(type, src, runatServer);
        }
        return type;
    }


    public int getLangType(String lang) {
        lang = clearAllQuotesAndWhiteSpaces(lang);
        if (lang.equalsIgnoreCase("js")
        		|| lang.equalsIgnoreCase("jsscript")
        		|| lang.toLowerCase().startsWith("javascript")
        		|| lang.equalsIgnoreCase("jscript")) {
            return JS_START;
        } else if (lang.equalsIgnoreCase("vbscript")) {
            return VBS_START;
        } else {
            throw new RuntimeException("Unknown language:" + lang);
        }
    }


    public void addInclude(String includeType, String path) {
        path = clearAllQuotesAndWhiteSpaces(path);
        int type = 0;
        if (KEY_FILE.equalsIgnoreCase(includeType)) {
            type = TYPE_FILE;
        } else if (KEY_VIRTUAL.equalsIgnoreCase(includeType)) {
            type = TYPE_VIRTUAL;
        }
        lastInclude = new InputInfo(type, path);
    }


    public InputInfo getLastInclude() {
        InputInfo result = lastInclude;
        lastInclude = null;
        return result;
    }


    public InputInfo getLastScript() {
        InputInfo result = lastScript;
        lastScript = null;
        return result;
    }
}

