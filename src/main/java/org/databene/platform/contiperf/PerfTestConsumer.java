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

package org.databene.platform.contiperf;

import java.io.IOException;

import org.databene.commons.ArrayBuilder;
import org.databene.commons.ParseException;
import org.databene.contiperf.ExecutionLogger;
import org.databene.contiperf.Invoker;
import org.databene.contiperf.PercentileRequirement;
import org.databene.contiperf.PerfTestController;
import org.databene.contiperf.PerformanceRequirement;
import org.databene.contiperf.log.FileExecutionLogger;
import org.databene.model.consumer.Consumer;
import org.databene.model.consumer.ConsumerProxy;

import freemarker.template.utility.StringUtil;

/**
 * {@link Consumer} implementation that calls a ContiPerf {@link PerfTestConsumer}.<br/><br/>
 * Created: 22.10.2009 16:17:14
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PerfTestConsumer extends ConsumerProxy<Object> {
	
	private PerformanceRequirement requirement;
	private PerfTestController controller;

	public PerfTestConsumer() {
	    this(null);
    }
	
	public PerfTestConsumer(Consumer<Object> target) {
	    super(target);
	    this.requirement = new PerformanceRequirement();
    }
	
	public void setTarget(Consumer<Object> target) {
		this.target = target;
	}
	
	public void setMax(int max) {
		requirement.setMax(max);
	}

	public void setPercentiles(String percentilesSpec) {
		requirement.setPercentiles(parsePercentilesSpec(percentilesSpec));
	}
	
	@Override
    public void startConsuming(Object object) {
	    try {
	        getController().invoke(new Object[] { object });
        } catch (Exception e) {
	        throw new RuntimeException(e);
        }
    }
	
	@Override
	public void close() throws IOException {
	    super.close();
	    controller.stop();
	}
	
	protected PerfTestController getController() {
		if (controller == null) {
			Invoker invoker = new ConsumerInvoker("enrollCustomer", target); // TODO
			ExecutionLogger logger = new FileExecutionLogger();
			controller = new PerfTestController(invoker, requirement, logger);
		}
		return controller;
	}

	private PercentileRequirement[] parsePercentilesSpec(String percentilesSpec) {
		String[] assignments = StringUtil.split(percentilesSpec, ',');
		ArrayBuilder<PercentileRequirement> builder = new ArrayBuilder<PercentileRequirement>(
				PercentileRequirement.class, assignments.length);
		for (String assignment : assignments)
			builder.add(parsePercentileSpec(assignment));
	    return builder.toArray();
    }

	private PercentileRequirement parsePercentileSpec(String assignment) {
	    String[] parts = StringUtil.split(assignment, ':');
	    if (parts.length != 2)
	    	throw new ParseException("Ilegal percentile syntax: " + assignment);
	    int base  = Integer.parseInt(parts[0]);
	    int limit = Integer.parseInt(parts[1]);
		return new PercentileRequirement(base, limit);
    }

}
