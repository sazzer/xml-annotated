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

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser to parse an XML stream
 * @author graham
 */
public class Parser {
    /** The logger to use */
    private static final Log LOG = LogFactory.getLog(Parser.class);
    /** The SAX parser factory to use */
    private SAXParserFactory parserFactory;

    /**
     * Create the parser
     */
    public Parser() {
        parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        parserFactory.setValidating(true);
    }
    
    
    /**
     * Parse the given input stream
     * @param input the input stream
     * @param handler the handler to use
     * @throws IOException if an error occurs reading the stream
     * @throws XmlParseException if an error occurs parsing the stream
     */
    public void parse(final InputStream input, final Handler handler) 
            throws IOException, XmlParseException {
        try {
            LOG.debug("Parsing input stream: " + input + " into handler: " + handler);
            SAXParser parser = parserFactory.newSAXParser();
            DefaultHandler defaultHandler = new XmlHandler(handler);
            parser.parse(input, defaultHandler);
        } catch (ParserConfigurationException ex) {
            LOG.error("Configuration error creating parser", ex);
            throw new XmlParseException("Configuration error creating parser", ex);
        } catch (SAXException ex) {
            LOG.error("Error creating parser", ex);
            throw new XmlParseException("Error creating parser", ex);
        }
        
    }
}
