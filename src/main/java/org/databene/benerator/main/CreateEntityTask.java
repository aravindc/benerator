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
import java.util.concurrent.ExecutorService;

import org.databene.benerator.Generator;
import org.databene.model.Escalator;
import org.databene.model.Processor;
import org.databene.model.data.Entity;
import org.databene.model.escalate.LoggerEscalator;
import org.databene.task.AbstractTask;
import org.databene.task.PagedTask;
import org.databene.task.ThreadSafe;

public class CreateEntityTask extends PagedTask {
    
    private Generator<Entity> generator;
	private Collection<Processor<Entity>> processors;
	
	public CreateEntityTask(
			String name, int count, int pageSize, int threads, 
			Generator<Entity> generator, Collection<Processor<Entity>> processors, ExecutorService executor) {
		super(new Worker(name, generator, processors), count, null, pageSize, threads, executor);
		this.generator = generator;
		this.processors = processors;
	}
	
	public void pageFinished(int currentPageNo, long totalPages) {
		for (Processor<Entity> processor : processors)
			processor.flush();
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
	
	private static class Worker extends AbstractTask implements ThreadSafe {

	    private String name;
	    private Generator<Entity> generator;
		private Collection<Processor<Entity>> processors;
	    private Escalator noProcessorHandler;
	    private int generationCount;
		
		public Worker(String name, Generator<Entity> generator,
				Collection<Processor<Entity>> processors) {
			this.name = name;
			this.generator = generator;
			this.processors = processors;
	        this.noProcessorHandler = new LoggerEscalator();
	        this.generationCount = 0;
		}

		public void run() {
			Entity entity = null;
			synchronized (generator) {
				if (generator.available())
			        entity = generator.generate();
				else
				    return;
			}
			if (processors.size() == 0)
			    noProcessorHandler.escalate("No processors defined ", this, null);
			if (entity != null) {
			    generationCount++;
				for (Processor<Entity> processor : processors)
					processor.process(entity);
			}
		}
		
		@Override
		public void destroy() {
		    super.destroy();
		}
		
		@Override
		public String toString() {
		    return getClass().getSimpleName() + '[' + name + ']';
		}
	}
}
