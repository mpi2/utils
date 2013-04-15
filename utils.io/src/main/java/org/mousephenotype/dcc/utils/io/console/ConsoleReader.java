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
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public abstract class ConsoleReader {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConsoleReader.class);
    public List<Argument<?>> arguments;
    public boolean exceptionThrown = false;

    public ConsoleReader(List<Argument<?>> arguments) {
        this.arguments = arguments;
    }

    public abstract String example();

    public abstract int getMinimumOptionalParameters();

    public String usage() {
        StringBuilder sb = new StringBuilder("usage: [options]\n");

        int longestArgumentLength = 0;
        StringBuilder tmp = new StringBuilder();
        for (Argument<?> argument : this.arguments) {
            tmp.append(argument.getCharacterName());
            tmp.append(argument.getWordName());
            if (longestArgumentLength < tmp.length()) {
                longestArgumentLength = tmp.length();
            }
            tmp.delete(0, tmp.length());
        }

        int argumentLength;
        for (Argument<?> argument : this.arguments) {
            sb.append("-");
            sb.append(argument.getCharacterName());
            sb.append(", --");
            sb.append(argument.getWordName());
            sb.append(" ");
            argumentLength = argument.getCharacterName().length() + argument.getWordName().length();
            for (int i = 0; i < longestArgumentLength - argumentLength; i++) {
                sb.append(" ");
            }
            sb.append(argument.getHelp());
            sb.append("\n");
        }

        sb.append(this.example());

        return sb.toString();
    }

    public boolean parsingOK() {
        return !exceptionThrown;
    }

    public void setValues(String[] args) {
        for (Argument<?> argument : this.arguments) {
            try {
                argument.setValue(args);
            } catch (InstantiationException ex) {
                exceptionThrown = true;
                logger.error("", ex);
            } catch (IllegalAccessException ex) {
                exceptionThrown = true;
                logger.error("", ex);
            } catch (NoSuchMethodException ex) {
                exceptionThrown = true;
                logger.error("", ex);
            } catch (IllegalArgumentException ex) {
                exceptionThrown = true;
                logger.error("", ex);
            } catch (InvocationTargetException ex) {
                exceptionThrown = true;
                logger.error("", ex);
            } catch (Exception ex) {
                exceptionThrown = true;
                logger.error("", ex);
            }
        }

    }
}
