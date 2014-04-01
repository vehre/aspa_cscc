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
    public final static int SCRIPT = 30012;
    
    private final static String KEY_SCRIPT = "script";
    private final static String KEY_LANG = "language";
    private final static String KEY_MIMETYPE = "type";
    private final static String KEY_RUNAT = "runat";
    private final static String KEY_SRC = "src";
    private final static String KEY_FILE = "file";
    private final static String KEY_VIRTUAL = "virtual";

    /** Step over all server side code introduced by <% tag. */
    private boolean ignoreServerSide = false;
    
    /** Enable parsing of server side code instructions. */
    public void enableServerSideCode() {
    	ignoreServerSide = false;
    }
    
    /** Disable parsing of server side code instructions. */
    public void disableServerSideCode() {
    	ignoreServerSide = true;
    }
    
    /** Check if server side includes are allowed and should be processed.*/
    public boolean serverSideIncludesAllowed() {
    	return !ignoreServerSide;
    }
    
    /** Store and transport information of included or "scripted" files.
     * 
     * @author vehre
     *
     */
    public class InputInfo {
    	/** The type of the data in the file given by location. 
    	 * Expected to be either JS_START or VBS_START. */
    	public int fileType;
    	/**  The include type, either TYPE_FILE or TYPE_VIRTUAL. */
    	public int includeType;
    	/** The location where the input is stored. */
    	public String location;
    	/** Set if this is a server side include. */
    	public boolean serverSide;
    	/** Create a new instance. */
    	public InputInfo(int fileType, int includeType, String location, boolean serverSide) {
    		this.fileType = fileType;
    		this.includeType = includeType;
    		this.location = location;
    		this.serverSide = serverSide;
    	}
    }
    /** Store information about the last file to include. This can either
     * be a server side or a client side include, which is only 
     * determined by the flag serverside in the InputInfo. */
    private InputInfo storeInputInfo;

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

    /**
     * Process the attributes given in a script tag.
     * @param name The tag name. This has to be script and is checked here for security.
     * @param attributes The map of attributes.
     * @return Either HTML, when the script type could not be recognized,
     * 	or JS_START for a javascript script, or VBS_START for a vbscript.
     */
    public int processScriptAttributes(String name, Map<String, String> attributes) {
        if (!KEY_SCRIPT.equalsIgnoreCase(name)
                 || !attributes.keySet().contains(KEY_LANG) ){
            return HTML;
        }
        
        boolean runatServer = attributes.containsKey(KEY_RUNAT) 
        		&& clearAllQuotesAndWhiteSpaces(attributes.get(KEY_RUNAT).toString()).equalsIgnoreCase("server");
        /* When server side includes are to be ignored, then treat this script-tag
         * as HTML.
         */
        if ( runatServer && ignoreServerSide )
        	return HTML;

        String src = null;
        if (attributes.containsKey(KEY_SRC))
            src = attributes.get(KEY_SRC).toString();
        
        int type = getLanguageType(attributes.containsKey(KEY_LANG) ?
        		clearAllQuotesAndWhiteSpaces(attributes.get(KEY_LANG)) :
        			attributes.get(KEY_MIMETYPE));
        /* When the type is vbscript and we are doing client side processing, 
         * then continue, else treat the type as HTML, which ignores the script
         * tag for further processing. 
         */
        if(ignoreServerSide && type != VBS_START )
        	return HTML;
        if ( type != HTML)
        	storeInputInfo = new InputInfo(type, TYPE_FILE, src, runatServer);

        return attributes.containsKey(KEY_SRC) ? SCRIPT : type;
    }

    /** Determine the language type also checking for mime types.
     * @param lang Either the content of a language key or of a type key.
     * @return HTML, JS_START or VBS_START depending on the input lang.  
     */
    private int getLanguageType(String lang) {
    	int result = HTML;
    	if ( lang != null ) {
	    	try {
	    		result = getLangType(lang);
	    	} catch (RuntimeException e) {
	    		if ( lang.equalsIgnoreCase("text/javascript") )
	    			result = JS_START;
	    		else if ( lang.equalsIgnoreCase("text/vbscript") )
	    			result = VBS_START;
	    	}
    	}
    	return result;
    }

    /** Get the language type for a language attribute only.
     * @param lang The content of the language attribute of a script tag.
     * @return HTML, JS_START or VBS_START depending on the input lang.
     */
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


    /** Fill the storedInputInfo from a server side include statement.
     * 
     * @param includeType Either file or virtual
     * @param path The file name an path. The ending is used to determine
     * which type the file has.
     */
    public void addInclude(String includeType, String path) {
        int incType = TYPE_FILE, langType = INCLUDE;
        if (KEY_FILE.equalsIgnoreCase(includeType)) {
            incType = TYPE_FILE;
        } else if (KEY_VIRTUAL.equalsIgnoreCase(includeType)) {
            incType = TYPE_VIRTUAL;
        }
        String extention = path.substring(path.lastIndexOf('.') + 1);
        if ( extention.equalsIgnoreCase("vbs") )
        	langType = VBS_START;
        else if ( extention.equalsIgnoreCase("js") || extention.equalsIgnoreCase("jsp") )
        	langType = JS_START;
        else if (extention.equalsIgnoreCase("htm") || extention.equalsIgnoreCase("html") 
        		|| extention.equalsIgnoreCase("asp") )
        	langType = INCLUDE;
        storeInputInfo = new InputInfo(langType, incType, path, true);
    }

	/** Get the inputInfo stored.
	 * @return The stored input info, or null, if none is stored.
	 */
    public InputInfo getStoredInclude() {
        InputInfo result = storeInputInfo;
        storeInputInfo = null;
        return result;
    }
    
    /**
     * 
     * @param attr
     * @return
     */
    public String formatScriptAttributes(Map<String, String> attrs) {
    	StringBuilder result = new StringBuilder("<script");
    	for ( Map.Entry<String, String> entry : attrs.entrySet()) {
    		if ( entry.getKey().equalsIgnoreCase("language") ) {
    			if ( entry.getValue().equalsIgnoreCase("vbscript") )
    				result.append(" type=\"text/vbscript\"");
    			else if ( entry.getValue().equalsIgnoreCase("js")
    	        		|| entry.getValue().equalsIgnoreCase("jsscript")
    	        		|| entry.getValue().toLowerCase().startsWith("javascript")
    	        		|| entry.getValue().equalsIgnoreCase("jscript") )
    				result.append(" type=\"text/javascript\"");
    			else
    				result.append(" language=\"").append(entry.getValue()).append('"');
    		} else {
    			result.append(' ').append(entry.getKey()).append("=\"").append(entry.getValue()).append('"');
    		}
    	}
    	result.append('>');
    	return result.toString();
    }
}

