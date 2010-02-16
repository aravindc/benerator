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

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.ResourceManagerSupport;
import org.databene.benerator.engine.Statement;
import org.databene.commons.Assert;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.ThreadSupport;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.model.consumer.Consumer;
import org.databene.model.data.Entity;
import org.databene.task.AbstractTask;

/**
 * Task that creates one entity instance per run() invocation and sends it to the specified consumer.<br/><br/>
 * Created: 01.02.2008 14:39:11
 * @author Volker Bergmann
 */
public  class GenerateAndConsumeEntityTask extends AbstractTask implements ResourceManager {

    private Generator<Entity> entityGenerator;
    private boolean isSubTask;
    private Expression<Consumer<Entity>> consumerExpr;
    private List<Statement> subStatements;
    private ResourceManager resourceManager = new ResourceManagerSupport();

    private Consumer<Entity> consumer;
    
    public GenerateAndConsumeEntityTask(String taskName, Generator<Entity> entityGenerator, 
    		Expression<Consumer<Entity>> consumerExpr, boolean isSubTask) {
    	super(taskName);
    	Assert.notNull(entityGenerator, "entityGenerator");
        this.entityGenerator = entityGenerator;
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
    public ThreadSupport getThreading() {
        return ThreadSupport.MULTI_THREADED;
    }
    
    public boolean executeStep(Context ctx, ErrorHandler errorHandler) {
    	BeneratorContext context = (BeneratorContext) ctx;
    	try {
    		// generate entity
	        Entity entity = entityGenerator.generate();
	        if (entity == null) {
		        Thread.yield();
	        	return false;
	        }
	        // consume entity
        	Consumer<Entity> consumer = getConsumer(context);
        	if (consumer != null)
        		consumer.startConsuming(entity);
        	// generate and consume sub entities
        	runSubTasks(context);
        	if (consumer != null)
        		consumer.finishConsuming(entity);
	        Thread.yield();
	        return true;
    	} catch (Exception e) {
			errorHandler.handleError("Error in execution of task " + getTaskName(), e);
    		return true; // stay available if the ErrorHandler has not canceled execution
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
	        try {
	        	target.close();
	        } catch (IOException e) {
	        	throw new RuntimeException(e);
	        }
        } else
        	subStatement.execute(context);
    }
    
    public void reset() {
	    entityGenerator.reset();
    }

	@Override
    public void close() throws IOException {
		resourceManager.close();
        if (!isSubTask && consumer != null)
            consumer.flush();
    }

	public void flushConsumer() {
		if (consumer != null)
			consumer.flush();
    }

	public boolean addResource(Closeable resource) {
	    return resourceManager.addResource(resource);
    }

	// non-public helpers ----------------------------------------------------------------------------------------------
	
    Consumer<Entity> getConsumer(Context context) {
    	if (consumer == null)
    		consumer = ExpressionUtil.evaluate(consumerExpr, context);
    	return consumer;
    }

}
