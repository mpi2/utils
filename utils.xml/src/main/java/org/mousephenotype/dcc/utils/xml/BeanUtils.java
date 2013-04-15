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
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 *
 * A class defines fields, methods and constructors. A modifier can be access
 * (public, protected and private), requiring override (abstract) restricting to
 * one instance (static), prohibiting value modification (final) forcing strict
 * floating behaviour (strictfp) and annotations.
 *
 * If there is a setter, then the field is either a basic type or a reference to
 * a single object, not to a container. This method relies on the basis that for
 * a field fieldName there is a setter setFieldName and a getter getFieldName.
 *
 */
public class BeanUtils {

    protected static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);
    //http://stackoverflow.com/questions/709961/determining-if-an-object-is-of-primitive-type
    private static final Set<Class> WRAPPER_TYPES = new HashSet(Arrays.asList(String.class,
            Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class, BigDecimal.class, BigInteger.class, Void.class, String.class, Class.class,
            URI.class, URL.class, UUID.class, Pattern.class, Enum.class));

    private static boolean isWrapperType(Class clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    public static Method[] getSimpleGetters(Object object) {
        Set<Method> methods = new HashSet<Method>(object.getClass().getMethods().length);
        for (Method method : object.getClass().getMethods()) {
            if (method.getName().startsWith("get")
                    && !method.getReturnType().equals(Collections.class)) {
                methods.add(method);
            }
        }
        return methods.toArray(new Method[methods.size()]);
    }

    public static Method[] getSimpleSetters(Object object) {
        Set<Method> methods = new HashSet<Method>(object.getClass().getMethods().length);
        for (Method method : object.getClass().getMethods()) {
            if (method.getName().startsWith("set") &&
                    method.getParameterTypes().length == 1 &&
                    ! method.getParameterTypes().equals(Collections.class)) {
                methods.add(method);
            }
        }
        return methods.toArray(new Method[methods.size()]);
    }

    public static void clone(Object src, Object dst) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, DatatypeConfigurationException {

        Method[] srcMths = new Method[src.getClass().getSuperclass().getDeclaredMethods().length + src.getClass().getDeclaredMethods().length];
        System.arraycopy(src.getClass().getSuperclass().getDeclaredMethods(), 0, srcMths, 0, src.getClass().getSuperclass().getDeclaredMethods().length);
        System.arraycopy(src.getClass().getDeclaredMethods(), 0, srcMths, src.getClass().getSuperclass().getDeclaredMethods().length, src.getClass().getDeclaredMethods().length);
        //get all the methods declared in the object and the methods declared in the superclasses of the object


        Set<String> fieldsWithSetters = new HashSet<String>(src.getClass().getDeclaredFields().length);
        //Set<String> fieldsWithIs = new HashSet<String>(src.getClass().getDeclaredFields().length);
        //Set<String> fieldsWithGetters = new HashSet<String>(src.getClass().getDeclaredFields().length);

        //get all the methods that start with set or get or is(for booleans)
        int numberOfMethods = srcMths.length;
        for (int i = 0; i < numberOfMethods; i++) {
            if (srcMths[i].getName().matches("\\w*Item[s]*")) {
                continue;
            }

            if (srcMths[i].getName().startsWith("set")) {
                fieldsWithSetters.add(srcMths[i].getName().substring(3, srcMths[i].getName().length()));
            }

            /*
             * if (srcMths[i].getName().startsWith("is")) {
             * fieldsWithIs.add(srcMths[i].getName().substring(2,
             * srcMths[i].getName().length())); }
             *
             * if (srcMths[i].getName().startsWith("get")) {
             * fieldsWithGetters.add(srcMths[i].getName().substring(3,
             * srcMths[i].getName().length())); }
             */
        }
        //you don't want to set the class
        //fieldsWithGetters.remove("Class");

        //for each setter, invoke the getter in the src
        Method getter = null;
        Object field = null;
        for (String fieldWithSetter : fieldsWithSetters) {
            BeanUtils.logger.info("invoking get{}", fieldWithSetter);
            try {
                getter = src.getClass().getMethod("get" + fieldWithSetter, new Class[]{});
            } catch (NoSuchMethodException e) {
                getter = src.getClass().getMethod("is" + fieldWithSetter, new Class[]{});
                BeanUtils.logger.info("get{} doesn't exist. invoking is{} ", fieldWithSetter, fieldWithSetter);
            }

            field = getter.invoke(src, new Object[]{});


            if (field != null) {
                /*
                 * if
                 * (Arrays.binarySearch(getter.getReturnType().getClass().getInterfaces(),
                 * Cloneable.class) != -1) { dst.getClass().getMethod("set" +
                 * fieldWithSetter, field.getClass()).invoke(dst,field); }
                 */
                BeanUtils.logger.info("getter.getReturnType() {}", getter.getReturnType().getSuperclass());
                if (getter.getReturnType().equals(java.util.List.class) || getter.getReturnType().equals(java.util.ArrayList.class)) {
                    BeanUtils.logger.info("in list");
                    List<Object> objectsToClone = (List<Object>) getter.invoke(src, new Object[]{});
                    if (objectsToClone != null && objectsToClone.size() > 0) {
                        BeanUtils.logger.info("invoking get{} within list", fieldWithSetter);
                        getter = dst.getClass().getMethod("get" + fieldWithSetter, new Class[]{});
                        List<Object> dstContainer = (List<Object>) getter.invoke(dst, new Object[]{});
                        if (isWrapperType(objectsToClone.get(0).getClass()) || objectsToClone.get(0).getClass().isPrimitive()) {
                            for (Object object : objectsToClone) {
                                dstContainer.add(object);
                            }
                        } else {
                            for (Object object : objectsToClone) {
                                Object newDst = object.getClass().newInstance();
                                BeanUtils.clone(object, newDst);
                                dstContainer.add(newDst);
                            }
                        }
                    }

                } else if (getter.getReturnType().equals(Enum.class)
                        || (getter.getReturnType().getSuperclass() != null && getter.getReturnType().getSuperclass().equals(java.lang.Enum.class))) {
                    BeanUtils.logger.info("in enum");
                    dst.getClass().getMethod("set" + fieldWithSetter, field.getClass()).invoke(dst, field);

                } else if (getter.getReturnType().equals(java.util.Calendar.class)) {
                    BeanUtils.logger.info("in Calendar");
                    dst.getClass().getMethod("set" + fieldWithSetter, Calendar.class).invoke(dst, ((Calendar) field).clone());
                } else if (getter.getReturnType().equals(javax.xml.datatype.XMLGregorianCalendar.class)) {
                    BeanUtils.logger.info("in XMLGregorianCalendar");
                    dst.getClass().getMethod("set" + fieldWithSetter, XMLGregorianCalendar.class).invoke(dst, ((XMLGregorianCalendar) field).clone());
                } else if (isWrapperType(getter.getReturnType())
                        || getter.getReturnType().isPrimitive()) {
                    BeanUtils.logger.info("in basic");
                    dst.getClass().getMethod("set" + fieldWithSetter, getter.getReturnType()).invoke(dst, field);

                } else {
                    BeanUtils.logger.info("in recursive");
                    Object newDst = field.getClass().newInstance();
                    BeanUtils.clone(field, newDst);
                    dst.getClass().getMethod("set" + fieldWithSetter, field.getClass()).invoke(dst, newDst);
                }
            }
            BeanUtils.logger.info("{} cloned", fieldWithSetter);
        }
    }
}
