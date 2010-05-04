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

package org.databene.benerator.composite;

import java.util.Map;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Assert;
import org.databene.commons.Context;
import org.databene.commons.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates variables, entity generator and context behind a Generator interface.<br/><br/>
 * Created: 30.01.2008 20:45:21
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class VariableAwareGenerator<E> implements Generator<E> {
	
    private static Logger logger = LoggerFactory.getLogger(VariableAwareGenerator.class);
    
    private Generator<E> realGenerator;
	private Map<String, NullableGenerator<?>> variables;
	private Context context;
	
	public VariableAwareGenerator(Generator<E> realGenerator, Map<String, NullableGenerator<?>> variables, Context context) {
        Assert.notNull(realGenerator, "realGenerator");
		this.realGenerator = realGenerator;
		this.variables = variables;
		this.context = context;
	}
	
	// Generator implementation ----------------------------------------------------------------------------------------
	
	public Class<E> getGeneratedType() {
		return realGenerator.getGeneratedType();
	}

	public void init(GeneratorContext context) {
        for (NullableGenerator<?> varGen : variables.values())
        	varGen.init(context);
        realGenerator.init(context);
	}
	
	public boolean wasInitialized() {
	    return realGenerator.wasInitialized();
	}

	@SuppressWarnings("unchecked")
    public E generate() {
		// initialize variables
		for (Map.Entry<String, NullableGenerator<?>> entry : variables.entrySet()) {
			NullableGenerator<?> generator = entry.getValue();
			ProductWrapper<?> productWrapper = generator.generate(new ProductWrapper());
			if (productWrapper == null) {
	        	if (logger.isDebugEnabled())
	        		logger.debug("No more available: " + generator);
	            return null;
			}
            context.set(entry.getKey(), productWrapper.product);
        }

        E entity = realGenerator.generate();
        if (entity == null)
        	return null;
        if (logger.isDebugEnabled())
        	logger.debug("Generated " + entity);
        return entity;
	}

	public void reset() {
		for (NullableGenerator<?> variable : variables.values())
			variable.reset();
		realGenerator.reset();
	}

	public void close() {
		for (NullableGenerator<?> variable : variables.values())
			variable.close();
		realGenerator.close();
        for (String variableName : variables.keySet())
            context.remove(variableName);
	}

	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
	    return getClass().getSimpleName() + "[\n"
	        + (variables.size() > 0 ? "    variables" + variables + "\n" : "")
	        + "    " + realGenerator + "\n" + "]";
	}

	public boolean isParallelizable() {
	    return realGenerator.isParallelizable() && ThreadUtil.allParallelizable(variables.values());
    }

	public boolean isThreadSafe() {
	    return realGenerator.isThreadSafe() && ThreadUtil.allThreadSafe(variables.values());
    }

}