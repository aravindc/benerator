/*
 * (c) Copyright 2006-2011 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.util.WrapperProvider;

/**
 * Combines a a random number a source generator's products into a collection.<br/>
 * <br/>
 * Created: 06.03.2008 16:08:22
 * @author Volker Bergmann
 */
public abstract class CardinalGenerator<S, P> extends GeneratorWrapper<S, P> {

    /** Generator that determines the cardinality of generation */
    protected Generator<Integer> countGenerator;
    boolean resettingCountGenerator;
    
    int minCount;
    int maxCount;
    int countPrecision;
    Distribution countDistribution;
    WrapperProvider<Integer> countWrapperProvider = new WrapperProvider<Integer>();

    // constructors ----------------------------------------------------------------------------------------------------

    public CardinalGenerator(Generator<S> source, Generator<Integer> countGenerator) { 
    	// TODO remove this constructor forcing children to explicitly select count generator reset
        this(source, countGenerator, false);
    }
    
    public CardinalGenerator(Generator<S> source, Generator<Integer> countGenerator, boolean resettingCountGenerator) {
        super(source);
        this.countGenerator = countGenerator;
        this.resettingCountGenerator = resettingCountGenerator;
    }
    
    public CardinalGenerator(Generator<S> source) {
        this(source, 0, 30, 1, SequenceManager.RANDOM_SEQUENCE);
    }

    public CardinalGenerator(Generator<S> source, 
    		int minCount, int maxCount, int countPrecision, Distribution countDistribution) {
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
    	if (countGenerator == null)
    		countGenerator = countDistribution.createGenerator(Integer.class, minCount, maxCount, countPrecision, false);
        countGenerator.init(context);
        super.init(context);
    }

    @Override
    public void reset() {
    	assertInitialized();
    	if (resettingCountGenerator)
    		countGenerator.reset();
        super.reset();
    }
    
    // helpers ---------------------------------------------------------------------------------------------------------
    
    protected Integer generateCount() {
    	ProductWrapper<Integer> wrapper = countWrapperProvider.get();
    	wrapper = countGenerator.generate(wrapper);
    	if (wrapper == null)
    		return null;
    	return wrapper.unwrap();
    }
    
}
