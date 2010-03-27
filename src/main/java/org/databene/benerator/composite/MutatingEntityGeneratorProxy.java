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

import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.util.AbstractGenerator;
import org.databene.commons.Context;
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
public class MutatingEntityGeneratorProxy extends AbstractGenerator<Entity> {

    private static final Logger stateLogger = LoggerFactory.getLogger("org.databene.benerator.STATE");

    private String entityName;
    private Generator<Entity> source;
    private List<ComponentBuilder> componentBuilders;
    private Context context;
    private Entity currentEntity;

    // constructors --------------------------------------------------------------------------------------

    /**
     * @param descriptor Entity descriptor. 
     * @param componentBuilders Generators that generate values for the entities' components
     */
    public MutatingEntityGeneratorProxy(String name, ComplexTypeDescriptor descriptor, List<ComponentBuilder> componentBuilders, Context context) {
        this(name, new BlankEntityGenerator(descriptor), componentBuilders, context);
    }

    /**
     * @param name instance name for the generated entities. 
     * @param source another Generator of entities that serves as Entity builder. 
     *     It may construct empty Entities or may import them (so this may overwrite imported attributes). 
     * @param componentBuilders Generators that generate values for the entities' components
     */
    public MutatingEntityGeneratorProxy(String name, Generator<Entity> source, List<ComponentBuilder> componentBuilders, Context context) {
        this.entityName = name;
        this.source = source;
        this.componentBuilders = componentBuilders;
        this.context = context;
        this.currentEntity = null;
    }

    // Generator interface -----------------------------------------------------------------------------------
    
    public Class<Entity> getGeneratedType() {
        return Entity.class;
    }

    @Override
    public void init(GeneratorContext context) {
        source.init(context);
        for (ComponentBuilder compGen : componentBuilders)
            compGen.init(context);
        super.init(context);
    }
    
    public synchronized Entity generate() {
    	
    	currentEntity = source.generate();
        if (currentEntity == null) {
            if (stateLogger.isDebugEnabled())
                stateLogger.debug("Source for entity '" + entityName + "' is not available any more: " + source);
            return null;
        }
        
        context.set(entityName, currentEntity);
        context.set("this", currentEntity);
        
        for (ComponentBuilder componentBuilder : componentBuilders) {
            try {
                if (!componentBuilder.buildComponentFor(currentEntity)) {
                    if (stateLogger.isDebugEnabled())
                        stateLogger.debug("Component generator for entity '" + entityName + 
                        		"' is not available any more: " + componentBuilder);
                    return null;
                }
            } catch (Exception e) {
                throw new RuntimeException("Failure in generation of entity '" + entityName + "'", e);
            }
        }
    	Entity result = currentEntity;
    	currentEntity = null;
        return result;
    }

    @Override
    public void close() {
        source.close();
        for (ComponentBuilder compGen : componentBuilders)
            compGen.close();
        super.close();
    }
    
    @Override
    public void reset() {
        source.reset();
        for (ComponentBuilder compGen : componentBuilders)
            compGen.reset();
        super.reset();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
	public String toString() {
        return getClass().getSimpleName() + '[' + entityName + ']' + componentBuilders;
    }

	public boolean isParallelizable() {
	    return source.isParallelizable() && ThreadUtil.allParallelizable(componentBuilders);
    }

	public boolean isThreadSafe() {
	    return source.isThreadSafe() && ThreadUtil.allThreadSafe(componentBuilders);
    }

}
