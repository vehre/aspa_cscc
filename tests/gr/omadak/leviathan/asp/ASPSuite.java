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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import junit.framework.Assert;
import junit.framework.TestSuite;

public class ASPSuite {
  public static TestSuite suite() {
    try {
        org.apache.log4j.PropertyConfigurator.configure(
        ASPSuite.class.getResource("log4j.properties"));
    } catch (Exception ex) {
        Assert.fail("No logging!");
    }
    TestSuite suite;
    suite = new TestSuite("gr.omadak.leviathan.asp");
    try {
        InputStream is = ASPSuite.class.getResourceAsStream("files.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr, 1024);
        String line = br.readLine();
        if (line == null) {
            throw new NullPointerException("The file is empty");
        }
        int sup = line.indexOf(':');
        if (sup == -1) {
            throw new IllegalStateException(
            "structure basesource:basexml but found:" + line);
        }
        File baseDir = new File(line.substring(0, sup));
        if (!(baseDir.exists() && baseDir.isDirectory())) {
            throw new IllegalStateException("The file:" + baseDir.getName()
            + " should exist and should be a directory");
        }
        File baseXml = new File(line.substring(sup + 1));
        if (!(baseXml.exists() && baseXml.isDirectory())) {
            throw new IllegalStateException("The file:" + baseXml.getName()
            + " should exist and should be a directory");
        }
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0 || line.charAt(0) == '#') {
                continue;
            }
            sup = line.indexOf(':');
            if (sup == -1) {
                throw new IllegalStateException(
                "structure sourcename:xmlFile but found:" + line);
            }
            File sourceFile = new File(baseDir, line.substring(0, sup));
            if (!(sourceFile.exists() && sourceFile.isFile())) {
                throw new IllegalStateException(
                "source file:" + sourceFile.getAbsolutePath()
				+ " should exist and should be a file");
            }
            line = line.substring(sup + 1);
            sup = line.indexOf(':');
            boolean caseInsens = sup != -1;
            if (caseInsens) {
                line = line.substring(0, line.length() - 1);
            }
            File xmlFile =  new File(baseXml, line);
            if (!(xmlFile.exists() && xmlFile.isFile())) {
                throw new IllegalStateException(
                "xml file:" + xmlFile.getAbsolutePath()
				+ " should exist and should be a file");
            }
            suite.addTest(new ASPTest("testASP",
            baseDir, sourceFile, xmlFile, !caseInsens));
        }
    } catch (IOException ioex) {
        Assert.fail("Failed to read configuration");
    } catch (Exception ex) {
        Assert.fail("Failed:" + ex.getMessage());
    }
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}

