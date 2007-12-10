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

package org.databene.benerator.factory;

import org.databene.model.data.*;
import org.databene.model.system.System;
import org.databene.model.*;
import org.databene.benerator.*;
import org.databene.benerator.composite.EntityGenerator;
import org.databene.benerator.wrapper.*;
import org.databene.task.TaskContext;
import org.databene.commons.*;
import org.databene.platform.dbunit.DbUnitEntityIterable;
import org.databene.platform.csv.CSVEntityIterable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Creates entity generators from entity metadata.<br/>
 * <br/>
 * Created: 08.09.2007 07:45:40
 */
public class EntityGeneratorFactory extends FeatureGeneratorFactory {

    // TODO v0.3.04 separate functionality entityGen/attribGen/common

    private static final String COUNT = "count";

    private static final Log logger = LogFactory.getLog(EntityGeneratorFactory.class);

    public static Generator<Entity> createEntityGenerator(EntityDescriptor descriptor, TaskContext context) {
        Set<String> usedDetails = new HashSet<String>();
        // TODO v0.3.04 create from generator class
        // create original generator
        Generator<Entity> generator = createSourceEntityGenerator(descriptor, context, usedDetails);
        if (generator != null)
            generator = createMutatingEntityGenerator(descriptor, context, generator);
        else
            generator = createGeneratingEntityGenerator(descriptor, context);
        // create wrappers
        generator = createValidatingGenerator(descriptor, generator, usedDetails);
        generator = createLimitCountGenerator(descriptor, generator, usedDetails);
        checkUsedDetails(descriptor, usedDetails);
        return generator;
    }

    private static Generator<Entity> createLimitCountGenerator(EntityDescriptor descriptor, Generator<Entity> generator, Set<String> usedDetails) {
        if (descriptor.getCount() != null) {
            usedDetails.add(COUNT);
            generator = new NShotGeneratorProxy<Entity>(generator, descriptor.getCount());
        }
        return generator;
    }

    private static Generator<Entity> createGeneratingEntityGenerator(EntityDescriptor descriptor, TaskContext context) {
        Map<String, Generator<? extends Object>> componentGenerators = new HashMap<String, Generator<? extends Object>>();
        Collection<ComponentDescriptor> descriptors = descriptor.getComponentDescriptors();
        for (ComponentDescriptor component : descriptors) {
            if (component.getMode() != Mode.ignored) {
                Generator<? extends Object> componentGenerator = ComponentGeneratorFactory.getComponentGenerator(component, context);
                componentGenerators.put(component.getName(), componentGenerator);
            }
        }
        return new EntityGenerator(descriptor, componentGenerators);
    }

    private static Generator<Entity> createMutatingEntityGenerator(
            EntityDescriptor descriptor, TaskContext context, Generator<Entity> generator) {
        Map<String, Generator<? extends Object>> componentGenerators = new HashMap<String, Generator<? extends Object>>();
        Collection<ComponentDescriptor> descriptors = descriptor.getDeclaredComponentDescriptors();
        for (ComponentDescriptor component : descriptors) {
            if (component.getMode() != Mode.ignored) {
                Generator<? extends Object> componentGenerator = ComponentGeneratorFactory.getComponentGenerator(component, context);
                componentGenerators.put(component.getName(), componentGenerator);
            }
        }
        return new EntityGenerator(descriptor, generator, componentGenerators);
    }

    private static Generator<Entity> createSourceEntityGenerator(
            EntityDescriptor descriptor, TaskContext context, Set<String> usedDetails) {
        // if no sourceObject is specified, there's nothing to do
        String sourceName = descriptor.getSource();
        if (sourceName == null)
            return null;
        usedDetails.add(SOURCE);
        // create sourceObject generator
        Generator<Entity> generator = null;
        Object sourceObject = context.get(sourceName);
        if (sourceObject != null) {
            if (sourceObject instanceof System) {
                System system = (System) sourceObject;
                generator = new IteratingGenerator<Entity>(system.getEntities(descriptor.getName()));
            } else if (sourceObject instanceof TypedIterable) {
                generator = new IteratingGenerator((TypedIterable) sourceObject);
            } else if (sourceObject instanceof Generator) {
                generator = (Generator) sourceObject;
            } else
                throw new UnsupportedOperationException("Source type not supported: " + sourceObject.getClass());
        } else {
            if (sourceName.endsWith(".xml"))
                generator = new IteratingGenerator<Entity>(new DbUnitEntityIterable(sourceName));
            else if (sourceName.endsWith(".csv")) {
                String encoding = descriptor.getEncoding();
                if (encoding != null)
                    usedDetails.add("encoding");
                else
                    encoding = SystemInfo.fileEncoding();
                generator = new IteratingGenerator(new CSVEntityIterable(sourceName, descriptor.getName(), ',', encoding));
            } else
                throw new UnsupportedOperationException("Unknown source type: " + sourceName);
        }
        return createProxy(descriptor, generator, usedDetails);
    }

}
