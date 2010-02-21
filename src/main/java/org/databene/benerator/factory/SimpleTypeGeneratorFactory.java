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
import org.databene.benerator.sample.AttachedWeightSampleGenerator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.sample.WeightedCSVSampleGenerator;
import org.databene.benerator.sample.WeightedSample;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.wrapper.AccessingGenerator;
import org.databene.benerator.wrapper.AlternativeGenerator;
import org.databene.benerator.wrapper.AsByteGeneratorWrapper;
import org.databene.benerator.wrapper.ByteArrayGenerator;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ConversionException;
import org.databene.commons.Converter;
import org.databene.commons.NumberUtil;
import org.databene.commons.TimeUtil;
import org.databene.commons.Validator;
import org.databene.commons.accessor.GraphAccessor;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ArrayElementExtractor;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.ConvertingIterable;
import org.databene.commons.converter.LiteralParser;
import org.databene.commons.iterator.ArrayIterable;
import org.databene.commons.iterator.DefaultTypedIterable;
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

	@SuppressWarnings("unchecked")
    public static Generator<?> createSimpleTypeGenerator(
			SimpleTypeDescriptor descriptor, boolean nullable, Uniqueness uniqueness,
			BeneratorContext context) {
        if (logger.isDebugEnabled())
            logger.debug("create(" + descriptor.getName() + ')');
        // try constructive setup
        Generator<?> generator = createConstructiveGenerator(descriptor, uniqueness, context);
        // fall back to descriptive setup
        if (generator == null)
        	generator = createConstantGenerator(descriptor, context);
        if (generator == null)
        	generator = createSampleGenerator(descriptor, uniqueness, context);
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
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
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

    @SuppressWarnings("unchecked")
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
			WeightedSample<?>[] samples;
		        samples = BeneratorScriptParser.parseWeightedLiteralList(valueSpec);
			Distribution distribution;
			if (weightsUsed(samples)) {
				AttachedWeightSampleGenerator generator = new AttachedWeightSampleGenerator(targetType);
				for (int i = 0; i < samples.length; i++)
					generator.addSample(samples[i]);
				return generator;
			} else {
				Object[] values = new Object[samples.length];
				for (int i = 0; i < samples.length; i++)
					values[i] = samples[i].getValue();
				distribution = GeneratorFactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, true, context);
		        return distribution.applyTo(new IteratingGenerator(new ArrayIterable(values, targetType)), uniqueness.isUnique());
			}
        } catch (org.databene.commons.ParseException e) {
	        throw new ConfigurationError("Error parsing samples: " + valueSpec, e);
        }
    }

    private static boolean weightsUsed(WeightedSample<?>[] samples) {
	    for (WeightedSample<?> sample : samples)
	    	if (sample.getWeight() != 1)
	    		return true;
	    return false;
    }

	@SuppressWarnings("unchecked")
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
            generator = createStringGenerator(descriptor, uniqueness);
		return generator;
	}

    @SuppressWarnings("unchecked")
    public static Generator<?> createSourceAttributeGenerator(
    		SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        String source = descriptor.getSource();
        if (source == null)
            return null;
        String lcn = source.toLowerCase();
        String selector = descriptor.getSelector();
        Generator<?> generator;
        if (context.get(source) != null) {
            Object sourceObject = context.get(source);
            if (sourceObject instanceof StorageSystem)
                generator = new IteratingGenerator(((StorageSystem) sourceObject).query(selector, context));
            else if (sourceObject instanceof Generator)
                generator = (Generator<?>) sourceObject;
            else
                throw new UnsupportedOperationException("Not a supported source: " + sourceObject);
        } else if (lcn.endsWith(".csv")) {
            return createSimpleTypeCSVSourceGenerator(descriptor, source, uniqueness, context);
        } else if (lcn.endsWith(".txt")) {
            generator = GeneratorFactory.getTextLineGenerator(source, false);
        } else {
        	try {
	        	Object sourceObject = BeneratorScriptParser.parseBeanSpec(source).evaluate(context);
	        	generator = createSourceGeneratorFromObject(descriptor, context, sourceObject);
        	} catch (Exception e) {
                generator = new AccessingGenerator(Object.class, new GraphAccessor(source), context);
        	}
        }

        Distribution distribution = GeneratorFactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
        if (distribution != null)
            generator = distribution.applyTo(generator, uniqueness.isUnique());
        
    	return generator;
    }

	@SuppressWarnings("unchecked")
    private static Generator<?> createSourceGeneratorFromObject(SimpleTypeDescriptor descriptor,
            BeneratorContext context, Object sourceObject) {
		Generator generator;
	    if (sourceObject instanceof StorageSystem) {
	        StorageSystem storage = (StorageSystem) sourceObject;
	        String selector = descriptor.getSelector();
	        generator = new IteratingGenerator(storage.queryEntities(descriptor.getName(), selector, context));
	    } else if (sourceObject instanceof Generator) {
	        generator = (Generator) sourceObject;
	    } else
	        throw new UnsupportedOperationException("Source type not supported: " + sourceObject.getClass());
	    return generator;
    }

	@SuppressWarnings("unchecked")
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
    	    generator = new IteratingGenerator<Object>(new DefaultTypedIterable<Object>(Object.class, iterable));
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
            return createStringGenerator(descriptor, uniqueness);
        } else if (Boolean.class == targetType) {
            return createBooleanGenerator(descriptor);
        } else if (Character.class == targetType) {
            return createCharacterGenerator(descriptor, uniqueness);
        } else if (Date.class == targetType) {
            return createDateGenerator(descriptor, uniqueness, context);
        } else if (Timestamp.class == targetType) {
            return createTimestampGenerator(descriptor, uniqueness, context);
        } else if (byte[].class == targetType) {
            return createByteArrayGenerator(descriptor);
        } else
            return null;
    }

    @SuppressWarnings("unchecked")
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
    private static Generator<?> createByteArrayGenerator(
            SimpleTypeDescriptor descriptor) {
        Generator<Byte> byteGenerator = new AsByteGeneratorWrapper<Integer>(new RandomIntegerGenerator(-128, 127, 1));
        return new ByteArrayGenerator(byteGenerator, 
                getMinLength(descriptor), getMaxLength(descriptor));
    }

    @SuppressWarnings("unchecked")
    private static Generator<Timestamp> createTimestampGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        Generator<Date> source = createDateGenerator(descriptor, uniqueness, context);
        Converter<Date, Timestamp> converter = (Converter) new AnyConverter<Timestamp>(Timestamp.class);
		return new ConvertingGenerator<Date, Timestamp>(source, converter);
    }

    private static Generator<Date> createDateGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        Date min = parseDate(descriptor, MIN, TimeUtil.date(1970, 0, 1));
        Date max = parseDate(descriptor, MAX, TimeUtil.today().getTime());
        Date precisionDate = parseDate(descriptor, PRECISION, TimeUtil.date(1970, 0, 2));
        long precision = precisionDate.getTime() - TimeUtil.date(1970, 0, 1).getTime();
        Distribution distribution = GeneratorFactoryUtil.getDistribution(
        		descriptor.getDistribution(), uniqueness, true, context);
	    return GeneratorFactory.getDateGenerator(min, max, precision, distribution);
    }

    private static Generator<Character> createCharacterGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness) {
        String pattern = descriptor.getPattern();
        if (pattern == null)
            pattern = ".";
        Locale locale = DescriptorUtil.getLocale(descriptor);
        if (uniqueness.isUnique())
            return GeneratorFactory.getUniqueCharacterGenerator(pattern, locale);
        else
            return GeneratorFactory.getCharacterGenerator(pattern, locale);
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

    private static Generator<Boolean> createBooleanGenerator(SimpleTypeDescriptor descriptor) {
        Double trueQuota = descriptor.getTrueQuota();
        if (trueQuota == null)
            trueQuota = (Double) descriptor.getDetailDefault(TRUE_QUOTA);
        return GeneratorFactory.getBooleanGenerator(trueQuota);
    }

    private static <T extends Number> Generator<T> createNumberGenerator(
            SimpleTypeDescriptor descriptor, Class<T> targetType, Uniqueness uniqueness, BeneratorContext context) {
        T min = getNumberDetail(descriptor, MIN, targetType);
        T max = getMax(descriptor, targetType, uniqueness.isUnique());
        if (min.equals(max)) {
            return new ConstantGenerator<T>(min);
        }
        Integer totalDigits = getNumberDetail(descriptor, "totalDigits", Integer.class);
        Integer fractionDigits = getNumberDetail(descriptor, "fractionDigits", Integer.class);
        T precision = getNumberDetail(descriptor, PRECISION, targetType);
        Distribution distribution = GeneratorFactoryUtil.getDistribution(
        		descriptor.getDistribution(), uniqueness, true, context);
        return GeneratorFactory.getNumberGenerator(
                targetType, min, max, totalDigits, fractionDigits, precision, distribution, uniqueness);
    }

    private static Generator<String> createStringGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness) {
        // evaluate max length
        Integer maxLength = (Integer) descriptor.getDeclaredDetailValue(MAX_LENGTH);
        if (maxLength == null) {
            // maxLength was not set in this descriptor. So check the parent setting's value 
            // (it is interpreted as constraint which may be too high to be useful by default)
            maxLength = descriptor.getMaxLength();
            if (maxLength == null)
                maxLength = (Integer) descriptor.getDetailDefault(MAX_LENGTH);
            if (maxLength == null || maxLength > 10000)
                maxLength = 10000;
        }

        // check pattern against null
        String pattern = descriptor.getPattern();
        if (pattern == null)
            pattern = (String) descriptor.getDetailDefault(PATTERN);

        // evaluate min length
        Integer minLength = descriptor.getMinLength();
        if (minLength == null) {
        	if (pattern != null && pattern.length() == 0)
        		minLength = 0;
        	else {
	            int defaultMinLength = (Integer) descriptor.getDetailDefault(MIN_LENGTH);
	            minLength = Math.min(maxLength, defaultMinLength);
        	}
        }

        // evaluate pattern
        if (pattern == null)
            pattern = "[A-Z]{" + minLength + ',' + maxLength + '}';

        // evaluate locale
        Locale locale = descriptor.getLocale();
        if (locale == null)
            locale = (Locale) descriptor.getDetailDefault(LOCALE);
        // evaluate uniqueness and create generator
        if (uniqueness.isUnique())
            return GeneratorFactory.getUniqueRegexStringGenerator(pattern, minLength, maxLength);
        else
            return GeneratorFactory.getRegexStringGenerator(pattern, minLength, maxLength);
    }
    
    @SuppressWarnings("unchecked")
    protected static <A extends Annotation, T> Validator<T> createRestrictionValidator(
            SimpleTypeDescriptor descriptor, boolean nullable) {
        if ((descriptor.getMinLength() != null || descriptor.getMaxLength() != null) && "string".equals(descriptor.getName())) {
            Integer minLength = getMinLength(descriptor);
            Integer maxLength = getMaxLength(descriptor);
            return (Validator<T>) new StringLengthValidator(minLength, maxLength, nullable);
        }
        return null;
    }

    // descriptor access -----------------------------------------------------------------------------------------------

    protected static Integer getMinLength(SimpleTypeDescriptor descriptor) {
        Integer minLength = descriptor.getMinLength();
        if (minLength == null)
            minLength = 0;
        return minLength;
    }

    protected static Integer getMaxLength(SimpleTypeDescriptor descriptor) {
        // evaluate max length
        Integer maxLength = (Integer) descriptor.getDeclaredDetailValue(MAX_LENGTH);
        if (maxLength == null) {
            // maxLength was not set in this descriptor. So check the parent setting's value 
            // (it is interpreted as constraint which may be to high to be useful by default)
            maxLength = descriptor.getMaxLength();
            if (maxLength == null)
                maxLength = (Integer) descriptor.getDetailDefault(MAX_LENGTH);
            if (maxLength > 10000)
                maxLength = 10000;
        }
        return maxLength;
    }

    private static <T extends Number> T getMax(SimpleTypeDescriptor descriptor, Class<T> targetType, boolean unique) {
        try {
            String detailValue = (String) descriptor.getDetailValue(MAX);
            if (detailValue == null)
            	if (unique)
            		return NumberUtil.maxValue(targetType);
            	else
            		detailValue = (String) descriptor.getDetailDefault(MAX);
            return AnyConverter.convert(detailValue, targetType);
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private static <T extends Number> T getNumberDetail(SimpleTypeDescriptor descriptor, String detailName, Class<T> targetType) {
        try {
            String detailValue = (String) descriptor.getDetailValue(detailName);
            if (detailValue == null)
                detailValue = (String) descriptor.getDetailDefault(detailName);
            return AnyConverter.convert(detailValue, targetType);
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private SimpleTypeGeneratorFactory() {}
    
    private static final Logger logger = LoggerFactory.getLogger(ComponentBuilderFactory.class);

}
