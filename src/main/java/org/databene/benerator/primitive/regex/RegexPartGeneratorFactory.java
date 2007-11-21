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

package org.databene.benerator.primitive.regex;

import org.databene.benerator.Generator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.benerator.wrapper.AlternativeGenerator;
import org.databene.benerator.wrapper.UniqueAlternativeGenerator;
import org.databene.benerator.primitive.UniqueStringGenerator;
import org.databene.benerator.primitive.CharacterGenerator;
import org.databene.regex.*;
import org.databene.model.converter.ToStringConverter;

import java.util.Set;

/**
 * Creates generators for regex parts. @see RegexPart<br/>
 * <br/>
 * Created: 17.11.2007 16:30:09
 */
class RegexPartGeneratorFactory {

    public static Generator<String> createRegexPartGenerator(RegexPart part, int maxQuantity, boolean unique) {
        SubPattern pattern = part.getPattern();
        int min = part.getQuantifier().getMin();
        int max = part.getQuantifier().getMax();
        if (max == -1)
            max = Math.max(min, maxQuantity);
        if (pattern instanceof CharSetPattern)
            return createCharSetPatternGenerator(pattern, min, max, unique);
        else if (pattern instanceof Group)
            return createGroupGenerator((Group)pattern, min, max, maxQuantity, unique);
        else if (pattern instanceof AlternativePattern)
            return createAlternativeGenerator((AlternativePattern) pattern, min, max, maxQuantity, unique);
        else
            throw new UnsupportedOperationException("Unsupported RegexPart type: " + part.getClass().getName());
    }

    public static Generator<String>[] getRegexGenerators(AlternativePattern alternatives, int maxPartLength, boolean unique) {
        /** creates an array of generators, of which each one will generate a pattern from the alternatives */
        Regex[] regexes = alternatives.getPatterns();
        RegexStringGenerator[] sources = new RegexStringGenerator[regexes.length];
        for (int i = 0; i < regexes.length; i++)
            sources[i] = new RegexStringGenerator(regexes[i], maxPartLength, unique);
        return sources;
    }

// private helpers -------------------------------------------------------------------------------------------------

    private static Generator<String> createAlternativeGenerator(
            AlternativePattern pattern, int min, int max, int maxQuantity, boolean unique) {
        if (unique) {
            if (min == max && min == 1)
                return new UniqueAlternativeGenerator<String>(String.class, getRegexGenerators(pattern, maxQuantity, unique));
            else
                return new UniqueCompositeStringGenerator(pattern, min, max, maxQuantity);
        } else
            return new NFoldCompositeStringGenerator(
                new AlternativeGenerator<String>(String.class, getRegexGenerators(pattern, maxQuantity, unique)),
                min, max);
    }

    private static Generator<String> createGroupGenerator(
            Group pattern, int min, int max, int maxQuantity, boolean unique) {
        if (unique)
            return new UniqueCompositeStringGenerator(pattern, min, max, maxQuantity);
        else
            return new NFoldCompositeStringGenerator(
                new RegexStringGenerator(pattern.getRegex(), maxQuantity, unique),
                min, max);
    }

    private static Generator<String> createCharSetPatternGenerator(
            SubPattern pattern, int min, int max, boolean unique) {
        Set<Character> charSet = ((CharSetPattern) pattern).getCharSet();
        if (min == max && max == 1 && charSet.size() == 1)
            return new ConstantGenerator<String>(String.valueOf(charSet.iterator().next()));
        if (unique) {
            return new UniqueStringGenerator(min, max, charSet);
        } else {
            Generator<String> patternGenerator = new ConvertingGenerator<Character, String>(
                    new CharacterGenerator(((CharSetPattern)pattern).getCharSet()),
                    new ToStringConverter<Character>());
            return new NFoldCompositeStringGenerator(patternGenerator, min, max);
        }
    }
}
