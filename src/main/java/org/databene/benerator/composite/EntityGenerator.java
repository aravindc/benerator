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

import org.databene.model.data.Entity;
import org.databene.model.data.EntityDescriptor;
import org.databene.benerator.Generator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * Generates instances of an entity type as specified by its EntityDescriptor.<br/>
 * <br/>
 * Created: 27.06.2007 23:51:42
 */
public class EntityGenerator implements Generator<Entity> {

    private static final Log stateLogger = LogFactory.getLog("org.databene.benerator.STATE");

    private String entityName;
    private Generator<Entity> source;
    private Map<String, Generator<? extends Object>> componentGenerators;

    // constructors --------------------------------------------------------------------------------------

    /**
     * @param descriptor Entity descriptor. 
     * @param componentGenerators Generators that generate values for the entities' components
     */
    public EntityGenerator(EntityDescriptor descriptor, Map<String, Generator<? extends Object>> componentGenerators) {
        this(descriptor, new SimpleEntityGenerator(descriptor), componentGenerators);
    }

    /**
     * @param descriptor Entity descriptor. 
     * @param source another Generator of entities that serves as Entity builder. 
     *     It may construct empty Entities or may import them (so this may overwrite imported attributes). 
     * @param componentGenerators Generators that generate values for the entities' components
     */
    public EntityGenerator(EntityDescriptor descriptor, Generator<Entity> source, Map<String, Generator<? extends Object>> componentGenerators) {
        this.entityName = descriptor.getName();
        this.source = source;
        this.componentGenerators = componentGenerators;
    }

    // Generator interface -----------------------------------------------------------------------------------
    
    public Class<Entity> getGeneratedType() {
        return Entity.class;
    }

    public void validate() {
        source.validate();
        for (Generator<? extends Object> compGen : componentGenerators.values())
            compGen.validate();
    }

    public boolean available() {
        if (!source.available()) {
            if (stateLogger.isDebugEnabled())
                stateLogger.debug("Source for entity '" + entityName + "' is no more available: " + source);
            return false;
        }
        for (Generator<? extends Object> compGen : componentGenerators.values()) {
            if (!compGen.available()) {
                if (stateLogger.isDebugEnabled())
                    stateLogger.debug("Generator for entity '" + entityName + "' is no more available: " + compGen);
                return false;
            }
        }
        return true;
    }

    public Entity generate() {
        Entity instance = source.generate();
        for (Map.Entry<String, Generator<? extends Object>> entry : componentGenerators.entrySet()) {
            String componentName = entry.getKey();
            try {
                Object componentValue = entry.getValue().generate();
                instance.setComponent(componentName, componentValue);
            } catch (Exception e) {
                throw new RuntimeException("Failure in generation of component '" + componentName + "'", e);
            }
        }
        return instance;
    }

    public void close() {
        source.close();
        for (Generator<? extends Object> compGen : componentGenerators.values())
            compGen.close();
    }
    
    public void reset() {
        source.reset();
        for (Generator<? extends Object> compGen : componentGenerators.values())
            compGen.reset();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------
    
    public String toString() {
        return getClass().getSimpleName() + '[' + entityName + ']' + componentGenerators;
    }

}
