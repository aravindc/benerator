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

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.util.AbstractGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.MessageHolder;
import org.databene.commons.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Generates instances of an entity type as specified by its EntityDescriptor.<br/>
 * <br/>
 * Created: 27.06.2007 23:51:42
 * @author Volker Bergmann
 */
public class MutatingGeneratorProxy<E> extends AbstractGenerator<E> implements MessageHolder {

    private static final Logger stateLogger = LoggerFactory.getLogger("org.databene.benerator.STATE");

    private String instanceName;
    private Generator<E> source;
    private List<ComponentBuilder<E>> componentBuilders;
    private Context context;
    private E currentInstance;
    private String message;

    // constructors --------------------------------------------------------------------------------------

    /**
     * @param instanceName instance name for the generated entities. 
     * @param source another Generator of entities that serves as Entity builder. 
     *     It may construct empty Entities or may import them (so this may overwrite imported attributes). 
     * @param componentBuilders Generators that generate values for the entities' components
     */
    public MutatingGeneratorProxy(String instanceName, Generator<E> source, List<ComponentBuilder<E>> componentBuilders, Context context) {
        this.instanceName = instanceName;
        this.source = source;
        this.componentBuilders = componentBuilders;
        this.context = context;
        this.currentInstance = null;
    }

	public String getMessage() {
	    return message;
    }

    // Generator interface -----------------------------------------------------------------------------------
    
    public Class<E> getGeneratedType() {
        return source.getGeneratedType();
    }

    @Override
    public void init(GeneratorContext context) {
        source.init(context);
        for (ComponentBuilder<E> compGen : componentBuilders) {
            try {
	            compGen.init(context);
            } catch (RuntimeException e) {
	            throw new ConfigurationError("Error initializing component builder: " + compGen, e);
            }
        }
        super.init(context);
    }
    
    public synchronized E generate() {
    	
    	currentInstance = source.generate();
        if (currentInstance == null) {
        	message = "Source for entity '" + instanceName + "' is not available any more: " + source;
            stateLogger.debug(message);
            return null;
        }
        
        if (instanceName != null)
        	context.set(instanceName, currentInstance);
        context.set("this", currentInstance);
        
        for (ComponentBuilder<E> componentBuilder : componentBuilders) {
            try {
                if (!componentBuilder.buildComponentFor(currentInstance)) {
                	message = "Component generator for entity '" + instanceName + 
                		"' is not available any more: " + componentBuilder;
                    stateLogger.debug(message);
                    return null;
                }
            } catch (Exception e) {
                throw new RuntimeException("Failure in generation of entity '" + instanceName + "'", e);
            }
        }
    	E result = currentInstance;
    	currentInstance = null;
        return result;
    }

    @Override
    public void close() {
        source.close();
        for (ComponentBuilder<E> compGen : componentBuilders)
            compGen.close();
        super.close();
    }
    
    @Override
    public void reset() {
        source.reset();
        for (ComponentBuilder<E> compGen : componentBuilders)
            compGen.reset();
        super.reset();
    }

	public boolean isParallelizable() {
	    return source.isParallelizable() && ThreadUtil.allParallelizable(componentBuilders);
    }

	public boolean isThreadSafe() {
	    return source.isThreadSafe() && ThreadUtil.allThreadSafe(componentBuilders);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
	public String toString() {
        return getClass().getSimpleName() + '[' + instanceName + ']' + componentBuilders;
    }

}
