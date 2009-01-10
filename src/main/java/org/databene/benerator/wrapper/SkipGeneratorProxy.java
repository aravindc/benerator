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

import org.databene.benerator.*;
import org.databene.benerator.primitive.number.adapter.LongGenerator;
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;

/**
 * This forwards a source generator's products.
 * Iterates through the products of another generator with a variable step width.
 * This is intended mainly for use with importing generators that provide data
 * volumes too big to keep in RAM.<br/>
 * <br/>
 * Created: 26.08.2006 16:16:04
 */
public class SkipGeneratorProxy<E> extends GeneratorProxy<E> {
	
	public static final long DEFAULT_MIN_INCREMENT = 1L;
	public static final long DEFAULT_MAX_INCREMENT = 1L;

    /** The increment generator, which creates an individual increment on each generation */
    private LongGenerator incrementGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    public SkipGeneratorProxy() {
        this(null);
    }

    /** Initializes the generator to iterate with increment 1 */
    public SkipGeneratorProxy(Generator<E> source) {
        this(source, DEFAULT_MIN_INCREMENT, DEFAULT_MAX_INCREMENT);
    }

    /** Initializes the generator to use a random increment of uniform distribution */
    public SkipGeneratorProxy(Generator<E> source, Long minIncrement, Long maxIncrement) {
        this(source, minIncrement, maxIncrement, Sequence.RANDOM);
    }

    public SkipGeneratorProxy(Long minIncrement, Long maxIncrement) {
        this(null, minIncrement, maxIncrement);
    }

    /** Initializes the generator */
    public SkipGeneratorProxy(Generator<E> source,
            Long minIncrement, Long maxIncrement, Distribution incrementDistribution) {
        super(source);
        if (minIncrement == null)
        	minIncrement = DEFAULT_MIN_INCREMENT;
        if (maxIncrement == null)
        	maxIncrement = DEFAULT_MAX_INCREMENT;
        this.incrementGenerator = new LongGenerator(minIncrement, maxIncrement, 1L, incrementDistribution);
    }

    // config properties -----------------------------------------------------------------------------------------------

    public long getMinIncrement() {
        return incrementGenerator.getMin();
    }

    public void setMinIncrement(long minIncrement) {
        incrementGenerator.setMin(minIncrement);
    }

    public long getMaxIncrement() {
        return incrementGenerator.getMax();
    }

    public void setMaxIncrement(long maxIncrement) {
        incrementGenerator.setMax(maxIncrement);
    }

    public Distribution getIncrementDistribution() {
        return incrementGenerator.getDistribution();
    }

    public void setIncrementDistribution(Distribution distribution) {
        incrementGenerator.setDistribution(distribution);
    }

    public Long getIncrementVariation1() {
        return incrementGenerator.getVariation1();
    }

    public void setIncrementVariation1(Long varation1) {
        incrementGenerator.setVariation1(varation1);
    }

    public Long getIncrementVariation2() {
        return incrementGenerator.getVariation2();
    }

    public void setIncrementVariation2(Long variation2) {
        incrementGenerator.setVariation2(variation2);
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    public void validate() {
        if (incrementGenerator.getMin() < 0)
            throw new InvalidGeneratorSetupException("minIncrement", "less than 0");
        incrementGenerator.validate();
        super.validate();
    }

    /** @see org.databene.benerator.Generator#reset() */
    public E generate() {
        if (!source.available())
            throw new IllegalGeneratorStateException("source is not available");
        long increment = incrementGenerator.generate();
        for (long i = 0; i < increment - 1; i++)
            source.generate();
        return source.generate();
    }

    public void close() {
        super.close();
        incrementGenerator.close();
    }

    public void reset() {
        super.reset();
        incrementGenerator.reset();
    }
}
