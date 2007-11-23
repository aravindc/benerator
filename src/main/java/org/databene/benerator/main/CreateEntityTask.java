package org.databene.benerator.main;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import org.databene.benerator.Generator;
import org.databene.model.Processor;
import org.databene.model.data.Entity;
import org.databene.task.AbstractTask;
import org.databene.task.PagedTask;
import org.databene.task.ThreadSafe;

public class CreateEntityTask extends PagedTask {
	
    private Generator<Entity> generator;
	private Collection<Processor<Entity>> processors;
	
	public CreateEntityTask(
			String name, int count, int pageSize, int threads, 
			Generator<Entity> generator, Collection<Processor<Entity>> processors, ExecutorService executor) {
		super(new Runner(generator, processors), count, null, pageSize, threads, executor);
		this.generator = generator;
		this.processors = processors;
	}
	
	public void pageFinished(int currentPageNo, long totalPages) {
		for (Processor<Entity> processor : processors)
			processor.flush();
	}

	@Override
	protected boolean workPending(int currentPageNo) {
		return generator.available();
	}
	
	private static class Runner extends AbstractTask implements ThreadSafe {

	    private Generator<Entity> generator;
		private Collection<Processor<Entity>> processors;
		
		public Runner(Generator<Entity> generator,
				Collection<Processor<Entity>> processors) {
			super();
			this.generator = generator;
			this.processors = processors;
		}

		public void run() {
			Entity entity = null;
			synchronized (generator) {
				if (generator.available())
			        entity = generator.generate();
			}
			if (entity != null)
				for (Processor<Entity> processor : processors)
					processor.process(entity);
		}
	}
}
