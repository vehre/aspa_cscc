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

import gr.omadak.leviathan.asp.objects.XmlASPClass;
import gr.omadak.leviathan.asp.objects.XmlObjectParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

public class MapLoader {
    private static Logger LOG = Logger.getLogger(MapLoader.class);

    public Map loadMap(URL desiredTokens, URL[] tokensFile)
    throws IOException {
        LOG.info("Loading map from urls:" + desiredTokens + " => "
        + tokensFile[0]);
        BufferedReader brDesired = getReader(desiredTokens);
        Map ref = new HashMap();
        Map result = new TreeMap();
        String line;
        for (int i = 0; i < tokensFile.length; i++) {
            BufferedReader brRef = getReader(tokensFile[i]);
            //ignore 2 first lines
            brRef.readLine();
            brRef.readLine();
            while ((line = brRef.readLine()) != null) {
                int eqIndex = line.indexOf('=');
                if (eqIndex != -1) {
                    String name = line.substring(0, eqIndex);
                    String value = line.substring(eqIndex + 1);
                    try {
                        Integer iValue = new Integer(value);
                        ref.put(name, iValue);
                    } catch (NumberFormatException nfe) {
                        LOG.warn("Failed to parse a token from:"
                        + tokensFile[i], nfe);
                    }
                }
            }
            brRef.close();
        }
        StringBuffer errors = new StringBuffer();
        if (!ref.isEmpty()) {
            while ((line = brDesired.readLine()) != null) {
                int eqIndex = line.indexOf('=');
                String name = line;
                String alias = null;
                if (eqIndex != -1) {
                    name = line.substring(0, eqIndex);
                    alias = line.substring(eqIndex + 1);
                }
                Integer numValue = (Integer) ref.get(
                alias != null ? alias : name);
                if (numValue != null) {
                    result.put(name, numValue);
                } else {
                    errors.append("Token code not found for name:").append(name)
						.append('\n');
                }
            }
            brDesired.close();
        }
        if (errors.length() > 0) {
            throw new IOException(
            "Check urls because errors occured during parsing\n"
            + errors);
        }
        return result;
    }


    public void loadObjects(URL configFile, Class resourceResolver,
    Map objectClasses, Map functions, XmlObjectParser parser)
    throws IOException, DocumentException {
        Properties props = new Properties();
        props.load(configFile.openStream());
        /*
        The classes may have interdependencies.
        To solve this, add an entry for each class
        in the objectClasses map and later configure them.
        */
        List classes = new ArrayList();
        List resources = new ArrayList();
        for (Iterator it = props.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            String resourceName = props.getProperty(key);
            if (key.startsWith("Object.")) {
                String objName = key.substring(7);
                URL resource = resourceResolver.getResource(resourceName);
                XmlASPClass clazz = new XmlASPClass(objName);
                objectClasses.put(objName.toUpperCase(), clazz);
                classes.add(clazz);
                resources.add(resource);
            } else if (key.startsWith("Functions.")) {
                URL resource = resourceResolver.getResource(resourceName);
                parser.configureMethods(functions, resource);
            } else {
                LOG.warn("Unrecognized key:" + key);
            }
        }
        for (int i = 0; i < classes.size(); i++) {
            XmlASPClass clazz = (XmlASPClass) classes.get(i);
            URL resource = (URL) resources.get(i);
            parser.configure(clazz, resource);
        }
    }


    private BufferedReader getReader(URL url) throws IOException {
        InputStream is = url.openStream();
        InputStreamReader isr = new InputStreamReader(is);
        return new BufferedReader(isr, 1024);
    }
}

