/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.engine.DynamicInstanceSequenceGenerator;
import org.databene.benerator.primitive.IncrementGenerator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.wrapper.*;
import org.databene.commons.Expression;
import org.databene.commons.expression.ExpressionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates entity generators from entity metadata.<br/>
 * <br/>
 * Created: 08.09.2007 07:45:40
 * @author Volker Bergmann
 */
public class InstanceGeneratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(InstanceGeneratorFactory.class);

    // protected constructor for preventing instantiation --------------------------------------------------------------
    
    protected InstanceGeneratorFactory() {}

    public static Generator<? extends Object> createInstanceGenerator(InstanceDescriptor descriptor, BeneratorContext context) {
        Generator<? extends Object> generator = createSingleInstanceGenerator(descriptor, context);
        generator = createInstanceGeneratorWrapper(descriptor, generator, context);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return generator;
    }
    
    // protected helpers for child classes -----------------------------------------------------------------------------

    protected static Generator<? extends Object> createSingleInstanceGenerator(
            InstanceDescriptor descriptor, BeneratorContext context) {
        Generator<? extends Object> generator = null;
        // create a source generator
        generator = createNullQuotaOneGenerator(descriptor);
        if (generator == null) {
            boolean unique = (descriptor instanceof IdDescriptor || DescriptorUtil.isUnique(descriptor));
            TypeDescriptor type = descriptor.getType();
            if (type instanceof SimpleTypeDescriptor)
				generator = SimpleTypeGeneratorFactory.createSimpleTypeGenerator(
						(SimpleTypeDescriptor) type, false, unique, context);
            else if (type instanceof ComplexTypeDescriptor)
        		generator = ComplexTypeGeneratorFactory.createComplexTypeGenerator(
        				(ComplexTypeDescriptor) type, unique, context);
            else if (type == null) {
            	if (descriptor instanceof IdDescriptor)
    				generator = new IncrementGenerator(1);
            	else
            		throw new UnsupportedOperationException("Type of " + descriptor.getName() + " is not defined");
            } else
            	throw new UnsupportedOperationException("Not a supported descriptor type: " + type.getClass());
            generator = wrapWithNullQuota(generator, descriptor);
        }
        return generator;
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static Generator<?> createInstanceGeneratorWrapper(
            InstanceDescriptor descriptor, Generator<?> typeGenerator, BeneratorContext context) {
        Expression<Long> maxCountEx = DescriptorUtil.getMaxCount(descriptor, context);
        if (ExpressionUtil.isNull(maxCountEx))
        	return typeGenerator;
        DynamicInstanceSequenceGenerator generator = new DynamicInstanceSequenceGenerator(typeGenerator, context);
        // configure count
        Expression<Long> minCountEx = DescriptorUtil.getMinCount(descriptor, context);
		generator.setMinCount(minCountEx);
        generator.setMaxCount(maxCountEx);
        generator.setCountPrecision(DescriptorUtil.getCountPrecision(descriptor, context));
        generator.setCountDistribution(GeneratorFactoryUtil.getDistributionExpression(descriptor.getCountDistribution(), false, true, context));
        return generator;
    }
    
    public static Generator<? extends Object> createNullQuotaOneGenerator(InstanceDescriptor descriptor) {
        Double nullQuota = descriptor.getNullQuota();
        if (nullQuota != null && nullQuota.doubleValue() == 1.)
            return new ConstantGenerator<Object>(null);
        return null;
    }

    private static <T> Generator<T> wrapWithNullQuota(
            Generator<T> generator, InstanceDescriptor descriptor) {
        Double nullQuota = descriptor.getNullQuota();
        if (nullQuota != null && nullQuota > 0) {
            if (descriptor.isNullable() != null && !descriptor.isNullable())
                logger.error("nullQuota is set to " + nullQuota + " but the value is not nullable. " +
                        "Ignoring nullQuota for: " + descriptor);
            else
                generator = new NullableGenerator<T>(generator, nullQuota);
        }
        return generator;
    }

    public static Generator<? extends Object> createNullableGenerator(
    		InstanceDescriptor descriptor, BeneratorContext context) {
        if (!descriptor.overwritesParent() && descriptor.isNullable() && context.isDefaultNull()) 
            return new ConstantGenerator<Object>(null);
        return null;
    }

/*
    private static Generator createLimitCountGenerator(InstanceDescriptor descriptor, Generator generator) {
        if (descriptor.getCount() != null)
            generator = new NShotGeneratorProxy(generator, descriptor.getCount());
        return generator;
    }
*/
}
