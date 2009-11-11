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

package org.databene.platform.java;

import static org.junit.Assert.*;

import org.databene.model.data.Entity;
import org.databene.platform.java.JavaInvoker;
import org.junit.Test;

/**
 * Tests the {@link JavaInvoker}.<br/><br/>
 * Created: 21.10.2009 18:28:17
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class JavaInvokerTest {

	@Test
	public void testInstanceMethodEntity() {
		POJO target = new POJO();
		JavaInvoker invoker = new JavaInvoker(target, "dynP2");
		invoker.startConsuming(new Entity("params", "name", "Alice", "age", 23));
		invoker.startConsuming(new Entity("params", "name", "Bob", "age", 34));
		assertEquals(2, target.dynCountP2);
	}

	@Test
	public void testStaticMethodEntity() {
		Class<POJO> target = POJO.class;
		JavaInvoker invoker = new JavaInvoker(target, "statP2");
		invoker.startConsuming(new Entity("params", "age", 23, "name", "Alice"));
		invoker.startConsuming(new Entity("params", "age", 34, "name", "Bob"));
		assertEquals(2, POJO.statCountP2);
	}
	
	@Test
	public void testInstanceMethodObject() {
		POJO target = new POJO();
		JavaInvoker invoker = new JavaInvoker(target, "dynP1");
		invoker.startConsuming(new Entity("params", "name", "Alice"));
		invoker.startConsuming(new Entity("params", "name", "Bob"));
		assertEquals(2, target.dynCountP1);
	}

	@Test
	public void testStaticMethodObject() {
		Class<POJO> target = POJO.class;
		JavaInvoker invoker = new JavaInvoker(target, "statP1");
		invoker.startConsuming(new Entity("params", "age", 23));
		invoker.startConsuming(new Entity("params", "age", 34));
		assertEquals(2, POJO.statCountP1);
	}
	
	public static class POJO {
		public int dynCountP1 = 0;
		public static int statCountP1 = 0;
		public int dynCountP2 = 0;
		public static int statCountP2 = 0;
		
		public void dynP1(String name) {
			dynCountP1++;
		}
		
		public static void statP1(int age) {
			statCountP1++;
		}
		
		public void dynP2(String name, int age) {
			dynCountP2++;
		}
		
		public static void statP2(int age, String name) {
			statCountP2++;
		}
	}

}
