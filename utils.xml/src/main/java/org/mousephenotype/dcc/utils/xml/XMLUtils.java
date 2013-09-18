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
package org.mousephenotype.dcc.utils.xml;

import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author julian
 */
public class XMLUtils {

    private static HashMap<String, JAXBContext> jAXBContexts = new HashMap<String, JAXBContext>();
    private static HashMap<String, Unmarshaller> unmarshallers = new HashMap<String, Unmarshaller>();
    private static HashMap<String, Marshaller> marshallers = new HashMap<String, Marshaller>();
    
    private static final ImmutableMap<String, String> TIME_ZONES = new ImmutableMap.Builder<String, String>().put("J", "US/Eastern").put("Ucd", "US/Pacific").put("Ncom", "Canada/Eastern").put("Bay", "US/Central").put("Wtsi", "UTC").put("Ics", "CET").put("Hmgu", "CET").put("Rbrc", "Japan").put("Ning", "PRC").put("H", "UTC").put("Gmc", "CET").build();
    
    public static String getTimeZone(String CentreILARcode){
        return TIME_ZONES.get(CentreILARcode);
    }
    

    
    public static final String CONTEXTPATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";

    public static <T> T unmarshal(String contextPath, Class<T> clazz, String filename) throws JAXBException, FileNotFoundException, IOException {
        if (!XMLUtils.jAXBContexts.containsKey(contextPath)) {
            XMLUtils.jAXBContexts.put(contextPath, JAXBContext.newInstance(contextPath));
        }

        if (!XMLUtils.unmarshallers.containsKey(contextPath)) {
            XMLUtils.unmarshallers.put(contextPath, XMLUtils.jAXBContexts.get(contextPath).createUnmarshaller());
        }
        
        FileInputStream fileInputStream = new FileInputStream(filename);
        JAXBElement<T> element = XMLUtils.unmarshallers.get(contextPath).unmarshal(new StreamSource(fileInputStream), clazz);
        fileInputStream.close();
        return element.getValue();
    }

    public static <T> void marshall(String contextPath, T object, String filename) throws JAXBException {
        if (!XMLUtils.jAXBContexts.containsKey(contextPath)) {
            XMLUtils.jAXBContexts.put(contextPath, JAXBContext.newInstance(contextPath));
        }

        if (!XMLUtils.marshallers.containsKey(contextPath)) {
            XMLUtils.marshallers.put(contextPath, XMLUtils.jAXBContexts.get(contextPath).createMarshaller());
            XMLUtils.marshallers.get(contextPath).setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        }
        XMLUtils.marshallers.get(contextPath).marshal(object, new File(filename));
    }

    public static <T> void marshall(String contextPath,String schemaLocation, T object, String filename) throws JAXBException {
        if (!XMLUtils.jAXBContexts.containsKey(contextPath)) {
            XMLUtils.jAXBContexts.put(contextPath, JAXBContext.newInstance(contextPath));
        }

        if (!XMLUtils.marshallers.containsKey(contextPath)) {
            XMLUtils.marshallers.put(contextPath, XMLUtils.jAXBContexts.get(contextPath).createMarshaller());
            XMLUtils.marshallers.get(contextPath).setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            XMLUtils.marshallers.get(contextPath).setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
        }
        XMLUtils.marshallers.get(contextPath).marshal(object, new File(filename));
    }
    
   
}
