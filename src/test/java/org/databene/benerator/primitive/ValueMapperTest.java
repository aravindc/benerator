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

package org.databene.benerator.primitive;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests the {@link ValueMapper}.<br/><br/>
 * Created: 24.10.2009 09:14:20
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ValueMapperTest {

	@Test
	public void testNormalMapping() throws Exception {
		ValueMapper mapper = new ValueMapper("1->2,4->3");
		assertEquals(Integer.class, mapper.getSourceType());
		assertEquals(2, mapper.convert(1));
		assertEquals(3, mapper.convert(4));
	}
	
	@Test
	public void testCanConvert_Strict() throws Exception {
		ValueMapper mapper = new ValueMapper("1->2,4->3");
		assertEquals(Integer.class, mapper.getSourceType());
		assertEquals(2, mapper.convert(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvert_Strict() throws Exception {
		ValueMapper mapper = new ValueMapper("1->2,4->3");
		mapper.convert(2);
	}

	@Test
	public void testLenientMapping() throws Exception {
		ValueMapper mapper = new ValueMapper("1->2,4->3", true);
		assertEquals(Integer.class, mapper.getSourceType());
		assertEquals(2, mapper.convert(1));
		assertEquals(2, mapper.convert(2));
	}

	@Test
	public void testLenientWOMapping() throws Exception {
		ValueMapper mapper = new ValueMapper(null, true);
		assertEquals(Object.class, mapper.getSourceType());
		assertEquals(1, mapper.convert(1));
		assertEquals(2, mapper.convert(2));
	}

}
