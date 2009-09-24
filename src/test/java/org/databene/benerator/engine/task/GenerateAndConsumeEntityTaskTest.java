/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.commons.CollectionUtil;
import org.databene.commons.Expression;
import org.databene.commons.TypedIterable;
import org.databene.commons.expression.ConstantExpression;
import org.databene.model.consumer.AbstractConsumer;
import org.databene.model.consumer.Consumer;
import org.databene.model.data.Entity;

import junit.framework.TestCase;

/**
 * TODO document class GenerateAndConsumeEntityGeneratorTest.<br/>
 * <br/>
 * Created at 25.07.2009 12:42:25
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class GenerateAndConsumeEntityTaskTest extends TestCase {
	
    static final Entity ALICE = new Entity("Person", "name", "Alice");
    static final Entity BOB = new Entity("Person", "name", "Bob");
    
    // tests -----------------------------------------------------------------------------------------------------------

    public void testFlat() throws Exception {
		Generator<Entity> generator = new IteratingGenerator<Entity>(new AB());
		final ListConsumer consumer = new ListConsumer();
		Expression<Consumer<Entity>> consumerExpr = new ConstantExpression<Consumer<Entity>>(consumer);
		GenerateAndConsumeEntityTask task = new GenerateAndConsumeEntityTask(
				"tn", generator, consumerExpr, null, false, null);
		checkIteration(task, consumer);
		consumer.list.clear();
		task.reset();
		checkIteration(task, consumer);
		task.close();
		assertFalse(task.available());
		assertEquals("tn", task.getTaskName());
		assertEquals("fatal", task.getErrorHandler(null).getLevel().name());
	}
/*
    public void testRecursive() throws Exception {
		Generator<Entity> generator = new IteratingGenerator<Entity>(new AB());
		final ListConsumer consumer = new ListConsumer();
		Expression<Consumer<Entity>> consumerExpr = new ConstantExpression<Consumer<Entity>>(consumer);
		List<? extends Task> subTasks = ;
		GenerateAndConsumeEntityTask task = new GenerateAndConsumeEntityTask(
				"tn", generator, consumerExpr, subTasks , false, null);
		checkIteration(task, consumer);
		consumer.list.clear();
		task.reset();
		checkIteration(task, consumer);
		task.close();
		assertFalse(task.available());
		assertEquals("tn", task.getTaskName());
		assertEquals("fatal", task.getErrorHandler(null).getLevel().name());
	}
*/
    // test helpers ----------------------------------------------------------------------------------------------------

	private void checkIteration(GenerateAndConsumeEntityTask task, final ListConsumer consumer) {
		BeneratorContext context = new BeneratorContext();
		// check life cycle
	    assertTrue(task.available());
		task.run(context);
		assertTrue(task.available());
		task.run(context);
		assertFalse(task.available());
		// check output
		assertEquals(2, consumer.list.size());
		assertEquals(ALICE, consumer.list.get(0));
		assertEquals(BOB, consumer.list.get(1));
    }
	
	static class ListConsumer extends AbstractConsumer<Entity> {
		List<Entity> list = new ArrayList<Entity>();
		public void startConsuming(Entity object) {
	        list.add(object);
        }
	}
	
	static class AB implements TypedIterable<Entity> {
		private static final List<Entity> ab = CollectionUtil.toList(ALICE, BOB);
        public Class<Entity> getType() {
	        return Entity.class;
        }
        public Iterator<Entity> iterator() {
	        return ab.iterator();
        }
	}

}
