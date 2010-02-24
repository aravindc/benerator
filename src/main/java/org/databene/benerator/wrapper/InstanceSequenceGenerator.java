/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.engine.BeneratorContext;

/**
 * Creates a stochastic number of instances in subsequent calls before it becomes unavailable.
 * The number of instances created in an availability period is determined by values of minCount, 
 * maxCount, countDistribution, countVariation1 and countVariation2.<br/>
 * <br/>
 * Created: 06.03.2008 16:03:01
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class InstanceSequenceGenerator<E> extends CardinalGenerator<E, E> {

    private long sequenceLength;
    private long lengthSoFar;
    private boolean limited;
    
    public InstanceSequenceGenerator(Generator<E> source) {
        super(source);
        limited = false;
        sequenceLength = -1;
    }

    @Override
    public void setMaxCount(long maxCount) {
        limited = true;
        super.setMaxCount(maxCount);
    }
    
    public long getSequenceLength() {
    	assertInitialized();
    	return sequenceLength;
    }

	public Class<E> getGeneratedType() {
        return source.getGeneratedType();
    }
    
    @Override
    public void init(BeneratorContext context) {
        if (sequenceLength == -1)
        	sequenceLength = countGenerator.generate();
        super.init(context);
    }
    
    public E generate() {
        assertInitialized();
        if (limited && lengthSoFar >= sequenceLength)
        	return null;
        E product = source.generate();
        if (product != null) {
	        this.lengthSoFar++;
	        return product;
        } else
        	return null;
    }

    @Override
    public void reset() {
        assertInitialized();
        super.reset();
    	sequenceLength = countGenerator.generate();
        lengthSoFar = 0;
    }

    @Override
    public void close() {
        assertInitialized();
        super.close();
        lengthSoFar = sequenceLength;
    }
    
}
