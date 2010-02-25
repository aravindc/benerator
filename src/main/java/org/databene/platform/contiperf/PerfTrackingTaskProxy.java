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

package org.databene.platform.contiperf;

import org.databene.commons.BeanUtil;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.stat.LatencyCounter;
import org.databene.task.Task;
import org.databene.task.TaskProxy;
import org.databene.task.TaskResult;

/**
 * Proxies a {@link Task} and tracks its execution times.<br/><br/>
 * Created: 25.02.2010 09:08:48
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PerfTrackingTaskProxy<E extends Task> extends TaskProxy<E> {
	
	private LatencyCounter counter;

	public PerfTrackingTaskProxy(E realTask) {
	    this(realTask, new LatencyCounter());
    }

	public PerfTrackingTaskProxy(E realTask, LatencyCounter counter) {
	    super(realTask);
	    this.counter = counter;
    }

	@Override
	public TaskResult execute(Context context, ErrorHandler errorHandler) {
		long startTime = System.currentTimeMillis();
	    TaskResult result = super.execute(context, errorHandler);
	    counter.addSample((int) (System.currentTimeMillis() - startTime));
		return result;
	}
	
	@Override
    public Object clone() {
	    return new PerfTrackingTaskProxy<E>(BeanUtil.clone(realTask), counter);
    }

	public LatencyCounter getCounter() {
	    return counter;
    }

}
