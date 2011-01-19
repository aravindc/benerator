/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.databene.benerator.Generator;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.GeneratorTask;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.util.SimpleGenerator;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.expression.ConstantExpression;
import org.databene.model.data.Entity;
import org.junit.Test;

/**
 * Tests the {@link GenerateOrIterateStatement}.<br/><br/>
 * Created: 05.11.2009 08:18:17
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class GenerateOrIterateStatementTest {

	private static final int THREAD_COUNT = 30;
	private static final long INVOCATION_COUNT = 6000L;

	@Test
	public void testThreadCount() {
		EntityGeneratorMock entityGenerator = new EntityGeneratorMock();
		
		GeneratorTask task = new GenerateAndConsumeTask("myTask", entityGenerator, null, false, new BeneratorContext());
		
		Generator<Long> countGenerator = new ConstantGenerator<Long>(INVOCATION_COUNT);
		Expression<Long> pageSize = new ConstantExpression<Long>(300L);
		Expression<Integer> threads = new ConstantExpression<Integer>(THREAD_COUNT);
		Expression<Long> minCount = new ConstantExpression<Long>(INVOCATION_COUNT);
		ConstantExpression<ErrorHandler> errorHandler = new ConstantExpression<ErrorHandler>(ErrorHandler.getDefault());
		GenerateOrIterateStatement statement = new GenerateOrIterateStatement(task, countGenerator, minCount, pageSize, null, threads, errorHandler, true, false);
		statement.execute(new BeneratorContext());
		
		assertEquals(INVOCATION_COUNT, entityGenerator.invocationCount);
		int found = entityGenerator.threads.size();
		assertTrue("Exprected at least " + THREAD_COUNT + " threads, but had only " + found, found >= THREAD_COUNT);
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------
	
	class EntityGeneratorMock extends SimpleGenerator<Entity> {
		
		public int invocationCount;
		public Set<Thread> threads = new HashSet<Thread>();

		public Class<Entity> getGeneratedType() {
	        return Entity.class;
        }
		
		public Entity generate() throws IllegalGeneratorStateException {
			int tmp = invocationCount;
			threads.add(Thread.currentThread());
            invocationCount = tmp + 1; 						// update is slightly delayed in order to provoke update errors 
	        return new Entity("Person", "name", "Alice");   // in case of concurrency issues
        }
	}
	
}
