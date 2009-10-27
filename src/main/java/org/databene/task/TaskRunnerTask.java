/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.task;

import java.util.concurrent.ExecutorService;

import org.databene.commons.Context;
import org.databene.commons.Expression;

/**
 * {@link Task} implementation that executes another task 
 * a certain number of times with a certain number of threads.
 * Depending on the Task class' abilities it may create clones 
 * of the specified tasks or runs it in a single thread.<br/>
 * <br/>
 * Created at 23.07.2009 07:01:38
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class TaskRunnerTask<E extends Task> extends TaskProxy<E> {

    private Expression count;
    private Expression pageSize;
    private Expression threads;
    private Expression pager;
    private Expression executor;

	public TaskRunnerTask(E realTask, Expression count, Expression pageSize, 
			Expression threads, Expression pager, Expression executor) {
	    super(realTask);
	    this.count = count;
	    this.pageSize = pageSize;
	    this.threads = threads;
	    this.pager = pager;
	    this.executor = executor;
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
    	return pager;
    }

	@Override
	public void run(Context context) {
	    TaskRunner.run(realTask, context, 
	    		(Long) count.evaluate(context), 
	    		(pager != null ? (PageListener) pager.evaluate(context) : null), 
	    		(Long) pageSize.evaluate(context), 
	    		(Integer) threads.evaluate(context), 
	    		(ExecutorService) executor.evaluate(context));
	}
	
}
