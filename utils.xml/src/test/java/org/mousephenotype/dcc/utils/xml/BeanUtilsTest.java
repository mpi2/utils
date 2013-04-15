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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.Assert;

import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class BeanUtilsTest {

    private class MyClass {

        private String myFirstAttribute;
        private String mySecondAttribute;
        private List<String> myComplexAttribute;
        private volatile int hashCode = 0;

        /**
         * @return the myFirstAttribute
         */
        public String getMyFirstAttribute() {
            return myFirstAttribute;
        }

        /**
         * @param myFirstAttribute the myFirstAttribute to set
         */
        public void setMyFirstAttribute(String myFirstAttribute) {
            this.myFirstAttribute = myFirstAttribute;
        }

        /**
         * @return the myComplexAttribute
         */
        public List<String> getMyComplexAttribute() {
            if (myComplexAttribute == null) {
                myComplexAttribute = new ArrayList<String>();
            }
            return this.myComplexAttribute;
        }

        /**
         * @return the mySecondAttribute
         */
        public String getMySecondAttribute() {
            return mySecondAttribute;
        }

        /**
         * @param mySecondAttribute the mySecondAttribute to set
         */
        public void setMySecondAttribute(String mySecondAttribute) {
            this.mySecondAttribute = mySecondAttribute;
        }

        @Override
        public boolean equals(Object _other) {
            if (this == _other) {
                return true;
            }
            if (_other != null && this.getClass() == _other.getClass()) {
                MyClass other = (MyClass) _other;
                if (other.getMyFirstAttribute().equals(this.getMyFirstAttribute())
                        && other.getMySecondAttribute().equals(this.getMySecondAttribute())) {
                    if ((this.getMyComplexAttribute() == null && other.getMyComplexAttribute() == null)
                            || (this.getMyComplexAttribute() != null && this.getMyComplexAttribute().isEmpty()
                            && other.getMyComplexAttribute() != null && other.getMyComplexAttribute().isEmpty())) {
                        return true;
                    }
                    if (this.getMyComplexAttribute().size() != other.getMyComplexAttribute().size()) {
                        return false;
                    }
                    if (this.getMyComplexAttribute().containsAll(other.getMyComplexAttribute())
                            && other.getMyComplexAttribute().containsAll(this.getMyComplexAttribute())) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            //http://java.sun.com/developer/Books/effectivejava/Chapter3.pdf
            if (hashCode == 0) {
                int result = 17;
                result = 37 * result + (int) this.getMyFirstAttribute().hashCode();
                result = 37 * result + this.getMySecondAttribute().hashCode();
                if (this.getMyComplexAttribute() != null && this.getMyComplexAttribute().size() > 0) {
                    result = 37 * result + this.getMyComplexAttribute().hashCode();
                }
                hashCode = result;
            }
            return hashCode;
        }
    }
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BeanUtilsTest.class);

    //@Test
    public void cloneTest() {


        MyClass src = new MyClass();
        src.setMyFirstAttribute("myfirstAttribute");
        src.setMySecondAttribute("mysecondAttribute");
        src.getMyComplexAttribute().add("a string");

        MyClass dest = new MyClass();
        try {

            BeanUtils.clone(src, dest);

        } catch (NoSuchMethodException ex) {
            logger.error("not method found",ex);
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

        Assert.assertEquals(src, dest);

    }
}
