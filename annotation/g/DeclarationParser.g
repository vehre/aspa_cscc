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
// The Annotation parser
//----------------------------------------------------------------------------
class DeclarationParser extends Parser;

options {   
    exportVocab = DeclarationParser; 
    k = 2;                    
}

tokens {
    START = "START";
}

{
    public Map insertMap = new HashMap();
} 

start       
    :   antet (pair)* EOF
    ;

antet
    :   (WS)? (TEXT WS)*
    ;

pair    {   StringBuffer txt = new StringBuffer();   }
    :   (ATID WS ATID) => ATID WS
    |   (ATID WS EOF) => ATID WS
    |   id:ATID
        WS
        (ti:TEXT {txt.append(ti.getText());})?
        (ws:WS t:TEXT {txt.append(ws.getText()).append(t.getText());})*  
        WS
        {   
            if (insertMap.containsKey(id.getText())) {
                System.err.println("Redeclaration of rule:" + id.getText());
            }
            insertMap.put( id.getText(), txt.toString());   
        } 
    ; 

