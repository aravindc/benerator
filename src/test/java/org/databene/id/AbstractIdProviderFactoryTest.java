/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

package org.databene.id;

import junit.framework.TestCase;

/**
 * Tests the {@link AbstractIdProviderFactory}.<br/>
 * <br/>
 * Created at 05.11.2008 09:02:39
 * @since 0.5.6
 * @author Volker Bergmann
 */
public class AbstractIdProviderFactoryTest extends TestCase {

	public void test() {
		MyIdProviderFactory factory = new MyIdProviderFactory();
		IdStrategy<? extends Object> strategy = factory.getIdStrategies()[0];
		IdProvider<? extends Object> local1 = factory.idProvider(strategy, "1", null);
		IdProvider<? extends Object> local2 = factory.idProvider(strategy, "1", "local");
		assertTrue(local1 != local2);
		assertEquals(local1.next(), local2.next());
		IdProvider<? extends Object> global = factory.idProvider(strategy, "1", "global");
		assertTrue(global != local2);
		assertEquals(1, global.next());
	}
	
	public static class MyIdProviderFactory extends AbstractIdProviderFactory {

		@Override
		public <T> IdProvider<T> createIdProvider(IdStrategy<T> strategy,
				String param) {
			return (IdProvider<T>) new MyIdProvider(Integer.parseInt(param));
		}

		public IdStrategy<? extends Object>[] getIdStrategies() {
			return new IdStrategy[] { new IdStrategy("my", Integer.class) };
		}
	}
	
	public static class MyIdProvider extends AbstractIdProvider<Integer> {
		
		private int value;

		public MyIdProvider(int value) {
			super(Integer.class);
			this.value = value;
		}

		public boolean hasNext() {
			return true;
		}

		public Integer next() {
			return value;
		}

		public void close() {
		}
		
	}
}
