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
package org.mousephenotype.dcc.utils.io.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mousephenotype.dcc.utils.io.conf.Reader;
import org.slf4j.LoggerFactory;

public class ReaderTest {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(ReaderTest.class);
    private static final String PROPERTIES_FILENAME = "test.properties";
    private static Reader reader;

    @BeforeClass
    public static void setup() {
        try {
            reader = new Reader(PROPERTIES_FILENAME);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @Test
    public void testGetIntProperty() {
        String key = "int";
        Assert.assertEquals(reader.getPropertiesConfiguration().getInt(key), 1);
    }

    @Test
    public void testGetGroupOfProperties() {
        String groupkey = "imits";
        Properties properties = reader.getProperties(groupkey);
        Properties expected = new Properties();

        expected.put("imits.username", "my_imits_username");
        expected.put("imits.password", "my_imits_password");

        Assert.assertEquals(expected, properties);
    }

    @Test
    public void testGetMultivalueProperties() {
        String key = "text_list";
        String[] expected = new String[]{"uno", "dos", "tres y cuatro", "cinco"};
        String[] actuals = reader.getPropertiesConfiguration().getStringArray(key);
        Assert.assertArrayEquals(expected, actuals);
    }

    //Fails as it returns strings :-)
    @Test(expected = java.lang.AssertionError.class)
    public void testGetMultivaluePropertiesInt() {
        String key = "int_list";
        List<Object> expected = new ArrayList<Object>();
        expected.add(Integer.valueOf(1));
        expected.add(Integer.valueOf(3));
        expected.add(Integer.valueOf(4));
        expected.add(Integer.valueOf(6));
        List actuals = reader.getPropertiesConfiguration().getList(key);
        Assert.assertEquals(expected, actuals);
    }
    
    @Test
    public void testLoadAllProperties(){
        
    }
}
