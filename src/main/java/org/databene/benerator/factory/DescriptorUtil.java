/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.parser.BasicParser;
import org.databene.benerator.wrapper.CyclicGeneratorProxy;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.Expression;
import org.databene.commons.LocaleUtil;
import org.databene.commons.StringCharacterIterator;
import org.databene.commons.StringUtil;
import org.databene.commons.TimeUtil;
import org.databene.commons.Validator;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.FormatFormatConverter;
import org.databene.commons.expression.ConstantExpression;
import org.databene.commons.expression.MinExpression;
import org.databene.commons.validator.AndValidator;
import org.databene.commons.validator.bean.BeanConstraintValidator;
import org.databene.model.consumer.Consumer;
import org.databene.model.consumer.ConsumerChain;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.storage.StorageSystem;
import org.databene.model.storage.StorageSystemConsumer;

/**
 * Utility class for parsing and combining descriptor settings.<br/>
 * <br/>
 * Created at 31.12.2008 09:28:28
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class DescriptorUtil {

    private static final BasicParser basicParser = new BasicParser();
	
	private DescriptorUtil() {}

    public static boolean isWrappedSimpleType(ComplexTypeDescriptor complexType) {
		List<ComponentDescriptor> components = complexType.getComponents();
		return (components.size() == 1 
				&& ComplexTypeDescriptor.__SIMPLE_CONTENT.equals(components.get(0).getName()));
	}

    @SuppressWarnings("unchecked")
	public static Generator<? extends Object> getGeneratorByName(TypeDescriptor descriptor, BeneratorContext context) {
        Generator<? extends Object> generator = null;
        String generatorClassName = descriptor.getGenerator();
        if (generatorClassName != null) {
        	generator = (Generator) basicParser.resolveConstructionOrReference(generatorClassName, context, context);
            mapDetailsToBeanProperties(descriptor, generator, context);
        }
        return generator;
    }

    @SuppressWarnings("unchecked")
	public static Validator getValidator(TypeDescriptor descriptor, BeneratorContext context) {
        String validatorSpec = descriptor.getValidator();
        if (StringUtil.isEmpty(validatorSpec))
            return null;
        
        Validator result = null;
        StringCharacterIterator iterator = new StringCharacterIterator(validatorSpec);
        boolean done = false;
        do {
        	Object tmp = basicParser.resolveConstructionOrReference(iterator, context, context);
        	
        	// check validator type
        	Validator<?> validator;
        	if (tmp instanceof Validator)
        		validator = (Validator<?>) tmp;
        	else if (tmp instanceof ConstraintValidator)
        		validator = new BeanConstraintValidator((ConstraintValidator) tmp);
        	else
        		throw new ConfigurationError("Unknown validator type: " + String.valueOf(tmp));
        	
        	// compose one or more validators
        	if (result == null) // if it is the first or even only validator, simply use it
        		result = validator;
        	else if (result instanceof AndValidator) // else compose all validators to an AndValidator
        		((AndValidator) result).add(validator);
        	else
        		result = new AndValidator(result, validator);
        	iterator.skipWhitespace();

        	// check if we're done
        	if (!iterator.hasNext())
        		done = true;
        	else if (iterator.peekNext() != ',')
        		done = true;
        	else
        		iterator.next();
        } while (!done);
        return result;
    }

	@SuppressWarnings("unchecked")
	public static Converter getConverter(TypeDescriptor descriptor, BeneratorContext context) {
        String converterSpec = descriptor.getConverter();
        if (StringUtil.isEmpty(converterSpec))
            return null;
        StringCharacterIterator iterator = new StringCharacterIterator(converterSpec);
        Converter result = null;
        boolean done = false;
        do {
        	Converter tmp = parseSingleConverterSpec(iterator, context);
        	if (result == null)
        		result = tmp;
        	else if (result instanceof ConverterChain)
        		((ConverterChain) result).addComponent(tmp);
        	else
        		result = new ConverterChain(result, tmp);
        	iterator.skipWhitespace();
        	if (!iterator.hasNext())
        		done = true;
        	else if (iterator.peekNext() != ',')
        		done = true;
        	else
        		iterator.next();
        } while (!done);
        return result;
    }

	@SuppressWarnings("unchecked")
    public static ConsumerChain<Entity> parseConsumersSpec(String consumerSpec, BeneratorContext context) {
        if (StringUtil.isEmpty(consumerSpec))
            return null;
        StringCharacterIterator iterator = new StringCharacterIterator(consumerSpec);
        ConsumerChain<Entity> result = new ConsumerChain<Entity>();
        boolean done = false;
        do {
        	Consumer<Entity> consumer = parseSingleConsumer(iterator, true, context);
        	if (consumer != null)
        		result.addComponent(consumer);
        	iterator.skipWhitespace();
        	if (!iterator.hasNext())
        		done = true;
        	else if (iterator.peekNext() != ',')
        		done = true;
        	else
        		iterator.next();
        } while (!done);
		return result;
	}

	@SuppressWarnings("unchecked")
    private static Consumer<Entity> parseSingleConsumer(
			StringCharacterIterator consumerSpec, boolean insert, BeneratorContext context) {
		Expression expression = basicParser.parseConstructionOrReference(consumerSpec, context, context);
		Object consumer = expression.evaluate(context);
		if (consumer == null)
			throw new ConfigurationError("Consumer not found: " + consumerSpec);

		// check consumer type
		if (consumer instanceof StorageSystem)
			return new StorageSystemConsumer((StorageSystem) consumer, insert);
		else if (consumer instanceof Consumer)
			return (Consumer<Entity>) consumer;
		else
			throw new UnsupportedOperationException(
					"Consumer type not supported: " + consumer.getClass());
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
	
	@SuppressWarnings("unchecked")
    public static Expression<Long> getMinCount(InstanceDescriptor descriptor, BeneratorContext context) {
		Expression<Long> result = null;
		if (descriptor.getCount() != null)
			result = descriptor.getCount();
		else if (descriptor.getMinCount() != null)
        	result = descriptor.getMinCount();
		if (result == null)
			result = new ConstantExpression<Long>(1L);
		Long globalMaxCount = context.getMaxCount();
		if (globalMaxCount != null)
			result = new MinExpression<Long>(result, new ConstantExpression<Long>(globalMaxCount));
        return result;
	}

	@SuppressWarnings("unchecked")
    public static Expression<Long> getMaxCount(InstanceDescriptor descriptor, BeneratorContext context) {
		Expression<Long> result = null;
		if (descriptor.getCount() != null)
			result = descriptor.getCount();
		else if (descriptor.getMaxCount() != null)
        	result = descriptor.getMaxCount();
		if (result == null)
			result = new ConstantExpression<Long>(1L);
		Long globalMaxCount = context.getMaxCount();
		if (globalMaxCount != null)
			result = new MinExpression<Long>(result, new ConstantExpression<Long>(globalMaxCount));
        return result;
	}

	public static Expression<Long> getCountPrecision(InstanceDescriptor descriptor, BeneratorContext context) {
		return (descriptor.getCountPrecision() != null ? descriptor.getCountPrecision() : new ConstantExpression<Long>(1L));
	}



    // private helpers -------------------------------------------------------------------------------------------------
    
	@SuppressWarnings("unchecked")
	private static Converter parseSingleConverterSpec(StringCharacterIterator iterator, BeneratorContext context) {
		Object converter = basicParser.resolveConstructionOrReference(iterator, context, context);
        if (converter instanceof java.text.Format)
        	converter = new FormatFormatConverter(Object.class, (java.text.Format) converter);
        if (!(converter instanceof Converter))
        	throw new ConfigurationError(converter + " is not an instance of " + Converter.class);
		return (Converter) converter;
	}

}
