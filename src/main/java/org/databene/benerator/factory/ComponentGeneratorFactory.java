/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.id.GlobalIdProviderFactory;
import org.databene.id.IdProvider;
import org.databene.id.IdProviderFactory;
import org.databene.id.IdStrategy;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.AttributeDescriptor;
import org.databene.model.data.FeatureDescriptor;
import org.databene.model.data.FeatureDetail;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.storage.StorageSystem;
import org.databene.platform.bean.BeanDescriptorProvider;
import org.databene.platform.csv.CSVEntityIterable;
import org.databene.script.Script;
import org.databene.script.ScriptConverter;
import org.databene.script.ScriptUtil;
import org.databene.benerator.AccessingGenerator;
import org.databene.benerator.Distribution;
import org.databene.benerator.Generator;
import org.databene.benerator.Sequence;
import org.databene.benerator.ValidatingGeneratorProxy;
import org.databene.benerator.WeightFunction;
import org.databene.benerator.primitive.ScriptGenerator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.sample.SequencedSampleGenerator;
import org.databene.benerator.sample.WeightedSampleGenerator;
import org.databene.benerator.wrapper.ByteArrayGenerator;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.benerator.wrapper.IdGenerator;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.benerator.wrapper.NullableGenerator;
import org.databene.benerator.wrapper.UniqueAlternativeGenerator;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ConversionException;
import org.databene.commons.Converter;
import org.databene.commons.LocaleUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.TimeUtil;
import org.databene.commons.TypedIterable;
import org.databene.commons.Validator;
import org.databene.commons.accessor.GraphAccessor;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ParseFormatConverter;
import org.databene.commons.converter.String2DateConverter;
import org.databene.commons.validator.NotNullValidator;
import org.databene.commons.validator.StringLengthValidator;

/**
 * Creates generators that generate entity components.<br/>
 * <br/>
 * Created: 14.10.2007 22:16:34
 * @author Volker Bergmann
 */
public class ComponentGeneratorFactory extends FeatureGeneratorFactory {

    // factory methods for component generators ------------------------------------------------------------------------
    
    public static Generator<? extends Object> getComponentGenerator(
            ComponentDescriptor descriptor, Context context, GenerationSetup setup) {
        if (descriptor instanceof AttributeDescriptor)
            return createAttributeGenerator((AttributeDescriptor)descriptor, context, setup);
        else if (descriptor instanceof ReferenceDescriptor)
            return createReferenceGenerator((ReferenceDescriptor)descriptor, context);
        else if (descriptor instanceof IdDescriptor)
            return createIdGenerator((IdDescriptor)descriptor, context);
        else 
            throw new ConfigurationError("Unsupported element: " + descriptor.getClass());
    }
    
    private static Generator<? extends Object> createIdGenerator(
            IdDescriptor descriptor, Context context) {
        Set<String> usedDetails = new HashSet<String>();
        IdProviderFactory source = null;
        // check strategy
        String strategyName = descriptor.getStrategy();
        if (strategyName != null)
            usedDetails.add(STRATEGY);
        else
            throw new ConfigurationError("No strategy defined for key: " + descriptor.getName());

        // check scope
        String scope = descriptor.getScope();
        if (scope != null)
            usedDetails.add(SCOPE);
        // check source
        String sourceId = descriptor.getSource();
        if (sourceId != null) {
            usedDetails.add(SOURCE);
            source = (IdProviderFactory) context.get(sourceId);
        }
        // check param
        String param = descriptor.getParam();
        if (param != null)
            usedDetails.add(PARAM);
        

        checkUsedDetails(descriptor, usedDetails);
        IdStrategy idStrategy = new IdStrategy(strategyName, null);
        IdProvider idProvider;
        if (source != null)
            idProvider = source.idProvider(idStrategy, param, scope);
        else
            idProvider = GLOBAL_ID_PROVIDER_FACTORY.idProvider(idStrategy, param, scope);
        Generator<Object> generator = new IdGenerator(idProvider);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return generator;
    }

    public static Generator<? extends Object> createAttributeGenerator(
            AttributeDescriptor descriptor, Context context, GenerationSetup setup) {
        Generator<? extends Object> generator = null;
        Set<String> usedDetails = new HashSet<String>();
        // create a source generator
        generator = createNullQuotaOneGenerator(descriptor, usedDetails);
        if (generator == null) {
            generator = createGeneratorByClass(descriptor, context, usedDetails, generator);
            if (generator == null)
                generator = createSourceAttributeGenerator(descriptor, context, setup, usedDetails);
            if (generator == null)
                generator = createScriptGenerator(descriptor, context, usedDetails, setup.getDefaultScript());
            if (generator == null)
                generator = createSampleGenerator(descriptor, usedDetails, generator);
            if (generator == null)
                generator = createNullGenerator(descriptor, setup, usedDetails);
            if (generator == null) {
                if (generator == null)
                    generator = createTypeGenerator(descriptor, generator, usedDetails);
                if (generator == null)
                    generator = createStringGenerator(descriptor, usedDetails);
                // by now, we must have created a generator
                if (generator == null)
                    throw new ConfigurationError("Don't know how to handle descriptor " + descriptor);
                // create wrappers
                generator = createConvertingGenerator(descriptor, generator, usedDetails);
                generator = createTypeConvertingGenerator(descriptor, generator, usedDetails);
                generator = createValidatingGenerator(descriptor, generator, usedDetails);
                generator = createProxy(descriptor, generator, usedDetails);
                generator = createNullQuotaGenerator(descriptor, generator, usedDetails);
            }
        }
        checkUsedDetails(descriptor, usedDetails);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return generator;
    }

    public static Generator<? extends Object> createReferenceGenerator(ReferenceDescriptor descriptor, Context context) {
        Set<String> usedDetails = new HashSet<String>();
        Generator<? extends Object> generator = null;
        String targetTye = descriptor.getTargetTye();
        if (targetTye != null) {
            usedDetails.add(TARGET_TYPE);
            String selector = descriptor.getSelector();
            if (selector != null)
                usedDetails.add(SELECTOR);
            String sourceName = descriptor.getSource();
            if (sourceName != null)
                usedDetails.add(SOURCE);
            else
                throw new ConfigurationError("'" + SOURCE + "' is not set for " + descriptor);
            Object sourceObject = context.get(sourceName);
            if (sourceObject instanceof StorageSystem) {
                StorageSystem sourceSystem = (StorageSystem) sourceObject;
                TypedIterable<Object> entityIds = sourceSystem.queryEntityIds(targetTye, selector);
                generator = new IteratingGenerator<Object>(entityIds);
            } else if (sourceObject instanceof org.databene.model.system.System) {
                org.databene.model.system.System sourceSystem = (org.databene.model.system.System) sourceObject;
                TypedIterable<Object> entityIds = (TypedIterable<Object>) sourceSystem.getIds(targetTye, selector);
                generator = new IteratingGenerator<Object>(entityIds);
            }
        } else
            generator = new ConstantGenerator<Object>(null);
        generator = createValidatingGenerator(descriptor, generator, usedDetails);
        checkUsedDetails(descriptor, usedDetails);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return generator;
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private static Generator<? extends Object> createScriptGenerator(
            AttributeDescriptor descriptor, Context context, Set<String> usedDetails, String defaultEngineId) {
        Generator<String> generator = null;
        String scriptText = descriptor.getScript();
        if (scriptText != null) {
            usedDetails.add(SCRIPT);
            Script script = ScriptUtil.parseUnspecificText(scriptText, defaultEngineId);
            generator = new ScriptGenerator(script, context);
        }
        return generator;
    }

    private static Generator<? extends Object> createNullQuotaOneGenerator(AttributeDescriptor descriptor, Set<String> usedDetails) {
        Double nullQuota = descriptor.getNullQuota();
        if (nullQuota != null && nullQuota.doubleValue() == 1) {
            usedDetails.add(NULL_QUOTA);
            return new ConstantGenerator<Object>(null);
        }
        return null;
    }

    private static Generator<? extends Object> createNullGenerator(
            AttributeDescriptor descriptor, GenerationSetup setup, Set<String> usedDetails) {
        Boolean nullable = descriptor.isNullable();
        if (nullable != null) {
            usedDetails.add("nullable");
            if (nullable.booleanValue()) {
                Boolean defaultNull = setup.isDefaultNull();
                if (defaultNull != null && defaultNull.booleanValue())
                    return new ConstantGenerator<Object>(null);
            }
        }
        return null;
    }

    private static Generator<? extends Object> createSampleGenerator(AttributeDescriptor descriptor, Set<String> usedDetails, Generator<? extends Object> generator) {
        // check for samples
        String[] values = descriptor.getValues();
        if (values.length > 0) {
            usedDetails.add(VALUES);
            Distribution distribution = getDistribution(descriptor, usedDetails);
            if (distribution instanceof Sequence)
                generator = new SequencedSampleGenerator<String>(String.class, (Sequence) distribution, values);
            else if (distribution instanceof WeightFunction)
                generator = new WeightedSampleGenerator<String>((WeightFunction) distribution, values);
            else
                throw new ConfigurationError("Unsupported distribution type: " + distribution.getClass());
        }
        return generator;
    }

    private static Generator<? extends Object> createGeneratorByClass(AttributeDescriptor descriptor, Context context, Set<String> usedDetails, Generator<? extends Object> generator) {
        String generatorClassName = descriptor.getGenerator();
        if (generatorClassName != null) {
            usedDetails.add(GENERATOR);
            generator = BeanUtil.newInstance(generatorClassName);
            for (FeatureDetail<? extends Object> detail : descriptor.getDetails()) {
                setProperty(generator, detail, context, usedDetails);
            }
        }
        return generator;
    }

    private static void setProperty(Object bean, FeatureDetail<? extends Object> detail, Context context, Set<String> usedDetails) {
        String detailName = detail.getName();
        if (detail.getValue() != null && BeanUtil.hasProperty(bean.getClass(), detailName)) {
            try {
                PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(bean.getClass(), detail.getName());
                Class<?> propertyType = propertyDescriptor.getPropertyType();
                Object propertyValue = detail.getValue();
                if (System.class.isAssignableFrom(propertyType))
                    propertyValue = context.get(propertyValue.toString());
                else if (propertyValue.getClass() != propertyType)
                    propertyValue = AnyConverter.convert(propertyValue, propertyType);
                BeanUtil.setPropertyValue(bean, detailName, propertyValue);
                usedDetails.add(detailName);
            } catch (RuntimeException e) {
                throw new RuntimeException("Error setting '" + detailName + "' of class " + bean.getClass().getName(), e); 
            }
        }
    }

    private static <T> Generator<T> createNullQuotaGenerator(
            AttributeDescriptor descriptor, Generator<T> generator, Set<String> usedDetails) {
        Double nullQuota = descriptor.getNullQuota();
        if (nullQuota != null) {
            usedDetails.add(NULL_QUOTA);
            if (nullQuota > 0) {
                if (descriptor.isNullable() != null && !descriptor.isNullable())
                    logger.error("nullQuota is set to " + nullQuota + " but the value is not nullable. " +
                            "Ignoring nullQuota for: " + descriptor);
                else
                    generator = new NullableGenerator<T>(generator, nullQuota);
            }
        }
        return generator;
    }

    private static Generator<? extends Object> createTypeGenerator(AttributeDescriptor descriptor, Generator<? extends Object> generator, Set<String> usedDetails) {
        if (descriptor.getType() == null)
            return generator;
        usedDetails.add(TYPE);
        Class<? extends Object> targetType = javaClassFor(descriptor.getType());
        return createTypeGenerator(targetType, descriptor, usedDetails);
    }

    private static <T> Generator<T> createTypeConvertingGenerator(
            AttributeDescriptor descriptor, Generator<? extends Object> generator, Set<String> usedDetails) {
        if (descriptor.getType() == null)
            return (Generator<T>) generator;
        usedDetails.add(TYPE);
        Class<T> targetType = javaClassFor(descriptor.getType());
        Converter converter = null;
        if (Date.class.equals(targetType) && generator.getGeneratedType() == String.class) {
            // String needs to be converted to Date
            if (descriptor.getPattern() != null) {
                // We can use the SimpleDateFormat with a pattern
                usedDetails.add(PATTERN);
                String pattern = descriptor.getPattern();
                converter = new ParseFormatConverter(Date.class, new SimpleDateFormat(pattern));
            } else {
                // we need to expect the standard date format
                converter = new String2DateConverter();
            }
        }
        if (converter == null)
            converter = new AnyConverter<T>(targetType);
        return new ConvertingGenerator<Object, T>((Generator<Object>)generator, converter);
    }

    private static Generator<? extends Object> createSourceAttributeGenerator(AttributeDescriptor descriptor, Context context, GenerationSetup setup, Set<String> usedDetails) {
        String source = descriptor.getSource();
        if (source == null)
            return null;
        usedDetails.add(SOURCE);
        String lcn = source.toLowerCase();
        String selector = descriptor.getSelector();
        if (selector != null)
            usedDetails.add(SELECTOR);
        Generator<? extends Object> generator;
        if (context.get(source) != null) {
            Object sourceObject = context.get(source);
            if (sourceObject instanceof StorageSystem)
                generator = new IteratingGenerator(((StorageSystem) sourceObject).query(selector));
            else if (sourceObject instanceof org.databene.model.system.System)
                generator = new IteratingGenerator(((org.databene.model.system.System) sourceObject).getBySelector(selector));
            else if (sourceObject instanceof Generator)
                generator = (Generator) sourceObject;
            else
                throw new UnsupportedOperationException("Not a supported source: " + sourceObject);
        } else if (lcn.endsWith(".csv")) {
            String type = descriptor.getType();
            if (type != null)
                usedDetails.add(TYPE);
            char separator = ',';
            if (descriptor.getSelector() != null && descriptor.getSelector().length() == 1) {
                usedDetails.add(SELECTOR);
                separator = descriptor.getSelector().charAt(0);
            }
            String encoding = descriptor.getEncoding();
            if (encoding != null)
                usedDetails.add(ENCODING);
            else
                encoding = SystemInfo.fileEncoding();
            // TODO v0.4.2 decide whether to import Entities or cells
            ScriptConverter scriptConverter = new ScriptConverter(context, setup.getDefaultScript());
            generator = new IteratingGenerator(new CSVEntityIterable(source, descriptor.getName(), scriptConverter, separator, encoding));
        } else if (lcn.endsWith(".txt")) {
            generator = GeneratorFactory.getTextLineGenerator(source, false, null, null, null);
        } else {
            generator = new AccessingGenerator(Object.class, new GraphAccessor(source), context);
        }

        // check distribution
        Distribution distribution = descriptor.getDistribution();
        if (distribution != null) {
            usedDetails.add(DISTRIBUTION);
            List<Object> values = new ArrayList<Object>();
            while (generator.available()) {
                Object value = generator.generate();
                values.add(value);
            }
            if (distribution instanceof Sequence)
                generator = new SequencedSampleGenerator(generator.getGeneratedType(), (Sequence) distribution, values);
            else if (distribution instanceof WeightFunction)
                generator = new WeightedSampleGenerator((WeightFunction) distribution, values);
            else
                throw new UnsupportedOperationException("Distribution type not supported: " + distribution.getClass());
        }
//        generator = createConvertingGenerator(descriptor, generator, usedDetails);
        return createProxy(descriptor, generator, usedDetails);
    }

    private static Generator<? extends Object> createConvertingGenerator(AttributeDescriptor descriptor, Generator<? extends Object> generator, Set<String> usedDetails) {
        if (descriptor.getConverter() != null) {
            usedDetails.add(CONVERTER);
            Converter converter = descriptor.getConverter();
            if (descriptor.getPattern() != null && BeanUtil.hasProperty(converter.getClass(), PATTERN)) {
                BeanUtil.setPropertyValue(converter, PATTERN, descriptor.getPattern(), false);
                usedDetails.add(PATTERN);
            }
            generator = GeneratorFactory.getConvertingGenerator(generator, converter);
        }
        return generator;
    }

    private static <T extends Number> T getNumberDetail(AttributeDescriptor descriptor, String detailName, Class<T> targetType,
                                                        Set<String> usedDetails) {
        try {
            String detailValue = (String) descriptor.getDetailValue(detailName);
            if (detailValue == null)
                detailValue = (String) descriptor.getDetailDefault(detailName);
            else
                usedDetails.add(detailName);
            return AnyConverter.convert(detailValue, targetType);
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private static Generator<? extends Object> createTypeGenerator(Class targetType, AttributeDescriptor descriptor, Set<String> usedDetails) {
        if (Number.class.isAssignableFrom(targetType)) {
            usedDetails.add(TYPE);
            return createNumberGenerator(descriptor, targetType, usedDetails);
        } else if (String.class.isAssignableFrom(targetType)) {
            usedDetails.add(TYPE);
            return createStringGenerator(descriptor, usedDetails);
        } else if (Boolean.class == targetType) {
            usedDetails.add(TYPE);
            return createBooleanGenerator(descriptor, usedDetails);
        } else if (Character.class == targetType) {
            usedDetails.add(TYPE);
            return createCharacterGenerator(descriptor, usedDetails);
        } else if (Date.class == targetType) {
            usedDetails.add(TYPE);
            return createDateGenerator(descriptor, usedDetails);
        } else if (Timestamp.class == targetType) {
            usedDetails.add(TYPE);
            return createTimestampGenerator(descriptor, usedDetails);
        } else if (byte[].class == targetType) {
            usedDetails.add(TYPE);
            return createByteArrayGenerator(descriptor, usedDetails);
        } else
            return null;
    }

    private static Generator<? extends Object> createByteArrayGenerator(
            AttributeDescriptor descriptor, Set<String> usedDetails) {
        Generator<Byte> byteGenerator = GeneratorFactory.getNumberGenerator(Byte.class, (byte)-128, (byte)127, (byte)1, Sequence.RANDOM, 0);
        return new ByteArrayGenerator(byteGenerator, 
                getMinLength(descriptor, usedDetails), getMaxLength(descriptor, usedDetails));
    }

    private static Generator<Date> createDateGenerator(AttributeDescriptor descriptor, Set<String> usedDetails) {
        Date min = parseDate(descriptor, MIN, TimeUtil.date(1970, 0, 1), usedDetails);
        Date max = parseDate(descriptor, MAX, TimeUtil.today().getTime(), usedDetails);
        Date precisionDate = parseDate(descriptor, PRECISION, TimeUtil.date(1970, 0, 2), usedDetails);
        long precision = precisionDate.getTime() - TimeUtil.date(1970, 0, 1).getTime();
        Distribution distribution = getDistribution(descriptor, usedDetails);
        return GeneratorFactory.getDateGenerator(min, max, precision, distribution, 0);
    }

    private static Generator<Timestamp> createTimestampGenerator(AttributeDescriptor descriptor, Set<String> usedDetails) {
        Generator<Date> dateGenerator = createDateGenerator(descriptor, usedDetails);
        return new ConvertingGenerator<Date, Timestamp>(dateGenerator, new AnyConverter(Timestamp.class));
    }

    private static Generator<Character> createCharacterGenerator(AttributeDescriptor descriptor, Set<String> usedDetails) {
        String pattern = descriptor.getPattern();
        if (pattern != null)
            usedDetails.add(PATTERN);
        else
            pattern = ".";
        Locale locale = getLocale(descriptor, usedDetails);
        if (isUnique(descriptor, usedDetails))
            return GeneratorFactory.getUniqueCharacterGenerator(pattern, locale);
        else
            return GeneratorFactory.getCharacterGenerator(pattern, locale, getNullQuota(descriptor, usedDetails));
    }

    private static Date parseDate(AttributeDescriptor descriptor, String detailName, Date defaultDate, Set<String> usedDetails) {
        try {
            String detail = (String) descriptor.getDeclaredDetailValue(detailName);
            if (detail != null) {
                usedDetails.add(detailName);
                DateFormat dateFormat = getDateFormat(descriptor, usedDetails);
                return dateFormat.parse(detail);
            } else
                return defaultDate;
        } catch (ParseException e) {
            logger.error(e, e);
            return defaultDate;
        }
    }

    private static DateFormat getDateFormat(AttributeDescriptor descriptor, Set<String> usedDetails) {
        String pattern = descriptor.getPattern();
        if (pattern != null) {
            usedDetails.add(PATTERN);
            return new SimpleDateFormat(pattern);
        }
        return DateFormat.getDateInstance(DateFormat.SHORT, getLocale(descriptor, usedDetails));
    }

    // primitive generators --------------------------------------------------------------------------------------------

    private static Generator<Boolean> createBooleanGenerator(AttributeDescriptor descriptor, Set<String> usedDetails) {
        Double trueQuota = descriptor.getTrueQuota();
        if (trueQuota == null)
            trueQuota = (Double) descriptor.getDetailDefault(TRUE_QUOTA);
        else
            usedDetails.add(TRUE_QUOTA);
        if (isUnique(descriptor, usedDetails))
            return new UniqueAlternativeGenerator<Boolean>(
                    Boolean.class, new ConstantGenerator<Boolean>(false), new ConstantGenerator<Boolean>(true));
        else
            return GeneratorFactory.getBooleanGenerator(trueQuota, 0);
    }

    private static <T extends Number> Generator<T> createNumberGenerator(
            AttributeDescriptor descriptor, Class<T> targetType, Set<String> usedDetails) {
        T min = getNumberDetail(descriptor, MIN, targetType, usedDetails);
        T max = getNumberDetail(descriptor, MAX, targetType, usedDetails);
        if (min.equals(max)) {
            return new ConstantGenerator<T>(min);
        }
        T precision = getNumberDetail(descriptor, PRECISION, targetType, usedDetails);
        Distribution distribution = getDistribution(descriptor, usedDetails);
        T variation1 = getNumberDetail(descriptor, VARIATION1, targetType, usedDetails);
        T variation2 = getNumberDetail(descriptor, VARIATION2, targetType, usedDetails);
        return GeneratorFactory.getNumberGenerator(
                targetType, min, max, precision, distribution, variation1, variation2, 0);
    }

    private static Generator<String> createStringGenerator(AttributeDescriptor descriptor, Set<String> usedDetails) {

        // evaluate max length
        Integer maxLength = (Integer) descriptor.getDeclaredDetailValue(MAX_LENGTH);
        if (maxLength != null) {
            usedDetails.add(MAX_LENGTH);
        } else {
            // maxLength was not set in this descriptor. So check the parent setting's value 
            // (it is interpreted as constraint which may be too high to be useful by default)
            maxLength = descriptor.getMaxLength();
            if (maxLength != null)
                usedDetails.add(MAX_LENGTH);
            else
                maxLength = (Integer) descriptor.getDetailDefault(MAX_LENGTH);
            if (maxLength == null || maxLength > 10000)
                maxLength = 10000;
        }

        // evaluate min length
        Integer minLength = descriptor.getMinLength();
        if (minLength == null) {
            int defaultMinLength = (Integer) descriptor.getDetailDefault(MIN_LENGTH);
            minLength = Math.min(maxLength, defaultMinLength);
        } else
            usedDetails.add(MIN_LENGTH);

        // evaluate pattern
        String pattern = descriptor.getPattern();
        if (pattern == null)
            pattern = (String) descriptor.getDetailDefault(PATTERN);
        else
            usedDetails.add(PATTERN);
        if (pattern == null)
            pattern = "[A-Z]{" + minLength + ',' + (maxLength != null ? maxLength : 30) + '}';

        // evaluate locale
        Locale locale = descriptor.getLocale();
        if (locale == null)
            locale = (Locale) descriptor.getDetailDefault(LOCALE);
        else
            usedDetails.add(LOCALE);

        // evaluate uniqueness and create generator
        boolean unique = isUnique(descriptor, usedDetails);
        if (unique)
            return GeneratorFactory.getUniqueRegexStringGenerator(pattern, minLength, maxLength, locale);
        else
            return GeneratorFactory.getRegexStringGenerator(pattern, minLength, maxLength, locale, 0);
    }

    private static <T> Generator<T> createValidatingGenerator(
            FeatureDescriptor descriptor, Generator<T> generator, Set<String> usedDetails) {
        Validator<T> validator = null;
        if (descriptor.getValidator() != null) {
            usedDetails.add(VALIDATOR);
            validator = (Validator<T>) descriptor.getValidator();
        } else if (descriptor instanceof AttributeDescriptor)
            validator = createAttributeConstraintValidator((AttributeDescriptor) descriptor, usedDetails);
        if (validator != null)
            generator = new ValidatingGeneratorProxy<T>(generator, validator);
        return generator;
    }

    private static <T> Validator<T> createAttributeConstraintValidator(
            AttributeDescriptor descriptor, Set<String> usedDetails) {
        if ((descriptor.getMinLength() != null || descriptor.getMaxLength() != null) && "string".equals(descriptor.getType())) {
            // TODO v0.5 check for number lengths?
            Integer minLength = getMinLength(descriptor, usedDetails);
            Integer maxLength = getMaxLength(descriptor, usedDetails);
            boolean nullable = false;
            if (descriptor.isNullable() != null) {
                usedDetails.add(NULLABLE);
                nullable = descriptor.isNullable();
            }
            return (Validator<T>)new StringLengthValidator(minLength, maxLength, nullable);
        } else if (descriptor.isNullable() != null) {
            usedDetails.add(NULLABLE);
            if (!descriptor.isNullable())
                return new NotNullValidator<T>();
        }
        return null;
    }

    private static final BeanDescriptorProvider beanDescriptorProvider = new BeanDescriptorProvider();
    
    private static <T> T javaClassFor(String type) {
        return (T)beanDescriptorProvider.concreteType(type);
    }

    // descriptor accessors --------------------------------------------------------------------------------------------

    private static Distribution getDistribution(AttributeDescriptor descriptor, Set<String> usedDetails) {
        Distribution distribution = descriptor.getDistribution();
        if (distribution != null)
            usedDetails.add(DISTRIBUTION);
        else if (isUnique(descriptor, usedDetails))
            distribution = Sequence.BIT_REVERSE;
        else
            distribution = Sequence.RANDOM;
        return distribution;
    }

    private static boolean isUnique(AttributeDescriptor descriptor, Set<String> usedDetails) {
        Boolean unique = descriptor.isUnique();
        if (unique == null)
            unique = false;
        else
            usedDetails.add(UNIQUE);
        return unique;
    }

    private static Locale getLocale(AttributeDescriptor descriptor, Set<String> usedDetails) {
        Locale locale = descriptor.getLocale();
        if (locale != null)
            usedDetails.add(LOCALE);
        else
            locale = (Locale) descriptor.getDetailDefault(LOCALE);
        if (locale == null)
            locale = LocaleUtil.getFallbackLocale();
        return locale;
    }

    private static double getNullQuota(AttributeDescriptor descriptor, Set<String> usedDetails) {
        Double nullQuota = descriptor.getNullQuota();
        if (nullQuota != null)
            usedDetails.add(NULL_QUOTA);
        else
            nullQuota = 0.;
        return nullQuota;
    }

    private static Integer getMaxLength(AttributeDescriptor descriptor,
            Set<String> usedDetails) {
        // evaluate max length
        Integer maxLength = (Integer) descriptor.getDeclaredDetailValue(MAX_LENGTH);
        if (maxLength != null) {
            usedDetails.add(MAX_LENGTH);
        } else {
            // maxLength was not set in this descriptor. So check the parent setting's value 
            // (it is interpreted as constraint which may be to high to be useful by default)
            maxLength = descriptor.getMaxLength();
            if (maxLength != null)
                usedDetails.add(MAX_LENGTH);
            else
                maxLength = (Integer) descriptor.getDetailDefault(MAX_LENGTH);
            if (maxLength > 10000)
                maxLength = 10000;
        }
        return maxLength;
    }

    private static Integer getMinLength(AttributeDescriptor descriptor,
            Set<String> usedDetails) {
        Integer minLength = descriptor.getMinLength();
        if (minLength != null)
            usedDetails.add(MIN_LENGTH);
        else
            minLength = 0;
        return minLength;
    }

    private ComponentGeneratorFactory() {}
    
    private static final GlobalIdProviderFactory GLOBAL_ID_PROVIDER_FACTORY = new GlobalIdProviderFactory();

    private static final Log logger = LogFactory.getLog(ComponentGeneratorFactory.class);
    
    // descriptor feature names ----------------------------------------------------------------------------------------
    
    private static final String TARGET_TYPE  = "target-type";
    private static final String NULLABLE     = "nullable";
    private static final String VALUES       = "values";
    private static final String SELECTOR     = "selector";
    private static final String MIN          = "min";
    private static final String MAX          = "max";
    private static final String PRECISION    = "precision";
    private static final String DISTRIBUTION = "distribution";
    private static final String VARIATION1   = "variation1";
    private static final String VARIATION2   = "variation2";
    private static final String CONVERTER    = "converter";
    private static final String SCRIPT       = "script";
    private static final String STRATEGY     = "strategy";
    private static final String PARAM        = "param";
    private static final String SCOPE        = "scope";

}
