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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.csv.DatasetCSVGenerator;
import org.databene.benerator.primitive.ScriptGenerator;
import org.databene.benerator.sample.SequencedSampleGenerator;
import org.databene.benerator.sample.WeightedSampleGenerator;
import org.databene.benerator.wrapper.AccessingGenerator;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.benerator.wrapper.ValidatingGeneratorProxy;
import org.databene.commons.ArrayUtil;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.commons.LocaleUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.Validator;
import org.databene.commons.accessor.GraphAccessor;
import org.databene.commons.iterator.DefaultTypedIterable;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Iteration;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;
import org.databene.model.function.WeightFunction;
import org.databene.model.storage.StorageSystem;
import org.databene.platform.csv.CSVCellIterable;
import org.databene.platform.csv.CSVEntityIterable;
import org.databene.script.Script;
import org.databene.script.ScriptConverter;
import org.databene.script.ScriptUtil;
import static org.databene.model.data.TypeDescriptor.*;
import static org.databene.benerator.factory.GeneratorFactoryUtil.*;

/**
 * Creates generators of type instances.<br/><br/>
 * Created: 05.03.2008 16:51:44
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class TypeGeneratorFactory {
    
    private static final Log logger = LogFactory.getLog(TypeGeneratorFactory.class);

    public static Generator<? extends Object> createTypeGenerator(TypeDescriptor descriptor, boolean unique, Context context, GenerationSetup setup) {
    	if (logger.isDebugEnabled())
    		logger.debug(descriptor + ", " + unique);
        if (descriptor instanceof SimpleTypeDescriptor)
            return SimpleTypeGeneratorFactory.create((SimpleTypeDescriptor) descriptor, unique, context, setup);
        else if (descriptor instanceof ComplexTypeDescriptor)
            return ComplexTypeGeneratorFactory.createComplexTypeGenerator((ComplexTypeDescriptor) descriptor, context, setup);
        else
            throw new UnsupportedOperationException("Descriptor type not supported: " + descriptor.getClass());
    }

    protected static Generator<? extends Object> createByGeneratorName(TypeDescriptor descriptor, Context context) {
        Generator<? extends Object> generator = null;
        String generatorClassName = descriptor.getGenerator();
        if (generatorClassName != null) {
            generator = BeanUtil.newInstance(generatorClassName);
            mapDetailsToBeanProperties(descriptor, generator, context);
        }
        return generator;
    }

    protected static Generator<? extends Object> createSampleGenerator(TypeDescriptor descriptor, boolean unique) {
        Generator<? extends Object> generator = null;
        // check for samples
        String[] values = descriptor.getValues();
        if (!ArrayUtil.isEmpty(values)) {
            Distribution distribution = getDistribution(descriptor, unique);
            if (distribution instanceof Sequence)
                generator = new SequencedSampleGenerator<String>(String.class, (Sequence) distribution, values);
            else if (distribution instanceof WeightFunction)
                generator = new WeightedSampleGenerator<String>(String.class, (WeightFunction) distribution, values);
            else
                throw new ConfigurationError("Unsupported distribution type: " + distribution.getClass());
        }
        return generator;
    }

    protected static Generator<? extends Object> createScriptGenerator(
            TypeDescriptor descriptor, Context context, String defaultEngineId) {
        Generator<String> generator = null;
        String scriptText = descriptor.getScript();
        if (scriptText != null) {
            Script script = ScriptUtil.parseUnspecificText(scriptText, defaultEngineId);
            generator = new ScriptGenerator(script, context);
        }
        return generator;
    }

    protected static Generator<? extends Object> createSourceAttributeGenerator(TypeDescriptor descriptor, Context context, GenerationSetup setup) {
        String source = descriptor.getSource();
        if (source == null)
            return null;
        String lcn = source.toLowerCase();
        String selector = descriptor.getSelector();
        Generator<? extends Object> generator;
        if (context.get(source) != null) {
            Object sourceObject = context.get(source);
            if (sourceObject instanceof StorageSystem)
                generator = new IteratingGenerator(((StorageSystem) sourceObject).query(selector));
            else if (sourceObject instanceof Generator)
                generator = (Generator) sourceObject;
            else
                throw new UnsupportedOperationException("Not a supported source: " + sourceObject);
        } else if (lcn.endsWith(".csv")) {
            char separator = ',';
            if (descriptor.getSelector() != null && descriptor.getSelector().length() == 1) {
                separator = descriptor.getSelector().charAt(0);
            }
            String encoding = descriptor.getEncoding();
            if (encoding == null)
                encoding = SystemInfo.fileEncoding();
            String dataset = descriptor.getDataset();
            String nesting = descriptor.getNesting();
            ScriptConverter scriptConverter = new ScriptConverter(context, setup.getDefaultScript());
            Iterable iterable = null;
            if (descriptor instanceof ComplexTypeDescriptor) {
                iterable = new CSVEntityIterable(source, descriptor.getName(), scriptConverter, separator, encoding);
                generator = new IteratingGenerator<String>(new DefaultTypedIterable<String>(String.class, iterable));
            } else {
                if (dataset != null && nesting != null) {
                    generator = new DatasetCSVGenerator(source, dataset, nesting, encoding);
                } else {
                    iterable = new CSVCellIterable(source, separator);
                    generator = new IteratingGenerator<String>(new DefaultTypedIterable<String>(String.class, iterable));
                }
            }
        } else if (lcn.endsWith(".txt")) {
            generator = GeneratorFactory.getTextLineGenerator(source, false, null, null, null);
        } else {
            generator = new AccessingGenerator(Object.class, new GraphAccessor(source), context);
        }

        // check distribution
        Distribution distribution = descriptor.getDistribution();
        if (distribution != null) {
            List<Object> values = new ArrayList<Object>();
            while (generator.available()) {
                Object value = generator.generate();
                values.add(value);
            }
            if (distribution instanceof Sequence)
                generator = new SequencedSampleGenerator(generator.getGeneratedType(), (Sequence) distribution, values);
            else if (distribution instanceof WeightFunction)
                generator = new WeightedSampleGenerator(generator.getGeneratedType(), (WeightFunction) distribution, values);
            else
                throw new UnsupportedOperationException("Distribution type not supported: " + distribution.getClass());
        }
//        generator = createConvertingGenerator(descriptor, generator);
        return createProxy(descriptor, generator);
    }

    private static <S, T> Converter<S, T> getConverter(TypeDescriptor descriptor) {
        String converterClass = descriptor.getConverter();
        if (converterClass == null)
            return null;
        return BeanUtil.newInstance(converterClass);
    }
/*
    public static void checkUsedDetails(TypeDescriptor descriptor,
            Set<String> usedDetails) {
        for (FeatureDetail<? extends Object> detail : descriptor.getDetails()) {
            String name = detail.getName();
            if (!NAME.equals(name) && detail.getValue() != null
                    && !usedDetails.contains(name))
                logger.debug("Ignored detail: " + detail + " in descriptor "
                        + descriptor); // TODO v1.0 improve tracking of unused features
        }
    }
*/
    protected static <T> Generator<T> createProxy(TypeDescriptor descriptor,
            Generator<T> generator) {
        // check cyclic flag
        Boolean cyclic = descriptor.isCyclic();
        if (cyclic == null)
            cyclic = false;

        // check proxy
        Long proxyParam1 = null;
        Long proxyParam2 = null;
        Iteration iteration = descriptor.getProxy();
        if (iteration != null) {
            proxyParam1 = descriptor.getProxyParam1();
            proxyParam2 = descriptor.getProxyParam2();
        }
        return GeneratorFactory.createProxy(generator, cyclic, iteration,
                proxyParam1, proxyParam2);
    }

    protected static <T> Generator<T> createValidatingGenerator(
            TypeDescriptor descriptor, Generator<T> generator) {
        Validator<T> validator = null;
        String validatorName = descriptor.getValidator();
        if (validatorName != null) {
            validator = BeanUtil.newInstance(validatorName);
            generator = new ValidatingGeneratorProxy<T>(generator, validator);
        }
        return generator;
    }

    protected static Locale getLocale(TypeDescriptor descriptor) {
        Locale locale = descriptor.getLocale();
        if (locale == null)
            locale = (Locale) descriptor.getDetailDefault(LOCALE);
        if (locale == null)
            locale = LocaleUtil.getFallbackLocale();
        return locale;
    }

    protected static DateFormat getPatternAsDateFormat(TypeDescriptor descriptor) {
        String pattern = descriptor.getPattern();
        if (pattern != null)
            return new SimpleDateFormat(pattern);
        return DateFormat.getDateInstance(DateFormat.SHORT, getLocale(descriptor));
    }

    protected static Generator createConvertingGenerator(TypeDescriptor descriptor, Generator generator) {
        if (getConverter(descriptor) != null) {
            Converter converter = getConverter(descriptor);
            if (descriptor.getPattern() != null && BeanUtil.hasProperty(converter.getClass(), PATTERN)) {
                BeanUtil.setPropertyValue(converter, PATTERN, descriptor.getPattern(), false);
            }
            generator = GeneratorFactory.getConvertingGenerator(generator, converter);
        }
        return generator;
    }

    protected static Distribution getDistribution(TypeDescriptor descriptor, boolean unique) {
        Distribution distribution = descriptor.getDistribution();
        if (distribution == null)
            distribution = (unique ? Sequence.BIT_REVERSE : Sequence.RANDOM);
        return distribution;
    }
}
