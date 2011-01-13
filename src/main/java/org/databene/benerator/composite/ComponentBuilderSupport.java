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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.GeneratorContext;
import org.databene.commons.ConfigurationError;
import org.databene.commons.MessageHolder;
import org.databene.commons.Resettable;
import org.databene.commons.ThreadAware;
import org.databene.commons.ThreadUtil;
import org.databene.model.data.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Assembles several {@link ComponentBuilder}s and applies them to an {@link Entity}.<br/><br/>
 * Created: 29.08.2010 10:01:08
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class ComponentBuilderSupport<E> implements MessageHolder, Closeable, Resettable, ThreadAware {

    private static final Logger stateLogger = LoggerFactory.getLogger("org.databene.benerator.STATE");

    private String instanceName;
    private List<ComponentBuilder<E>> componentBuilders;
    private String message;

    // constructors --------------------------------------------------------------------------------------

    /**
     * @param componentBuilders Generators that generate values for the entities' components
     */
    public ComponentBuilderSupport(List<ComponentBuilder<E>> componentBuilders) {
        this.componentBuilders = (componentBuilders != null ? componentBuilders : new ArrayList<ComponentBuilder<E>>());
    }

	public String getMessage() {
	    return message;
    }

    // Generator interface -----------------------------------------------------------------------------------
    
    public void init(GeneratorContext context) {
        for (ComponentBuilder<E> compGen : componentBuilders) {
            try {
	            compGen.init(context);
            } catch (RuntimeException e) {
	            throw new ConfigurationError("Error initializing component builder: " + compGen, e);
            }
        }
    }
    
	public boolean buildComponents(E target) {
	    for (ComponentBuilder<E> componentBuilder : componentBuilders) {
            try {
                if (!componentBuilder.buildComponentFor(target)) {
                	message = "Component generator for entity '" + instanceName + 
                		"' is not available any more: " + componentBuilder;
                    stateLogger.debug(message);
                    return false;
                }
            } catch (Exception e) {
                throw new RuntimeException("Failure in generation of entity '" + instanceName + "', " +
                		"Failed component builder: " + componentBuilder, e);
            }
        }
        return true;
    }

    public void close() {
        for (ComponentBuilder<E> compGen : componentBuilders)
            compGen.close();
    }
    
    public void reset() {
        for (ComponentBuilder<E> compGen : componentBuilders)
            compGen.reset();
    }

	public boolean isParallelizable() {
	    return ThreadUtil.allParallelizable(componentBuilders);
    }

	public boolean isThreadSafe() {
	    return ThreadUtil.allThreadSafe(componentBuilders);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
	public String toString() {
        return getClass().getSimpleName() + '[' + instanceName + ']' + componentBuilders;
    }

}
