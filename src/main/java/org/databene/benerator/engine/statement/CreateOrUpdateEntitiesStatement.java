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
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.model.data.Entity;
import org.databene.task.PageListener;
import org.databene.task.PagedTaskRunner;
import org.databene.task.Task;

/**
 * Creates a number of entities in parallel execution and a given page size.<br/><br/>
 * Created: 01.02.2008 14:43:15
 * @author Volker Bergmann
 */
public class CreateOrUpdateEntitiesStatement extends AbstractStatement implements PageListener {

	protected Task task;
	protected Expression<Long> count;
	protected Expression<Long> pageSize;
	protected Expression<Integer> threads;
	protected Expression<PageListener> pageListener;

	public CreateOrUpdateEntitiesStatement(GenerateAndConsumeEntityTask task,
			Expression<Long> count, Expression<Long> pageSize, Expression<PageListener> pageListener, 
			Expression<Integer> threads, Expression<ErrorHandler> errorHandler) {
	    this.task = task;
	    this.count = count;
	    this.pageSize = pageSize;
	    this.threads = threads;
	    this.pageListener = pageListener;
		this.generator = task.getEntityGenerator();
    }

	private Generator<Entity> generator;
	
	// PagedTask interface ---------------------------------------------------------------------------------------------
	
    public void execute(BeneratorContext context) {
	    PagedTaskRunner.execute(task, context, 
	    		count.evaluate(context), 
	    		getPageListeners(context), 
	    		pageSize.evaluate(context),
	    		threads.evaluate(context),
	    		context.getExecutorService(),
	    		getErrorHandler(context));
        // TODO count all generations // context.countGenerations(getActualCount());
	    synchronized(generator) {
	        generator.close();
	    }
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

	public GenerateAndConsumeEntityTask getTarget() {
	    return (GenerateAndConsumeEntityTask) task;
    }

	public void pageStarting() {
	}

	public void pageFinished() {
		getTarget().flushConsumer();
	}
	
}
