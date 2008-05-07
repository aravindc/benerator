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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.benerator.Generator;
import org.databene.benerator.composite.ComponentGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import static org.databene.benerator.factory.GeneratorFactoryUtil.*;

/**
 * Creates generators that generate entity components.<br/>
 * <br/>
 * Created: 14.10.2007 22:16:34
 * @author Volker Bergmann
 */
public class ComponentGeneratorFactory extends InstanceGeneratorFactory {
    
    protected ComponentGeneratorFactory() { }

    private static final Log logger = LogFactory.getLog(ComponentGeneratorFactory.class);
    
    // factory methods for component generators ------------------------------------------------------------------------
    
    public static Generator<? extends Object> createComponentGenerator(
            ComponentDescriptor descriptor, Context context, GenerationSetup setup) {
        if (logger.isDebugEnabled())
            logger.debug("createComponentGenerator(" + descriptor.getName() + ')');
        if (descriptor instanceof PartDescriptor)
            return PartGeneratorFactory.createPartGenerator((PartDescriptor)descriptor, context, setup);
        else if (descriptor instanceof ReferenceDescriptor)
            return ReferenceGeneratorFactory.createReferenceGenerator((ReferenceDescriptor)descriptor, context, setup);
        else if (descriptor instanceof IdDescriptor)
            return IdGeneratorFactory.createIdGenerator((IdDescriptor)descriptor, context);
        else 
            throw new ConfigurationError("Unsupported element: " + descriptor.getClass());
    }

    protected static Generator<Object> createComponentGeneratorWrapper(
            ComponentDescriptor descriptor, Generator<? extends Object> elementGenerator, Context context) {
        ComponentGenerator wrapper = new ComponentGenerator(elementGenerator);
        mapDetailsToBeanProperties(descriptor, wrapper, context);
        wrapper.setMaxCount(getMaxCount(descriptor));
        wrapper.setMinCount(getMinCount(descriptor));
        return (Generator<Object>) wrapper;
    }

	private static long getMaxCount(ComponentDescriptor descriptor) {
		if (descriptor.getCount() != null)
			return descriptor.getCount();
        if (descriptor.getMaxCount() != null)
        	return descriptor.getMaxCount();
        return 1;
	}

	private static long getMinCount(ComponentDescriptor descriptor) {
		if (descriptor.getCount() != null)
			return descriptor.getCount();
        if (descriptor.getMinCount() != null)
        	return descriptor.getMinCount();
        return 1;
	}

}
