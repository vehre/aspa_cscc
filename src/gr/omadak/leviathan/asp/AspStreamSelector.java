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

import antlr.CommonToken;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.TokenStreamSelector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class AspStreamSelector extends TokenStreamSelector {
/*
    1) Convert HtmlLexerUtil.HTML to vbLexer.HTML or JsLexer.HTML
    2) JS_END => to JsTokenTypes.EOF
    3) JS_START => VbsTokenTypes.EOF
*/
    private static final int LEX_HTML = 0;
    private static final int LEX_JS = 1;
    private static final int LEX_VB = 2;
    private static Logger LOG = Logger.getLogger(AspStreamSelector.class);

    private File baseDir;
    private File currentFile;
    private FileInputStream fis;
    private BufferedReader bis;
    private LinkedList<Token> storedTokens = new LinkedList<Token>();
    private HtmlLexerUtil utility = new HtmlLexerUtil();
    private HtmlLexer htmlLexer;
    private VbsLexer vbsLexer;
    private JsLexer jsLexer;

    private int lexerType = LEX_HTML;
    private int pageLanguage = LEX_VB;
    private boolean foundEOF = false;
    private boolean languageDefined;
	private boolean includeNonLanguageASPDirectives = true;

    private static class ICaseFileFilter implements FileFilter {
        String fileName;
        boolean getDirs;
        public ICaseFileFilter(String fName, boolean preferDirs) {
            fileName = fName;
            getDirs = preferDirs;
        }


        public boolean accept(File f) {
            boolean result = getDirs && f.isDirectory()
            || (!getDirs && f.isFile());
            if (result) {
                result = f.getName().equalsIgnoreCase(fileName);
            }
            return result;
        }
    }


    private Token tokenNext() throws TokenStreamException {
        return getCurrentStream().nextToken();
    }


    private Token grabHTML(StringBuffer sb, int[] line_col)
    throws TokenStreamException {
        Token t = tokenNext();
        boolean firstHtml = true;
        while (t.getType() == HtmlLexerUtil.HTML ) {
            if (firstHtml) {
                line_col[0] = t.getLine();
                line_col[1] = t.getColumn();
                firstHtml = false;
            }
            sb.append(t.getText());
            t = tokenNext();
        }
        return t;
    }


    private Token createToken(int type, String text, int line, int col) {
        Token result = new CommonToken(type, text);
        result.setLine(line);
        result.setColumn(col);
        return result;
    }


    private Token aspStart() throws TokenStreamException {
        Token result;
        pushScriptLexer(pageLanguage == LEX_VB
        		? HtmlLexerUtil.VBS_START
        	    : HtmlLexerUtil.JS_START);
        Token next = tokenNext();
        switch (next.getType()) {
        case HtmlLexerUtil.LANGUAGE: 
            if (utility.getLangType(next.getText()) == HtmlLexerUtil.VBS_START) {
                pageLanguage = LEX_VB;
            } else {
                pageLanguage = LEX_JS;
            }
            languageDefined = true;
            //is %>
            super.nextToken();
            //htmlLexer is current
            pop();
            //The @language directive should be the first
            //in an asp.Previus tokens are of no interest
            result = processHTML();
            break;
        case HtmlLexerUtil.ASP_END:
			result = processJsVb(next);
			break;
        case HtmlLexerUtil.UNKNOWN_CONTROL:
        	result = next;
        	break;
		default:
            if ( lexerType == LEX_VB && next.getType() == VbsTokenTypes.ASSIGN
            	|| lexerType == LEX_JS && next.getType() == JsTokenTypes.ASSIGN ) {
                int typeEQ_HTML = lexerType == LEX_JS
                		? JsTokenTypes.EQ_HTML
                		: VbsTokenTypes.EQ_HTML;
                result = createToken(typeEQ_HTML, "=", next.getLine(), next.getColumn());
            } else {
                result = next;
            }
        }
        return result;
    }


    private File getFileElement(File dir, String fileName, boolean notLast) {
        File[] files = dir.listFiles(new ICaseFileFilter(fileName, notLast));
        File result;
        switch (files.length) {
        case 0 : result = null; break;
        case 1 : result = files[0]; break;
        default :
            result = null; //make compiler stop complaining
            for (int i = 0; i < files.length; i++) {
                result = files[i];
                if (result.getName().equals(fileName)) {
                    break;
                }
            }
            break;
        }
        return result;
    }


    private File getFile(File dir, String fileName) {
        String fName = fileName.replace('\\', '/');
        if (fName.charAt(0) == '/') {
            fName = fName.substring(1);
        }
        StringTokenizer st = new StringTokenizer(fName, "/");
        File cDir = dir;
        File result = null;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            File cdir = getFileElement(cDir, token, st.hasMoreTokens());
            if (cdir == null) {
                LOG.error("No file with name:" +  token + " found at dir:"
                + cDir.getAbsolutePath());
            } else if (cdir.isDirectory() && !st.hasMoreTokens()) {
                LOG.error(fileName + " is a directory path");
            } else if (cdir.isFile() && st.hasMoreTokens()) {
                LOG.error(fileName + " is incomplete");
                break;
            } else if (cdir.isFile()) {
                result = cdir;
            } else {
                cDir = cdir;
            }
        }
        return result;
    }


    /*
        Groups as many HTML tokens into one and processes the next
        non HTML Token.
        The next non HTML Token can be:
        1) ASP_START("<%") calls aspStart
        2) JS_START("<script language="Javascript" runat="Server")
        3) VBS_START("<script language="vbscript" runat="Server")
        4) INCLUDE("<!--#include")
        5) EOF
    */
    private Token processHTML() throws TokenStreamException{
        Token result = null;
        StringBuffer sb = new StringBuffer();
        int[] line_col = new int[2];
        Token t;
        int initialLexerType = lexerType;
        boolean isScript;
        do {
        	t = grabHTML(sb, line_col);
        	isScript = false;
	        switch (t.getType()) {
	            case HtmlLexerUtil.ASP_START:
	                result = aspStart();
	                if ( result.getType() == HtmlLexerUtil.UNKNOWN_CONTROL ) {
	                	if ( includeNonLanguageASPDirectives  ) {
		                	sb.append(t.getText());
		                	sb.append(result.getText());
	                	} else
	                		LOG.info("Discarding asp directive: "+ result.getText());
	                	/* Remove the vbs parser from the lexer stack. */
	                	pop();
	                	result = null;
	                }
	                break;
	            case HtmlLexerUtil.JS_START:
	                pushScriptLexer(HtmlLexerUtil.JS_START);
	                if (initialLexerType != LEX_JS && vbsLexer != null) {
	                    result = createToken(Token.EOF_TYPE, null, t.getLine(), t.getColumn());
	                } else {
	                    result = createToken(JsTokenTypes.NEW_LINE, null, t.getLine(),t.getColumn());
	                }
	                break;
	            case HtmlLexerUtil.VBS_START:
	                pushScriptLexer(HtmlLexerUtil.VBS_START);
	                if (initialLexerType != LEX_VB && jsLexer != null) {
	                    result = createToken(Token.EOF_TYPE, null, t.getLine(), t.getColumn());
	                } else {
	                    result = createToken(VbsTokenTypes.STATEMENT_END,
	                    		null, htmlLexer.getLine(), htmlLexer.getColumn());
	                }
	                break;
	            case HtmlLexerUtil.SCRIPT:
	            	isScript = true; 
	            	// Intentionally fall through.
	            case HtmlLexerUtil.INCLUDE:
	                HtmlLexerUtil.InputInfo include = utility.getStoredInclude();
	                File dir = HtmlLexerUtil.TYPE_FILE == include.includeType
	                		? currentFile.getParentFile(): baseDir;
	                File included = getFile(dir, include.location);
	                result = createToken(
	                		pageLanguage == LEX_JS ? JsTokenTypes.INCLUDE : (isScript ? VbsTokenTypes.SCRIPT : VbsTokenTypes.INCLUDE),
	                		included == null ? include.location : included.getAbsolutePath(), 
	                		t.getColumn(), t.getLine());
	                break;
	            case Token.EOF_TYPE:
	                foundEOF = true;
	                result = t;
	                break;
	            default: throw new RuntimeException("Unexpected token:"+ t.toString());
	        }
        } while ( result == null );
        int i = 0;
        //check if everything is just blanks
        while (i < sb.length() && (sb.charAt(i) == 13 || sb.charAt(i) == 10)) {
            i++;
        }
        if (sb.length() > i) {
            storedTokens.add(result);
            result = createToken(pageLanguage == LEX_JS || t != null && t.getType() == HtmlLexerUtil.JS_START
            		? JsTokenTypes.HTML : VbsTokenTypes.HTML, sb.toString(),
            				line_col[0], line_col[1]);
        }
        return result;
    }


    /*
      Called after <% Token found.
    */
    private Token processJsVb(Token t) throws TokenStreamException {
        Token result = t != null ? t : tokenNext();
        int type = result.getType();
        boolean shouldPop = type == HtmlLexerUtil.ASP_END
        		|| (lexerType == LEX_JS && type == HtmlLexerUtil.JS_END)
        		|| (lexerType == LEX_VB && (type == HtmlLexerUtil.VBS_END 
        			|| type == VbsTokenTypes.SCRIPT || type == Token.EOF_TYPE));
        if (shouldPop) {
            int lastLexer = lexerType;
            //job done
            try {
            	pop();
            } catch (java.util.NoSuchElementException ex) {
            	; // Everything fine for the moment. TODO:!!!
            }
            int tType;
            if (type == HtmlLexerUtil.ASP_END) {
                tType = lastLexer == LEX_JS
                		? JsTokenTypes.NEW_LINE
                		: VbsTokenTypes.STATEMENT_END;
            } else {
                tType = Token.EOF_TYPE;
                /* Only set foundEOF, when EOF really occurred. */
                if ( type == Token.EOF_TYPE ) 
                	foundEOF = true;
            }
            result = createToken(tType, null, result.getLine(),
            result.getColumn());
        }
        return result;
    }

    /** Depending on the current lexer type switch getting of tokens.
     * @return The next token depending on the currently active lexer.
     * @throws TokenStreamException
     */
    private Token getNextToken() throws TokenStreamException {
        switch (lexerType) {
            case LEX_HTML:
            	return processHTML();
            case LEX_JS:
            case LEX_VB:
            	return processJsVb(null);
            default:
                //should never happen
                throw new RuntimeException("Unknown lexer type");
        }
    }


    private void checkLexerType() {
        if (input instanceof HtmlLexer) {
            lexerType = LEX_HTML;
        } else if (input instanceof JsLexer) {
            lexerType = LEX_JS;
        } else if (input instanceof VbsLexer) {
            lexerType = LEX_VB;
        } else {
            throw new RuntimeException(input.getClass().getName());
        }
    }


    private void pushScriptLexer(int lang_type)
    throws TokenStreamException {
        if (lang_type == HtmlLexerUtil.JS_START) {
            if (jsLexer == null) {
                jsLexer = new JsLexer(htmlLexer.getInputState());
                jsLexer.setFilename(currentFile.getAbsolutePath());
                addInputStream(jsLexer, "js");
            }
            push(jsLexer);
        } else if (lang_type == HtmlLexerUtil.VBS_START) {
            if (vbsLexer == null) {
                vbsLexer = new VbsLexer(htmlLexer.getInputState());
                vbsLexer.setFilename(currentFile.getAbsolutePath());
                addInputStream(vbsLexer, "vbs");
            }
            push(vbsLexer);
        } else {
            throw new RuntimeException("Unknown lang type:" + lang_type);
        }
    }


    public AspStreamSelector(File f, File baseDir, boolean findLang, boolean incNonLangDirectives,
    		boolean disServerSideCode)
    				throws TokenStreamException {
        super();
        includeNonLanguageASPDirectives = incNonLangDirectives;
        try {
            currentFile = f;
            fis = new FileInputStream(currentFile);
            bis = new BufferedReader(
            		new InputStreamReader(fis), 1024);
            if ( currentFile.getName().endsWith(".vbs")) {
	            vbsLexer = new VbsLexer(bis);
	            vbsLexer.setFilename(currentFile.getAbsolutePath());
	            addInputStream(vbsLexer, "1");
	            select(vbsLexer);
	            lexerType = LEX_VB;
            } else {
	            htmlLexer = new HtmlLexer(bis);
	            htmlLexer.setFilename(currentFile.getAbsolutePath());
	            htmlLexer.setHtmlLexerUtil(utility);
	            if ( disServerSideCode ) utility.disableServerSideCode();
	            addInputStream(htmlLexer, "1");
	            select(htmlLexer);
            }
        } catch (IOException ioe) {
            throw new TokenStreamException(ioe.getMessage());
        }
        if (baseDir != null) {
            setBasePath(baseDir);
        }
        //make an attempt to determine default language
        if (findLang) {
            Token first = getNextToken();
            storedTokens.add(first);
        }
    }


    public AspStreamSelector(File f, File baseDir, boolean incNonLangDirectives, boolean disServerSideCode) 
    		throws TokenStreamException {
        this(f, baseDir, true, incNonLangDirectives, disServerSideCode);
    }


    public String getFilename() throws TokenStreamException {
        return  currentFile.getAbsolutePath();
    }


    public Token nextToken() throws TokenStreamException {
        Token result = !storedTokens.isEmpty()
        		? storedTokens.remove()
        		: getNextToken();
        return result;
    }


    public void setDefaultVb(boolean vb) {
        if (!languageDefined) {
            pageLanguage = vb ? LEX_VB : LEX_JS;
        }
    }

    /** Determine, if the language of the next non-HTML token is VB.
     * 
     * @return true, if the next language is vbscript.
     * @throws TokenStreamException
     */
    public boolean isVbCurrent() throws TokenStreamException {
    	Token nextT;
    	if ( !storedTokens.isEmpty() )
    		/* Get the last token from the stored ones. That token is the
    		 * first available to learn if the parser to use is VB or JS. 
    		 */
    		nextT = storedTokens.peekFirst();
    	else {
    		/* nextToken() looks into the queue, but we already know, that that is empty. */
    		nextT = getNextToken();
        	// Don't pop the token, just analyze it.
        	storedTokens.add(nextT);    		
    	}
    	switch ( nextT.getType() ) {
    	case HtmlLexerUtil.JS_START:
    	case JsTokenTypes.NEW_LINE:
    	case JsTokenTypes.INCLUDE:
    		return false;
    	default:
	        return (lexerType == LEX_HTML && pageLanguage == LEX_VB)
	        		|| lexerType == LEX_VB;
    	}
    }


    public void setBasePath(File baseDir) {
        this.baseDir = baseDir;
    }

    /** Tell the parsing loop, that there are still tokens available.
     * Only close the files, when no tokens are stored anymore.
     * @return True, when tokens are still available.
     */
    public boolean hasMoreTokens() {
    	if (!storedTokens.isEmpty())
    		return true;
        if (foundEOF) {
            //close the streams
            try {
                bis.close();
                fis.close();
                bis = null;
                fis = null;
            } catch (IOException ioe) {
                System.err.println("Failed closing streams??");
            }
        }
        return !foundEOF;
    }


    public void push(TokenStream t) {
        super.push(t);
        checkLexerType();
    }


    public TokenStream pop() {
        TokenStream s = super.pop();
        checkLexerType();
        return s;
    }
}
