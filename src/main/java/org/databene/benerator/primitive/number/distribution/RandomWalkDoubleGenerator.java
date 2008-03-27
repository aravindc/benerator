/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive.number.distribution;

import org.databene.benerator.primitive.number.AbstractDoubleGenerator;
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;

/**
 * Double Generator that implements a 'randomWalk' Double Sequence.<br/>
 * <br/>
 * Created: 13.06.2006 07:36:45
 */
public class RandomWalkDoubleGenerator extends AbstractDoubleGenerator {

    private double next;

    private RandomDoubleGenerator incrementGenerator;

    public RandomWalkDoubleGenerator() {
        this(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public RandomWalkDoubleGenerator(double min, double max) {
        this(min, max, 1, 1);
    }

    public RandomWalkDoubleGenerator(double min, double max, double minIncrement, double maxIncrement) {
        super(min, max);
        incrementGenerator = new RandomDoubleGenerator(minIncrement, maxIncrement);
    }

    public RandomWalkDoubleGenerator(double min, double max, double precision, double minIncrement, double maxIncrement) {
        super(min, max, precision);
        incrementGenerator = new RandomDoubleGenerator(minIncrement, maxIncrement, precision);
    }

    public void setPrecision(double precision) {
        incrementGenerator.setPrecision(precision);
    }

    public Distribution getDistribution() {
        return Sequence.RANDOM_WALK;
    }

    public double getNext() {
        return next;
    }

    public void setNext(double next) {
        this.next = next;
    }

    public void validate() {
        if (dirty) {
            double minIncrement = incrementGenerator.getMin();
            double maxIncrement = incrementGenerator.getMax();
            if (minIncrement < 0 && maxIncrement <= 0)
                next = max;
            else if (minIncrement >= 0 && maxIncrement > 0)
                next = min;
            else
                next = (min + max) / 2;
            super.validate();
        }
    }

    public Double generate() {
        if (dirty)
            validate();
        double value = next;
        next += incrementGenerator.generate();
        if (next > max)
            next = max;
        else if (next < min)
            next = min;
        return value;
    }

}
