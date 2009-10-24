/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

import org.databene.benerator.Generator;
import org.databene.commons.IOUtil;
import org.databene.model.data.Entity;
import org.junit.Test;

/**
 * Tests the BeneratorMainTask.<br/><br/>
 * Created: 24.10.2009 11:22:25
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class BeneratorMainTaskTest {

	@Test
	public void testGetGenerator() throws Exception {
		BeneratorMainTask task = null;
		try {
			DescriptorRunner runner = new DescriptorRunner("string://<setup>" +
					"<create-entities name='Person' count='1'>" +
					"<attribute name='name' values='Alice'/>" +
					"</create-entities>" +
					"</setup>");
			task = runner.parseDescriptorFile();
			Generator<Entity> generator = task.getGenerator("Person", runner.getContext());
			assertEquals(Entity.class, generator.getGeneratedType());
			assertNotNull(generator);
			for (int i = 0; i < 10; i++)
				checkGeneration(generator);
			assertTrue(generator.available());
		} finally {
			IOUtil.close(task);
		}
	}

	private void checkGeneration(Generator<Entity> generator) {
	    Entity entity = generator.generate();
		assertEquals("Person", entity.name());
		assertEquals("Alice", entity.get("name"));
    }
	
}
