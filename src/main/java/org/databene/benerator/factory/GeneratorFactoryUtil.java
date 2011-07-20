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

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.AttachedWeight;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.FeatureWeight;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.primitive.DynamicCountGenerator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.util.ExpressionBasedGenerator;
import org.databene.benerator.wrapper.AsStringGenerator;
import org.databene.benerator.wrapper.CollectionGenerator;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.benerator.wrapper.CyclicGeneratorProxy;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.benerator.wrapper.NShotGeneratorProxy;
import org.databene.benerator.wrapper.NonClosingGeneratorProxy;
import org.databene.benerator.wrapper.SimpleArrayGenerator;
import org.databene.benerator.wrapper.SimpleCompositeArrayGenerator;
import org.databene.benerator.wrapper.ValidatingGeneratorProxy;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.commons.Expression;
import org.databene.commons.LocaleUtil;
import org.databene.commons.ParseException;
import org.databene.commons.StringUtil;
import org.databene.commons.converter.MessageConverter;
import org.databene.commons.expression.DynamicExpression;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.commons.iterator.TextLineIterable;
import org.databene.commons.validator.StringLengthValidator;
import org.databene.document.csv.CSVCellIterable;
import org.databene.document.csv.CSVLineIterable;
import org.databene.model.data.FeatureDescriptor;
import org.databene.model.data.FeatureDetail;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.model.storage.StorageSystem;
import org.databene.platform.xls.XLSLineIterable;
import org.databene.regex.RegexParser;

import static org.databene.benerator.engine.DescriptorConstants.*;

/**
 * Provides utility methods for Generator factories.<br/><br/>
 * Created: 08.03.2008 09:39:05
 * @author Volker Bergmann
 */
public class GeneratorFactoryUtil {
	
    /**
     * Creates a generator that combines several products of a source generator to a collection.
     *
     * @param source             the generator that provides the array items
     * @param type               the type of the array
     * @param sizeDistribution   distribution for the array length
     * @return a generator of the desired characteristics
     */
    public static <T> Generator<T[]> createArrayGeneratorOfVariableLength(Generator<T> source, Class<T> type, 
            int minSize, int maxSize, Distribution sizeDistribution) {
        return new SimpleArrayGenerator<T>(source, type, minSize, maxSize, sizeDistribution);
    }

    /**
     * Creates a generator that combines several products of a source generator to a collection.
     *
     * @param collectionType     the type of collection to create, e.g. java.util.List or java.util.TreeSet
     * @param source             the generator that provides the collection items
     * @param sizeDistribution      distribution for the collection size
     * @return a generator of the desired characteristics
     */
    public static <C extends Collection<I>, I> Generator<C> createCollectionGeneratorOfVariableSize(
            Class<C> collectionType, Generator<I> source, 
            int minSize, int maxSize, Distribution sizeDistribution) {
        return new CollectionGenerator<C, I>(collectionType, source, minSize, maxSize, sizeDistribution);
    }



    // source generators -----------------------------------------------------------------------------------------------

    /**
     * Creates a generator that iterates through the cells of a CSV file.
     *
     * @param uri         the uri of the CSV file
     * @param separator   the cell separator used in the CSV file
     * @return a generator of the desired characteristics
     */
    public static Generator<String> createCSVCellGenerator(String uri, char separator, String encoding) {
        return new IteratingGenerator<String>(new CSVCellIterable(uri, separator));
    }

    /**
     * Creates a generator that creates lines from a CSV file as String arrays.
     *
     * @param uri              the uri of the CSV file
     * @param separator        the cell separator used in the CSV file
     * @param ignoreEmptyLines flag wether to leave out empty lines
     * @param encoding 
     * @return a generator of the desired characteristics
     */
    public static Generator<String[]> createCSVLineGenerator(String uri, char separator, boolean ignoreEmptyLines, String encoding) {
        return new IteratingGenerator<String[]>(new CSVLineIterable(uri, separator, ignoreEmptyLines, encoding));
    }

    /**
     * Creates a generator that creates lines from a XLS file as {@link Object} arrays.
     * @param uri the uri of the XLS file
     * @return a generator of the desired characteristics
     */
    public static Generator<Object[]> createXLSLineGenerator(String uri, boolean skipFirstRow) {
        return new IteratingGenerator<Object[]>(new XLSLineIterable(uri, skipFirstRow, null));
    }

    /**
     * Creates a generator that iterates through the lines of a text file.
     *
     * @param uri         the URI of the text file
     * @param cyclic      indicates whether iteration should restart from the first line after it reaches the file end.
     * @return a generator of the desired characteristics
     */
    public static Generator<String> createTextLineGenerator(String uri, boolean cyclic) {
        Generator<String> generator = new IteratingGenerator<String>(new TextLineIterable(uri));
        return DescriptorUtil.wrapWithProxy(generator, cyclic);
    }

	public static <T> Generator<T> wrapNonClosing(Generator<T> generator) {
		return new NonClosingGeneratorProxy<T>(generator);
	}

    // formatting generators -------------------------------------------------------------------------------------------

    /**
     * Creates a generator that accepts products from a source generator
     * and converts them to target products by the converter
     *
     * @param source    the source generator
     * @param converter the converter to apply to the products of the source generator
     * @return a generator of the desired characteristics
     */
    public static <S, T> Generator<T> createConvertingGenerator(Generator<S> source, Converter<S, T> converter) {
        return new ConvertingGenerator<S, T>(source, converter);
    }

    /**
     * Creates a generator that generates messages by reading the products of several source generators and
     * combining them by a Java MessageFormat.
     *
     * @param pattern   the MessageFormat pattern
     * @param minLength the minimum length of the generated value
     * @param maxLength the maximum length of the generated value
     * @param sources   the source generators of which to assemble the products
     * @return a generator of the desired characteristics
     * @see java.text.MessageFormat
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Generator<String> createMessageGenerator(
            String pattern, int minLength, int maxLength, Generator ... sources) {
        Generator<String> generator = new ConvertingGenerator<Object[], String>(
                new SimpleCompositeArrayGenerator<Object>(Object.class, sources), (Converter) new MessageConverter(pattern, null));
        generator = new ValidatingGeneratorProxy<String>(generator, new StringLengthValidator(minLength, maxLength));
        return generator;
    }

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
	public static Distribution getDistribution(String spec, Uniqueness uniqueness, boolean required, BeneratorContext context) {
        
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
	        Expression beanEx = BeneratorScriptParser.parseBeanSpec(spec);
	        return (Distribution) beanEx.evaluate(context);
        } catch (ParseException e) {
        	throw new ConfigurationError("Error parsing distribution spec: " + spec);
        }
	}

    
    public static Expression<Distribution> getDistributionExpression(
    		final String spec, final Uniqueness uniqueness, final boolean required) {
    	return new DynamicExpression<Distribution>() {

			public Distribution evaluate(Context context) {
	            return getDistribution(spec, uniqueness, required, (BeneratorContext) context);
            }
    		
    	};
    }

	public static Generator<Long> getCountGenerator(final InstanceDescriptor descriptor, boolean resetToMin, BeneratorContext context) {
    	Expression<Long> count = DescriptorUtil.getCount(descriptor);
    	if (count != null)
    		return new ExpressionBasedGenerator<Long>(count, Long.class);
    	else {
			final Expression<Long> minCount = DescriptorUtil.getMinCount(descriptor);
			final Expression<Long> maxCount = DescriptorUtil.getMaxCount(descriptor);
			if (minCount.isConstant() && maxCount.isConstant() && descriptor.getCountDistribution() == null) {
				Long minCountValue = minCount.evaluate(context);
				Long maxCountValue = maxCount.evaluate(context);
				if (minCountValue.equals(maxCountValue))
					return new ConstantGenerator<Long>(minCountValue);
			}
			final Expression<Long> countPrecision = DescriptorUtil.getCountPrecision(descriptor);
			final Expression<Distribution> countDistribution = 
				getDistributionExpression(descriptor.getCountDistribution(), Uniqueness.NONE, true);
			return new DynamicCountGenerator(minCount, maxCount, countPrecision, countDistribution, 
					ExpressionUtil.constant(false), resetToMin);
    	}
    }

	public static <T> Generator<T> createCyclicHeadGenerator(Generator<T> source) {
		return new CyclicGeneratorProxy<T>(new NShotGeneratorProxy<T>(source, 1));
	}

	@SuppressWarnings("unchecked")
	public static Generator<String>[] stringGenerators(Generator<?>[] sources) {
		Generator<String>[] result = new Generator[sources.length];
		for (int i = 0; i < sources.length; i++)
			result[i] = stringGenerator(sources[i]);
		return result;
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Generator<String> stringGenerator(Generator<?> source) {
		if (source.getGeneratedType() == String.class)
			return (Generator<String>) source;
		else
			return new AsStringGenerator(source);
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

}
