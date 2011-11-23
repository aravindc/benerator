/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.databene.platform.csv;

import org.junit.Test;
import static junit.framework.Assert.*;

import org.databene.commons.Encodings;
import org.databene.model.data.Entity;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.PartDescriptor;
import org.databene.platform.AbstractEntityIteratorTest;
import org.databene.webdecs.DataIterator;

/**
 * Tests the {@link CSVEntityIterator}.<br/>
 * <br/>
 * Created: 07.04.2008 12:30:17
 * @since 0.5.1
 * @author Volker Bergmann
 */
public class CSVEntityIteratorTest extends AbstractEntityIteratorTest {

    private static final String PLAIN_URI = "org/databene/platform/csv/person-bean.csv";
    private static final String GRAPH_URI = "org/databene/platform/csv/person-graph.csv";

    // test methods ----------------------------------------------------------------------------------------------------

    @Test
    public void testWithHeader() throws Exception {
    	CSVEntityIterator iterator = new CSVEntityIterator(PLAIN_URI, "Person", ',');
        checkIteration(iterator, "name", "age", false);
    }

    @Test
    public void testWithoutHeader() throws Exception {
    	CSVEntityIterator iterator = new CSVEntityIterator(PLAIN_URI, "Person", ',');
    	iterator.setColumns(new String[] { "c1", "c2" });
        checkIteration(iterator, "c1", "c2", true);
    }

    @Test
    public void testGraph() throws Exception {
    	// Define Country type
    	ComplexTypeDescriptor countryDescriptor = new ComplexTypeDescriptor("Country");
    	countryDescriptor.addComponent(new PartDescriptor("isoCode", "string"));
    	countryDescriptor.addComponent(new PartDescriptor("name", "string"));
    	// Define Person type
    	ComplexTypeDescriptor personDescriptor = new ComplexTypeDescriptor("Person");
    	personDescriptor.addComponent(new PartDescriptor("name", "string"));
    	personDescriptor.addComponent(new PartDescriptor("age", "int"));
    	personDescriptor.addComponent(new PartDescriptor("country", countryDescriptor));
    	// Define expected countries
    	Entity germany = new Entity(countryDescriptor, "isoCode", "DE", "name", "Germany");
    	Entity usa = new Entity(countryDescriptor, "isoCode", "US", "name", "USA");
    	// Define expected persons
    	Entity alice = new Entity(personDescriptor, "name", "Alice", "age", 23, "country", germany);
    	Entity bob   = new Entity(personDescriptor, "name", "Bob", "age", 34, "country", usa);
    	// iterate CSV file and check iterator output 
    	CSVEntityIterator iterator = new CSVEntityIterator(GRAPH_URI, personDescriptor, null, ',', Encodings.UTF_8);
        assertEquals(alice, nextOf(iterator));
        assertEquals(bob, nextOf(iterator));
        assertUnavailable(iterator);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void checkIteration(DataIterator<Entity> iterator, String header1, String header2, boolean expectHeaderRow) {
        ComplexTypeDescriptor descriptor = new ComplexTypeDescriptor("Person");
        if (expectHeaderRow)
	        assertEquals(new Entity(descriptor, header1, "name", header2, "age"), nextOf(iterator));
        assertEquals(new Entity(descriptor, header1, "Alice", header2, "23"), nextOf(iterator));
        assertEquals(new Entity(descriptor, header1, "Bob", header2, "34"), nextOf(iterator));
        assertEquals(new Entity(descriptor, header1, "Charly", header2, "45"), nextOf(iterator));
        assertUnavailable(iterator);
    }

}
