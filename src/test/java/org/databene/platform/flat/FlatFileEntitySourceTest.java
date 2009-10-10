/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.flat;

import org.junit.Test;
import static junit.framework.Assert.*;
import org.databene.document.flat.FlatFileColumnDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.commons.SystemInfo;
import org.databene.commons.format.Alignment;

import java.util.Iterator;

/**
 * Tests the {@link FlatFileEntitySource}.<br/>
 * <br/>
 * Created: 27.08.2007 19:20:25
 * @author Volker Bergmann
 */
public class FlatFileEntitySourceTest {

    private static final String URI = "org/databene/platform/flat/person-bean.flat";

    @Test
    public void test() {
        ComplexTypeDescriptor descriptor = new ComplexTypeDescriptor("person");
        FlatFileColumnDescriptor[] descriptors = new FlatFileColumnDescriptor[] {
                new FlatFileColumnDescriptor("name", 6, Alignment.LEFT, ' '),
                new FlatFileColumnDescriptor("age", 3, Alignment.RIGHT, '0')
        };
        FlatFileEntitySource source = new FlatFileEntitySource(URI, descriptor, SystemInfo.getFileEncoding(), descriptors);
        Iterator<Entity> generator = source.iterator();
        assertTrue(generator.hasNext());
        assertEquals(new Entity(descriptor, "name", "Alice", "age", "23"), generator.next());
        assertTrue(generator.hasNext());
        assertEquals(new Entity(descriptor, "name", "Bob", "age", "34"), generator.next());
        assertTrue(generator.hasNext());
        assertEquals(new Entity(descriptor, "name", "Charly", "age", "45"), generator.next());
        assertFalse(generator.hasNext());
        generator = source.iterator();
        assertTrue(generator.hasNext());
        assertEquals(new Entity(descriptor, "name", "Alice", "age", "23"), generator.next());
        assertTrue(generator.hasNext());
        assertEquals(new Entity(descriptor, "name", "Bob", "age", "34"), generator.next());
        assertTrue(generator.hasNext());
        assertEquals(new Entity(descriptor, "name", "Charly", "age", "45"), generator.next());
        assertFalse(generator.hasNext());
    }
    
}
