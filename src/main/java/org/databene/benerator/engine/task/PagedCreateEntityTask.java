/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.task;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.model.consumer.Consumer;
import org.databene.model.data.Entity;
import org.databene.task.PagedTask;
import org.databene.task.Task;

/**
 * Creates a number of entities in parallel execution and a given page size.<br/><br/>
 * Created: 01.02.2008 14:43:15
 * @author Volker Bergmann
 */
public class PagedCreateEntityTask extends PagedTask<GenerateAndConsumeEntityTask> {

	private Expression<Long> countEx;
    private Generator<Entity> generator;
	
	public PagedCreateEntityTask(
			String taskName, Expression<Long> countEx, int pageSize, int threads, 
			Generator<Entity> generator, Expression<Consumer<Entity>> consumerExpr, 
			Expression<ExecutorService> executor, 
			boolean isSubTask, Expression<ErrorHandler> errorHandler) {
		super(new GenerateAndConsumeEntityTask(taskName, generator, consumerExpr, isSubTask, errorHandler), 
				0, null, pageSize, threads, executor);
		this.generator = generator;
		this.countEx = countEx;
	}
	
    public void addSubTask(Task task) {
    	realTask.addSubTask(task);
    }
    
	// PagedTask interface ---------------------------------------------------------------------------------------------
	
    @Override
    public void run(Context context) {
    	setMaxCount(countEx.evaluate(context));
        super.run(context);
        ((BeneratorContext) context).countGenerations(getActualCount());
    }
    
	@Override
	public void pageFinished(int currentPageNo, Context context) {
		Consumer<Entity> consumer = realTask.getConsumer(context);
		if (consumer != null)
			consumer.flush();
	}

	@Override
	public void close() throws IOException {
		super.close();
	    synchronized(generator) {
	        generator.close();
	    }
	}
	
	public void reset() {
	    realTask.reset();
	}

	// java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public String toString() {
	    return getClass().getSimpleName() + '[' + realTask + ']';
	}

}
