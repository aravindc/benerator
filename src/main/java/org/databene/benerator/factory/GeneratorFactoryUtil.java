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

import java.beans.PropertyDescriptor;
import java.text.ParseException;

import org.databene.benerator.distribution.AttachedWeight;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.FeatureWeight;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.expression.DistributedNumberExpression;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.StringUtil;
import org.databene.commons.expression.ConstantExpression;
import org.databene.model.data.FeatureDescriptor;
import org.databene.model.data.FeatureDetail;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.storage.StorageSystem;

/**
 * Provides utility methods for Generator factories.<br/><br/>
 * Created: 08.03.2008 09:39:05
 * @author Volker Bergmann
 */
public class GeneratorFactoryUtil {

    public static void mapDetailsToBeanProperties(FeatureDescriptor descriptor, Object bean, Context context) {
        for (FeatureDetail<? extends Object> detail : descriptor.getDetails())
            mapDetailToBeanProperty(descriptor, detail.getName(), bean, context);
    }

    public static void mapDetailToBeanProperty(FeatureDescriptor descriptor, String detailName, Object bean, Context context) {
        Object detailValue = descriptor.getDetailValue(detailName);
        if (detailValue instanceof Expression)
        	detailValue = ((Expression<?>) detailValue).evaluate(context); // TODO is it OK to always evaluate the expression?
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
        
    public static Expression<Long> getCountExpression(final InstanceDescriptor descriptor) {
    	Expression<Long> count = DescriptorUtil.getCount(descriptor);
    	if (count != null)
    		return count;
    	else {
			final Expression<Long> min = DescriptorUtil.getMinCount(descriptor);
			final Expression<Long> max = DescriptorUtil.getMaxCount(descriptor);
			final Expression<Long> prec = DescriptorUtil.getCountPrecision(descriptor);
			final Expression<Long> distSpecExpr = new Expression<Long>() {

				public Long evaluate(Context context) {
					// TODO this sucks!!!
					Long minVal = min.evaluate(context);
					Long maxVal = max.evaluate(context);
					if (minVal.equals(maxVal))
						return minVal;
					String distSpec = descriptor.getCountDistribution();
	                Distribution d = getDistribution(distSpec, false, true, (BeneratorContext) context);
	    			return new DistributedNumberExpression(new ConstantExpression<Distribution>(d), min, max, prec).evaluate(context);
                }
				
			};
			return distSpecExpr;
    	}
    }

    /**
     * Extracts distribution information from the descriptor.
     * @param spec the textual representation of the distribution
     * @param unique tells if a unique distribution is requested
     * @param required if set the method will never return null
     * @param context the {@link BeneratorContext}
     * @return a distribution that reflects the descriptor setup, null if distribution info is not found nor required.
     */
    @SuppressWarnings("unchecked")
    public static Distribution getDistribution(String spec, boolean unique, boolean required, BeneratorContext context) {
        
        // handle absence of distribution spec
        if (StringUtil.isEmpty(spec)) {
        	if (unique)
        		return Sequence.BIT_REVERSE;
        	else if (required)
        		return Sequence.RANDOM;
        	else
        		return null;
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
        Distribution result = Sequence.getInstance(spec, false);
        if (result != null)
        	return result;

        // check for explicit construction
    	try {
	        Expression<?> beanEx = BeneratorScriptParser.parseBeanSpec(spec);
	        return (Distribution) beanEx.evaluate(context);
        } catch (ParseException e) {
        	throw new ConfigurationError("Error parsing distribution spec: " + spec);
        }
	}

    
    public static Expression<Distribution> getDistributionExpression(
    		final String spec, final boolean unique, final boolean required, final BeneratorContext context) {
    	return new Expression<Distribution>() {

			public Distribution evaluate(Context context) {
	            return getDistribution(spec, unique, required, (BeneratorContext) context);
            }
    		
    	};
    }
    
}
