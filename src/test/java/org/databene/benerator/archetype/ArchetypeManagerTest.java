/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

package org.databene.benerator.archetype;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests the {@link ArchetypeManager}.<br/><br/>
 * Created: 02.04.2010 10:04:23
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ArchetypeManagerTest {

	@SuppressWarnings("null")
    @Test
	public void testArchetypeLookup() throws Exception {
		Archetype[] archetypes = ArchetypeManager.getInstance().getArchetypes();
		// verify that archetypes have been found
		assertTrue(archetypes != null && archetypes.length > 2);
		// verify that the first archetype is 'hello'
		assertEquals("hello", archetypes[0].getId());
	}

    @Test
	public void testDefaultArchetype() throws Exception {
		Archetype defaultArchetype = ArchetypeManager.getInstance().getDefaultArchetype();
		assertNotNull(defaultArchetype);
		assertEquals("simple", defaultArchetype.getId());
	}

}
