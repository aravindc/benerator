/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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
import org.databene.commons.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Generates instances of an entity type as specified by its EntityDescriptor.<br/>
 * <br/>
 * Created: 27.06.2007 23:51:42
 * @author Volker Bergmann
 */
public class EntityGenerator implements Generator<Entity> {

    private static final Log stateLogger = LogFactory.getLog("org.databene.benerator.STATE");

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
    public EntityGenerator(ComplexTypeDescriptor descriptor, List<ComponentBuilder> componentBuilders, Context context) {
        this(descriptor, new SimpleEntityGenerator(descriptor), componentBuilders, context);
    }

    /**
     * @param descriptor Entity descriptor. 
     * @param source another Generator of entities that serves as Entity builder. 
     *     It may construct empty Entities or may import them (so this may overwrite imported attributes). 
     * @param componentBuilders Generators that generate values for the entities' components
     */
    public EntityGenerator(ComplexTypeDescriptor descriptor, Generator<Entity> source, List<ComponentBuilder> componentBuilders, Context context) {
        this.entityName = descriptor.getName();
        this.source = source;
        this.componentBuilders = componentBuilders;
        this.context = context;
        this.currentEntity = null;
    }

    // Generator interface -----------------------------------------------------------------------------------
    
    public Class<Entity> getGeneratedType() {
        return Entity.class;
    }

    public void validate() {
        source.validate();
        for (ComponentBuilder compGen : componentBuilders)
            compGen.validate();
    }

    public boolean available() {
    	if (currentEntity != null)
    		return true;
    	
        if (!source.available()) {
            if (stateLogger.isDebugEnabled())
                stateLogger.debug("Source for entity '" + entityName + "' is not available any more: " + source);
            return false;
        }
        
    	currentEntity = source.generate();
        context.set(currentEntity.getName(), currentEntity);
        
        for (ComponentBuilder compGen : componentBuilders) {
            if (!compGen.available()) {
                if (stateLogger.isDebugEnabled())
                    stateLogger.debug("Generator for entity '" + entityName + "' is not available any more: " + compGen);
                return false;
            }
        }
        return true;
    }

    public Entity generate() {
        for (ComponentBuilder componentBuilder : componentBuilders) {
            try {
                componentBuilder.buildComponentFor(currentEntity);
            } catch (Exception e) {
                throw new RuntimeException("Failure in generation of entity '" + entityName + "'", e);
            }
        }
    	Entity result = currentEntity;
    	currentEntity = null;
        return result;
    }

    public void close() {
        source.close();
        for (ComponentBuilder compGen : componentBuilders)
            compGen.close();
    }
    
    public void reset() {
        source.reset();
        for (ComponentBuilder compGen : componentBuilders)
            compGen.reset();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
	public String toString() {
        return getClass().getSimpleName() + '[' + entityName + ']' + componentBuilders;
    }

}
