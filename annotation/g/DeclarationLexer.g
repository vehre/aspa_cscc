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
// The Annotation scanner
//----------------------------------------------------------------------------
class DeclarationLexer extends Lexer;

options {
    
    importVocab = DeclarationParser;
    exportVocab=Declaration; 
    testLiterals=false;     
    k=2;                    
    charVocabulary = '\3'..'\377';
}


ATID    :   '@'! ID (WS!)? ':'!  ;

protected
ID  :   ('a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '-')+   ;

WS  :   (   /*  '\r' '\n' can be matched in one alternative or by matching
                '\r' in one iteration and '\n' in another.  I am trying to
                handle any flavor of newline that comes in, but the language
                that allows both "\r\n" and "\r" and "\n" to all be valid
                newline is ambiguous.  Consequently, the resulting grammar
                must be ambiguous.  I'm shutting this warning off.
             */
            options {
                generateAmbigWarnings=false;
            }
        :   ' '
        |   '\t'
        |   '\r' '\n'   {newline();}
        |   '\r'        {newline();}
        |   '\n'        {newline();}
        )+
    ;

TEXT:   ( ~( ' ' | '\t' | '\r' | '\n' | '@' | '`' ) | ESCAPE)+    ;

protected
ESCAPE
    :   (
            options {
                generateAmbigWarnings=false;
            }
        :   '`'! '@'
        |   '`'! '`'
        |   '`' {$setText("");}
        )
    ;


