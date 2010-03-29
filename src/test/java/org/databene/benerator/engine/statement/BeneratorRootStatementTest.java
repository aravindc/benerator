/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.statement;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorRootStatement;
import org.databene.benerator.engine.DescriptorRunner;
import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.SystemInfo;
import org.databene.model.data.Entity;
import org.junit.Test;

/**
 * Tests the BeneratorMainTask.<br/><br/>
 * Created: 24.10.2009 11:22:25
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class BeneratorRootStatementTest extends GeneratorTest {

	@Test
	public void testGetGenerator() throws Exception {
		BeneratorRootStatement task = null;
		String lf = SystemInfo.getLineSeparator();
		DescriptorRunner runner = new DescriptorRunner("string://<setup>" + lf +
				"	<generate type='Person' count='1'>" + lf +
				"		<attribute name='name' constant='Alice'/>" + lf +
				"	</generate>" + lf +
				"</setup>");
		task = runner.parseDescriptorFile();
		Generator<Entity> generator = task.getGenerator("Person", runner.getContext());
		assertEquals(Entity.class, generator.getGeneratedType());
		assertNotNull(generator);
		generator.init(context);
		for (int i = 0; i < 10; i++)
			checkGeneration(generator);
		assertAvailable(generator);
		generator.close();
	}

	private void checkGeneration(Generator<Entity> generator) {
	    Entity entity = generator.generate();
		assertEquals("Person", entity.type());
		assertEquals("Alice", entity.get("name"));
    }
	
}
