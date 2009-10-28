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
 * TODO Document class.<br/><br/>
 * Created: 27.10.2009 20:29:47
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class RunTaskStatement implements Statement {
	
	protected Task task;
	protected Expression count;
	protected Expression pageSize;
	protected Expression threads;
	protected Expression pageListener;

	public RunTaskStatement(Task task) {
	    this(task, new ConstantExpression(1L), new ConstantExpression(1L), 
	    		new ConstantExpression(null), new ConstantExpression(1));
    }

	public RunTaskStatement(Task task, Expression count, Expression pageSize, 
			Expression pageListener, Expression threads) {
	    this.task = task;
	    this.count = count;
	    this.pageSize = pageSize;
	    this.threads = threads;
	    this.pageListener = pageListener;
    }

	public Expression getCount() {
    	return count;
    }

	public Expression getPageSize() {
    	return pageSize;
    }

	public Expression getThreads() {
    	return threads;
    }

	public Expression getPager() {
    	return pageListener;
    }

	public void execute(BeneratorContext context) {
	    PagedTaskRunner.execute(task, context, 
	    		evaluateCount(context), 
	    		getPageListeners(context), 
	    		(Long) pageSize.evaluate(context), 
	    		(Integer) threads.evaluate(context));
	}

	private List<PageListener> getPageListeners(BeneratorContext context) {
		List<PageListener> listeners = new ArrayList<PageListener>();
	    if (pageListener != null)
	    	listeners.add((PageListener) pageListener.evaluate(context));
	    return listeners;
    }

	protected Long evaluateCount(BeneratorContext context) {
	    return (Long) count.evaluate(context);
    }

}
