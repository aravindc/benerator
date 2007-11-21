/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

import junit.framework.TestCase;

import java.io.IOException;

import org.databene.model.data.Entity;
import org.databene.model.data.EntityDescriptor;

/**
 * Tests the DBUnitXmlDataSetImporter with a standard and a flat dataset file.<br/>
 * <br/>
 * Created: 05.08.2007 08:05:10
 */
public class DBUnitEntityIterableTest extends TestCase {

    public void testNormalDataset() throws IOException{
        check("org/databene/platform/importer/dbunit/person+role-dbunit.xml");
    }


    public void testFlatDataset() throws IOException{
        check("org/databene/platform/importer/dbunit/person+role-dbunit.flat.xml");
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    private void check(String uri) throws IOException {
        DBUnitEntityIterator iterator = new DBUnitEntityIterator(uri);
        assertTrue(iterator.hasNext());
        assertEquals(createPerson("Alice", "23"), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(createPerson("Bob", "34"), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(createPerson("Charly", "45"), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(createRole("Admin"), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(createRole("User"), iterator.next());
        assertFalse(iterator.hasNext());
        iterator.close();
    }

    private Entity createPerson(String name, String age) {
        Entity person = new Entity(new EntityDescriptor("PERSON", false));
        person.setComponent("name", name);
        person.setComponent("age", age);
        return person;
    }

    private Entity createRole(String name) {
        Entity role = new Entity(new EntityDescriptor("ROLE", false));
        role.setComponent("name", name);
        return role;
    }

}
