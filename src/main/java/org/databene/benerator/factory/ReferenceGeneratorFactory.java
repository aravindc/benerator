/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.Generator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.TypedIterable;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.storage.StorageSystem;

/**
 * Creates generators that generate references.<br/><br/>
 * Created: 03.03.2008 15:45:50
 * @author Volker Bergmann
 */
public class ReferenceGeneratorFactory {
	
    private static final Log logger = LogFactory.getLog(ReferenceGeneratorFactory.class);

    private static DataModel dataModel = DataModel.getDefaultInstance();

    public static Generator<? extends Object> createReferenceGenerator(ReferenceDescriptor descriptor, Context context, GenerationSetup setup) {
        Generator<? extends Object> generator = null;
        TypeDescriptor typeDescriptor = descriptor.getType();
        String targetTypeName = descriptor.getTargetTye();
		ComplexTypeDescriptor targetType = (ComplexTypeDescriptor) dataModel.getTypeDescriptor(targetTypeName);
        if (targetType == null)
            throw new ConfigurationError("Type not defined: " + targetTypeName);
        else {
            String sourceName = typeDescriptor.getSource();
            if (sourceName == null)
                throw new ConfigurationError("'source' is not set for " + descriptor);
            Object sourceObject = context.get(sourceName);
            if (sourceObject instanceof StorageSystem) {
                StorageSystem sourceSystem = (StorageSystem) sourceObject;
                String selector = typeDescriptor.getSelector();
                TypedIterable<Object> entityIds = sourceSystem.queryEntityIds(targetTypeName, selector);
                generator = new IteratingGenerator<Object>(entityIds);
            } else
            	throw new ConfigurationError("Not a supported source type: " + sourceName);
        }
        generator = ComponentGeneratorFactory.createComponentGeneratorWrapper(descriptor, generator, context);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return generator;
    }

}
