/*
 * (c) Copyright 2010-2012 by Volker Bergmann. All rights reserved.
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

package org.databene;

import static org.junit.Assert.*;

import org.databene.benerator.consumer.ConsumerChain;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.GenerateOrIterateStatement;
import org.databene.benerator.engine.statement.StatementProxy;
import org.databene.benerator.engine.statement.TimedGeneratorStatement;
import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.benerator.test.ConsumerMock;
import org.databene.platform.contiperf.PerfTrackingConsumer;
import org.databene.stat.LatencyCounter;
import org.junit.Test;

/**
 * Integration test of the {@link PerfTrackingConsumer}.<br/><br/>
 * Created: 14.03.2010 12:17:07
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PerfTrackingConsumerIntegrationTest extends BeneratorIntegrationTest {

	@Test
	public void testNesting() throws Exception {
		TimedGeneratorStatement statement = (TimedGeneratorStatement) parse(
				"<generate type='bla' count='10'>" +
				"	<consumer class='org.databene.platform.contiperf.PerfTrackingConsumer'>" +
				"		<property name='target'>" +
				"			<bean id='c' spec='new org.databene.benerator.test.ConsumerMock(false, 0, 50, 100)' />" +
				"		</property>" +
				"	</consumer>" +
				"</generate>");
		statement.execute(context);
		ConsumerMock consumerMock = ConsumerMock.instances.get(0);
		assertEquals(10, consumerMock.startConsumingCount.get());
		checkStats(statement);
	}

	@Test
	public void testScript() throws Exception {
		TimedGeneratorStatement statement = (TimedGeneratorStatement) parse(
				"<generate type='bla' count='10'>" +
				"	<consumer spec='new org.databene.platform.contiperf.PerfTrackingConsumer(new org.databene.benerator.test.ConsumerMock(false, 0, 50, 100))'/>" +
				"</generate>");
		statement.execute(context);
		ConsumerMock consumerMock = ConsumerMock.instances.get(0);
		assertEquals(10, consumerMock.startConsumingCount.get());
		checkStats(statement);
	}

	private void checkStats(TimedGeneratorStatement statement) {
		Statement tmp = statement;
		while (tmp instanceof StatementProxy)
			tmp = ((StatementProxy) tmp).getRealStatement(context);
	    GenerateOrIterateStatement realStatement = (GenerateOrIterateStatement) tmp;
		ConsumerChain chain = (ConsumerChain) (realStatement.getTask()).getConsumer();
		PerfTrackingConsumer tracker = (PerfTrackingConsumer) chain.getComponent(0);
		LatencyCounter counter = tracker.getTracker().getCounter();
		assertEquals(10, counter.sampleCount());
		assertTrue("Expected latency greater than 29 ms, but measured " + counter.minLatency() + " ms", counter.minLatency() >= 30);
		assertTrue(counter.averageLatency() > 29);
		assertTrue(counter.minLatency() < counter.maxLatency());
    }
	
}
