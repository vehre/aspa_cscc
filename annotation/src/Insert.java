/* ====================================================================
 * This source is PUBLIC DOMAIN and is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. 
 *
 * Author: Bogdan Mitu
 * bogdan_mt@yahoo.com
 * ====================================================================
 */
package annotation;

import antlr.*;
import java.io.*;
import java.util.*;

public class Insert {

    private static void printUsage() {
        System.out.println( 
        "Usage: java Insert -s <source_file> -d <dest_file> -i <insert_file>");
        System.exit(-1);
    }


    public static void main( String[] args) {

        File sourceFile = null;
        File insertFile = null;
        File destFile = null;

        if (args.length < 6) {
            printUsage();
        } else {
            for( int i = 0; i < 6; i = i+2) {
                if( args[i].equals("-s"))
                    sourceFile = new File( args[i+1]);
                else if( args[i].equals("-d"))
                    destFile = new File( args[i+1]);
                else if( args[i].equals("-i"))
                    insertFile = new File( args[i+1]);
            }
        }
        if( sourceFile == null || destFile == null || insertFile == null) {
            printUsage();
        }

        if (destFile.exists() 
        && destFile.lastModified() >= Math.max(sourceFile.lastModified(),
        insertFile.lastModified())) {
            return;
        }
        try {
            Reader source = new BufferedReader(new FileReader( sourceFile));
            Reader insert = new BufferedReader(new FileReader( insertFile), 4096);
            Writer dest = new BufferedWriter(new FileWriter( destFile));

            DeclarationLexer declLexer = new DeclarationLexer( insert);
            DeclarationParser declParser = new DeclarationParser( declLexer);
            declParser.start();
            SourceLexer srcLexer = new SourceLexer( source);
            SourceParser srcParser = new SourceParser( srcLexer);
            srcLexer.insertMap = declParser.insertMap;
            srcLexer.destination = dest;
            srcParser.start();
            dest.close();
            Map insertMap = srcLexer.insertMap;
            for (Iterator it = insertMap.keySet().iterator(); it.hasNext();) {
                Object key =  it.next();
                Object value = insertMap.get(key);
                if (value instanceof String) {
                    System.err.println("Unused rule:" + key);
                }
            }
        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }
}
