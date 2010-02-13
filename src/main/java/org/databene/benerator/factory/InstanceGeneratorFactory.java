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

package org.databene.benerator.factory;

import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.benerator.*;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.primitive.IncrementGenerator;
import org.databene.benerator.sample.ConstantGenerator;

/**
 * Creates entity generators from entity metadata.<br/>
 * <br/>
 * Created: 08.09.2007 07:45:40
 * @author Volker Bergmann
 */
public class InstanceGeneratorFactory {

    // protected constructor for preventing instantiation --------------------------------------------------------------
    
    protected InstanceGeneratorFactory() {}

    public static Generator<?> createSingleInstanceGenerator(
            InstanceDescriptor descriptor, BeneratorContext context) {
        Generator<?> generator = null;
        // create a source generator
        generator = createNullQuotaOneGenerator(descriptor);
        if (generator == null) {
            boolean unique = (descriptor instanceof IdDescriptor || DescriptorUtil.isUnique(descriptor));
            TypeDescriptor type = descriptor.getTypeDescriptor();
            if (type instanceof SimpleTypeDescriptor)
				generator = SimpleTypeGeneratorFactory.createSimpleTypeGenerator(
						(SimpleTypeDescriptor) type, false, unique, context);
            else if (type instanceof ComplexTypeDescriptor)
        		generator = ComplexTypeGeneratorFactory.createComplexTypeGenerator(descriptor.getName(),
        				(ComplexTypeDescriptor) type, unique, context);
            else if (type == null) {
            	if (descriptor instanceof IdDescriptor)
    				generator = new IncrementGenerator(1);
            	else
            		throw new UnsupportedOperationException("Type of " + descriptor.getName() + " is not defined");
            } else
            	throw new UnsupportedOperationException("Not a supported descriptor type: " + type.getClass());
        }
        return generator;
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    public static Generator<?> createNullQuotaOneGenerator(InstanceDescriptor descriptor) {
        Double nullQuota = descriptor.getNullQuota();
        if (nullQuota != null && nullQuota.doubleValue() == 1.)
            return new ConstantGenerator<Object>(null);
        return null;
    }

    public static Generator<?> createNullableGenerator(
    		InstanceDescriptor descriptor, BeneratorContext context) {
        if (!descriptor.overwritesParent() && descriptor.isNullable() && context.isDefaultNull()) 
            return new ConstantGenerator<Object>(null);
        return null;
    }

}
