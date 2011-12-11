/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.fixedwidth;

import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.*;

import org.databene.document.fixedwidth.FixedWidthColumnDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.platform.AbstractEntityIteratorTest;
import org.databene.platform.fixedwidth.FixedWidthEntitySource;
import org.databene.webdecs.DataIterator;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.commons.SystemInfo;
import org.databene.commons.format.Alignment;

/**
 * Tests the {@link FixedWidthEntitySource}.<br/>
 * <br/>
 * Created: 27.08.2007 19:20:25
 * @author Volker Bergmann
 */
public class FixedWidthEntitySourceTest extends AbstractEntityIteratorTest {

    private static final String URI = "org/databene/platform/flat/person-bean.fcw";

    private static final FixedWidthColumnDescriptor[] descriptors = new FixedWidthColumnDescriptor[] {
            new FixedWidthColumnDescriptor("name", 6, Alignment.LEFT, ' '),
            new FixedWidthColumnDescriptor("age", 3, Alignment.RIGHT, '0')
    };
    private ComplexTypeDescriptor descriptor;

    private Entity ALICE;
	private Entity BOB;
	private Entity CHARLY;

	@Before
	public void setUpPersons() {
	    descriptor = createComplexType("person");
	    ALICE = new Entity(descriptor, "name", "Alice", "age", "23");
		BOB = new Entity(descriptor, "name", "Bob", "age", "34");
		CHARLY = new Entity(descriptor, "name", "Charly", "age", "45");
	}
    
    @Test
    public void testUnfiltered() {
        FixedWidthEntitySource source = new FixedWidthEntitySource(URI, descriptor, SystemInfo.getFileEncoding(), null, descriptors);
        source.setContext(new DefaultBeneratorContext());
        DataIterator<Entity> iterator = source.iterator();
        assertEquals(ALICE, nextOf(iterator));
        assertEquals(BOB, nextOf(iterator));
        assertEquals(CHARLY, nextOf(iterator));
        assertUnavailable(iterator);
        iterator = source.iterator();
        assertEquals(ALICE, nextOf(iterator));
        assertEquals(BOB, nextOf(iterator));
        assertEquals(CHARLY, nextOf(iterator));
        assertUnavailable(iterator);
    }
    
    @Test
    public void testFiltered() {
        FixedWidthEntitySource source = new FixedWidthEntitySource(URI, descriptor, SystemInfo.getFileEncoding(), "Bob.*", descriptors);
        source.setContext(new DefaultBeneratorContext());
        DataIterator<Entity> iterator = source.iterator();
        assertEquals(BOB, nextOf(iterator));
        assertUnavailable(iterator);
        iterator = source.iterator();
        assertEquals(BOB, nextOf(iterator));
        assertUnavailable(iterator);
    }
    
    @Test(expected = InvalidGeneratorSetupException.class)
    public void testMissingColumnSpec() {
        FixedWidthEntitySource source = new FixedWidthEntitySource(URI, descriptor, SystemInfo.getFileEncoding(), null);
        source.setContext(new DefaultBeneratorContext());
        DataIterator<Entity> iterator = source.iterator();
        assertEquals(BOB, nextOf(iterator));
    }
    
}
