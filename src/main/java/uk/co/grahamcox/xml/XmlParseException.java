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

/**
 * Exception thrown if parsing of an XML document fails for some reason
 * @author graham
 */
public class XmlParseException extends Exception {
    /**
     * Constructs an instance of <code>XmlParseException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public XmlParseException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>XmlParseException</code> with the specified detail message.
     * @param msg the detail message.
     * @param cause the root cause
     */
    public XmlParseException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    
}
