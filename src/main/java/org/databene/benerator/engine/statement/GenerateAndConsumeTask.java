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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.GeneratorTask;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.ResourceManagerSupport;
import org.databene.benerator.engine.Statement;
import org.databene.commons.Assert;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.MessageHolder;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.model.consumer.Consumer;
import org.databene.task.TaskResult;

/**
 * Task that creates one data set instance per run() invocation and sends it to the specified consumer.<br/><br/>
 * Created: 01.02.2008 14:39:11
 * @author Volker Bergmann
 */
public class GenerateAndConsumeTask implements GeneratorTask, ResourceManager, MessageHolder {

	private String taskName;
    private Generator<?> generator;
    private boolean isSubCreator;
    private Expression<Consumer<?>> consumerExpr;
    private List<Statement> subStatements;
    private ResourceManager resourceManager = new ResourceManagerSupport();
    private volatile AtomicBoolean generatorInitialized;

    private Consumer<?> consumer;
    
    public GenerateAndConsumeTask(String taskName, Generator<?> generator, 
    		Expression<Consumer<?>> consumerExpr, boolean isSubCreator) {
    	this.taskName = taskName;
    	Assert.notNull(generator, "generator");
        this.generator = generator;
        this.consumerExpr = consumerExpr;
    	this.subStatements = new ArrayList<Statement>();
        this.isSubCreator = isSubCreator;
        this.generatorInitialized = new AtomicBoolean(false);
    }

    // interface -------------------------------------------------------------------------------------------------------
    
    public void addSubStatement(Statement statement) {
    	this.subStatements.add(statement);
    }
    
    public Generator<?> getGenerator() {
    	return generator;
    }

	public void flushConsumer() {
		if (consumer != null)
			consumer.flush();
    }

    public Consumer<?> getConsumer(Context context) {
    	if (consumer == null)
    		consumer = ExpressionUtil.evaluate(consumerExpr, context);
    	return consumer;
    }

    // Task interface implementation -----------------------------------------------------------------------------------
    
	public String getTaskName() {
	    return taskName;
    }

    public boolean isThreadSafe() {
        return false;
    }
    
    public boolean isParallelizable() {
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public TaskResult execute(Context ctx, ErrorHandler errorHandler) {
    	BeneratorContext context = (BeneratorContext) ctx;
    	if (!generatorInitialized.get())
    		initGenerator(context);
    	try {
    		// generate data object
	        Object data = generator.generate();
	        if (data == null) {
		        Thread.yield();
	        	return TaskResult.UNAVAILABLE;
	        }
	        context.countGenerations(1);
	        // consume data object
        	Consumer consumer = getConsumer(context);
        	if (consumer != null)
        		consumer.startConsuming(data);
        	// generate and consume sub data objects
        	runSubTasks(context);
        	if (consumer != null)
        		consumer.finishConsuming(data);
	        Thread.yield();
	        return TaskResult.EXECUTING;
    	} catch (Exception e) {
			errorHandler.handleError("Error in execution of task " + getTaskName(), e);
    		return TaskResult.EXECUTING; // stay available if the ErrorHandler has not canceled execution
    	}
    }

    public void reset() {
	    generator.reset();
    }

    public void pageFinished() {
        if (!isSubCreator && consumer != null)
            consumer.flush();
    }
    
    public void close() {
        resourceManager.close();
    }

    // ResourceManager interface ---------------------------------------------------------------------------------------
    
	public boolean addResource(Closeable resource) {
	    return resourceManager.addResource(resource);
    }
	
	// MessageHolder interface -----------------------------------------------------------------------------------------
	
	public String getMessage() {
	    return (generator instanceof MessageHolder ? ((MessageHolder) generator).getMessage() : null);
    }
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + taskName + ')';
    }

    // private helpers -------------------------------------------------------------------------------------------------

	private void initGenerator(BeneratorContext context) {
	    synchronized (generatorInitialized) {
	    	if (!generatorInitialized.get()) {
	    		generator.init(context);
	    		generatorInitialized.set(true);
	    	}
	    }
    }
    
    private void runSubTasks(BeneratorContext context) {
	    for (Statement subStatement : subStatements)
	    	runSubTask(subStatement, context);
    }
    
    protected void runSubTask(Statement subStatement, BeneratorContext context) {
        if (subStatement instanceof GeneratorStatement) {
            GeneratorStatement generatorStatement = (GeneratorStatement) subStatement;
			generatorStatement.reset();
			generatorStatement.execute(context);
        	generatorStatement.close();
        } else
        	subStatement.execute(context);
    }
    
}
