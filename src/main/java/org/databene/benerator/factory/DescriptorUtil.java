/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

import static org.databene.model.data.SimpleTypeDescriptor.*;
import static org.databene.model.data.TypeDescriptor.PATTERN;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.validation.ConstraintValidator;

import static org.databene.benerator.engine.DescriptorConstants.*;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.parser.ModelParser;
import org.databene.benerator.primitive.DynamicCountGenerator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.util.ExpressionBasedGenerator;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ConversionException;
import org.databene.commons.Converter;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.ParseException;
import org.databene.commons.StringUtil;
import org.databene.commons.TimeUtil;
import org.databene.commons.Validator;
import org.databene.commons.context.ContextAware;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.FormatFormatConverter;
import org.databene.commons.converter.String2NumberConverter;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.validator.AndValidator;
import org.databene.commons.validator.bean.BeanConstraintValidator;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.model.data.VariableHolder;
import org.databene.script.BeanSpec;
import org.databene.script.DatabeneScriptParser;
import org.databene.script.Expression;
import org.databene.script.PrimitiveType;
import org.databene.script.ScriptConverterForStrings;
import org.databene.script.expression.ConstantExpression;
import org.databene.script.expression.ExpressionUtil;
import org.databene.script.expression.MinExpression;
import org.w3c.dom.Element;

/**
 * Utility class for parsing and combining descriptor settings.<br/>
 * <br/>
 * Created at 31.12.2008 09:28:28
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class DescriptorUtil {

	private DescriptorUtil() {}

	public static Object convertType(Object sourceValue, SimpleTypeDescriptor targetType) {
		if (sourceValue == null)
			return null;
		PrimitiveType primitive = targetType.getPrimitiveType();
		if (primitive == null)
			primitive = PrimitiveType.STRING;
        Class<?> javaType = primitive.getJavaType();
        return AnyConverter.convert(sourceValue, javaType);
	}
	
    public static boolean isWrappedSimpleType(ComplexTypeDescriptor complexType) {
		List<ComponentDescriptor> components = complexType.getComponents();
		return (components.size() == 1 
				&& ComplexTypeDescriptor.__SIMPLE_CONTENT.equals(components.get(0).getName()));
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Generator<?> createConvertingGenerator(TypeDescriptor descriptor,
            Generator<?> generator, BeneratorContext context) {
        Converter<?, ?> converter = DescriptorUtil.getConverter(descriptor.getConverter(), context);
        if (converter != null) {
            if (descriptor.getPattern() != null && BeanUtil.hasProperty(converter.getClass(), PATTERN))
                BeanUtil.setPropertyValue(converter, PATTERN, descriptor.getPattern(), false);
            return WrapperFactory.applyConverter((Generator) generator, converter);
        }
        return generator;
    }

	public static Generator<?> getGeneratorByName(TypeDescriptor descriptor, BeneratorContext context) {
    	try {
	        Generator<?> generator = null;
	        String generatorSpec = descriptor.getGenerator();
	        if (generatorSpec != null) {
	        	if (generatorSpec.startsWith("{") && generatorSpec.endsWith("}"))
	        		generatorSpec = generatorSpec.substring(1, generatorSpec.length() - 1);
	        	BeanSpec generatorBeanSpec = DatabeneScriptParser.resolveBeanSpec(generatorSpec, context);
	        	generator = (Generator<?>) generatorBeanSpec.getBean();
	            FactoryUtil.mapDetailsToBeanProperties(descriptor, generator, context);
	            if (generatorBeanSpec.isReference())
	            	generator = WrapperFactory.preventClosing(generator);
	        }
	        return generator;
    	} catch (ParseException e) {
    		throw new ConfigurationError("Error in generator spec", e);
    	}
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Validator getValidator(String validatorSpec, BeneratorContext context) {
		try {
	        if (StringUtil.isEmpty(validatorSpec))
	            return null;
	        
	        Validator result = null;
	        Expression[] beanExpressions = DatabeneScriptParser.parseBeanSpecList(validatorSpec);
			Object[] beans = ExpressionUtil.evaluateAll(beanExpressions, context);
	        for (Object bean : beans) {
	        	// check validator type
	        	Validator validator;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Converter getConverter(String converterSpec, BeneratorContext context) {
        try {
	        if (StringUtil.isEmpty(converterSpec))
	            return null;
	        
	        Converter result = null;
	        Expression[] beanExpressions = DatabeneScriptParser.parseBeanSpecList(converterSpec);
	        Object[] beans = ExpressionUtil.evaluateAll(beanExpressions, context);
	        for (Object bean : beans) {
	        	Converter converter;
	            if (bean instanceof java.text.Format)
	            	converter = new FormatFormatConverter(Object.class, (java.text.Format) bean, false);
	            else if (bean instanceof Converter)
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

	public static DateFormat getPatternAsDateFormat(TypeDescriptor descriptor) {
        String pattern = descriptor.getPattern();
        if (pattern != null)
            return new SimpleDateFormat(pattern);
        else
        	return TimeUtil.createDefaultDateFormat();
    }

	public static Uniqueness getUniqueness(InstanceDescriptor descriptor, BeneratorContext context) {
    	if (descriptor instanceof IdDescriptor)
    		return Uniqueness.ORDERED;
    	else if (isUnique(descriptor, context))
    		return Uniqueness.SIMPLE;
    	else
    		return Uniqueness.NONE;
    }

	public static boolean isUnique(InstanceDescriptor descriptor, BeneratorContext context) {
        Boolean unique = descriptor.isUnique();
        if (unique == null)
            unique = context.getGeneratorFactory().defaultUnique();
        return unique;
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

    public static Expression<Long> getMinCount(InstanceDescriptor descriptor) {
    	return getMinCount(descriptor, 1L);
    }
    
    @SuppressWarnings("unchecked")
    public static Expression<Long> getMinCount(InstanceDescriptor descriptor, Long defaultMin) {
    	Expression<Long> result = null;
		if (descriptor.getCount() != null)
			result = descriptor.getCount();
		else if (descriptor.getMinCount() != null)
        	result = descriptor.getMinCount();
		else if (defaultMin != null)
			result = new ConstantExpression<Long>(defaultMin);
		else
			return new ConstantExpression<Long>(null);
		Expression<Long> globalMaxCount = getGlobalMaxCount();
		if (!ExpressionUtil.isNull(globalMaxCount))
			result = new MinExpression<Long>(result, globalMaxCount);
        return result;
	}

    @SuppressWarnings("unchecked")
    public static Expression<Long> getMaxCount(InstanceDescriptor descriptor, Long defaultMax) {
    	Expression<Long> result = null;
		if (descriptor.getCount() != null)
			result = descriptor.getCount();
		else if (descriptor.getMaxCount() != null)
        	result = descriptor.getMaxCount();
		else if (descriptor instanceof ComponentDescriptor && defaultMax != null)			
			result = new ConstantExpression<Long>(defaultMax);
		else
			return getGlobalMaxCount();
		Expression<Long> globalMaxCount = getGlobalMaxCount();
		if (!ExpressionUtil.isNull(globalMaxCount))
			result = new MinExpression<Long>(result, globalMaxCount);
        return result;
	}

	private static Expression<Long> getGlobalMaxCount() {
		return new GlobalMaxCountExpression();
    }

	public static Expression<Long> getCountGranularity(InstanceDescriptor descriptor) {
		return (descriptor.getCountGranularity() != null ? 
					descriptor.getCountGranularity() : 
					new ConstantExpression<Long>(1L));
	}

	public static Converter<String, String> createStringScriptConverter(BeneratorContext context) {
		Converter<String, String> scriptConverter = new ConverterChain<String, String>(
				new ScriptConverterForStrings(context),
				new ToStringConverter(null)
			);
		return scriptConverter;
	}

	public static Generator<Long> createDynamicCountGenerator(final InstanceDescriptor descriptor, 
			Long defaultMin, Long defaultMax, boolean resetToMin, BeneratorContext context) {
    	Expression<Long> count = DescriptorUtil.getCount(descriptor);
    	if (count != null)
    		return new ExpressionBasedGenerator<Long>(count, Long.class);
    	else {
			final Expression<Long> minCount = DescriptorUtil.getMinCount(descriptor, defaultMin);
			final Expression<Long> maxCount = DescriptorUtil.getMaxCount(descriptor, defaultMax);
			if (minCount.isConstant() && maxCount.isConstant() && descriptor.getCountDistribution() == null) {
				Long minCountValue = minCount.evaluate(context);
				Long maxCountValue = maxCount.evaluate(context);
				if (NullSafeComparator.equals(minCountValue, maxCountValue))
					return new ConstantGenerator<Long>(minCountValue);
			}
			final Expression<Long> countGranularity = DescriptorUtil.getCountGranularity(descriptor);
			final Expression<Distribution> countDistribution = 
				FactoryUtil.getDistributionExpression(descriptor.getCountDistribution(), Uniqueness.NONE, true);
			return new DynamicCountGenerator(minCount, maxCount, countGranularity, countDistribution, 
							ExpressionUtil.constant(false), resetToMin);
    	}
    }

    public static <T extends Number> T getNumberDetail(SimpleTypeDescriptor descriptor, String detailName, Class<T> targetType) {
        try {
            String detailValue = (String) descriptor.getDetailValue(detailName);
            return (detailValue != null ? new String2NumberConverter<T>(targetType).convert(detailValue) : null);
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

	public static void parseComponentConfig(Element element, TypeDescriptor type, BeneratorContext context) {
		// parse child elements
		ModelParser parser = new ModelParser(context);
		int valueCount = 0;
		for (Element child : XMLUtil.getChildElements(element)) {
			String childType = XMLUtil.localName(child);
			if (EL_VARIABLE.equals(childType))
				parser.parseVariable(child, (VariableHolder) type);
			else if (COMPONENT_TYPES.contains(childType))
				parser.parseComponent(child, (ComplexTypeDescriptor) type);
			else if (EL_VALUE.equals(childType))
				parser.parseSimpleTypeArrayElement(child, (ArrayTypeDescriptor) type, valueCount++);
		}
	}

	public static boolean isNullable(InstanceDescriptor descriptor, BeneratorContext context) {
		Boolean nullable = descriptor.isNullable();
		if (nullable != null)
			return nullable;
		Double nullQuota = descriptor.getNullQuota();
		if (nullQuota != null && nullQuota > 0)
			return true;
		TypeDescriptor typeDescriptor = descriptor.getTypeDescriptor();
		if (descriptor.getNullQuota() == null && typeDescriptor != null) {
			// if nullability is not specified, but a source or generator, then do not generate nulls
			if (typeDescriptor.getSource() != null || typeDescriptor.getGenerator() != null)
				return false;
		}
		return context.getDefaultsProvider().defaultNullable();
	}

	public static boolean shouldNullifyEachNullable(
			InstanceDescriptor descriptor, BeneratorContext context) {
		// nullQuota == 1?
        Double nullQuota = descriptor.getNullQuota();
		if (nullQuota != null && nullQuota.doubleValue() == 1.)
			return true;
		// nullable?
		Boolean nullable = descriptor.isNullable();
		if (nullable != null && nullable == false) // nullable defaults to true
			return false;
		if (context.getDefaultsProvider().defaultNullQuota() < 1) 
			return false; // if the factory requires nullification, it overrides the context setting
		return (!descriptor.overwritesParent() && context.isDefaultNull());
	}

	// helpers ---------------------------------------------------------------------------------------------------------
	
	protected static <T> Generator<T> wrapWithProxy(Generator<T> generator, TypeDescriptor descriptor) {
		generator = processOffset(generator, descriptor);
		generator = processCyclic(generator, descriptor);
		return generator;
    }

	public static <T> Generator<T> processCyclic(Generator<T> generator,
			TypeDescriptor descriptor) {
		boolean cyclic = descriptor.isCyclic() != null && descriptor.isCyclic().booleanValue();
		if (cyclic)
			generator = WrapperFactory.applyCycler(generator);
		return generator;
	}

	public static <T> Generator<T> processOffset(Generator<T> generator, TypeDescriptor descriptor) {
		int offset = getOffset(descriptor);
		if (offset > 0)
			generator = WrapperFactory.applyOffset(generator, offset);
		return generator;
	}

	protected static int getOffset(TypeDescriptor descriptor) {
		Integer offset = descriptor.getOffset();
		return (offset != null ? offset : 0);
	}

    protected static Integer getMinLength(SimpleTypeDescriptor descriptor) {
        Integer minLength = descriptor.getMinLength();
        if (minLength == null)
            minLength = 0;
        return minLength;
    }

    protected static Integer getMaxLength(SimpleTypeDescriptor descriptor, DefaultsProvider defaultsProvider) {
        // evaluate max length
        Integer maxLength = (Integer) descriptor.getDeclaredDetailValue(MAX_LENGTH);
        if (maxLength == null) {
            // maxLength was not set in this descriptor, so check the default value 
            maxLength = descriptor.getMaxLength();
            if (maxLength == null)
                maxLength = defaultsProvider.defaultMaxLength();
        }
        return maxLength;
    }

    static class GlobalMaxCountExpression implements Expression<Long> {
		public boolean isConstant() {
			return true;
		}
		
		public Long evaluate(Context context) {
            return ((BeneratorContext) context).getMaxCount();
        }
	}

	public static Generator<?> createNullQuotaOneGenerator(InstanceDescriptor descriptor, BeneratorContext context) {
		// check if nullQuota is 1
        Double nullQuota = descriptor.getNullQuota();
        if (nullQuota != null && nullQuota.doubleValue() == 1.)
            return MetaGeneratorFactory.createNullGenerator(descriptor.getTypeDescriptor(), context);
        else
        	return null;
	}

	public static TypeDescriptor deriveType(String name, TypeDescriptor parentType) {
		if (parentType instanceof SimpleTypeDescriptor)
			return new SimpleTypeDescriptor(name, parentType.getProvider(), (SimpleTypeDescriptor) parentType);
		else if (parentType instanceof ComplexTypeDescriptor)
			return new ComplexTypeDescriptor(name, parentType.getProvider(), (ComplexTypeDescriptor) parentType);
		else if (parentType instanceof ArrayTypeDescriptor)
			return new ArrayTypeDescriptor(name, parentType.getProvider(), (ArrayTypeDescriptor) parentType);
		else
			throw new UnsupportedOperationException("Cannot derive child type from " + parentType.getClass());
	}

}
