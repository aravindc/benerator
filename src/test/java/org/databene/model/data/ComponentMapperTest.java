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

package org.databene.model.data;

import static org.junit.Assert.*;

import org.databene.benerator.test.ModelTest;
import org.junit.Test;

/**
 * Tests the {@link ComponentNameMapper}.<br/><br/>
 * Created: 22.02.2010 20:00:34
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ComponentMapperTest extends ModelTest {

	@Test
	public void test() {
		Entity in = createEntity("Person", "name", "Alice", "age", 23);
		ComponentNameMapper mapper = new ComponentNameMapper("'name'->'givenName','none'->'some'");
		Entity out = mapper.convert(in);
		assertEquals(in.type(), out.type());
		assertNull(out.get("name"));
		assertEquals("Alice", out.get("givenName"));
		assertEquals(23, out.get("age"));
	}
	
}
