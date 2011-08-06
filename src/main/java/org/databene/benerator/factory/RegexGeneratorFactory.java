/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorProvider;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.wrapper.AlternativeGenerator;
import org.databene.benerator.wrapper.ConcatenatingGenerator;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.CharSet;
import org.databene.commons.CollectionUtil;
import org.databene.model.data.Uniqueness;
import org.databene.regex.Choice;
import org.databene.regex.CustomCharClass;
import org.databene.regex.Factor;
import org.databene.regex.Group;
import org.databene.regex.RegexParser;
import org.databene.regex.Sequence;

/**
 * Creates generators for regular expressions and their sub parts.<br/>
 * <br/>
 * Created: 17.11.2007 16:30:09
 * @author Volker Bergmann
 */
public class RegexGeneratorFactory {
	
    public static NonNullGenerator<String> create(String pattern, GeneratorFactory factory) {
        return create(pattern, 0, null, Uniqueness.NONE, factory);
    }

    public static NonNullGenerator<String> create(String pattern, int minLength, Integer maxLength, 
    		Uniqueness uniqueness, GeneratorFactory factory) {
    	if (pattern == null)
    		throw new IllegalArgumentException("Not a regular expression: null");
        Object regex = new RegexParser().parseRegex(pattern);
        return createFromObject(regex, minLength, maxLength, uniqueness, factory);
	}

    // private helpers -------------------------------------------------------------------------------------------------

    static NonNullGenerator<String> createFromObject(Object part, int minLength, Integer maxLength, 
    		Uniqueness uniqueness, GeneratorFactory factory) {
        if (part instanceof Factor)
            return createFromFactor((Factor) part, minLength, maxLength, uniqueness, factory);
        else 
        	return createFromObject(part, 1, 1, minLength, maxLength, uniqueness, factory);
    }

    private static NonNullGenerator<String> createFromFactor(Factor part, int minLength, Integer maxLength, 
    		Uniqueness uniqueness, GeneratorFactory factory) {
        int minCount = part.getQuantifier().getMin();
        Integer maxCount = part.getQuantifier().getMax();
        Object atom = part.getAtom();
        return createFromObject(atom, minCount, maxCount, minLength, maxLength, uniqueness, factory);
    }

    private static NonNullGenerator<String> createFromObject(Object object, int minCount, Integer maxCount, 
    		int minLength, Integer maxLength, Uniqueness uniqueness, GeneratorFactory factory) {
        if (object instanceof Factor)
            return createFromFactor((Factor) object, minLength, maxLength, uniqueness, factory);
        else if (object instanceof Character)
        	return createFromCharacter((Character) object, minCount, maxCount, minLength, maxLength, 
        			uniqueness.isUnique(), factory);
        else if (object instanceof CharSet)
        	return createCharSetGenerator((CharSet) object, minCount, maxCount, minLength, maxLength, 
        			uniqueness, factory);
        else if (object instanceof CustomCharClass)
        	return createFromCustomCharClass((CustomCharClass) object, minCount, maxCount, minLength, maxLength, 
        			uniqueness, factory);
        else if (object instanceof Sequence)
            return createFromSequence((Sequence) object, minCount, maxCount, minLength, maxLength, uniqueness, factory);
        else if (object instanceof Group)
            return createFromGroup((Group) object, minCount, maxCount, minLength, maxLength, uniqueness, factory);
        else if (object instanceof Choice)
            return createFromChoice((Choice) object, minCount, maxCount, minLength, maxLength, uniqueness, factory);
        else if (object instanceof String)
        	return WrapperFactory.asNonNullGenerator(factory.createSingleValueGenerator(
        			(String) object, uniqueness.isUnique()));
        else if (object == null)
        	return WrapperFactory.asNonNullGenerator(new ConstantGenerator<String>(null, String.class));
        else
            throw new UnsupportedOperationException("Unsupported regex part type: " + object.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    private static NonNullGenerator<String> createFromSequence(Sequence sequence, int minCount, Integer maxCount, 
    		int minLength, Integer maxLength, Uniqueness uniqueness, GeneratorFactory factory) {
    	Object[] factors = sequence.getFactors();
		Generator<String>[] componentGenerators = createComponentGenerators(
				factors, minLength, maxLength, uniqueness, factory);
    	Generator<String[]> partGenerator = factory.createCompositeArrayGenerator(
    			String.class, componentGenerators, uniqueness);
    	return WrapperFactory.asNonNullGenerator(new ConcatenatingGenerator(partGenerator));
    }

    @SuppressWarnings("rawtypes")
	static NonNullGenerator[] createComponentGenerators(Object[] factors, int minLength, Integer maxLength, 
			Uniqueness uniqueness, GeneratorFactory factory) {
    	NonNullGenerator<?>[] components = new NonNullGenerator<?>[factors.length];
    	for (int i = 0; i < factors.length; i++)
    		components[i] = createFromObject(factors[i], minLength, maxLength, uniqueness, factory);
	    return components;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static NonNullGenerator<String> createFromChoice(
            final Choice choice, final int minCount, final int maxCount, final int minLength, final Integer maxLength, 
            final Uniqueness uniqueness, final GeneratorFactory factory) {
    	final Object[] alternatives = choice.getAlternatives();
    	GeneratorProvider<String> generatorProvider = new GeneratorProvider<String>() {
			public Generator<String> create() {
		    	final Generator[] altGens = createComponentGenerators(
		    			alternatives, minLength, maxLength, uniqueness, factory);
				return new AlternativeGenerator<String>(String.class, altGens);
			}
		};
    	return factory.createCompositeStringGenerator(generatorProvider, minCount, maxCount, uniqueness);
    }

	private static NonNullGenerator<String> createFromGroup(
            final Group group, final int minCount, final Integer maxCount, 
            final int minLength, final Integer maxLength, 
            final Uniqueness uniqueness, final GeneratorFactory factory) {
		GeneratorProvider<String> partGeneratorProvider = new GeneratorProvider<String>() {
			
			public Generator<String> create() {
				return createFromObject(group.getRegex(), minLength, maxLength, uniqueness, factory);
			}
		};
		return factory.createCompositeStringGenerator(partGeneratorProvider, minCount, maxCount, uniqueness);
    }

    private static NonNullGenerator<String> createFromCharacter(char c, int minCount, Integer maxCount, 
    		int minLength, Integer maxLength, boolean unique, GeneratorFactory factory) {
    	DefaultsProvider defaultsProvider = factory.getDefaultsProvider();
		int minReps = max(minLength, minCount, defaultsProvider.defaultMinLength()); 
		int maxReps = min(maxLength, maxCount, defaultsProvider.defaultMaxLength()); 
		return factory.createStringGenerator(CollectionUtil.toSet(c), minReps, maxReps, 1, null, (unique ? Uniqueness.ORDERED : Uniqueness.NONE));
    }
    
	private static NonNullGenerator<String> createCharSetGenerator(
			CharSet charSet, int minCount, Integer maxCount, int minLength, Integer maxLength, 
			Uniqueness uniqueness, GeneratorFactory factory) {
		DefaultsProvider defaultsProvider = factory.getDefaultsProvider();
		int minReps = max(minLength, minCount, defaultsProvider.defaultMinLength()); 
		int maxReps = min(maxLength, maxCount, defaultsProvider.defaultMaxLength());
		return factory.createStringGenerator(charSet.getSet(), minReps, maxReps, 1, null, uniqueness);
    }
    
    private static NonNullGenerator<String> createFromCustomCharClass(CustomCharClass ccc, 
    		int minCount, Integer maxCount, int minLength, Integer maxLength, 
    		Uniqueness uniqueness, GeneratorFactory factory) {
    	CharSet charSet = ccc.getCharSet();
		return createCharSetGenerator(charSet, minCount, maxCount, minLength, maxLength, uniqueness, factory);
    }
    
	private static int min(Integer v1, Integer v2, int defaultValue) {
		if (v1 != null) {
			if (v2 != null)
				return Math.min(v1, v2);
			else
				return v1;
		} else if (v2 != null)
			return v2;
		else
			return defaultValue;
	}

    private static int max(Integer v1, Integer v2, int defaultValue) {
		if (v1 != null) {
			if (v2 != null)
				return Math.max(v1, v2);
			else
				return v1;
		} else if (v2 != null)
			return v2;
		else
			return defaultValue;
	}

}
