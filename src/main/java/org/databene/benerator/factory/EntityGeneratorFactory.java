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

import org.databene.model.data.*;
import org.databene.model.system.System;
import org.databene.model.*;
import org.databene.model.accessor.GraphAccessor;
import org.databene.model.validator.StringLengthValidator;
import org.databene.model.validator.NotNullValidator;
import org.databene.model.converter.ParseFormatConverter;
import org.databene.model.converter.String2DateConverter;
import org.databene.model.converter.AnyConverter;
import org.databene.benerator.*;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.sample.SequencedSampleGenerator;
import org.databene.benerator.sample.WeightedSampleGenerator;
import org.databene.benerator.composite.EntityGenerator;
import org.databene.benerator.wrapper.*;
import org.databene.task.TaskContext;
import org.databene.commons.*;
import org.databene.platform.dbunit.DBUnitEntityIterable;
import org.databene.platform.csv.CSVEntityIterable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormat;
import java.io.IOException;

/**
 * Creates entity generators from entity metadata.<br/>
 * <br/>
 * Created: 08.09.2007 07:45:40
 */
public class EntityGeneratorFactory {

    // TODO separate functionality entityGen/attribGen/common

    private static final String TARGET_TYPE = "target-type";
    private static final String VALUES      = "values";
    private static final String SELECTOR    = "selector";
    private static final String SOURCE      = "source";
    private static final String GENERATOR   = "generator";
    private static final String NULL_QUOTA  = "nullQuota";
    private static final String TYPE        = "type";
    private static final String VALIDATOR   = "validator";
    private static final String TRUE_QUOTA  = "trueQuota";
    private static final String PATTERN     = "pattern";
    private static final String LOCALE      = "locale";
    private static final String MIN_LENGTH  = "minLength";
    private static final String MAX_LENGTH  = "maxLength";
    private static final String REGION      = "region";
    private static final String COUNT       = "count";
    private static final String UNIQUE      = "unique";

    private static final Log logger = LogFactory.getLog(EntityGeneratorFactory.class);

    public static Generator<Entity> createEntityGenerator(EntityDescriptor descriptor, TaskContext context) {
        Set<String> usedDetails = new HashSet<String>();
        // create original generator
        Generator<Entity> generator = createSourceEntityGenerator(descriptor, context, usedDetails);
        if (generator != null)
            generator = createMutatingEntityGenerator(descriptor, context, generator);
        else
            generator = createGeneratingEntityGenerator(descriptor, context);
        // create wrappers
        generator = createValidatingGenerator(descriptor, generator, usedDetails);
        generator = createLimitCountGenerator(descriptor, generator, usedDetails);
        checkUsedDetails(descriptor, usedDetails);
        return generator;
    }

    private static Generator<Entity> createLimitCountGenerator(EntityDescriptor descriptor, Generator<Entity> generator, Set<String> usedDetails) {
        if (descriptor.getCount() != null) {
            usedDetails.add(COUNT);
            generator = new NShotGeneratorProxy(generator, descriptor.getCount());
        }
        return generator;
    }

    private static Generator<Entity> createGeneratingEntityGenerator(EntityDescriptor descriptor, TaskContext context) {
        Map<String, Generator> componentGenerators = new HashMap<String, Generator>();
        Collection<ComponentDescriptor> descriptors = descriptor.getComponentDescriptors();
        for (ComponentDescriptor component : descriptors) {
            if (component.getMode() != Mode.ignored) {
                Generator componentGenerator = ComponentGeneratorFactory.getComponentGenerator(component, context);
                componentGenerators.put(component.getName(), componentGenerator);
            }
        }
        return new EntityGenerator(descriptor, componentGenerators);
    }

    private static Generator<Entity> createMutatingEntityGenerator(
            EntityDescriptor descriptor, TaskContext context, Generator<Entity> generator) {
        Map<String, Generator> componentGenerators = new HashMap<String, Generator>();
        Collection<ComponentDescriptor> descriptors = descriptor.getDeclaredComponentDescriptors();
        for (ComponentDescriptor component : descriptors) {
            if (component.getMode() != Mode.ignored) {
                Generator componentGenerator = ComponentGeneratorFactory.getComponentGenerator(component, context);
                componentGenerators.put(component.getName(), componentGenerator);
            }
        }
        return new EntityGenerator(descriptor, generator, componentGenerators);
    }

    public static Generator createAttributeGenerator(AttributeDescriptor descriptor, TaskContext context) {
        Generator generator = null;
        Set<String> usedDetails = new HashSet<String>();
        // create a source generator
        generator = createGeneratorByClass(descriptor, usedDetails, generator);
        if (generator == null)
            generator = createSampleGenerator(descriptor, usedDetails, generator);
        if (generator == null)
            generator = createSourceAttributeGenerator(descriptor, context, usedDetails);
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
        checkUsedDetails(descriptor, usedDetails);
        return generator;
    }

    public static Generator createReferenceGenerator(ReferenceDescriptor descriptor, TaskContext context) {
        Set<String> usedDetails = new HashSet<String>();
        Generator generator = null;
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
            org.databene.model.system.System sourceSystem = (org.databene.model.system.System) context.get(sourceName);
            generator = new IteratingGenerator(sourceSystem.getIds(targetTye, selector));
        } else
            generator = new ConstantGenerator(null);
        generator = createValidatingGenerator(descriptor, generator, usedDetails);
        checkUsedDetails(descriptor, usedDetails);
        return generator;
    }

    private static Generator createSampleGenerator(AttributeDescriptor descriptor, Set<String> usedDetails, Generator generator) {
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

    private static Generator createGeneratorByClass(AttributeDescriptor descriptor, Set<String> usedDetails, Generator generator) {
        String generatorClassName = descriptor.getGenerator();
        if (generatorClassName != null) {
            usedDetails.add(GENERATOR);
            generator = (Generator) BeanUtil.newInstance(generatorClassName);
            if (descriptor.getLocale() != null) {
                usedDetails.add(LOCALE);
                BeanUtil.setPropertyValue(generator, "locale", descriptor.getLocale(), false);
            }
            if (descriptor.getRegion() != null) {
                usedDetails.add(REGION);
                BeanUtil.setPropertyValue(generator, "region", descriptor.getRegion(), false);
            }
        }
        return generator;
    }

    private static <T> Generator createNullQuotaGenerator(
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

    private static Generator createTypeGenerator(AttributeDescriptor descriptor, Generator generator, Set<String> usedDetails) {
        if (descriptor.getType() == null)
            return generator;
        usedDetails.add(TYPE);
        Class targetType = javaClassFor(descriptor.getType());
        return createTypeGenerator(targetType, descriptor, usedDetails);
    }

    private static <T> Generator<T> createTypeConvertingGenerator(
            AttributeDescriptor descriptor, Generator generator, Set<String> usedDetails) {
        if (descriptor.getType() == null)
            return generator;
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
        return new ConvertingGenerator<Object, T>(generator, converter);
    }

    private static <T> Generator<T> createValidatingGenerator(
            FeatureDescriptor descriptor, Generator<T> generator, Set<String> usedDetails) {
        Validator<T> validator = null;
        if (descriptor.getValidator() != null) {
            usedDetails.add(VALIDATOR);
            validator = descriptor.getValidator();
        } else if (descriptor instanceof AttributeDescriptor)
            validator = createAttributeConstraintValidator((AttributeDescriptor) descriptor, usedDetails);
        if (validator != null)
            generator = new ValidatingGeneratorProxy<T>(generator, validator);
        return generator;
    }

    private static Validator createAttributeConstraintValidator(
            AttributeDescriptor descriptor, Set<String> usedDetails) {
        if ("string".equals(descriptor.getType()) && (descriptor.getMinLength() != null || descriptor.getMaxLength() != null)) {
            // TODO v0.4 check for number lengths?
            Integer minLength = descriptor.getMinLength();
            if (minLength != null)
                usedDetails.add("minLength");
            else
                minLength = 0;
            Integer maxLength = descriptor.getMaxLength();
            if (maxLength != null)
                usedDetails.add("maxLength");
            boolean nullable = false;
            if (descriptor.isNullable() != null) {
                usedDetails.add("nullable");
                nullable = descriptor.isNullable();
            }
            return new StringLengthValidator(minLength, maxLength, nullable);
        } else if (descriptor.isNullable() != null) {
            usedDetails.add("nullable");
            if (!descriptor.isNullable())
                return new NotNullValidator();
        }
        return null;
    }

    private static Generator<Entity> createSourceEntityGenerator(
            EntityDescriptor descriptor, TaskContext context, Set<String> usedDetails) {
        // if no sourceObject is specified, there's nothing to do
        String sourceName = descriptor.getSource();
        if (sourceName == null)
            return null;
        usedDetails.add(SOURCE);
        // create sourceObject generator
        Generator<Entity> generator = null;
        Object sourceObject = context.get(sourceName);
        if (sourceObject != null) {
            if (sourceObject instanceof System) {
                System system = (System) sourceObject;
                generator = new IteratingGenerator<Entity>(system.getEntities(descriptor.getName()));
            } else if (sourceObject instanceof TypedIterable) {
                generator = new IteratingGenerator((TypedIterable) sourceObject);
            } else if (sourceObject instanceof Generator) {
                generator = (Generator) sourceObject;
            } else
                throw new UnsupportedOperationException("Source type not supported: " + sourceObject.getClass());
        } else {
            if (sourceName.endsWith(".xml"))
                generator = new IteratingGenerator<Entity>(new DBUnitEntityIterable(sourceName));
            else if (sourceName.endsWith(".csv")) {
                String encoding = descriptor.getEncoding();
                if (encoding != null)
                    usedDetails.add("encoding");
                else
                    encoding = SystemInfo.fileEncoding();
                generator = new IteratingGenerator(new CSVEntityIterable(sourceName, descriptor.getName(), ',', encoding));
            } else
                throw new UnsupportedOperationException("Unknown source type: " + sourceName);
        }
        return createProxy(descriptor, generator, usedDetails);
    }

    private static <T> Generator<T> createProxy(FeatureDescriptor descriptor, Generator<T> generator, Set<String> usedDetails) {
        // check cyclic flag
        Boolean cyclic = descriptor.isCyclic();
        if (cyclic == null)
            cyclic = false;
        else
            usedDetails.add("cyclic");

// check proxy
        Long proxyParam1 = null;
        Long proxyParam2 = null;
        Iteration iteration = descriptor.getProxy();
        if (iteration != null) {
            usedDetails.add("proxy");
            proxyParam1 = descriptor.getProxyParam1();
            if (proxyParam1 != null)
                usedDetails.add("proxy-param1");
            proxyParam2 = descriptor.getProxyParam2();
            if (proxyParam2 != null)
                usedDetails.add("proxy-param2");
        }
        return GeneratorFactory.createProxy(generator, cyclic, iteration, proxyParam1, proxyParam2);
    }

    private static Generator createSourceAttributeGenerator(AttributeDescriptor descriptor, TaskContext context, Set<String> usedDetails) {
        String source = descriptor.getSource();
        if (source == null)
            return null;
        usedDetails.add(SOURCE);
        String lcn = source.toLowerCase();
        String selector = descriptor.getSelector();
        if (selector != null)
            usedDetails.add(SELECTOR);
        Generator generator;
        if (context.get(source) != null) {
            Object sourceObject = context.get(source);
            if (sourceObject instanceof org.databene.model.system.System)
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
                usedDetails.add("encoding");
            else
                encoding = SystemInfo.fileEncoding();
            // TODO check wether to import Entities or cells
            generator = new IteratingGenerator(new CSVEntityIterable(source, type, separator, encoding));
        } else if (lcn.endsWith(".txt")) {
            generator = GeneratorFactory.getTextLineGenerator(source, false, null, null, null);
        } else {
            generator = new AccessingGenerator(Object.class, new GraphAccessor(source), context);
        }

        // check distribution
        Distribution distribution = descriptor.getDistribution();
        if (distribution != null) {
            usedDetails.add("distribution");
            List values = new ArrayList();
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

    private static Generator createConvertingGenerator(AttributeDescriptor descriptor, Generator generator, Set<String> usedDetails) {
        if (descriptor.getConverter() != null) {
            usedDetails.add("converter");
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

    private static Generator createTypeGenerator(Class targetType, AttributeDescriptor descriptor, Set<String> usedDetails) {
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
        } else
            return null;
    }

    private static Generator createDateGenerator(AttributeDescriptor descriptor, Set<String> usedDetails) {
        Date min = parseDate(descriptor, "min", TimeUtil.date(1970, 0, 1), usedDetails);
        Date max = parseDate(descriptor, "max", TimeUtil.today().getTime(), usedDetails);
        Date precisionDate = parseDate(descriptor, "precision", TimeUtil.date(1970, 0, 2), usedDetails);
        long precision = precisionDate.getTime() - TimeUtil.date(1970, 0, 1).getTime();
        Distribution distribution = getDistribution(descriptor, usedDetails);
        return GeneratorFactory.getDateGenerator(min, max, precision, distribution, 0);
    }

    private static Generator createCharacterGenerator(AttributeDescriptor descriptor, Set<String> usedDetails) {
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
        T min = getNumberDetail(descriptor, "min", targetType, usedDetails);
        T max = getNumberDetail(descriptor, "max", targetType, usedDetails);
        if (min.equals(max)) {
            return new ConstantGenerator<T>(min);
        }
        T precision = getNumberDetail(descriptor, "precision", targetType, usedDetails);
        Distribution distribution = getDistribution(descriptor, usedDetails);
        T variation1 = getNumberDetail(descriptor, "variation1", targetType, usedDetails);
        T variation2 = getNumberDetail(descriptor, "variation2", targetType, usedDetails);
        return GeneratorFactory.getNumberGenerator(
                targetType, min, max, precision, distribution, variation1, variation2, 0);
    }

    private static Generator createStringGenerator(AttributeDescriptor descriptor, Set<String> usedDetails) {
        // evaluate min length
        Integer minLength = descriptor.getMinLength();
        if (minLength == null)
            minLength = (Integer) descriptor.getDetailDefault(MIN_LENGTH);
        else
            usedDetails.add(MIN_LENGTH);

        // evaluate max length
        Integer maxLength = descriptor.getMaxLength();
        if (maxLength == null)
            maxLength = (Integer) descriptor.getDetailDefault(MAX_LENGTH);
        else
            usedDetails.add(MAX_LENGTH);

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

    // helpers ---------------------------------------------------------------------------------------------------------

    private static Map<String, Class> classMappings;

    static {
        classMappings = new HashMap<String, Class>();
        try {
            Properties props = IOUtil.readProperties("org/databene/platform/bean/types.properties");
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                String abstractTypeName = (String) entry.getKey();
                String concreteTypeName = (String) entry.getValue();
                Class<?> concreteType = BeanUtil.forName(concreteTypeName);
                classMappings.put(abstractTypeName, concreteType);
            }
        } catch (IOException e) {
            throw new ConfigurationError(e);
        }
    }

    private static Class javaClassFor(String type) {
        try {
            Class result = classMappings.get(type);
            if (result == null)
                result = Class.forName(type);
            return result;
        } catch (ClassNotFoundException e) {
            throw new ConfigurationError("No class mapping found for '" + type + "'", e);
        }
    }

    private static void checkUsedDetails(FeatureDescriptor descriptor, Set<String> usedDetails) {
        for (FeatureDetail detail : descriptor.getDetails()) {
            String name = detail.getName();
            if (!"name".equals(name) && detail.getValue() != null && !usedDetails.contains(name))
                logger.warn("Ignored detail: " + detail + " in descriptor " + descriptor);
        }
    }

    // descriptor accessors --------------------------------------------------------------------------------------------

    private static Distribution getDistribution(AttributeDescriptor descriptor, Set<String> usedDetails) {
        Distribution distribution = descriptor.getDistribution();
        if (distribution != null)
            usedDetails.add("distribution");
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

}
