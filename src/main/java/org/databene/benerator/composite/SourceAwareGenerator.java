/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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
import org.databene.commons.MessageHolder;
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
	private boolean firstGeneration;
	private String message;
	private ComponentAndVariableSupport<E> support;
	
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
        this.support = new ComponentAndVariableSupport<E>(variables, componentBuilders, context);
		this.context = context;
	}
	
	// Generator implementation ----------------------------------------------------------------------------------------
	
	@Override
    public void init(GeneratorContext context) {
        source.init(context);
    	fetchNextSourceInstance(context);
        this.firstGeneration = true;
        support.init(context);
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
	
	@Override
    public E generate() {
		if (!firstGeneration)
			fetchNextSourceInstance(context);
		firstGeneration = false;
		if (currentInstance == null)
			return null;
		if (!support.apply(currentInstance))
			currentInstance = null;
        if (currentInstance != null)
        	logger.debug("Generated {}", currentInstance);
        return currentInstance;
	}

	@Override
    public void reset() {
		super.reset();
		firstGeneration = true;
		fetchNextSourceInstance(context);
		support.reset();
	}

	@Override
    public void close() {
		support.close();
		super.close();
	}
	
	public String getMessage() {
		if (message != null)
			return message;
	    if (source instanceof MessageHolder && ((MessageHolder) source).getMessage() != null)
	    	return ((MessageHolder) source).getMessage();
	    return support.getMessage();
	}
	
	
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
	    return getClass().getSimpleName() + "[" + source + "]";
	}

	@Override
    public boolean isParallelizable() {
	    return source.isParallelizable() && support.isParallelizable();
    }

	@Override
    public boolean isThreadSafe() {
	    return source.isThreadSafe() && support.isThreadSafe();
    }

}
