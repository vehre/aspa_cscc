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
// The Source parser
//----------------------------------------------------------------------------
class SourceParser extends Parser;

options {   
    exportVocab = SourceParser; 
    k = 2;                    
}

start   
    :   (INSERT)* EOF
    ;
