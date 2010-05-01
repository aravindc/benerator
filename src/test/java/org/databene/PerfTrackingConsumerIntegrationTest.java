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

package org.databene;

import static org.junit.Assert.*;

import org.databene.benerator.engine.parser.xml.GenerateOrIterateParser;
import org.databene.benerator.engine.parser.xml.ParserTest;
import org.databene.benerator.engine.statement.GenerateAndConsumeTask;
import org.databene.benerator.engine.statement.GenerateOrIterateStatement;
import org.databene.benerator.engine.statement.LazyStatement;
import org.databene.benerator.engine.statement.TimedGeneratorStatement;
import org.databene.benerator.test.ConsumerMock;
import org.databene.model.consumer.ConsumerChain;
import org.databene.platform.contiperf.PerfTrackingConsumer;
import org.databene.stat.LatencyCounter;
import org.junit.Test;

/**
 * Integration test of the {@link PerfTrackingConsumer}.<br/><br/>
 * Created: 14.03.2010 12:17:07
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PerfTrackingConsumerIntegrationTest extends ParserTest {

	@Override
	public void setUp() {
	    super.setUp();
	    this.parser = new GenerateOrIterateParser();
	}
	
	@Test
	public void testNesting() throws Exception {
		TimedGeneratorStatement statement = (TimedGeneratorStatement) parse(
				"<generate type='bla' count='10'>" +
				"	<consumer class='org.databene.platform.contiperf.PerfTrackingConsumer'>" +
				"		<property name='target'>" +
				"			<bean id='c' spec='new org.databene.benerator.test.ConsumerMock(false, 0, 20, 40)' />" +
				"		</property>" +
				"	</consumer>" +
				"</generate>");
		statement.execute(context);
		ConsumerMock<?> consumerMock = ConsumerMock.instances.get(0);
		assertEquals(10, consumerMock.startConsumingCount.get());
		checkStats(statement);
	}

	@Test
	public void testScript() throws Exception {
		TimedGeneratorStatement statement = (TimedGeneratorStatement) parse(
				"<generate type='bla' count='10'>" +
				"	<consumer spec='new org.databene.platform.contiperf.PerfTrackingConsumer(new org.databene.benerator.test.ConsumerMock(false, 0, 20, 40))'/>" +
				"</generate>");
		statement.execute(context);
		ConsumerMock<?> consumerMock = ConsumerMock.instances.get(0);
		assertEquals(10, consumerMock.startConsumingCount.get());
		checkStats(statement);
	}

	private void checkStats(TimedGeneratorStatement statement) {
	    GenerateOrIterateStatement realStatement = (GenerateOrIterateStatement) ((LazyStatement) statement.getRealStatement()).getTarget(null);
		ConsumerChain<?> chain = (ConsumerChain<?>) ((GenerateAndConsumeTask) realStatement.getTarget()).getConsumer(null);
		PerfTrackingConsumer tracker = (PerfTrackingConsumer) chain.getComponent(0);
		LatencyCounter counter = tracker.getTracker().getCounter();
		assertEquals(10, counter.sampleCount());
		assertTrue(counter.minLatency() >= 20);
		assertTrue(counter.averageLatency() > 20);
		assertTrue(counter.minLatency() < counter.maxLatency());
    }
	
}
