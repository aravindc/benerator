/*
 * (c) Copyright 2008-2013 by Volker Bergmann. All rights reserved.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.databene.benerator.Generator;
import org.databene.benerator.StorageSystem;
import org.databene.benerator.distribution.AttachedWeight;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.FeatureWeight;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.primitive.ScriptGenerator;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.LocaleUtil;
import org.databene.commons.ParseException;
import org.databene.commons.StringUtil;
import org.databene.model.data.FeatureDescriptor;
import org.databene.model.data.FeatureDetail;
import org.databene.model.data.Uniqueness;
import org.databene.regex.RegexParser;
import org.databene.script.DatabeneScriptParser;
import org.databene.script.Expression;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;
import org.databene.script.WeightedSample;
import org.databene.script.expression.DynamicExpression;

import static org.databene.benerator.engine.DescriptorConstants.*;

/**
 * Provides utility methods for Generator factories.<br/><br/>
 * Created: 08.03.2008 09:39:05
 * @author Volker Bergmann
 */
public class FactoryUtil {
	
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
	public static Distribution getDistribution(
			String spec, Uniqueness uniqueness, boolean required, BeneratorContext context) {
        
        // handle absence of distribution spec
        if (StringUtil.isEmpty(spec))
        	if (required)
        		return context.getGeneratorFactory().defaultDistribution(uniqueness);
        	else
        		return null;
        
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
	        Expression beanEx = DatabeneScriptParser.parseBeanSpec(spec);
	        return (Distribution) beanEx.evaluate(context);
        } catch (ParseException e) {
        	throw new ConfigurationError("Error parsing distribution spec: " + spec);
        }
	}

    
    public static Expression<Distribution> getDistributionExpression(
    		final String spec, final Uniqueness uniqueness, final boolean required) {
    	return new DynamicExpression<Distribution>() {

			@Override
			public Distribution evaluate(Context context) {
	            return getDistribution(spec, uniqueness, required, (BeneratorContext) context);
            }
    		
    	};
    }

    public static Set<Character> fullLocaleCharSet(String pattern, Locale locale) {
        Set<Character> chars;
        if (pattern != null) {
            try {
                chars = RegexParser.toCharSet(new RegexParser(locale).parseSingleChar(pattern)).getSet();
            } catch (ParseException e) {
                throw new ConfigurationError("Invalid regular expression.", e);
            }
        } else
            chars = LocaleUtil.letters(locale);
        return chars;
    }

	public static Locale defaultLocale() {
		return Locale.getDefault();
	}

	public static <T> List<T> extractValues(Collection<WeightedSample<T>> samples) {
		List<T> values = new ArrayList<T>(samples.size());
		Iterator<WeightedSample<T>> iterator = samples.iterator();
    	while (iterator.hasNext())
    		values.add(iterator.next().getValue());
		return values;
	}

    public static Generator<?> createScriptGenerator(String scriptText) {
        Script script = ScriptUtil.parseScriptText(scriptText);
        return new ScriptGenerator(script);
    }

}
