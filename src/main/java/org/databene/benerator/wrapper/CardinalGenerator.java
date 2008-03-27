/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.wrapper;

import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;
import org.databene.benerator.primitive.number.adapter.IntegerGenerator;
import org.databene.benerator.primitive.number.adapter.LongGenerator;
import org.databene.benerator.*;

/**
 * Combines a a random number a source generator's products into a collection.<br/>
 * <br/>
 * Created: 06.03.2008 16:08:22
 */
public abstract class CardinalGenerator<S, P> extends GeneratorWrapper<S, P> {

    /** Generator that determines the cardinality of generation */
    protected LongGenerator countGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    public CardinalGenerator() {
        this(null);
    }

    public CardinalGenerator(Generator<S> source) {
        this(source, 0, 30, Sequence.RANDOM);
    }

    public CardinalGenerator(Generator<S> source, long minLength, long maxLength) {
        this(source, minLength, maxLength, Sequence.RANDOM);
    }

    public CardinalGenerator(Generator<S> source,
            long minLength, long maxLength, Distribution lengthDistribution) {
        super(source);
        countGenerator = new LongGenerator(minLength, maxLength, 1L, lengthDistribution);
    }

    // configuration properties ----------------------------------------------------------------------------------------

    public long getMinCount() {
        return countGenerator.getMin();
    }

    public void setMinCount(long minCardinality) {
        countGenerator.setMin(minCardinality);
    }

    public long getMaxCount() {
        return countGenerator.getMin();
    }

    public void setMaxCount(long maxCount) {
        countGenerator.setMax(maxCount);
    }

    public Distribution getCountDistribution() {
        return countGenerator.getDistribution();
    }

    public void setCountDistribution(Distribution distribution) {
        countGenerator.setDistribution(distribution);
    }

    public Long getCountVariation1() {
        return countGenerator.getVariation1();
    }

    public void setCountVariation1(Long varation1) {
        countGenerator.setVariation1(varation1);
    }

    public Long getSizeVariation2() {
        return countGenerator.getVariation2();
    }

    public void setSizeVariation2(Long variation2) {
        countGenerator.setVariation2(variation2);
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    /** ensures consistency of the state */
    public void validate() {
        countGenerator.validate();
        super.validate();
    }
}
