/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.statement;

import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.GeneratorTask;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.contiperf.PerformanceTracker;
import org.databene.task.PageListener;
import org.databene.task.SynchronizedTask;
import org.databene.task.Task;
import org.databene.task.runner.PagedTaskRunner;

/**
 * Creates a number of entities in parallel execution and a given page size.<br/><br/>
 * Created: 01.02.2008 14:43:15
 * @author Volker Bergmann
 */
public class GenerateOrIterateStatement extends AbstractStatement 
		implements GeneratorStatement, PageListener {

	protected GeneratorTask task;
	protected Generator<Long> countGenerator;
	protected Expression<Long> pageSize;
	protected Expression<Integer> threads;
	protected Expression<PageListener> pageListener;
	protected PerformanceTracker tracker;
	protected boolean infoLog;
	protected boolean nested;
	protected boolean initialized;
	
	public GenerateOrIterateStatement(GeneratorTask task,
			Generator<Long> countGenerator, Expression<Long> pageSize, Expression<PageListener> pageListener, 
			Expression<Integer> threads, Expression<ErrorHandler> errorHandler, boolean infoLog, boolean nested) {
	    this.task = task;
	    this.countGenerator = countGenerator;
	    this.pageSize = pageSize;
	    this.threads = threads;
	    this.pageListener = pageListener;
	    this.infoLog = infoLog;
	    this.nested = nested;
	    this.initialized = false;
    }

	public void setTask(GeneratorTask task) {
		this.task = task;
	}
	
	// PagedTask interface ---------------------------------------------------------------------------------------------
	
    public void execute(BeneratorContext context) {
    	if (!initialized) {
    		countGenerator.init(context);
    		initialized = true;
    	}
    	Task taskToUse = this.task;
    	int threadCount = threads.evaluate(context);
		if (threadCount > 1 && !taskToUse.isParallelizable() && !task.isThreadSafe())
			taskToUse = new SynchronizedTask(taskToUse);
	    this.tracker = PagedTaskRunner.execute(taskToUse, context, 
	    		countGenerator.generate(), 
	    		getPageListeners(context), 
	    		pageSize.evaluate(context),
	    		threadCount,
	    		false, 
	    		context.getExecutorService(),
	    		getErrorHandler(context),
	    		infoLog);
	    if (!nested)
	    	close();
    }

	public void reset() {
	    task.reset();
	    countGenerator.reset();
    }
	
	public void close() {
	    task.close();
	    countGenerator.close();
    }

	private List<PageListener> getPageListeners(Context context) {
		List<PageListener> listeners = new ArrayList<PageListener>();
		if (pageListener != null) {
	        PageListener listener = pageListener.evaluate(context);
	        if (listener != null)
	        	listeners.add(listener);
        }
		listeners.add(this);
	    return listeners;
    }

	public GeneratorTask getTarget() {
	    return task;
    }

    public PerformanceTracker getTracker() {
	    return tracker;
    }
    
	public void pageStarting() {
	}

	public void pageFinished() {
		getTarget().flushConsumer();
	}

}
