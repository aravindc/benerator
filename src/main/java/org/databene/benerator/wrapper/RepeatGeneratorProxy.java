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

package org.databene.benerator.wrapper;

import org.databene.benerator.GeneratorProxy;
import org.databene.benerator.Generator;
import org.databene.benerator.SimpleRandom;
import org.databene.benerator.IllegalGeneratorStateException;

/**
 * A generator proxy that forwards the output of another generator with a random number of repetitions.<br/>
 * <br/>
 * Created: 18.08.2007 17:08:10
 */
public class RepeatGeneratorProxy<E> extends GeneratorProxy<E> {

    private long minRepetitions;
    private long maxRepetitions;
    private long repCount;
    private long totalReps;
    private E next;

    public RepeatGeneratorProxy() {
        this(null, 0L, 3L);
    }

    public RepeatGeneratorProxy(Generator<E> source, Long minRepetitions, Long maxRepetitions) {
        super(source);
        repCount = -1;
        setMinRepetitions(minRepetitions);
        setMaxRepetitions(maxRepetitions);
        totalReps = SimpleRandom.randomLong(minRepetitions, maxRepetitions);
    }

    public long getMinRepetitions() {
        return minRepetitions;
    }

    public void setMinRepetitions(long minRepetitions) {
        if (minRepetitions < 0)
            throw new IllegalArgumentException("minRepetitions must be >= 0, was: " + minRepetitions);
        this.minRepetitions = minRepetitions;
    }

    public long getMaxRepetitions() {
        return maxRepetitions;
    }

    public void setMaxRepetitions(long maxRepetitions) {
        if (maxRepetitions < minRepetitions)
            throw new IllegalArgumentException("maxRepetitions must be >= minRepetitions");
        this.maxRepetitions = maxRepetitions;
    }

    public void validate() {
        if (dirty) {
            super.validate();
            next = source.generate();
        }
    }

    public boolean available() {
        if (dirty)
            validate();
        return repCount < totalReps;
    }

    public E generate() {
        if (dirty)
            validate();
        if (next == null)
            throw new IllegalGeneratorStateException("Generator is no more available");
        E result = next;
        repCount++;
        if (repCount >= totalReps) {
            if (source.available()) {
                next = source.generate();
                totalReps = SimpleRandom.randomLong(minRepetitions, maxRepetitions);
                repCount = -1;
            } else {
                next = null;
            }
        }
        return result;
    }
}
