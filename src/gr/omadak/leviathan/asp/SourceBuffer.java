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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * SourceBuffer provides methods for pretty printing source code
 *
 * @author <a href="mailto:anakreonmejde@yahoo.gr">anakreon</a>
 * @version 1.0
 */
public class SourceBuffer {
    private PrintWriter out;
    private boolean inPhp;
    private boolean lastWasLF;
    private int nestedLevel;
    private PrintWriter oldWriter;
    private StringWriter writer;

    private static String IDENTATION_UNIT = "  ";


    private void printStartPhp() {
        out.println("<?php");
        inPhp = true;
    }


    private void printEndPhp() {
        out.println("?>");
        inPhp = false;
    }


    private void printIdent() {
        if (!inPhp) {
            printStartPhp();
        }
        if (lastWasLF) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < nestedLevel; i++) {
                sb.append(IDENTATION_UNIT);
            }
            out.print(sb.toString());
        }
    }


    /**
	 * Creates a new <code>SourceBuffer</code> instance.
	 *
	 * @param ps a <code>PrintWriter</code> value
	 */
	public SourceBuffer(PrintWriter ps) {
        this.out = ps;
    }


    public void startBuffering() {
        if (oldWriter == null) {
            oldWriter = out;
            writer = new StringWriter();
            out = new PrintWriter(writer);
        }
    }


    public void stopBuffering() {
        if (oldWriter != null) {
            out = oldWriter;
            writer = null;
            oldWriter = null;
        }
    }


    public void clearBuffer() {
        writer.getBuffer().setLength(0);
    }


    public StringBuffer getBuffer() {
        return writer.getBuffer();
    }


    public String makeString(String str) {
        StringBuffer sb = new StringBuffer(str);
        int i = 0;
        while (i < sb.length()) {
            char c = sb.charAt(i);
            if (c == '"' || c == '$') {
                sb.insert(i, '\\');
                i++;
            }
            i++;
        }
        sb.insert(0, '"');
        sb.append('"');
        return sb.toString();
    }


    public void print(char c) {
        printIdent();
        out.print(c);
        lastWasLF = false;
    }


    public void print(String s) {
        printIdent();
        out.print(s);
        lastWasLF = false;
    }


    public void println(String s) {
        printIdent();
        out.println(s);
        lastWasLF = true;
    }


    public void printHTML(String s) {
        if (inPhp) {
            printEndPhp();
        }
        out.print(s);
    }


    public void incLevel() {
        nestedLevel++;
    }


    public void decLevel() {
        nestedLevel--;
    }


    public int getLevel() {
        return nestedLevel;
    }


    public void end() {
        if (inPhp) {
            printEndPhp();
        }
    }
}
