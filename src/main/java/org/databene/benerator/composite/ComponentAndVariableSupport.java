/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.MessageHolder;
import org.databene.commons.Resettable;
import org.databene.commons.ThreadAware;
import org.databene.commons.ThreadUtil;
import org.databene.commons.collection.OrderedNameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Offers support for entity or array component generation with or without variable generation.<br/><br/>
 * Created: 13.01.2011 10:52:43
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class ComponentAndVariableSupport<E> implements ThreadAware, MessageHolder, Resettable, Closeable {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentAndVariableSupport.class);
    
	private Map<String, Generator<?>> variables;
	private OrderedNameMap<ProductWrapper<?>> variableResults;
	private ComponentBuilderSupport<E> allComponentsBuilder;
	private GeneratorContext context;
	private boolean firstRun;
	private String message;
	
	public ComponentAndVariableSupport(Map<String, Generator<?>> variables, List<ComponentBuilder<E>> componentBuilders, 
			GeneratorContext context) {
		this.variables = variables;
		this.allComponentsBuilder = new ComponentBuilderSupport<E>(componentBuilders);
		this.context = context;
	}
	
    public void init(GeneratorContext context) {
		initVariables(context);
        this.firstRun = true;
        allComponentsBuilder.init(context);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    private void initVariables(GeneratorContext context) {
	    this.variableResults = new OrderedNameMap<ProductWrapper<?>>();
        for (Map.Entry<String, Generator<?>> entry : variables.entrySet()) {
        	Generator<?> varGen = entry.getValue();
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

    public boolean apply(E target) {
		if (!calculateVariables())
			return false;
        if (target != null && !allComponentsBuilder.buildComponents(target))
        	return false;
    	LOGGER.debug("Generated {}", target);
    	return true;
	}

    public void reset() {
		for (Generator<?> variable : variables.values())
			variable.reset();
		allComponentsBuilder.reset();
	}

    public void close() {
		for (Generator<?> variable : variables.values())
			variable.close();
		allComponentsBuilder.close();
        for (String variableName : variables.keySet())
            context.remove(variableName);
	}
	
	public String getMessage() {
		if (message != null)
			return message;
		return allComponentsBuilder.getMessage();
	}
	
	
	
	// helper methods --------------------------------------------------------------------------------------------------
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean calculateVariables() {
		if (firstRun) { // for the first run, the variables are calculated in the initialization
			for (Map.Entry<String, ProductWrapper<?>> entry : variableResults.entrySet())
				if (!processVariable(entry.getKey(), entry.getValue()))
					return false;
			firstRun = false;
		} else { // in subsequent runs, they are calculated on demand
			for (Map.Entry<String, Generator<?>> entry : variables.entrySet()) {
				Generator<?> generator = entry.getValue();
				ProductWrapper<?> productWrapper = generator.generate(new ProductWrapper());
				if (!processVariable(entry.getKey(), productWrapper))
					return false;
	        }
		}
		return true;
	}
	
	private boolean processVariable(String varName, ProductWrapper<?> varValue) {
		if (varValue == null) {
			this.message = "Variable no more available: " + varName;
        	if (LOGGER.isDebugEnabled())
        		LOGGER.debug(message);
            return false;
		}
        context.set(varName, varValue.unwrap());
        return true;
	}

	
	
	// ThreadAware interface implementation ----------------------------------------------------------------------------
	
    public boolean isParallelizable() {
	    return ThreadUtil.allParallelizable(variables.values());
    }

    public boolean isThreadSafe() {
	    return ThreadUtil.allThreadSafe(variables.values());
    }
    
    
    
	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
	    return getClass().getSimpleName() + "[" + (variables.size() > 0 ? "variables: " + variables : "") + "]";
	}

}
