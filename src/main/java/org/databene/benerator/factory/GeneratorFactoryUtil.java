/*
 * (c) Copyright 2008-2010 by Volker Bergmann. All rights reserved.
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

import java.beans.PropertyDescriptor;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.AttachedWeight;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.FeatureWeight;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.primitive.DynamicCountGenerator;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.util.ExpressionBasedGenerator;
import org.databene.benerator.wrapper.CyclicGeneratorProxy;
import org.databene.benerator.wrapper.NShotGeneratorProxy;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.ParseException;
import org.databene.commons.StringUtil;
import org.databene.commons.expression.DynamicExpression;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.model.data.FeatureDescriptor;
import org.databene.model.data.FeatureDetail;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.model.storage.StorageSystem;
import static org.databene.benerator.engine.DescriptorConstants.*;

/**
 * Provides utility methods for Generator factories.<br/><br/>
 * Created: 08.03.2008 09:39:05
 * @author Volker Bergmann
 */
public class GeneratorFactoryUtil {

    public static void mapDetailsToBeanProperties(FeatureDescriptor descriptor, Object bean, Context context) {
        for (FeatureDetail<?> detail : descriptor.getDetails()) {
        	if (!ATT_NAME.equals(detail.getName()))
        		mapDetailToBeanProperty(descriptor, detail.getName(), bean, context);
        }
    }

    public static void mapDetailToBeanProperty(FeatureDescriptor descriptor, String detailName, Object bean, Context context) {
        Object detailValue = descriptor.getDetailValue(detailName);
        if (detailValue instanceof Expression)
        	detailValue = ((Expression<?>) detailValue).evaluate(context);
		setBeanProperty(bean, detailName, detailValue, context);
    }

    public static void setBeanProperty(Object bean, String detailName, Object detailValue, Context context) {
        if (detailValue != null && BeanUtil.hasProperty(bean.getClass(), detailName)) {
            try {
                PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(bean.getClass(), detailName);
                Class<?> propertyType = propertyDescriptor.getPropertyType();
                Object propertyValue = detailValue;
                if (detailValue instanceof String && StorageSystem.class.isAssignableFrom(propertyType))
                    propertyValue = context.get(propertyValue.toString());
                BeanUtil.setPropertyValue(bean, detailName, propertyValue, false);
            } catch (RuntimeException e) {
                throw new RuntimeException("Error setting '" + detailName + "' of class " + bean.getClass().getName(), e); 
            }
        }
    }

    /**
     * Extracts distribution information from the descriptor.
     * @param spec the textual representation of the distribution
     * @param uniqueness tells if a unique distribution is requested
     * @param required if set the method will never return null
     * @param context the {@link BeneratorContext}
     * @return a distribution that reflects the descriptor setup, null if distribution info is not found nor required.
     */
    @SuppressWarnings("rawtypes")
	public static Distribution getDistribution(String spec, Uniqueness uniqueness, boolean required, BeneratorContext context) {
        
        // handle absence of distribution spec
        if (StringUtil.isEmpty(spec)) {
        	switch (uniqueness) {
	        	case ORDERED: 	return SequenceManager.STEP_SEQUENCE;
	        	case SIMPLE: 	return SequenceManager.EXPAND_SEQUENCE;
	        	case NONE: 		if (required)
	        						return SequenceManager.RANDOM_SEQUENCE;
	        					else
	        						return null;
        	}
        }
        
        // check for context reference
        Object contextObject = context.get(spec);
        if (contextObject != null) {
        	if (contextObject instanceof Distribution)
        		return (Distribution) contextObject;
        	else
        		throw new ConfigurationError("Not a distribution: " + spec + "=" + contextObject);
        }

        // check for 'weighted' distribution
        if (spec.startsWith("weighted[") && spec.endsWith("]"))
    		return new FeatureWeight(spec.substring("weighted[".length(), spec.length() - 1).trim());
    	else if ("weighted".equals(spec))
    		return new AttachedWeight();
        
        // check for default sequence reference
        Distribution result = SequenceManager.getRegisteredSequence(spec, false);
        if (result != null)
        	return result;

        // check for explicit construction
    	try {
	        Expression beanEx = BeneratorScriptParser.parseBeanSpec(spec);
	        return (Distribution) beanEx.evaluate(context);
        } catch (ParseException e) {
        	throw new ConfigurationError("Error parsing distribution spec: " + spec);
        }
	}

    
    public static Expression<Distribution> getDistributionExpression(
    		final String spec, final Uniqueness uniqueness, final boolean required) {
    	return new DynamicExpression<Distribution>() {

			public Distribution evaluate(Context context) {
	            return getDistribution(spec, uniqueness, required, (BeneratorContext) context);
            }
    		
    	};
    }

	public static Generator<Long> getCountGenerator(final InstanceDescriptor descriptor, boolean resetToMin) {
    	Expression<Long> count = DescriptorUtil.getCount(descriptor);
    	if (count != null)
    		return new ExpressionBasedGenerator<Long>(count, Long.class);
    	else {
			final Expression<Long> minCount = DescriptorUtil.getMinCount(descriptor);
			final Expression<Long> maxCount = DescriptorUtil.getMaxCount(descriptor);
			final Expression<Long> countPrecision = DescriptorUtil.getCountPrecision(descriptor);
			final Expression<Distribution> countDistribution = 
				getDistributionExpression(descriptor.getCountDistribution(), Uniqueness.NONE, true);
			return new DynamicCountGenerator(minCount, maxCount, countPrecision, countDistribution, 
					ExpressionUtil.constant(false), resetToMin);
    	}
    }

	public static <T> Generator<T> createCyclicHeadGenerator(Generator<T> source) {
		return new CyclicGeneratorProxy<T>(new NShotGeneratorProxy<T>(source, 1));
	}

}
