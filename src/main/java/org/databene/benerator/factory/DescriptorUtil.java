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

import static org.databene.model.data.TypeDescriptor.LOCALE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.validation.ConstraintValidator;

import static org.databene.benerator.factory.GeneratorFactoryUtil.mapDetailsToBeanProperties;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.wrapper.CyclicGeneratorProxy;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.commons.Expression;
import org.databene.commons.LocaleUtil;
import org.databene.commons.ParseException;
import org.databene.commons.StringUtil;
import org.databene.commons.TimeUtil;
import org.databene.commons.Validator;
import org.databene.commons.context.ContextAware;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.FormatFormatConverter;
import org.databene.commons.expression.ConstantExpression;
import org.databene.commons.expression.DynamicExpression;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.commons.expression.MinExpression;
import org.databene.commons.validator.AndValidator;
import org.databene.commons.validator.bean.BeanConstraintValidator;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.TypeDescriptor;

/**
 * Utility class for parsing and combining descriptor settings.<br/>
 * <br/>
 * Created at 31.12.2008 09:28:28
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class DescriptorUtil {

	private DescriptorUtil() {}

    public static boolean isWrappedSimpleType(ComplexTypeDescriptor complexType) {
		List<ComponentDescriptor> components = complexType.getComponents();
		return (components.size() == 1 
				&& ComplexTypeDescriptor.__SIMPLE_CONTENT.equals(components.get(0).getName()));
	}

    @SuppressWarnings("unchecked")
	public static Generator<?> getGeneratorByName(TypeDescriptor descriptor, BeneratorContext context) {
    	try {
	        Generator<?> generator = null;
	        String generatorSpec = descriptor.getGenerator();
	        if (generatorSpec != null) {
	        	generator = (Generator) BeneratorScriptParser.parseBeanSpec(generatorSpec).evaluate(context);
	            mapDetailsToBeanProperties(descriptor, generator, context);
	        }
	        return generator;
    	} catch (ParseException e) {
    		throw new ConfigurationError("Error in generator spec", e);
    	}
    }

    @SuppressWarnings("unchecked")
	public static Validator getValidator(TypeDescriptor descriptor, BeneratorContext context) {
		try {
	        String validatorSpec = descriptor.getValidator();
	        if (StringUtil.isEmpty(validatorSpec))
	            return null;
	        
	        Validator result = null;
	        Expression[] beanExpressions = BeneratorScriptParser.parseBeanSpecList(validatorSpec);
			Object[] beans = ExpressionUtil.evaluateAll(beanExpressions, context);
	        for (Object bean : beans) {
	        	// check validator type
	        	Validator<?> validator;
	        	if (bean instanceof Validator)
	        		validator = (Validator<?>) bean;
	        	else if (bean instanceof ConstraintValidator)
	        		validator = new BeanConstraintValidator((ConstraintValidator) bean);
	        	else
	        		throw new ConfigurationError("Unknown validator type: " + BeanUtil.simpleClassName(bean));
	        	
	        	// compose one or more validators
	        	if (result == null) // if it is the first or even only validator, simply use it
	        		result = validator;
	        	else if (result instanceof AndValidator) // else compose all validators to an AndValidator
	        		((AndValidator) result).add(validator);
	        	else
	        		result = new AndValidator(result, validator);
	        }
	        return result;
        } catch (ParseException e) {
        	throw new ConfigurationError("Invalid validator definition", e);
        }
    }

	@SuppressWarnings("unchecked")
	public static Converter getConverter(TypeDescriptor descriptor, BeneratorContext context) {
        String converterSpec = descriptor.getConverter();
        try {
	        if (StringUtil.isEmpty(converterSpec))
	            return null;
	        
	        Converter result = null;
	        Expression[] beanExpressions = BeneratorScriptParser.parseBeanSpecList(converterSpec);
	        Object[] beans = ExpressionUtil.evaluateAll(beanExpressions, context);
	        for (Object bean : beans) {
	        	Converter converter;
	            if (bean instanceof java.text.Format)
	            	converter = new FormatFormatConverter(Object.class, (java.text.Format) bean, false);
	            if (bean instanceof Converter)
	            	converter = (Converter) bean;
	            else
	            	throw new ConfigurationError(bean + " is not an instance of " + Converter.class);
	            if (converter instanceof ContextAware)
	            	((ContextAware) converter).setContext(context);
	            
	        	if (result == null)
	        		result = converter;
	        	else if (result instanceof ConverterChain)
	        		((ConverterChain) result).addComponent(converter);
	        	else
	        		result = new ConverterChain(result, converter);
	        }
	        return result;
        } catch (ParseException e) {
        	throw new ConfigurationError("Error parsing converter spec: " + converterSpec, e);
        }
    }

	public static Locale getLocale(TypeDescriptor descriptor) {
        Locale locale = descriptor.getLocale();
        if (locale == null)
            locale = (Locale) descriptor.getDetailDefault(LOCALE);
        if (locale == null)
            locale = LocaleUtil.getFallbackLocale();
        return locale;
    }

	public static DateFormat getPatternAsDateFormat(TypeDescriptor descriptor) {
        String pattern = descriptor.getPattern();
        if (pattern != null)
            return new SimpleDateFormat(pattern);
        else
        	return TimeUtil.createDefaultDateFormat();
    }

	public static boolean isUnique(InstanceDescriptor descriptor) {
        Boolean unique = descriptor.isUnique();
        if (unique == null)
            unique = false;
        return unique;
    }
	
	public static Expression<Boolean> getUniqueness(final InstanceDescriptor descriptor) {
		return new DynamicExpression<Boolean>() {
			public Boolean evaluate(Context context) {
				return isUnique(descriptor);
            }
		};
    }

    public static double getNullQuota(InstanceDescriptor descriptor) {
        Double nullQuota = descriptor.getNullQuota();
        if (nullQuota == null)
            nullQuota = 0.;
        return nullQuota;
    }
    
	public static <T> Generator<T> wrapWithProxy(Generator<T> generator, TypeDescriptor descriptor) {
		boolean cyclic = descriptor.isCyclic() != null && descriptor.isCyclic().booleanValue();
		return wrapWithProxy(generator, cyclic);
    }

	public static <T> Generator<T> wrapWithProxy(Generator<T> generator, boolean cyclic) {
	    return (cyclic ? new CyclicGeneratorProxy<T>(generator) : generator);
    }

	public static char getSeparator(TypeDescriptor descriptor, BeneratorContext context) {
		char separator = (context != null ? context.getDefaultSeparator() : ',');
		if (!StringUtil.isEmpty(descriptor.getSeparator())) {
			if (descriptor.getSeparator().length() > 1)
				throw new ConfigurationError("A CSV separator must be one character, but was: " + descriptor.getSeparator());
		    separator = descriptor.getSeparator().charAt(0);
		}
		return separator;
	}
	
    /**
     * Calculates the 'count' value.
     * @return the 'count' value. If a global 'maxCount' was set too, it returns the minimum
     * of 'count' and 'maxCount'. If no 'count' value was specified, it returns null.
     */
    @SuppressWarnings("unchecked")
    public static Expression<Long> getCount(InstanceDescriptor descriptor) {
    	Expression<Long> result = descriptor.getCount();
		if (result != null) {
			Expression<Long> globalMaxCount = getGlobalMaxCount();
			if (globalMaxCount != null)
				result = new MinExpression<Long>(result, globalMaxCount);
		}
        return result;
	}

    @SuppressWarnings("unchecked")
    public static Expression<Long> getMinCount(InstanceDescriptor descriptor) {
    	Expression<Long> result = null;
		if (descriptor.getCount() != null)
			result = descriptor.getCount();
		else if (descriptor.getMinCount() != null)
        	result = descriptor.getMinCount();
		else
			result = new ConstantExpression<Long>(1L);
		Expression<Long> globalMaxCount = getGlobalMaxCount();
		if (!ExpressionUtil.isNull(globalMaxCount))
			result = new MinExpression<Long>(result, globalMaxCount);
        return result;
	}

    @SuppressWarnings("unchecked")
    public static Expression<Long> getMaxCount(InstanceDescriptor descriptor) {
    	Expression<Long> result = null;
		if (descriptor.getCount() != null)
			result = descriptor.getCount();
		else if (descriptor.getMaxCount() != null)
        	result = descriptor.getMaxCount();
		else if (descriptor instanceof ComponentDescriptor)			
			result = new ConstantExpression<Long>(1L);
		else
			return getGlobalMaxCount();
		Expression<Long> globalMaxCount = getGlobalMaxCount();
		if (!ExpressionUtil.isNull(globalMaxCount))
			result = new MinExpression<Long>(result, globalMaxCount);
        return result;
	}

	private static Expression<Long> getGlobalMaxCount() {
		return new DynamicExpression<Long>() {
			public Long evaluate(Context context) {
	            return ((BeneratorContext) context).getMaxCount();
            }
		};
    }

	public static Expression<Long> getCountPrecision(InstanceDescriptor descriptor) {
		return (descriptor.getCountPrecision() != null ? 
					descriptor.getCountPrecision() : 
					new ConstantExpression<Long>(1L));
	}
/*
	static Expression<Distribution> getCountDistribution(InstanceDescriptor descriptor) {
		return (descriptor.getCountDistribution() != null ? descriptor.getCountDistribution() : null);
	}
*/

}
