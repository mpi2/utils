/**
 * Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
 *
 * MEDICAL RESEARCH COUNCIL UK MRC
 *
 * Harwell Mammalian Genetics Unit
 *
 * http://www.har.mrc.ac.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mousephenotype.dcc.utils.io.jar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author julian
 */
public class JARUtils {
protected static final Logger logger = LoggerFactory.getLogger(JARUtils.class);
    public static <T> URL getURL(Class<T> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }

    public static <T> boolean inJar(Class<T> clazz) {
        return getURL(clazz).getPath().endsWith("jar");
    }

    public static <T> boolean inWar(Class<T> clazz) {
        return getURL(clazz).getPath().endsWith("war");
    }
    
    public static<T> boolean inTarget(Class<T> clazz) {
        return getURL(clazz).getPath().endsWith("classes/");
    }
    
    
    public static <T> URL getResource(Class<T> clazz, String name) {
        return clazz.getClassLoader().getResource(name);
    }

    public static org.w3c.dom.Document loadXMLFrom(InputStream is) throws SAXException, IOException {
        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (javax.xml.parsers.ParserConfigurationException ex) {
        }
        org.w3c.dom.Document doc = builder.parse(is);
        is.close();
        return doc;
    }

    public static InputStream getInputStreamFromJar(URL jarURL, String entryName) throws IOException {
        JarFile jarfile = new JarFile(jarURL.getFile());
        ZipEntry entry = jarfile.getEntry(entryName);
        return jarfile.getInputStream(entry);
    }

    public static boolean fileExistsOnJar(URL jarURL, String entryName) throws IOException {
        JarFile jarfile = new JarFile(jarURL.getFile());
        ZipEntry entry = jarfile.getEntry(entryName);
        if (entry == null) {
            return false;
        } else {
            return true;
        }
    }

    public static <T> InputStream getInputStreamFromClass(Class<T> clazz, String entryName) {
        return clazz.getClassLoader().getResourceAsStream(entryName);
    }

    public static String getInputStreamContents(InputStream inputStream) throws IOException {
        int read;
        StringBuilder sb = new StringBuilder();
        while ((read = inputStream.read()) != -1) {
            sb.append((char) read);
        }
        inputStream.close();
        return sb.toString();
    }
}
