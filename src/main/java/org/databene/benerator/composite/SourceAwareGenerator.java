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

import org.databene.BeneratorConstants;
import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.util.WrapperProvider;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.benerator.wrapper.ProductWrapper;
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
	
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceAwareGenerator.class);
    private static final Logger STATE_LOGGER = LoggerFactory.getLogger(BeneratorConstants.STATE_LOGGER);
    
    private String instanceName;
    private E currentInstance;
	private String message;
	private ComponentAndVariableSupport<E> support;
	private WrapperProvider<E> eWrapperProvider = new WrapperProvider<E>();
	
	/**
     * @param source another Generator of entities that serves as Entity builder. 
     *     It may construct empty Entities or may import them (so this may overwrite imported attributes). 
     * @param instanceName instance name for the generated entities. 
	 */
	public SourceAwareGenerator(String instanceName, Generator<E> source, 
			Map<String, Generator<?>> variables, List<ComponentBuilder<E>> componentBuilders, 
			GeneratorContext context) {
        super(source);
        this.instanceName = instanceName;
        this.support = new ComponentAndVariableSupport<E>(instanceName, variables, componentBuilders, context);
		this.context = context;
	}
	
	// Generator implementation ----------------------------------------------------------------------------------------
	
	@Override
    public void init(GeneratorContext context) {
        support.init(context);
		super.init(context);
	}

	@Override
    public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    	ProductWrapper<E> test = getSource().generate(eWrapperProvider.get());
        if (test == null) {
			currentInstance = null;
            STATE_LOGGER.debug("Source for entity '{}' is not available any more: {}", instanceName, getSource());
            return null; // TODO v0.7 remove current instance from context?
        } 
        currentInstance = test.unwrap();
        if (instanceName != null)
        	context.set(instanceName, currentInstance);
        context.set("this", currentInstance); // TODO v0.7 BUG: array sub generators use this too, overwriting a top-level entity generator
		if (!support.apply(currentInstance)) {
			currentInstance = null;
			return null;
		}
    	LOGGER.debug("Generated {}", currentInstance);
        return wrapper.wrap(currentInstance);
	}

	@Override
    public void reset() {
		support.reset();
		super.reset();
	}

	@Override
    public void close() {
		support.close();
		super.close();
	}
	
	public String getMessage() {
		if (message != null)
			return message;
		Generator<E> source = getSource();
	    if (source instanceof MessageHolder && ((MessageHolder) source).getMessage() != null)
	    	return ((MessageHolder) source).getMessage();
	    return support.getMessage();
	}
	
	
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
	    return getClass().getSimpleName() + "[" + getSource() + "]";
	}

	@Override
    public boolean isParallelizable() {
	    return getSource().isParallelizable() && support.isParallelizable();
    }

	@Override
    public boolean isThreadSafe() {
	    return getSource().isThreadSafe() && support.isThreadSafe();
    }

}
