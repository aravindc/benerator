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

import org.databene.commons.Weighted;

/**
 * Represents a single sample value for a generator.
 * The sample value may have an additional weight information.<br/>
 * <br/>
 * Created: 07.06.2006 19:05:13
 * @since 0.1
 * @author Volker Bergmann
 */
public class WeightedSample<E> implements Weighted {

    /** The value of the sample */
    private E value;

    /** The optional weight of the sample */
    private double weight;

    /** Initializes the Sample to the specified value and weight */
    public WeightedSample(E value, double weight) {
        this.weight = weight;
        this.value = value;
    }

    // properties ------------------------------------------------------------------------------------------------------

    /**
     * Returns the value property
     * @see #weight
     */
    public E getValue() {
        return value;
    }

    /**
     * Sets the weight property
     * @see #weight
     */
    public void setValue(E value) {
        this.value = value;
    }

    /**
     * Returns the weight property value
     * @see #weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight property value
     * @see #weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return value + "(" + weight + ')';
    }
}
