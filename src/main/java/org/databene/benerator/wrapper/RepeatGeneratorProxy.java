/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.Sequence;

/**
 * A generator proxy that forwards the output of another generator with a random number of repetitions.<br/>
 * <br/>
 * Created: 18.08.2007 17:08:10
 * @author Volker Bergmann
 */
public class RepeatGeneratorProxy<E> extends CardinalGenerator<E, E> {

    private long repCount;
    private long totalReps;
    private E next;

    public RepeatGeneratorProxy() {
        this(null, 0, 3);
    }

    public RepeatGeneratorProxy(Generator<E> source, long minRepetitions, long maxRepetitions) {
        this(source, minRepetitions, maxRepetitions, 1, Sequence.RANDOM);
    }

    public RepeatGeneratorProxy(Generator<E> source, long minRepetitions, long maxRepetitions, 
    		int repetitionPrecision, Distribution repetitionDistribution) {
        super(source, minRepetitions, maxRepetitions, repetitionPrecision, repetitionDistribution);
    }

    public Class<E> getGeneratedType() {
    	return source.getGeneratedType();
    }
    
    @Override
	public void validate() {
        if (dirty) {
            super.validate();
            repCount = -1;
            totalReps = countGenerator.generate();
            next = source.generate();
        }
    }

	public E generate() {
        validate();
        if (next == null)
            return null;
        E result = next;
        repCount++;
        if (repCount >= totalReps) {
            next = source.generate();
            if (next != null) {
                totalReps = countGenerator.generate();
                repCount = -1;
            }
        }
        return result;
    }
	
}
