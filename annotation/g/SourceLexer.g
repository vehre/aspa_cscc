/* ====================================================================
 * This source is PUBLIC DOMAIN and is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * Author: Bogdan Mitu
 * bogdan_mt@yahoo.com
 * ====================================================================
 */
header{
package annotation;
}

{
import java.util.*;
import java.io.*;
}

//----------------------------------------------------------------------------
// The Source scanner
//----------------------------------------------------------------------------
class SourceLexer extends Lexer;

options {

    importVocab = SourceParser;
    exportVocab=Source;
    testLiterals=false;
    k=2;
    charVocabulary = '\3'..'\377';
    filter = TEXT;
}

{
    void println() {
        try {
            destination.write( newline);
        } catch( Exception e) {
            e.printStackTrace();
        }

    }

    void print( char c) {
        try {
            destination.write( c);
        } catch( Exception e) {
            e.printStackTrace();
        }
    }

    void print( Token tok) {
        try {
            destination.write( tok.getText());
        } catch( Exception e) {
            e.printStackTrace();
        }
    }

    void replace( Token tok) {
        String key = tok.getText();
        Object repl = insertMap.get(key);
        if (repl != null) {
            String replacemant;
            if (repl instanceof String) {
                replacemant = (String) repl;
                insertMap.put(key, new String[] { replacemant });
            } else {
                replacemant = ((String[]) repl)[0];
            }
            try {
                destination.write( replacemant);
            } catch( Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Warning: No definition found for "
            + tok.getText());
        }
    }

    public Map insertMap = null;
    public Writer destination;
    private String newline = System.getProperty( "line.separator");
}

INSERT
    :   '<'! id:ID '>'!  { replace( id); }
    ;

protected
ID  :   ('a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '-')+   ;

protected
TEXT
    :   ( "\r\n" | '\r' | '\n' )   { newline(); println(); }
    |   c:.   { print(c); }
    ;

