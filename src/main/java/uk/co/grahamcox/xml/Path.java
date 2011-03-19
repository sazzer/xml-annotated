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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Path to an element in an XML document
 * @author graham
 */
public class Path {
    /** the actual element path */
    private final List<String> path = new ArrayList<String>();
    
    /**
     * Create the path
     * @param path the path
     */
    public Path(final String[] path) {
        this.path.addAll(Arrays.asList(path));
    }
    
    /**
     * Compare this path to another to see if it matches
     * @param match the path to match against
     * @return True if they match. False if not
     */
    public boolean matches(final List<String> match) {
        boolean result = (match.size() == path.size());
        for (int i = 0; result && i < match.size(); ++i) {
            result = (match.get(i).equals(path.get(i)));
        }
        return result;
    }
}
