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

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;

/**
 * This forwards a source generator's products.
 * Iterates through the products of another generator with a variable step width.
 * This is intended mainly for use with importing generators that provide data
 * volumes too big to keep in RAM.<br/>
 * <br/>
 * Created: 26.08.2006 16:16:04
 * @since 0.1
 * @author Volker Bergmann
 */
public class SkipGeneratorProxy<E> extends GeneratorProxy<E> {
	
	public static final long DEFAULT_MIN_INCREMENT = 1L;
	public static final long DEFAULT_MAX_INCREMENT = 1L;

    /** The increment generator, which creates an individual increment on each generation */
    private Generator<Long> incrementGenerator;
    
    private long count;
    private Long limit;

    // constructors ----------------------------------------------------------------------------------------------------

    public SkipGeneratorProxy() {
        this(null);
    }

    /** Initializes the generator to iterate with increment 1 */
    public SkipGeneratorProxy(Generator<E> source) {
        this(source, DEFAULT_MIN_INCREMENT, DEFAULT_MAX_INCREMENT);
    }

    public SkipGeneratorProxy(Long minIncrement, Long maxIncrement) {
        this(null, minIncrement, maxIncrement);
    }

    /** Initializes the generator to use a random increment of uniform distribution */
    public SkipGeneratorProxy(Generator<E> source, Long minIncrement, Long maxIncrement) {
        this(source, minIncrement, maxIncrement, SequenceManager.RANDOM_SEQUENCE, null);
    }

    /** Initializes the generator to use a random increment of uniform distribution */
    public SkipGeneratorProxy(Generator<E> source, Long minIncrement, Long maxIncrement, Distribution incrementDistribution, Long limit) {
        super(source);
        
        // replace nulls with default values
        if (minIncrement == null) {
        	if (maxIncrement != null)
        		minIncrement = Math.min(1L, maxIncrement);
        	else
        		minIncrement = maxIncrement = 1L;
        } else if (maxIncrement == null) {
        	maxIncrement = 3L;
        }
        
        // check values
        if (minIncrement < 0)
        	throw new InvalidGeneratorSetupException("minIncrement is less than zero");
        if (maxIncrement < 0)
        	throw new InvalidGeneratorSetupException("maxIncrement is less than zero");
        if (minIncrement > maxIncrement)
        	throw new InvalidGeneratorSetupException("minIncrement (" + minIncrement + ") is larger than maxIncrement (" + maxIncrement + ")");
        
        // create generator
        this.incrementGenerator = incrementDistribution.createGenerator(Long.class, minIncrement, maxIncrement, 1L, false);
        this.count = 0;
        this.limit = limit;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    @Override
    public void init(GeneratorContext context) {
        incrementGenerator.init(context);
        super.init(context);
    }

    /** @see org.databene.benerator.Generator#reset() */
    @Override
    public E generate() {
    	Long increment = incrementGenerator.generate();
    	if (increment == null)
    		return null;
        for (long i = 0; i < increment - 1; i++)
            source.generate();
        count += increment;
        if (limit != null && count > limit)
        	return null;
        return source.generate();
    }

    @Override
    public void close() {
        super.close();
        incrementGenerator.close();
    }

    @Override
    public void reset() {
        super.reset();
        incrementGenerator.reset();
        count = 0;
    }
    
}
