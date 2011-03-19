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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the Parser
 * @author graham
 */
public class TestParser {
    /**
     * Test that we can parse the document without a handler
     * @throws Exception if an error occurs
     */
    @Test
    public void testDoNothing() throws Exception {
        InputStream stream = getClass().getResourceAsStream("/testDocument.xml");
        Parser parser = new Parser();
        parser.parse(stream, null);
    }
    
    @Test
    public void testCollect() throws Exception {
        TestHandler handler = new TestHandler();
        InputStream stream = getClass().getResourceAsStream("/testDocument.xml");
        Parser parser = new Parser();
        parser.parse(stream, handler);
        Assert.assertEquals(6, handler.events.size());
        Assert.assertEquals("Start root", handler.events.get(0));
        Assert.assertEquals("start element", handler.events.get(1));
        Assert.assertEquals("a = b", handler.events.get(2));
        Assert.assertEquals("c = 1", handler.events.get(3));
        Assert.assertEquals("Characters Hello", handler.events.get(4));
        Assert.assertEquals("End root", handler.events.get(5));
    }
    
    public static class TestHandler implements Handler {
        /** Collect the events that occurred */
        public List<String> events = new ArrayList<String>();
        
        @StartElement({"root"})
        public void startRoot() {
            events.add("Start root");
        }
        
        @EndElement({"root"})
        public void endRoot() {
            events.add("End root");
        }
        
        @StartElement({"root", "element"})
        public void startElement(@Attribute("a") final String a, @Attribute("c") final String c) {
            events.add("start element");
            events.add("a = " + a);
            events.add("c = " + c);
        }
        
        @Characters({"root", "element", "subElement"})
        public void subChars(final String chars) {
            events.add("Characters " + chars);
        }
    }
}
