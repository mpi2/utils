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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Map.Entry;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.test.AllTheTimes;
import org.mousephenotype.dcc.exportlibrary.datastructure.test.FullTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 * http://joda-time.sourceforge.net/userguide.html
 * http://docs.oracle.com/javase/tutorial/i18n/format/simpleDateFormat.html
 * http://blog.frankel.ch/customize-your-jaxb-bindings
 * http://stackoverflow.com/questions/5106987/jax-ws-and-joda-time
 * http://blog.jadira.co.uk/jadira-support/
 * http://usertype.sourceforge.net/userguide.html
 * http://joda-time.sourceforge.net/contrib/hibernate/
 * http://joda-time.sourceforge.net/timezones.html
 * http://www.odi.ch/prog/design/datetime.php
 *
 */
public class DateAndTimeTest {

    private static final Logger logger = LoggerFactory.getLogger(DateAndTimeTest.class);
    private static final ImmutableMap<String, String> TIME_ZONES = new ImmutableMap.Builder<String, String>().put("J", "US/Eastern").put("Ucd", "US/Pacific").put("Ncom", "Canada/Eastern").put("Bay", "US/Central").put("Wtsi", "UTC").put("Ics", "CET").put("Hmgu", "CET").put("Rbrc", "Japan").put("Ning", "PRC").put("H", "UTC").put("Gmc", "CET").build();
    //
    public static final AllTheTimes ALL_THE_TIMES = new AllTheTimes();
    
    private static final String time_examples_filename = "src/test/resources/data/time_examples.xml";

    @BeforeClass
    public static void setup() {
        int index = 0;
        for (Entry<String, String> entry : TIME_ZONES.entrySet()) {
            ALL_THE_TIMES.getFullTime().add(new FullTime());
            ALL_THE_TIMES.getFullTime().get(index++).setTimezone(entry.getValue());
        }
    }

    @AfterClass
    public static void teardown() {

        //First Serialize xml

        try {
            XMLUtils.marshall("org.mousephenotype.dcc.exportlibrary.datastructure.test", ALL_THE_TIMES, time_examples_filename);
        } catch (JAXBException ex) {
            DateAndTimeTest.logger.error("JAXBException {}", ex);
            if (ex.getLinkedException() != null) {
                DateAndTimeTest.logger.error("linked JAXBException {}", ex.getLinkedException());
            }
            Assert.fail();
        }

        //Second, clone and compare

        AllTheTimes cloned = new AllTheTimes();
        try {
            BeanUtils.clone(ALL_THE_TIMES, cloned);
        } catch (NoSuchMethodException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalAccessException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (InvocationTargetException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (InstantiationException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (DatatypeConfigurationException ex) {
            logger.error("",ex);
            Assert.fail();
        }

        Assert.assertTrue(ALL_THE_TIMES.equals(cloned));


        //Third incarnate and compare

        AllTheTimes fromXML = null;

        try {
            fromXML =
                    XMLUtils.unmarshal("org.mousephenotype.dcc.exportlibrary.datastructure.test", ALL_THE_TIMES.getClass(), time_examples_filename);
        } catch (JAXBException ex) {
            logger.error("",ex);
            Assert.fail();
        } catch (FileNotFoundException ex) {
            logger.error("",ex);
            Assert.fail();
        }catch(IOException ex){
            logger.error("",ex);
            Assert.fail();
        }

        Assert.assertNotNull(fromXML);

        Assert.assertEquals(ALL_THE_TIMES, fromXML);


    }

    @Test
    public void testDateTimeWithJoda() {
        String lexicalDate = "2012-06-30T02:45:00+0000";
        String xmlPattern = "yyyy-MM-dd'T'HH:mm:ssZ";
        org.joda.time.DateTime dateTime = org.joda.time.DateTime.parse(lexicalDate, DateTimeFormat.forPattern(xmlPattern).withZone(DateTimeZone.UTC));

        logger.info("lexicalDate: {}", lexicalDate);
        logger.info("joda       : {}", dateTime.toString(xmlPattern));

        Assert.assertEquals(lexicalDate, dateTime.toString(xmlPattern));

        Calendar jodaCalendar = dateTime.toGregorianCalendar();

        org.joda.time.DateTime dateTime2 = new DateTime(jodaCalendar, ISOChronology.getInstance()).withZone(DateTimeZone.UTC);

        Assert.assertTrue(dateTime.equals(dateTime2));
    }

    @Test
    public void testDateWithJoda() {
        String lexicalDate = "2012-06-30+0000";
        String xmlPattern = "yyyy-MM-ddZ";
        org.joda.time.DateTime date = org.joda.time.DateTime.parse(lexicalDate, DateTimeFormat.forPattern(xmlPattern).withZone(DateTimeZone.UTC));

        logger.info("lexicalDate: {}", lexicalDate);
        logger.info("joda       : {}", date.toString(xmlPattern));

        Assert.assertEquals(lexicalDate, date.toString(xmlPattern));
    }

    @Test
    public void testTimeWithJoda() {
        String lexicalDate = "T02:45:00+0000";
        String xmlPattern = "'T'HH:mm:ssZ";

        org.joda.time.DateTime dateTime = org.joda.time.DateTime.parse(lexicalDate, DateTimeFormat.forPattern(xmlPattern).withZone(DateTimeZone.UTC));

        logger.info("lexicalDate: {}", lexicalDate);
        logger.info("joda       : {}", dateTime.toString(xmlPattern));

        Assert.assertEquals(lexicalDate, dateTime.toString(xmlPattern));

        Calendar jodaCalendar = dateTime.toGregorianCalendar();

        org.joda.time.DateTime dateTime2 = new DateTime(jodaCalendar, ISOChronology.getInstance()).withZone(DateTimeZone.UTC);

        Assert.assertTrue(dateTime.equals(dateTime2));
    }

   
    @Test
    public void testDateWithJodaAndXML2() {
        String lexicalDate = "2012-06-30";
        String READ_xmlPattern = "yyyy-MM-dd";
        for (int i = 0; i < ALL_THE_TIMES.getFullTime().size(); i++) {
            LocalDate localDate = LocalDate.parse(lexicalDate, DateTimeFormat.forPattern(READ_xmlPattern));
            Calendar jodaCalendar = localDate.toDateTimeAtStartOfDay().toGregorianCalendar();
            Assert.assertNotNull(jodaCalendar);
            ALL_THE_TIMES.getFullTime().get(i).setDate(jodaCalendar);
        }
    }

    @Test
    public void testTimeWithJodaAndXML() {
        String lexicalDate = "02:45:00";
        String READ_xmlPattern = "HH:mm:ss";
        
        
        for (int i = 0; i < ALL_THE_TIMES.getFullTime().size(); i++) {
            LocalTime localTime = LocalTime.parse(lexicalDate, DateTimeFormat.forPattern(READ_xmlPattern));
            Calendar jodaCalendar = localTime.toDateTime(new DateTime(0)).toGregorianCalendar();
            Assert.assertNotNull(jodaCalendar);
            ALL_THE_TIMES.getFullTime().get(i).setTime(jodaCalendar);
        }
    }

    @Test
    public void testDateTimeWithJodaAndXML() {
        String lexicalDate = "2012-06-30T02:45:00";
        String READ_xmlPattern = "yyyy-MM-dd'T'HH:mm:ss";
        for (int i = 0; i < ALL_THE_TIMES.getFullTime().size(); i++) {
            org.joda.time.DateTime dateTime = org.joda.time.DateTime.parse(lexicalDate, DateTimeFormat.forPattern(READ_xmlPattern).withZone(DateTimeZone.forID(TIME_ZONES.values().asList().get(i))));
            org.joda.time.DateTime dateTimeUTC = dateTime.withZone(DateTimeZone.UTC);
            Calendar jodaCalendar = dateTimeUTC.toGregorianCalendar();
            Assert.assertNotNull(jodaCalendar);
            ALL_THE_TIMES.getFullTime().get(i).setDateTime(jodaCalendar);
        }
    }
    
    @Test
    public void testNowWithJoda(){
        org.joda.time.DateTime dateTime = org.joda.time.DateTime.now(DateTimeZone.UTC);
        logger.info("now in UTC {}", dateTime);
        dateTime = org.joda.time.DateTime.now(DateTimeZone.forID("Canada/Eastern"));
        logger.info("now in Canada/Eastern {}", dateTime);
    }
}
