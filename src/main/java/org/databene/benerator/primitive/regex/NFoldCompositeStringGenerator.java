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
import org.databene.benerator.primitive.number.adapter.IntegerGenerator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.model.function.Sequence;

/**
 * Calls another string generator a variable number of times and appends a variable number of times.<br/>
 * <br/>
 * Created: 17.11.2007 16:37:43
 */
class NFoldCompositeStringGenerator extends GeneratorProxy<String> {

    private int minCount;

    private int maxcount;

    /** A number generator for generating quanities */
    private Generator<Integer> quantityGenerator;

    public NFoldCompositeStringGenerator(Generator<String> patternGenerator, int minCount, int maxCount) {
        super(patternGenerator);
        this.minCount = minCount;
        this.maxcount = maxCount;
        this.quantityGenerator = new IntegerGenerator(minCount, maxCount, 1, Sequence.RANDOM);
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public Class<String> getGeneratedType() {
        return String.class;
    }

    /** Determines a quantity, invokes the pattern as many times and assembles the products to a String */
    public String generate() {
        StringBuilder builder = new StringBuilder();
        int count = quantityGenerator.generate();
        for (int i = 0; i < count; i++)
            if (source.available())
                builder.append(source.generate());
        return builder.toString();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + "[source=" + source + ", " + minCount + "<=partCount<=" + maxcount + ']';
    }
}
