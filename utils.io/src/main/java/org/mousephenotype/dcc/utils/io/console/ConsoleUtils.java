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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author julian
 *
 *
 *
 */
public class ConsoleUtils {

    private StringBuilder usage;
    private Map<String, Argument<?>> smallArguments;
    private Map<String, Argument<?>> bigArguments;
    private final String charNamePrefix = "-";
    private final String stringNamePrefix = "--";

    public ConsoleUtils() {
    }

    public void extractArguments(String args[])
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

        for (int i = 0; i < args.length;) {
            if (args[i].startsWith(charNamePrefix) || args[i].startsWith(stringNamePrefix)) {
                //see how can you get a reference for the same object based on three different keys
                this.getArgument(args[i].replaceAll("-*","")).setValue(args[++i]);
            }

        }
    }

    public void defineUsage(StringBuilder usage) {
        this.usage = usage;
    }

    public void addArgument(Argument<?> argument) {
        if (smallArguments == null) {
            this.smallArguments = new HashMap<String, Argument<?>>();
            this.bigArguments = new HashMap<String, Argument<?>>();
        }
        this.smallArguments.put(argument.getCharacterName(), argument);
        this.bigArguments.put(argument.getWordName(), argument);
    }

    public Argument<?> getArgument(String id) {
        if (this.smallArguments.containsKey(id)) {
            return this.smallArguments.get(id);
        }
        return this.bigArguments.get(id);
    }
}
