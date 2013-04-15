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
import org.junit.Assert;

import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class ConsoleUtilsTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConsoleUtilsTest.class);

    @Test
    public void testStringArgument() {
        String characterName = "t";
        String wordName = "template";
        String identifier = "template";
        boolean isOptional = false;
        String rawValue = "34";
        String help = "this is help";


        Argument<String> a = new Argument<String>(String.class, characterName, wordName, identifier, isOptional, help);

        Assert.assertNotNull(a);
        try {
            a.setValue("hey");
        } catch (InstantiationException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalAccessException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (NoSuchMethodException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (InvocationTargetException ex) {
            logger.error("",ex);
            Assert.fail();
        }


    }

    @Test
    public void usage() {

        String[] args = new String[]{"-1", "uno", "--segundo", "2"};

        Argument<String> first = new Argument<String>(String.class, "1", "primero", "one", false, "This is help for primero");
        Argument<Integer> second = new Argument<Integer>(Integer.class, "2", "segundo", "two", false, "This is help for segundo");


        try {
            first.setValue(args);
            second.setValue(args);
        } catch (InstantiationException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalAccessException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (NoSuchMethodException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (InvocationTargetException ex) {
            logger.error("",ex);
            Assert.fail();
        }

        Assert.assertEquals("uno", first.getValue());
        Assert.assertEquals(new Integer(2), second.getValue());


    }

    @Test
    public void testConsoleUtils() {
        String[] args = new String[]{"-1", "uno", "-2", "2"};
        ConsoleUtils consoleUtils = new ConsoleUtils();
        consoleUtils.addArgument(new Argument<String>(String.class, "1", "primero", "one", false, "This is help for primero"));
        consoleUtils.addArgument(new Argument<Integer>(Integer.class, "2", "segundo", "two", false, "This is help for segundo"));

        Argument a = consoleUtils.getArgument("1");
        Assert.assertNotNull(a);
        
        a = consoleUtils.getArgument("primero");
        Assert.assertNotNull(a);

        /*
         * try { consoleUtils.extractArguments(args); } catch
         * (InstantiationException ex) { logger.error("",ex);
         * Assert.fail(); } catch (IllegalAccessException ex) {
         * logger.error("",ex); Assert.fail(); } catch
         * (NoSuchMethodException ex) { logger.error("",ex);
         * Assert.fail(); } catch (IllegalArgumentException ex) {
         * logger.error("",ex); Assert.fail(); } catch
         * (InvocationTargetException ex) { logger.error("",ex);
         * Assert.fail(); }
         *
         */

    }

    @Test
    public void testArgument() {
        String characterName = "t";
        String wordName = "template";
        String identifier = "template";
        String rawValue = "34";
        boolean isOptional = false;
        String help = "this is help";

        Argument<Integer> a = new Argument<Integer>(Integer.class, characterName, wordName, identifier, isOptional, help);

        try {
            a.setValue(rawValue);
        } catch (InstantiationException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalAccessException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (NoSuchMethodException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (InvocationTargetException ex) {
            logger.error("",ex);
            Assert.fail();
        }

        Assert.assertTrue(34 == a.getValue().intValue());
        /**
         * Boolean Character Byte Short Integer Long Float Double Void
         */
        Argument<Boolean> b = new Argument<Boolean>(Boolean.class, characterName, wordName, identifier,isOptional, help);
        try {
            b.setValue("false");
        } catch (InstantiationException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalAccessException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (NoSuchMethodException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (InvocationTargetException ex) {
            logger.error("",ex);
            Assert.fail();
        }

        Assert.assertTrue(false == b.getValue());
    }
}
