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
package org.mousephenotype.dcc.utils.io.console;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.format.DateTimeFormat;

/**
 *
 * @author julian
 */
public final class Argument<T> {

    private final String charNamePrefix = "-";
    private final String stringNamePrefix = "--";
    private static final List<String> numberHeirs = new ArrayList<String>();
    public static final String dateTimeXMLpattern = "yyyy-MM-dd'T'HH:mm:ssZ";

    static {
        numberHeirs.add("java.lang.Boolean");
        numberHeirs.add("java.lang.Character");
        numberHeirs.add("java.lang.Byte");
        numberHeirs.add("java.lang.Short");
        numberHeirs.add("java.lang.Integer");
        numberHeirs.add("java.lang.Long");
        numberHeirs.add("java.lang.Float");
        numberHeirs.add("java.lang.Double");

    }
    private final Class<T> clazz;
    private final String characterName;
    private final String wordName;
    private final String identifier;
    private final boolean isOptional;
    private final String help;
    private String rawValue;
    private T value;

    public Argument(Class<T> clazz, String characterName, String wordName, String identifier, boolean isOptional, String help) {
        this.clazz = clazz;
        this.characterName = characterName;
        this.wordName = wordName;
        this.identifier = identifier;
        this.isOptional = isOptional;
        this.help = help;
    }

    private void cast() throws InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        if (this.getClazz().getName().equals("java.lang.String")) {
            value =  (T) this.rawValue;
            return;
        }

        if (!this.clazz.isPrimitive() && Argument.numberHeirs.contains(this.getClazz().getName())) {
            Method valueOf = getClazz().getDeclaredMethod("valueOf", String.class);
            value = (T) valueOf.invoke(null, this.rawValue);
        }
        
        if(!this.clazz.isPrimitive() && this.getClazz().getName().equals("java.util.Calendar")){
        
            value = (T)org.joda.time.DateTime.parse(this.rawValue, DateTimeFormat.forPattern(dateTimeXMLpattern).withZoneUTC()).toGregorianCalendar();
        }
        //@TODO: missing how to deal with primitive types as int, float, ...

    }

    public void setValue(String rawValue) throws InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        this.rawValue = rawValue;
        this.cast();
    }

    /*
     * @TODO: refactor to support single parameter programs @TODO: refactor to
     * support non-registered parameters
     */
    public void setValue(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].startsWith(charNamePrefix) || args[i].startsWith(stringNamePrefix)) {
                if (args[i].replaceAll("-*", "").equals(this.getCharacterName()) || args[i].replaceAll("-*", "").equals(this.getWordName())) {
                    this.rawValue = args[i + 1];
                    break;
                }
            }
        }
        if (this.rawValue != null) {
            this.cast();
        }
    }

    /**
     * @return the characterName
     */
    public String getCharacterName() {
        return characterName;
    }

    /**
     * @return the wordName
     */
    public String getWordName() {
        return wordName;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return the rawValue
     */
    public String getRawValue() {
        return rawValue;
    }

    /**
     * @return the help
     */
    public String getHelp() {
        return help;
    }

    /**
     * @return the value
     */
    public T getValue() {
        return value;
    }

    /**
     * @return the clazz
     */
    public Class<T> getClazz() {
        return clazz;
    }

    /**
     * @return the isOptional
     */
    public boolean isIsOptional() {
        return isOptional;
    }
}
