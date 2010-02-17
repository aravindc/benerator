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
import org.databene.commons.NullSafeComparator;

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
        this(source, 0, 30);
    }

    public CardinalGenerator(Generator<S> source, long minCount, long maxCount) {
        this(source, minCount, maxCount, 1, SequenceManager.RANDOM_SEQUENCE);
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
    public void validate() {
    	if (dirty) {
	        this.countGenerator = countDistribution.createGenerator(Long.class, minCount, maxCount, countPrecision, false);
	        super.validate();
    	}
    }

    public void setMinCount(long minCount) {
    	if (!NullSafeComparator.equals(minCount, this.minCount)) {
	    	this.minCount = minCount;
		    dirty = true;
    	}
    }

    public void setMaxCount(long maxCount) {
    	if (!NullSafeComparator.equals(maxCount, this.maxCount)) {
	    	this.maxCount = maxCount;
		    dirty = true;
    	}
    }
    
    public void setCountPrecision(long countPrecision) {
    	if (!NullSafeComparator.equals(countPrecision, this.countPrecision)) {
	    	this.countPrecision = countPrecision;
		    dirty = true;
    	}
    }
    
    public void setCountDistribution(Distribution distribution) {
    	if (!NullSafeComparator.equals(distribution, this.countDistribution)) {
	    	this.countDistribution = distribution;
	    	dirty = true;
    	}
    }

    @Override
    public void reset() {
        super.reset(); // no reset on the countGenerator!
    }
}
