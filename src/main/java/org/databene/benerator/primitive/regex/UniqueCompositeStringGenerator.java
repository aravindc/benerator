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
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.SimpleRandom;
import org.databene.benerator.wrapper.UniqueAlternativeGenerator;
import org.databene.regex.AlternativePattern;
import org.databene.regex.SubPattern;
import org.databene.regex.Group;

/**
 * Composes the output of different unique String generators in a unique way.<br/>
 * <br/>
 * Created: 17.11.2007 19:21:58
 */
public class UniqueCompositeStringGenerator implements Generator<String> {

    private SubPattern pattern;
    private int min;
    private int max;

    private Generator<String>[] sources;

    public UniqueCompositeStringGenerator() {
        this(null, 1, 1, 30);
    }

    public UniqueCompositeStringGenerator(SubPattern pattern, int min, int max, int maxQuantity) {
        if (pattern == null)
            return;
        this.pattern = pattern;
        this.min = min;
        this.max = max;
        sources = new Generator[max - min + 1];
        for (int length = min; length <= max; length++) {
            // create UniqueFixedCoundCompositeStringGenerator for this count
            if (pattern instanceof AlternativePattern) {
                Generator<String>[] subGens = new Generator[length];
                for (int j = 0; j < length; j++) {
                    Generator<String>[] altGens = RegexPartGeneratorFactory.getRegexGenerators((AlternativePattern)pattern, maxQuantity, true);
                    subGens[j] = new UniqueAlternativeGenerator<String>(String.class, altGens);
                }
                sources[length - min] = new UniqueFixedCountCompositeStringGenerator(subGens);
            } else if (pattern instanceof Group) {
                Generator<String>[] subGens = new Generator[length];
                for (int j = 0; j < length; j++) {
                    Group group = (Group) pattern;
                    subGens[j] = new RegexStringGenerator(group.getRegex(), maxQuantity, true);
                }
                sources[length - min] = new UniqueFixedCountCompositeStringGenerator(subGens);
            } else
                throw new UnsupportedOperationException("Not a supported pattern: " + pattern);
        }
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public Class<String> getGeneratedType() {
        return String.class;
    }
    public void validate() {
        if (sources == null || sources.length == 0)
            throw new InvalidGeneratorSetupException("sources", "not set");
        for (Generator<String> source : sources)
            source.validate();
    }

    public boolean available() {
        for (Generator<String> source : sources)
            if (source.available())
                return true;
        return false;
    }
    public String generate() {
        int index;
        do {
            index = SimpleRandom.randomInt(0, sources.length - 1);
        } while (!sources[index].available());
        return sources[index].generate();
    }

    public void reset() {
        for (Generator<String> source : sources)
            source.reset();
    }

    public void close() {
        for (Generator<String> source : sources)
            source.close();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + "['" + pattern + ", " + min + "<=length<=" + max + ']';
    }
}
