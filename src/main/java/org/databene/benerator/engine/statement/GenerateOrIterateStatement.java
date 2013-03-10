/*
 * (c) Copyright 2007-2013 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.IOUtil;
import org.databene.script.Expression;
import org.databene.task.PageListener;
import org.databene.task.TaskExecutor;

/**
 * Creates a number of entities in parallel execution and a given page size.<br/><br/>
 * Created: 01.02.2008 14:43:15
 * @author Volker Bergmann
 */
public class GenerateOrIterateStatement extends AbstractStatement implements Closeable, PageListener {

	protected GenerateAndConsumeTask task;
	protected Generator<Long> countGenerator;
	protected Expression<Long> minCount;
	protected Expression<Long> pageSize;
	protected Expression<PageListener> pageListenerEx;
	protected PageListener pageListener;
	protected boolean infoLog;
	protected boolean isSubCreator;
	protected BeneratorContext context;
	protected BeneratorContext childContext;
	
	public GenerateOrIterateStatement(String productName, Generator<Long> countGenerator, Expression<Long> minCount, 
			Expression<Long> pageSize, Expression<PageListener> pageListenerEx,  
			Expression<ErrorHandler> errorHandler, boolean infoLog, boolean isSubCreator, BeneratorContext context) {
	    this.task = null;
	    this.countGenerator = countGenerator;
	    this.minCount = minCount;
	    this.pageSize = pageSize;
	    this.pageListenerEx = pageListenerEx;
	    this.infoLog = infoLog;
	    this.isSubCreator = isSubCreator;
	    this.context = context;
    	this.childContext = context.createSubContext(productName);
    }

	public void setTask(GenerateAndConsumeTask task) {
		this.task = task;
	}
	
	public GenerateAndConsumeTask getTask() {
	    return task;
    }
	
	public BeneratorContext getContext() {
		return context;
	}

	public BeneratorContext getChildContext() {
		return childContext;
	}

    
    
	// Statement interface ---------------------------------------------------------------------------------------------
	
    @Override
	public boolean execute(BeneratorContext ctx) {
    	if (!beInitialized(ctx))
    		task.reset();
	    executeTask(generateCount(childContext), minCount.evaluate(childContext), pageSize.evaluate(childContext),
				evaluatePageListeners(childContext), getErrorHandler(childContext));
	    if (!isSubCreator)
	    	close();
    	return true;
    }

	public Long generateCount(BeneratorContext context) {
		beInitialized(context);
	    ProductWrapper<Long> count = countGenerator.generate(new ProductWrapper<Long>());
	    return (count != null ? count.unwrap() : null);
    }

	@Override
	public void close() {
	    task.close();
	    countGenerator.close();
	    if (pageListener instanceof Closeable)
	    	IOUtil.close((Closeable) pageListener);
    }

    // PageListener interface implementation ---------------------------------------------------------------------------
    
	@Override
	public void pageStarting() {
	}

	@Override
	public void pageFinished() {
		getTask().pageFinished();
	}
	
	
	
	// internal helpers ------------------------------------------------------------------------------------------------

	protected List<PageListener> evaluatePageListeners(Context context) {
		List<PageListener> listeners = new ArrayList<PageListener>();
		if (pageListener != null) {
	        pageListener = pageListenerEx.evaluate(context);
	        if (pageListener != null)
	        	listeners.add(pageListener);
        }
	    return listeners;
    }

	protected boolean beInitialized(BeneratorContext context) {
		if (!countGenerator.wasInitialized()) {
	    	countGenerator.init(childContext);
		    task.init(childContext);
		    return true;
		}
		return false;
	}

	protected void executeTask(Long requestedExecutions, Long minExecutions, Long pageSizeValue, 
			List<PageListener> pageListeners, ErrorHandler errorHandler) {
		TaskExecutor.execute(task, childContext, requestedExecutions, minExecutions,
	    		pageListeners, pageSizeValue, false, errorHandler, infoLog);
	}

}
