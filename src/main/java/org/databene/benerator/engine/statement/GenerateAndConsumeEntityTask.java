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

package org.databene.benerator.engine.statement;

import java.io.IOException;
import java.util.ArrayList;

import org.databene.benerator.Generator;
import org.databene.commons.Assert;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.model.consumer.Consumer;
import org.databene.model.data.Entity;
import org.databene.task.SerialTask;
import org.databene.task.Task;
import org.databene.task.TaskException;
import org.databene.task.ThreadSafe;

/**
 * Task that creates one entity instance per run() invocation and sends it to the specified consumer.<br/><br/>
 * Created: 01.02.2008 14:39:11
 * @author Volker Bergmann
 */
public  class GenerateAndConsumeEntityTask extends SerialTask implements ThreadSafe {

    private Generator<Entity> entityGenerator;
    private boolean isSubTask;
    private Expression<Consumer<Entity>> consumerExpr;

    private Consumer<Entity> consumer;
    
    public GenerateAndConsumeEntityTask(String taskName, Generator<Entity> entityGenerator, 
    		Expression<Consumer<Entity>> consumerExpr, boolean isSubTask, 
    		Expression<ErrorHandler> errorHandler) {
    	super(taskName, errorHandler);
    	Assert.notNull(entityGenerator, "entityGenerator");
        this.entityGenerator = entityGenerator; // TODO make this an expression?
        this.consumerExpr = consumerExpr;
    	this.subTasks = new ArrayList<Task>();
        this.isSubTask = isSubTask;
    }

    // interface -------------------------------------------------------------------------------------------------------
    
    public Generator<Entity> getEntityGenerator() {
    	return entityGenerator;
    }

	@Override
    public boolean available() {
        return entityGenerator.available();
    }
    
    @Override
    public void run(Context context) {
    	try {
    		// generate entity
	        Entity entity = null;
	        synchronized (entityGenerator) {
	            if (entityGenerator.available())
	                entity = entityGenerator.generate();
	            else
	                return;
	        }
	        if (entity != null) {
		        // consume entity
	        	Consumer<Entity> consumer = getConsumer(context);
	        	if (consumer != null)
	        		consumer.startConsuming(entity);
	        	// generate and consume sub entities
	        	super.run(context);
	        	if (consumer != null)
	        		consumer.finishConsuming(entity);
	        }
    	} catch (Exception e) {
    		getErrorHandler(context).handleError("Error in execution of task " + getTaskName(), e);
    	}
    }
    
    @Override
    protected void runSubTask(Context context, Task subTask) {
        try {
	        if (subTask instanceof CreateOrUpdateEntityStatement)
	            ((CreateOrUpdateEntityStatement) subTask).getTarget().reset();
	        super.runSubTask(context, subTask);
	        subTask.close();
        } catch (IOException e) {
	        throw new TaskException(e);
        }
    }
    
    public void reset() {
	    entityGenerator.reset();
    }

	@Override
    public void close() {
        if (!isSubTask && consumer != null)
            consumer.flush();
        super.close();
    }

	public void flushConsumer() {
		if (consumer != null)
			consumer.flush();
    }

	// non-public helpers ----------------------------------------------------------------------------------------------
	
    Consumer<Entity> getConsumer(Context context) {
    	if (consumer == null)
    		consumer = consumerExpr.evaluate(context);
    	return consumer;
    }

}
