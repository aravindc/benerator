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

import java.lang.annotation.Annotation;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.databene.benerator.Generator;
import org.databene.benerator.csv.SequencedDatasetCSVGenerator;
import org.databene.benerator.csv.WeightedDatasetCSVGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.IndividualWeight;
import org.databene.benerator.distribution.sequence.RandomIntegerGenerator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DescriptorConstants;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.sample.WeightedCSVSampleGenerator;
import org.databene.benerator.script.BeanSpec;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.wrapper.AccessingGenerator;
import org.databene.benerator.wrapper.AlternativeGenerator;
import org.databene.benerator.wrapper.AsByteGeneratorWrapper;
import org.databene.benerator.wrapper.ByteArrayGenerator;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.StringUtil;
import org.databene.commons.Validator;
import org.databene.commons.accessor.GraphAccessor;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ArrayElementExtractor;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.ConvertingIterable;
import org.databene.commons.converter.DateString2DurationConverter;
import org.databene.commons.converter.LiteralParser;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.iterator.TypedIterableProxy;
import org.databene.commons.validator.StringLengthValidator;
import org.databene.document.csv.CSVLineIterable;
import org.databene.model.data.PrimitiveType;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.UnionSimpleTypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.model.storage.StorageSystem;
import org.databene.script.ScriptConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.databene.model.data.SimpleTypeDescriptor.*;

/**
 * Creates generators of simple types.<br/>
 * <br/>
 * @author Volker Bergmann
 */
public class SimpleTypeGeneratorFactory extends TypeGeneratorFactory {
	
    //@SuppressWarnings("unchecked")
    //private static final FeatureWeight EMPTY_WEIGHT = new FeatureWeight(null);

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public static Generator<?> createSimpleTypeGenerator(
			SimpleTypeDescriptor descriptor, boolean nullable, Uniqueness uniqueness,
			BeneratorContext context) {
        logger.debug("create({})", descriptor.getName());
        Generator<?> generator = null;
        generator = createConstantGenerator(descriptor, context);
        if (generator == null)
        	generator = createSampleGenerator(descriptor, uniqueness, context);
        if (generator == null)
        	generator = createConstructiveGenerator(descriptor, uniqueness, context);
		if (generator == null && nullable) {
	        Class<?> javaType = descriptor.getPrimitiveType().getJavaType();
			generator = new ConstantGenerator(null, javaType);
		}
		// fall back to default setup
        if (generator == null)
        	generator = createDefaultGenerator(descriptor, uniqueness, context);
        // by now, we must have created a generator
        if (generator == null)
            throw new ConfigurationError("Can't handle descriptor " + descriptor);
        // create wrappers
        generator = wrapWithPostprocessors(generator, descriptor, context);
        generator = DescriptorUtil.wrapWithProxy(generator, descriptor);
        // done
        logger.debug("Created {}", generator);
        return generator;
    }
	
	// private helpers -------------------------------------------------------------------------------------------------

	private static Generator<?> createConstructiveGenerator(
			SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
		Generator<?> generator;
		generator = DescriptorUtil.getGeneratorByName(descriptor, context);
        if (generator == null)
            generator = createSourceAttributeGenerator(descriptor, uniqueness, context);
        if (generator == null)
            generator = createScriptGenerator(descriptor, context);
		return generator;
	}

    protected static Generator<?> createSampleGenerator(
    		SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
    	PrimitiveType primitiveType = descriptor.getPrimitiveType();
		Class<?> targetType = (primitiveType != null ? primitiveType.getJavaType() : String.class);
		String valueSpec = descriptor.getValues();
		if (valueSpec == null)
			return null;
		if ("".equals(valueSpec))
			return new ConstantGenerator<String>("");
        try {
			Distribution distribution = GeneratorFactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
			return context.getGeneratorFactory().createFromWeightedLiteralList(valueSpec, targetType, distribution, uniqueness.isUnique());
        } catch (org.databene.commons.ParseException e) {
	        throw new ConfigurationError("Error parsing samples: " + valueSpec, e);
        }
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
    protected static Generator<?> createConstantGenerator(
    		SimpleTypeDescriptor descriptor, BeneratorContext context) {
        Generator<?> generator = null;
        // check for constant
        String constant = descriptor.getConstant();
        if ("".equals(constant))
        	generator = new ConstantGenerator<String>("");
        else if (constant != null) {
        	Object value = LiteralParser.parse(constant);
            generator = new ConstantGenerator(value);
        }
        return generator;
    }

	private static Generator<?> createDefaultGenerator(
			SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
		Generator<?> generator = createTypeGenerator(descriptor, uniqueness, context);
        if (generator == null)
            generator = createStringGenerator(descriptor, uniqueness, context);
		return generator;
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Generator<?> createSourceAttributeGenerator(
    		SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        String source = descriptor.getSource();
        if (source == null)
            return null;
        String lcn = source.toLowerCase();
        String selector = descriptor.getSelector();
        String subSelector = descriptor.getSubSelector();
        Generator<?> generator;
        if (context.get(source) != null) {
            Object sourceObject = context.get(source);
            if (sourceObject instanceof StorageSystem)
            	if (!StringUtil.isEmpty(subSelector)) {
            		generator = new IteratingGenerator(((StorageSystem) sourceObject).query(subSelector, true, context));
                    generator = GeneratorFactoryUtil.createCyclicHeadGenerator(generator);
            	} else
            		generator = new IteratingGenerator(((StorageSystem) sourceObject).query(selector, true, context));
            else if (sourceObject instanceof Generator)
                generator = (Generator<?>) sourceObject;
            else // TODO v0.7.0 support Iterable
                throw new UnsupportedOperationException("Not a supported source: " + sourceObject);
        } else if (lcn.endsWith(".csv")) {
            return createSimpleTypeCSVSourceGenerator(descriptor, source, uniqueness, context);
        } else if (lcn.endsWith(".txt")) {
            generator = context.getGeneratorFactory().createTextLineGenerator(source, false);
        } else {
        	try {
	        	BeanSpec sourceSpec = BeneratorScriptParser.resolveBeanSpec(source, context);
	        	generator = createSourceGeneratorFromObject(descriptor, context, sourceSpec);
        	} catch (Exception e) {
                generator = new AccessingGenerator(Object.class, new GraphAccessor(source), context);
        	}
        }

        Distribution distribution = GeneratorFactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
        if (distribution != null)
            generator = distribution.applyTo(generator, uniqueness.isUnique());
        
    	return generator;
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
    private static Generator<?> createSourceGeneratorFromObject(SimpleTypeDescriptor descriptor,
            BeneratorContext context, BeanSpec sourceSpec) {
		Object sourceObject = sourceSpec.getBean();
		Generator<?> generator;
	    if (sourceObject instanceof StorageSystem) {
	        StorageSystem storage = (StorageSystem) sourceObject;
	        String selector = descriptor.getSelector();
	        String subSelector = descriptor.getSubSelector();
	        if (!StringUtil.isEmpty(subSelector)) {
	        	generator = new IteratingGenerator(storage.queryEntities(descriptor.getName(), subSelector, context));
		        generator = GeneratorFactoryUtil.createCyclicHeadGenerator(generator);
	        } else
		        generator = new IteratingGenerator(storage.queryEntities(descriptor.getName(), selector, context));
	    } else if (sourceObject instanceof Generator) {
	        generator = (Generator<?>) sourceObject;
	    } else
	        throw new UnsupportedOperationException("Source type not supported: " + sourceObject.getClass());
	    if (sourceSpec.isReference())
	    	generator = context.getGeneratorFactory().wrapNonClosing(generator);
	    return generator;
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
    private static Generator<?> createSimpleTypeCSVSourceGenerator(
			SimpleTypeDescriptor descriptor, String sourceName, Uniqueness uniqueness, BeneratorContext context) {
		Generator<?> generator;
		char separator = DescriptorUtil.getSeparator(descriptor, context);
		String encoding = descriptor.getEncoding();
		if (encoding == null)
		    encoding = context.getDefaultEncoding();
        Distribution distribution = GeneratorFactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);

		String dataset = descriptor.getDataset();
		String nesting = descriptor.getNesting();
		if (dataset != null && nesting != null) {
			if (uniqueness.isUnique()) {
			    generator = new SequencedDatasetCSVGenerator(sourceName, separator, dataset, nesting, 
			    		distribution, encoding, new ScriptConverter(context));
			} else {
			    generator = new WeightedDatasetCSVGenerator(sourceName, separator, dataset, nesting, 
			    		encoding, new ScriptConverter(context));
			}
		} else if (sourceName.toLowerCase().endsWith(".wgt.csv") || distribution instanceof IndividualWeight) {
        	generator = new WeightedCSVSampleGenerator(sourceName, encoding, new ScriptConverter(context));
        } else {
    		Iterable<String[]> src = new CSVLineIterable(sourceName, separator, true, encoding);
    		Converter<String[], Object> converterChain = new ConverterChain<String[], Object>(
    				new ArrayElementExtractor<String>(String.class, 0), 
    				new ScriptConverter(context));
    		Iterable<Object> iterable = new ConvertingIterable<String[], Object>(src, converterChain);
    	    generator = new IteratingGenerator<Object>(new TypedIterableProxy<Object>(Object.class, iterable));
            if (distribution != null)
            	generator = distribution.applyTo(generator, uniqueness.isUnique());
        }
		return generator;
	}


    @SuppressWarnings("unchecked")
    private static Generator<?> createTypeGenerator(
            SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        if (descriptor instanceof UnionSimpleTypeDescriptor)
            return createUnionTypeGenerator((UnionSimpleTypeDescriptor) descriptor, context);
        PrimitiveType primitiveType = descriptor.getPrimitiveType();
        if (primitiveType == null)
            return null;
        Class<?> targetType = primitiveType.getJavaType();
        if (Number.class.isAssignableFrom(targetType)) {
            return createNumberGenerator(descriptor, (Class<? extends Number>) targetType, uniqueness, context);
        } else if (String.class.isAssignableFrom(targetType)) {
            return createStringGenerator(descriptor, uniqueness, context);
        } else if (Boolean.class == targetType) {
            return createBooleanGenerator(descriptor, context);
        } else if (Character.class == targetType) {
            return createCharacterGenerator(descriptor, uniqueness, context);
        } else if (Date.class == targetType) {
            return createDateGenerator(descriptor, uniqueness, context);
        } else if (Timestamp.class == targetType) {
            return createTimestampGenerator(descriptor, uniqueness, context);
        } else if (byte[].class == targetType) {
            return createByteArrayGenerator(descriptor, context);
        } else
            return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Generator<?> createUnionTypeGenerator(
            UnionSimpleTypeDescriptor descriptor, BeneratorContext context) {
        int n = descriptor.getAlternatives().size();
        Generator<?>[] sources = new Generator[n];
        for (int i = 0; i < n; i++) {
            SimpleTypeDescriptor alternative = descriptor.getAlternatives().get(i);
            sources[i] = createSimpleTypeGenerator(alternative, false, Uniqueness.NONE, context);
        }
        Class<?> javaType = descriptor.getPrimitiveType().getJavaType();
        return new AlternativeGenerator(javaType, sources);
    }
    
    private static Generator<?> createByteArrayGenerator(SimpleTypeDescriptor descriptor, BeneratorContext context) {
        Generator<Byte> byteGenerator = new AsByteGeneratorWrapper<Integer>(new RandomIntegerGenerator(-128, 127, 1));
        return new ByteArrayGenerator(byteGenerator, 
        		DescriptorUtil.getMinLength(descriptor), DescriptorUtil.getMaxLength(descriptor, context.getGeneratorFactory()));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Generator<Timestamp> createTimestampGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        Generator<Date> source = createDateGenerator(descriptor, uniqueness, context);
        Converter<Date, Timestamp> converter = (Converter) new AnyConverter<Timestamp>(Timestamp.class);
		return new ConvertingGenerator<Date, Timestamp>(source, converter);
    }

    private static Generator<Date> createDateGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        Date min = parseDate(descriptor, MIN, null);
        Date max = parseDate(descriptor, MAX, null);
        long precision = parseDatePrecision(descriptor);
        Distribution distribution = GeneratorFactoryUtil.getDistribution(
        		descriptor.getDistribution(), uniqueness, true, context);
	    return context.getGeneratorFactory().createDateGenerator(min, max, precision, distribution);
    }

    private static Generator<Character> createCharacterGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        String pattern = descriptor.getPattern();
        if (pattern == null)
            pattern = ".";
        Locale locale = descriptor.getLocale();
        GeneratorFactory generatorFactory = context.getGeneratorFactory();
		if (uniqueness.isUnique())
            return generatorFactory.createUniqueCharacterGenerator(pattern, locale);
        else
            return generatorFactory.createCharacterGenerator(pattern, locale);
    }

    private static Date parseDate(SimpleTypeDescriptor descriptor, String detailName, Date defaultDate) {
        String detail = (String) descriptor.getDeclaredDetailValue(detailName);
        try {
            if (detail != null) {
                DateFormat dateFormat = DescriptorUtil.getPatternAsDateFormat(descriptor);
                return dateFormat.parse(detail);
            } else
                return defaultDate;
        } catch (java.text.ParseException e) {
            logger.error("Error parsing date " + detail, e);
            return defaultDate;
        }
    }

    private static long parseDatePrecision(SimpleTypeDescriptor descriptor) {
        String detail = (String) descriptor.getDeclaredDetailValue(DescriptorConstants.ATT_PRECISION);
		if (detail != null)
        	return DateString2DurationConverter.defaultInstance().convert(detail);
        else
            return 24 * 3600 * 1000L;
    }

    private static Generator<Boolean> createBooleanGenerator(SimpleTypeDescriptor descriptor, BeneratorContext context) {
        return context.getGeneratorFactory().createBooleanGenerator(descriptor.getTrueQuota());
    }

    private static <T extends Number> Generator<T> createNumberGenerator(
            SimpleTypeDescriptor descriptor, Class<T> targetType, Uniqueness uniqueness, BeneratorContext context) {
        T min = DescriptorUtil.getNumberDetail(descriptor, MIN, targetType);
        T max = DescriptorUtil.getNumberDetail(descriptor, MAX, targetType);
        Integer totalDigits = DescriptorUtil.getNumberDetail(descriptor, "totalDigits", Integer.class);
        Integer fractionDigits = DescriptorUtil.getNumberDetail(descriptor, "fractionDigits", Integer.class);
        T precision = DescriptorUtil.getNumberDetail(descriptor, PRECISION, targetType);
        Distribution distribution = GeneratorFactoryUtil.getDistribution(
        		descriptor.getDistribution(), uniqueness, true, context);
        return context.getGeneratorFactory().createNumberGenerator(
                targetType, min, max, totalDigits, fractionDigits, precision, distribution, uniqueness.isUnique());
    }

    private static Generator<String> createStringGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        // evaluate max length
        Integer maxLength = (Integer) descriptor.getDeclaredDetailValue(MAX_LENGTH);
        if (maxLength == null) {
            // maxLength was not set in this descriptor. So check the parent setting's value 
            maxLength = descriptor.getMaxLength();
        }

        // check pattern against null
        String pattern = ToStringConverter.convert(descriptor.getDetailValue(PATTERN), null);

        Integer minLength = descriptor.getMinLength();
        Distribution lengthDistribution = GeneratorFactoryUtil.getDistribution(
        		descriptor.getLengthDistribution(), Uniqueness.NONE, false, context);
        Locale locale = descriptor.getLocale();
        return context.getGeneratorFactory().createStringGenerator(pattern, minLength, maxLength, lengthDistribution, locale, uniqueness.isUnique());
    }
    
    @SuppressWarnings("unchecked")
    protected static <A extends Annotation, T> Validator<T> createRestrictionValidator(
            SimpleTypeDescriptor descriptor, boolean nullable, GeneratorFactory context) {
        if ((descriptor.getMinLength() != null || descriptor.getMaxLength() != null) && "string".equals(descriptor.getName())) {
            Integer minLength = DescriptorUtil.getMinLength(descriptor);
            Integer maxLength = DescriptorUtil.getMaxLength(descriptor, context);
            return (Validator<T>) new StringLengthValidator(minLength, maxLength, nullable);
        }
        return null;
    }

    private SimpleTypeGeneratorFactory() {}
    
    private static final Logger logger = LoggerFactory.getLogger(ComponentBuilderFactory.class);

}
