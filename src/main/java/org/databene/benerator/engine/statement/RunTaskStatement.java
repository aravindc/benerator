/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.commons.Expression;
import org.databene.commons.expression.ConstantExpression;
import org.databene.task.PageListener;
import org.databene.task.PagedTaskRunner;
import org.databene.task.Task;

/**
 * {@link Statement} that executes a {@link Task} supporting paging and multithreading.<br/><br/>
 * Created: 27.10.2009 20:29:47
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class RunTaskStatement implements Statement {
	
	protected Task task;
	protected Expression<Long> count;
	protected Expression<Long> pageSize;
	protected Expression<Integer> threads;
	protected Expression<PageListener> pageListener;

	public RunTaskStatement(Task task) {
	    this(task, new ConstantExpression<Long>(1L), new ConstantExpression<Long>(1L), 
	    		new ConstantExpression<PageListener>(null), new ConstantExpression<Integer>(1));
    }

	public RunTaskStatement(Task task, Expression<Long> count, Expression<Long> pageSize, 
			Expression<PageListener> pageListener, Expression<Integer> threads) {
	    this.task = task;
	    this.count = count;
	    this.pageSize = pageSize;
	    this.threads = threads;
	    this.pageListener = pageListener;
    }

	public Expression<Long> getCount() {
    	return count;
    }

	public Expression<Long> getPageSize() {
    	return pageSize;
    }

	public Expression<Integer> getThreads() {
    	return threads;
    }

	public Expression<PageListener> getPager() {
    	return pageListener;
    }

	public void execute(BeneratorContext context) {
	    PagedTaskRunner.execute(task, context, 
	    		count.evaluate(context), 
	    		getPageListeners(context), 
	    		pageSize.evaluate(context), 
	    		threads.evaluate(context));
	}

	private List<PageListener> getPageListeners(BeneratorContext context) {
		List<PageListener> listeners = new ArrayList<PageListener>();
	    if (pageListener != null)
	    	listeners.add(pageListener.evaluate(context));
	    return listeners;
    }

}
