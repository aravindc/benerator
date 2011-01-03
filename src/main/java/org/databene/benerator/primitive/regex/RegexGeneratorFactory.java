/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive.regex;

import java.text.ParseException;

import org.databene.benerator.Generator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.sample.OneShotGenerator;
import org.databene.benerator.wrapper.CompositeArrayGenerator;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.benerator.wrapper.AlternativeGenerator;
import org.databene.benerator.wrapper.UniqueAlternativeGenerator;
import org.databene.benerator.primitive.ConcatenatingGenerator;
import org.databene.benerator.primitive.UniqueStringGenerator;
import org.databene.benerator.primitive.CharacterGenerator;
import org.databene.commons.CharSet;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.converter.ToStringConverter;
import org.databene.regex.*;

/**
 * Creates generators for regular expressions and their sub parts.<br/>
 * <br/>
 * Created: 17.11.2007 16:30:09
 * @author Volker Bergmann
 */
public class RegexGeneratorFactory {
	
	public static final int DEFAULT_QUANTITY_LIMIT = 10;

    public static Generator<String> create(String pattern) {
        return create(pattern, DEFAULT_QUANTITY_LIMIT, false);
    }

    public static Generator<String> create(String pattern, int quantityLimit, boolean unique) {
		try {
	        Object regex = new RegexParser().parseRegex(pattern);
	        return createFromObject(regex, quantityLimit, unique);
        } catch (ParseException e) {
        	throw new ConfigurationError("Error creating RegexGenerator for pattern: " + pattern, e);
        }
	}

    // private helpers -------------------------------------------------------------------------------------------------

    private static Generator<String> createFromObject(Object part, int quantityLimit, boolean unique) {
        if (part instanceof Factor)
            return createFromFactor((Factor) part, quantityLimit, unique);
        else 
        	return createFromObject(part, 1, 1, quantityLimit, unique);
    }

    private static Generator<String> createFromFactor(Factor part, int quantityLimit, boolean unique) {
        int min = part.getQuantifier().getMin();
        Integer max = part.getQuantifier().getMax();
        if (max == null)
            max = quantityLimit;
        Object atom = part.getAtom();
        return createFromObject(atom, min, max, quantityLimit, unique);
    }

    private static Generator<String> createFromObject(Object object, int min, int max, int quantityLimit, boolean unique) {
        if (object instanceof Factor)
            return createFromFactor((Factor) object, quantityLimit, unique);
        else if (object instanceof Character)
        	return createFromCharacter((Character) object, min, max, unique);
        else if (object instanceof CharSet)
        	return createCharSetGenerator((CharSet) object, min, max, unique);
        else if (object instanceof CustomCharClass)
        	return createFromCustomCharClass((CustomCharClass) object, min, max, unique);
        else if (object instanceof Sequence)
            return createFromSequence((Sequence) object, min, max, quantityLimit, unique);
        else if (object instanceof Group)
            return createFromGroup((Group) object, min, max, quantityLimit, unique);
        else if (object instanceof Choice)
            return createFromChoice((Choice) object, min, max, quantityLimit, unique);
        else if (object instanceof String)
        	return new ConstantGenerator<String>((String) object);
        else if (object == null)
        	return new ConstantGenerator<String>(null);
        else
            throw new UnsupportedOperationException("Unsupported regex part type: " + object.getClass().getName());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Generator<String> createFromSequence(Sequence sequence, int min, int max, int quantityLimit, boolean unique) {
    	Object[] factors = sequence.getFactors();
		Generator[] components = createComponents(factors, quantityLimit, unique);
    	Generator<String[]> partGenerator = new CompositeArrayGenerator<String>(String.class, unique, components);
    	return new ConcatenatingGenerator(partGenerator);
    }

    @SuppressWarnings("rawtypes")
	private static Generator[] createComponents(Object[] factors, int quantityLimit, boolean unique) {
	    Generator<?>[] components = new Generator<?>[factors.length];
    	for (int i = 0; i < factors.length; i++)
    		components[i] = createFromObject(factors[i], quantityLimit, unique);
	    return components;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Generator<String> createFromChoice(
            Choice choice, int min, int max, int quantityLimit, boolean unique) {
    	Object[] alternatives = choice.getAlternatives();
    	Generator[] altGens = createComponents(alternatives, quantityLimit, unique);
        if (unique) {
            if (min == 1 && max == 1)
                return new UniqueAlternativeGenerator<String>(String.class, altGens);
            else
                return createUniqueFromChoice(choice, min, max, quantityLimit);
        } else
            return repeater(new AlternativeGenerator<String>(String.class, altGens), min, max);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Generator<String> createUniqueFromChoice(Choice choice, int min, int max, int quantityLimit) {
        Generator[] sources = new Generator[max - min + 1];
        for (int length = min; length <= max; length++) {
            Generator<String>[] subGens = new Generator[length];
            Object[] alternatives = choice.getAlternatives();
            for (int j = 0; j < length; j++) {
				Generator<String>[] altGens = createComponents(alternatives, quantityLimit, true);
                subGens[j] = new UniqueAlternativeGenerator<String>(String.class, altGens);
            }
            sources[length - min] = new UniqueCompositeStringGenerator(subGens);
        }
        return new UniqueAlternativeGenerator<String>(String.class, sources);
    }

	private static Generator<String> createFromGroup(
            Group group, int min, int max, int quantityLimit, boolean unique) {
        if (unique)
            return createFromUniqueGroup(group, min, max, quantityLimit);
        else
            return repeater(
                createFromObject(group.getRegex(), quantityLimit, unique),
                min, max);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Generator<String> createFromUniqueGroup(Group group, int min, int max, int quantityLimit) {
        Generator[] sources = new Generator[max - min + 1];
        for (int length = min; length <= max; length++) {
            Generator<String>[] subGens = new Generator[length];
            for (int j = 0; j < length; j++) {
                subGens[j] = createFromObject(group.getRegex(), quantityLimit, true);
            }
            sources[length - min] = new UniqueCompositeStringGenerator(subGens);
        }
        return new UniqueAlternativeGenerator<String>(String.class, sources);
    }

    private static Generator<String> createFromCharacter(char c, int min, int max, boolean unique) {
    	String value = String.valueOf(c);
    	Generator<String> atomGenerator;
    	if (unique)
    		atomGenerator = new OneShotGenerator<String>(value);
    	else
    		atomGenerator = new ConstantGenerator<String>(value);
		return repeater(atomGenerator, min, max);
    }
    
    @SuppressWarnings("rawtypes")
	private static Generator<String> createCharSetGenerator(CharSet charSet, int min, int max, boolean unique) {
        if (unique) {
            return new UniqueStringGenerator(min, max, charSet.getSet());
        } else {
			return repeater(
				new ConvertingGenerator<Character, String>(
						new CharacterGenerator(charSet.getSet()),
						(Converter) new ToStringConverter()
				), 
				min, max);
        }
    }
    
    private static Generator<String> createFromCustomCharClass(CustomCharClass ccc, int min, int max, boolean unique) {
    	CharSet charSet = ccc.getCharSet();
		return createCharSetGenerator(charSet, min, max, unique);
    }
    
    private static Generator<String> repeater(Generator<String> atomGenerator, int min, int max) {
    	if (min == 1 && max == 1)
    		return atomGenerator;
    	else
    		return new RepetitiveStringGenerator(atomGenerator, min, max);
    }

}
