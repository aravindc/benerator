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

import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.commons.Assert;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.model.consumer.Consumer;
import org.databene.model.data.Entity;
import org.databene.task.AbstractTask;
import org.databene.task.ThreadSafe;

/**
 * Task that creates one entity instance per run() invocation and sends it to the specified consumer.<br/><br/>
 * Created: 01.02.2008 14:39:11
 * @author Volker Bergmann
 */
public  class GenerateAndConsumeEntityTask extends AbstractTask implements ThreadSafe {

    private Generator<Entity> entityGenerator;
    private boolean isSubTask;
    private Expression<Consumer<Entity>> consumerExpr;
    private List<Statement> subStatements;

    private Consumer<Entity> consumer;
    
    public GenerateAndConsumeEntityTask(String taskName, Generator<Entity> entityGenerator, 
    		Expression<Consumer<Entity>> consumerExpr, boolean isSubTask, 
    		Expression<ErrorHandler> errorHandler) {
    	super(taskName, errorHandler);
    	Assert.notNull(entityGenerator, "entityGenerator");
        this.entityGenerator = entityGenerator; // TODO make this an expression?
        this.consumerExpr = consumerExpr;
    	this.subStatements = new ArrayList<Statement>();
        this.isSubTask = isSubTask;
    }

    // interface -------------------------------------------------------------------------------------------------------
    
    public void addSubStatement(Statement statement) {
    	this.subStatements.add(statement);
    }
    
    public Generator<Entity> getEntityGenerator() {
    	return entityGenerator;
    }
    
	@Override
    public boolean available() {
        return entityGenerator.available();
    }
    
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
	        	runSubTasks((BeneratorContext) context);
	        	if (consumer != null)
	        		consumer.finishConsuming(entity);
	        }
    	} catch (Exception e) {
    		getErrorHandler(context).handleError("Error in execution of task " + getTaskName(), e);
    	}
    }
    
    private void runSubTasks(BeneratorContext context) {
	    for (Statement subStatement : subStatements)
	    	runSubTask(subStatement, context);
    }

    protected void runSubTask(Statement subStatement, BeneratorContext context) {
        if (subStatement instanceof CreateOrUpdateEntitiesStatement) {
            GenerateAndConsumeEntityTask target = ((CreateOrUpdateEntitiesStatement) subStatement).getTarget();
			target.reset();
	        subStatement.execute(context);
	        target.close();
        } else
        	subStatement.execute(context);
    }
    
    public void reset() {
	    entityGenerator.reset();
    }

	@Override
    public void close() {
        if (!isSubTask && consumer != null)
            consumer.flush();
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
