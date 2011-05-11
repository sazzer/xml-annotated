/*
 * Copyright (C) 2011 graham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.grahamcox.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * DefaultHandler implementation that delegates correctly to the annotated Handler implementation
 * @author graham
 */
public class XmlHandler extends DefaultHandler {
    /** The logger to use */
    private static final Log LOG = LogFactory.getLog(XmlHandler.class);
    /** The handler to ultimately delegate to */
    private final Handler handler;
    /** The stack of elements so far */
    private final Stack<String> elementStack = new Stack<String>();
    /** Map of the methods with StartElement annotations to the path the annotation describes */
    private final Map<Method, Path> startElements = new HashMap<Method, Path>();
    /** Map of the methods with EndElement annotations to the path the annotation describes */
    private final Map<Method, Path> endElements = new HashMap<Method, Path>();
    /** Map of the methods with Characters annotations to the path the annotation describes */
    private final Map<Method, Path> characters = new HashMap<Method, Path>();
    /**
     * Create the handler
     * @param handler The handler to delegate to
     */
    public XmlHandler(Handler handler) {
        this.handler = handler;
        if (handler != null) {
            for (Method method : handler.getClass().getMethods()) {
                StartElement startElement = method.getAnnotation(StartElement.class);
                if (startElement != null) {
                    LOG.debug("Found handler for start element: " + method);
                    startElements.put(method, new Path(startElement.value()));
                }
                EndElement endElement = method.getAnnotation(EndElement.class);
                if (endElement != null) {
                    LOG.debug("Found handler for end element: " + method);
                    endElements.put(method, new Path(endElement.value()));
                }
                Characters characters = method.getAnnotation(Characters.class);
                if (characters != null) {
                    LOG.debug("Found handler for characters: " + method);
                    this.characters.put(method, new Path(characters.value()));
                }
            }
        }
    }

    /**
     * Build a name string that is the URI in curly brackets iff there is a URI
     * with the local name appended to it
     * @param uri the URI
     * @param localName the local name
     * @return the built name
     */
    private String buildName(final String uri, final String localName) {
        String name = localName;
        if (uri != null && !uri.isEmpty()) {
            name = "{" + uri + "}" + localName;
        }
        return name;
    }
    
    /**
     * Handle the start of an element
     * @param uri The namespace URI
     * @param localName The local name - without prefix - of the element
     * @param qNAme The qualified name - with prefix - of the element
     * @param attributes The attributes of the element
     * @throws SAXException if an error occurs
     */
    @Override
    public void startElement(String uri,
                         String localName,
                         String qName,
                         Attributes attributes)
            throws SAXException {
        String name = buildName(uri, localName);
        LOG.debug("Processing " + name);
        elementStack.push(name);
        for (Entry<Method, Path> entry : startElements.entrySet()) {
            if (entry.getValue().matches(elementStack)) {
                Method method = entry.getKey();
                LOG.debug("Found matching callback method: " + method);
                Class<?>[] parameterTypes = method.getParameterTypes();
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                
                Object[] params = new Object[parameterTypes.length];
                for (int i = 0; i < params.length; ++i) {
                    params[i] = null;
                    for (Annotation annotation : parameterAnnotations[i]) {
                        if (annotation instanceof Attribute) {
                            String attribute = ((Attribute)annotation).value();
                            String value = attributes.getValue(attribute);
                            params[i] = value;
                            break;
                        }
                    }
                }
                invokeCallback(method, params);
            }
        }
    }

    /**
     * Invoke the given callback on the handler
     * @param method the callback to invoke
     * @param params the parameters of the callback
     * @throws SAXException if an error occurs
     */
    private void invokeCallback(Method method, Object[] params) throws SAXException {
        try {
            method.invoke(handler, params);
        } catch (IllegalAccessException ex) {
            LOG.error("Illegal access calling method " + method, ex);
            throw new SAXException("Illegal access calling method " + method, ex);
        } catch (IllegalArgumentException ex) {
            LOG.error("Illegal argument calling method " + method, ex);
            throw new SAXException("Illegal argument calling method " + method, ex);
        } catch (InvocationTargetException ex) {
            LOG.error("Exception occurred calling method " + method, ex);
                SAXException toThrow = new SAXException("An error occurred in method " + method);
            toThrow.initCause(ex.getTargetException());
            throw toThrow;
        }
    }

    /**
     * Handle the end of an element
     * @param uri The namespace URI
     * @param localName The local name - without prefix - of the element
     * @param qNAme The qualified name - with prefix - of the element
     * @throws SAXException if an error occurs
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String name = buildName(uri, localName);
        LOG.debug("Processing " + name);
        String expected = elementStack.peek();
        if (expected.equals(name)) {
            for (Entry<Method, Path> entry : endElements.entrySet()) {
                if (entry.getValue().matches(elementStack)) {
                    Method method = entry.getKey();
                    LOG.debug("Found matching callback method: " + method);
                    Class<?>[] parameterTypes = method.getParameterTypes();

                    Object[] params = new Object[parameterTypes.length];
                    for (int i = 0; i < params.length; ++i) {
                        params[i] = null;
                    }
                    invokeCallback(method, params);
                }
            }
            elementStack.pop();
        }
        else {
            throw new SAXException("Unexpected end of element " + name + ". Expected " + expected);
        }
    }

    /**
     * Handle character data
     * @param chars the character data
     * @param start start index in the buffer
     * @param length length of data in the buffer
     * @throws SAXException if an error occurs
     */
    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        String name = elementStack.peek();
        LOG.debug("Processing " + name);
        String value = new String(chars, start, length);
        for (Entry<Method, Path> entry : characters.entrySet()) {
            if (entry.getValue().matches(elementStack)) {
                Method method = entry.getKey();
                LOG.debug("Found matching callback method: " + method);
                Class<?>[] parameterTypes = method.getParameterTypes();

                Object[] params = new Object[parameterTypes.length];
                for (int i = 0; i < params.length; ++i) {
                    params[i] = null;
                    if (parameterTypes[i].isAssignableFrom(String.class)) {
                        params[i] = value;
                    }
                }
                invokeCallback(method, params);
            }
        }
        
    }
    
}
