/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.sample;

import org.databene.benerator.Generator;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.util.RandomUtil;

import java.util.List;
import java.util.ArrayList;

/**
 * Generates values from an unweighted list of samples.<br/>
 * <br/>
 * Created: 07.06.2006 19:04:08
 * @author Volker Bergmann
 */
public class SequencedSampleGenerator<E> extends AbstractSampleGenerator<E> {

    /** Keeps the Sample information */
    private List<E> samples = new ArrayList<E>();

    /** Sequence for choosing a List index of the sample list */
    private Distribution distribution = null;

    /** Sequence for choosing a List index of the sample list */
    private Generator<Integer> indexGenerator = null;

    /** Flag that indicates if the generator needs to be initialized */
    private boolean dirty = true;

    // constructors ----------------------------------------------------------------------------------------------------

    public SequencedSampleGenerator() {
        this(null);
    }

    /** Initializes the generator to an empty sample list */
    public SequencedSampleGenerator(Class<E> generatedType) {
        this(generatedType, new ArrayList<E>());
    }

    /** Initializes the generator to a sample list */
    public SequencedSampleGenerator(Class<E> generatedType, E ... values) {
    	super(generatedType);
        setValues(values);
        this.distribution = Sequence.RANDOM;
    }

    /** Initializes the generator to a sample list */
    public SequencedSampleGenerator(Class<E> generatedType, Distribution distribution, E ... values) {
    	super(generatedType);
        this.distribution = distribution;
        setValues(values);
    }

    /** Initializes the generator to a sample list */
    public SequencedSampleGenerator(Class<E> generatedType, Iterable<E> values) {
    	super(generatedType);
        setValues(values);
        this.distribution = Sequence.RANDOM;
    }

    /** Initializes the generator to a sample list */
    public SequencedSampleGenerator(Class<E> generatedType, Distribution distribution, Iterable<E> values) {
    	super(generatedType);
        this.distribution = distribution;
        setValues(values);
    }

    // values property -------------------------------------------------------------------------------------------------

    /** Adds a value to the sample list */
    @Override
    public void addValue(E value) {
        samples.add(value);
        this.dirty = true;
    }

    @Override
    public void clear() {
    	this.samples.clear();
    	this.dirty = true;
    }
    
    // Generator implementation ----------------------------------------------------------------------------------------

    @Override
    public boolean available() {
    	if (dirty)
    		validate();
    	return samples.size() > 0 && indexGenerator.available();
    }
    
    /** Initializes all attributes */
    @Override
    public void validate() {
        if (dirty) {
            if (samples.size() == 0) 
            	throw new InvalidGeneratorSetupException("No samples defined in " + this);
            else {
            	indexGenerator = distribution.createGenerator(Integer.class, 0, samples.size() - 1, 1);
            	indexGenerator.validate();
            }
            this.dirty = false;
        }
    }

    public E generate() {
        if (dirty)
            validate();
        if (samples.size() == 0)
            return null;
        return samples.get(indexGenerator.generate());
    }
    
    @Override
    public void reset() {
    	indexGenerator.reset();
    }
    
    @Override
    public void close() {
    	indexGenerator.close();
    }

    // static interface ------------------------------------------------------------------------------------------------

    /** Convenience utility method that chooses one sample out of a list with uniform random distribution */
    public static <T> T generate(T ... samples) {
        return samples[RandomUtil.randomInt(0, samples.length - 1)];
    }

    /** Convenience utility method that chooses one sample out of a list with uniform random distribution */
    public static <T> T generate(List<T> samples) {
        return samples.get(RandomUtil.randomInt(0, samples.size() - 1));
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
