package org.mousephenotype.dcc.utils.io.conf;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Reader {

    private final PropertiesConfiguration propertiesConfiguration;

    public Reader(String filename) throws ConfigurationException {
        propertiesConfiguration = new PropertiesConfiguration(filename);
    }

    public PropertiesConfiguration getPropertiesConfiguration() {
        return propertiesConfiguration;
    }
    
    public Map getMap(){
        return ConfigurationConverter.getMap(propertiesConfiguration);
    }
    
    public Properties getProperties() {
        return ConfigurationConverter.getProperties(propertiesConfiguration);
    }

    public Properties getProperties(String code) {
        Properties properties = new Properties();
        Iterator<String> keys = propertiesConfiguration.getKeys(code);
        String key = null;
        while (keys.hasNext()) {
            key = keys.next();
            properties.put(key, propertiesConfiguration.getProperty(key));
        }
        return properties;

    }
}
