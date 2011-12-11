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

package org.databene.platform.dbunit;

import org.junit.Test;
import static junit.framework.Assert.*;

import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.model.data.Entity;
import org.databene.platform.AbstractEntityIteratorTest;
import org.databene.webdecs.DataUtil;

/**
 * Tests the DBUnitXmlEntityImporter with a standard and a flat dataset file.<br/>
 * <br/>
 * Created: 05.08.2007 08:05:10
 * @author Volker Bergmann
 */
public class DbUnitEntityIterableTest extends AbstractEntityIteratorTest { // TODO split off tests for the 2 *Iterator classes
	
	@Test
    public void testNormalDataset() {
        NestedDbUnitEntityIterator iterator = new NestedDbUnitEntityIterator(
        		"org/databene/platform/importer/dbunit/person+role-dbunit.xml", 
        		new DefaultBeneratorContext());
        check(iterator);
    }

	@Test
    public void testFlatDataset() {
        FlatDbUnitEntityIterator iterator = new FlatDbUnitEntityIterator(
        		"org/databene/platform/importer/dbunit/person+role-dbunit.flat.xml", 
        		new DefaultBeneratorContext());
        check(iterator);
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    private void check(AbstractDbUnitEntityIterator iterator) {
        assertEquals(createPerson("Alice", "23"), DataUtil.nextNotNullData(iterator));
        assertEquals(createPerson("Bob", "34"), DataUtil.nextNotNullData(iterator));
        assertEquals(createPerson("Charly", "45"), DataUtil.nextNotNullData(iterator));
        assertEquals(createRole("Admin"), DataUtil.nextNotNullData(iterator));
        assertEquals(createRole("User"), DataUtil.nextNotNullData(iterator));
        assertUnavailable(iterator);
        iterator.close();
    }

    private Entity createPerson(String name, String age) {
        Entity person = new Entity(createComplexType("PERSON"));
        person.setComponent("name", name);
        person.setComponent("age", age);
        return person;
    }

    private Entity createRole(String name) {
        Entity role = new Entity(createComplexType("ROLE"));
        role.setComponent("name", name);
        return role;
    }

}
