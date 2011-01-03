/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.composite;

import java.util.List;
import java.util.Map;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.MessageHolder;
import org.databene.commons.ThreadUtil;
import org.databene.commons.collection.OrderedNameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Generator} proxy that combines a 'source' entity generator 
 * with variable support and ComponentBuilders.<br/><br/>
 * Created: 29.08.2010 09:59:03
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class SourceAwareGenerator<E> extends GeneratorProxy<E> implements MessageHolder {
	
    private static final Logger logger = LoggerFactory.getLogger(SourceAwareGenerator.class);
    private static final Logger stateLogger = LoggerFactory.getLogger("org.databene.benerator.STATE");
    
    private String instanceName;
    private E currentInstance;
	private Map<String, NullableGenerator<?>> variables;
	private OrderedNameMap<ProductWrapper<?>> variableResults;
	private AllComponentsBuilder<E> allComponentsBuilder;
	private GeneratorContext context;
	private boolean firstGeneration;
	private String message;
	
	/**
     * @param source another Generator of entities that serves as Entity builder. 
     *     It may construct empty Entities or may import them (so this may overwrite imported attributes). 
     * @param instanceName instance name for the generated entities. 
	 */
	public SourceAwareGenerator(String instanceName, Generator<E> source, 
			Map<String, NullableGenerator<?>> variables, List<ComponentBuilder<E>> componentBuilders, 
			GeneratorContext context) {
        super(source);
        this.instanceName = instanceName;
		this.variables = variables;
		this.allComponentsBuilder = new AllComponentsBuilder<E>(componentBuilders);
		this.context = context;
	}
	
	// Generator implementation ----------------------------------------------------------------------------------------
	
	@Override
    public void init(GeneratorContext context) {
        source.init(context);
    	fetchNextSourceInstance(context);
		initVariables(context);
        this.firstGeneration = true;
        allComponentsBuilder.init(context);
		super.init(context);
	}

	private void fetchNextSourceInstance(GeneratorContext context) {
    	currentInstance = source.generate();
        if (currentInstance == null) {
        	message = "Source for entity '" + instanceName + "' is not available any more: " + source;
            stateLogger.debug(message);
        }
        
        if (instanceName != null)
        	context.set(instanceName, currentInstance);
        context.set("this", currentInstance);
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    private void initVariables(GeneratorContext context) {
	    this.variableResults = new OrderedNameMap<ProductWrapper<?>>();
        for (Map.Entry<String, NullableGenerator<?>> entry : variables.entrySet()) {
        	NullableGenerator<?> varGen = entry.getValue();
        	try {
	        	varGen.init(context);
	        	ProductWrapper<?> result = varGen.generate(new ProductWrapper());
				variableResults.put(entry.getKey(), result);
				context.set(entry.getKey(), ProductWrapper.unwrap(result));
        	} catch (Exception e) {
        		throw new RuntimeException("Error initializing variable '" + entry.getKey() + "': " + varGen, e);
        	}
        }
    }

	@Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public E generate() {
		if (!firstGeneration)
			fetchNextSourceInstance(context);
		if (currentInstance == null)
			return null;
		
		// calculate variables
		if (firstGeneration) {
			for (Map.Entry<String, ProductWrapper<?>> entry : variableResults.entrySet()) {
				ProductWrapper<?> productWrapper = entry.getValue();
				if (productWrapper == null) {
					this.message = "Variable no more available: " + entry.getKey();
		        	if (logger.isDebugEnabled())
		        		logger.debug(message);
		            return null;
				}
	            context.set(entry.getKey(), productWrapper.product);
	        }
			firstGeneration = false;
		} else {
			for (Map.Entry<String, NullableGenerator<?>> entry : variables.entrySet()) {
				NullableGenerator<?> generator = entry.getValue();
				ProductWrapper<?> productWrapper = generator.generate(new ProductWrapper());
				if (productWrapper == null) {
					this.message = "No more available: " + generator;
		        	if (logger.isDebugEnabled())
		        		logger.debug(message);
		            return null;
				}
	            context.set(entry.getKey(), productWrapper.product);
	        }
		}

        if (!allComponentsBuilder.buildComponents(currentInstance))
        	currentInstance = null;
        else
        	logger.debug("Generated {}", currentInstance);
        return currentInstance;
	}

	@Override
    public void reset() {
		super.reset();
		firstGeneration = true;
		fetchNextSourceInstance(context);
		for (NullableGenerator<?> variable : variables.values())
			variable.reset();
		allComponentsBuilder.reset();
	}

	@Override
    public void close() {
		for (NullableGenerator<?> variable : variables.values())
			variable.close();
		allComponentsBuilder.close();
		super.close();
        for (String variableName : variables.keySet())
            context.remove(variableName);
	}
	
	public String getMessage() {
		if (message != null)
			return message;
	    return (source instanceof MessageHolder ? ((MessageHolder) source).getMessage() : null); // TODO v0.6.4 the AllComponentsBuilder might have a message too
	}

	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
	    return getClass().getSimpleName() + "[\n"
	        + (variables.size() > 0 ? "    variables" + variables + "\n" : "")
	        + "    " + source + "\n" + "]";
	}

	@Override
    public boolean isParallelizable() {
	    return source.isParallelizable() && ThreadUtil.allParallelizable(variables.values());
    }

	@Override
    public boolean isThreadSafe() {
	    return source.isThreadSafe() && ThreadUtil.allThreadSafe(variables.values());
    }

}
