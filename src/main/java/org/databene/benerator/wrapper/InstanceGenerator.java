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

import org.databene.benerator.Generator;
import org.databene.benerator.IllegalGeneratorStateException;

/**
 * Creates a stochastic number of instances in subsequent calls before it becomes unavailable.
 * The number of instances created in an availability period is determined by values of minCount, 
 * maxCount, countDistribution, countVariation1 and countVariation2.<br/>
 * <br/>
 * Created: 06.03.2008 16:03:01
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class InstanceGenerator<S> extends CardinalGenerator<S, S> {

    private long countAvailable;
    private long countUsed;
    private boolean limited;
    private boolean dirty;
    
    public InstanceGenerator(Generator<S> source) {
        super(source);
        limited = false;
        dirty = true;
    }

    @Override
    public void setMaxCount(long maxCount) {
        limited = true;
        super.setMaxCount(maxCount);
    }
    
    public Class<S> getGeneratedType() {
        return source.getGeneratedType();
    }
    
    @Override
    public void validate() {
        if (dirty) {
            countAvailable = countGenerator.generate();
            super.validate();
            dirty = false;
        }
    }
    
    public boolean available() {
        validate();
        return super.available() && (countUsed < countAvailable || !limited);
    }

    public S generate() {
        validate();
        if (!available())
            throw new IllegalGeneratorStateException("Generator not available.");
        this.countUsed++;
        return source.generate();
    }

    public void reset() {
        validate();
        super.reset();
        countAvailable = countGenerator.generate();
        countUsed = 0;
    }

    public void close() {
        validate();
        super.close();
        countUsed = countAvailable;
    }
}
