/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.main;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.databene.benerator.Generator;
import org.databene.commons.ErrorHandler;
import org.databene.model.consumer.Consumer;
import org.databene.model.data.Entity;
import org.databene.task.PagedTask;
import org.databene.task.Task;

/**
 * Creates a number of entities in parallel execution and a given page size.<br/><br/>
 * Created: 01.02.2008 14:43:15
 * @author Volker Bergmann
 */
public class PagedCreateEntityTask extends PagedTask {
    
    private Generator<Entity> generator;
	private Collection<Consumer<Entity>> consumers;
	
	public PagedCreateEntityTask(
			String taskName, int count, int pageSize, int threads, List<? extends Task> subTasks, 
			Generator<Entity> generator, Collection<Consumer<Entity>> consumers, ExecutorService executor, 
			boolean isSubTask, ErrorHandler errorHandler) {
		super(new CreateEntityTask(taskName, generator, consumers, subTasks, isSubTask, errorHandler), count, null, pageSize, threads, executor);
		this.generator = generator;
		this.consumers = consumers;
	}
	
	// PagedTask interface ---------------------------------------------------------------------------------------------
	
	@Override
	public void pageFinished(int currentPageNo) {
		for (Consumer<Entity> consumer : consumers)
			consumer.flush();
	}

	@Override
	public void destroy() {
		super.destroy();
	    synchronized(generator) {
	        generator.close();
	    }
	};
	
	@Override
	protected boolean workPending(int currentPageNo) {
		return generator.available();
	}
	
	public void reset() {
	    generator.reset();
	}

	// java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public String toString() {
	    return getClass().getSimpleName() + '[' + realTask + ']';
	}

}
