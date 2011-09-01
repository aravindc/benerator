/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.GeneratorTask;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.IOUtil;
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
	protected Expression<Long> minCount;
	protected Expression<Long> pageSize;
	protected Expression<Integer> threads;
	protected Expression<PageListener> pageListenerEx;
	protected PageListener pageListener;
	protected PerformanceTracker tracker;
	protected boolean infoLog;
	protected boolean isSubCreator;
	protected boolean initialized;
	
	public GenerateOrIterateStatement(GeneratorTask task, Generator<Long> countGenerator, Expression<Long> minCount, 
			Expression<Long> pageSize, Expression<PageListener> pageListenerEx, Expression<Integer> threads, 
			Expression<ErrorHandler> errorHandler, boolean infoLog, boolean isSubCreator) {
	    this.task = task;
	    this.countGenerator = countGenerator;
	    this.minCount = minCount;
	    this.pageSize = pageSize;
	    this.threads = threads;
	    this.pageListenerEx = pageListenerEx;
	    this.infoLog = infoLog;
	    this.isSubCreator = isSubCreator;
	    this.initialized = false;
    }

	public void setTask(GeneratorTask task) {
		this.task = task;
	}
	
	// PagedTask interface ---------------------------------------------------------------------------------------------
	
    public boolean execute(BeneratorContext context) {
    	Task taskToUse = this.task;
    	int threadCount = threads.evaluate(context);
		if (threadCount > 1 && !taskToUse.isParallelizable() && !task.isThreadSafe())
			taskToUse = new SynchronizedTask(taskToUse);
	    this.tracker = PagedTaskRunner.execute(taskToUse, context, 
	    		generateCount(context), 
	    		minCount.evaluate(context),
	    		evaluatePageListeners(context), 
	    		pageSize.evaluate(context),
	    		threadCount,
	    		false, 
	    		context.getExecutorService(),
	    		getErrorHandler(context),
	    		infoLog);
	    if (!isSubCreator)
	    	close();
    	return true;
    }

	public void prepare(GeneratorContext context) {
	    task.prepare(context);
	    if (!countGenerator.wasInitialized()) {
	    	countGenerator.init(context);
	    	initialized = true;
	    } else
	    	countGenerator.reset();
    }
	
	public void close() {
	    task.close();
	    countGenerator.close();
	    if (pageListener instanceof Closeable)
	    	IOUtil.close((Closeable) pageListener);
    }

	public Long generateCount(GeneratorContext context) {
    	if (!initialized) {
    		countGenerator.init(context);
    		initialized = true;
    	}
	    return countGenerator.generate(new ProductWrapper<Long>()).unwrap();
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
		getTarget().pageFinished();
	}

	// private helper --------------------------------------------------------------------------------------------------

	private List<PageListener> evaluatePageListeners(Context context) {
		List<PageListener> listeners = new ArrayList<PageListener>();
		if (pageListener != null) {
	        pageListener = pageListenerEx.evaluate(context);
	        if (pageListener != null)
	        	listeners.add(pageListener);
        }
		//listeners.add(this);
	    return listeners;
    }

}
