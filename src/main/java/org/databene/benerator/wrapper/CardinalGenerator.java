/*
 * (c) Copyright 2006-2010 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;

/**
 * Combines a a random number a source generator's products into a collection.<br/>
 * <br/>
 * Created: 06.03.2008 16:08:22
 * @author Volker Bergmann
 */
public abstract class CardinalGenerator<S, P> extends GeneratorWrapper<S, P> {

    /** Generator that determines the cardinality of generation */
    protected Generator<Long> countGenerator;
    
    long minCount;
    long maxCount;
    long countPrecision;
    Distribution countDistribution;

    // constructors ----------------------------------------------------------------------------------------------------

    public CardinalGenerator() {
        this(null);
    }

    public CardinalGenerator(Generator<S> source) {
        this(source, 0, 30, 1, SequenceManager.RANDOM_SEQUENCE);
    }

    public CardinalGenerator(Generator<S> source, 
            long minCount, long maxCount, long countPrecision, Distribution countDistribution) {
        super(source);
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.countPrecision = countPrecision;
        this.countDistribution = countDistribution;
    }
    
    // Generator interface ---------------------------------------------------------------------------------------------

	/** ensures consistency of the state */
    @Override
    public void init(GeneratorContext context) {
        countGenerator = countDistribution.createGenerator(Long.class, minCount, maxCount, countPrecision, false);
        countGenerator.init(context);
        super.init(context);
    }

    public void setMinCount(long minCount) {
    	assertNotInitialized();
	    this.minCount = minCount;
    }

    public void setMaxCount(long maxCount) {
    	assertNotInitialized();
	    this.maxCount = maxCount;
    }
    
    public void setCountPrecision(long countPrecision) {
    	assertNotInitialized();
	    this.countPrecision = countPrecision;
    }
    
    public void setCountDistribution(Distribution distribution) {
    	assertNotInitialized();
	    this.countDistribution = distribution;
    }

    @Override
    public void reset() {
    	assertInitialized();
        super.reset(); // don't reset the countGenerator!
    }
    
}
