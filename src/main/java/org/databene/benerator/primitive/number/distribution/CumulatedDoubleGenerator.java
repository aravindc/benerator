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
import org.databene.model.Distribution;
import org.databene.model.Sequence;

/**
 * Double Generator that implements a 'cumulated' Double Sequence.
 *  * Double Generator that implements a 'cumulated' Double Sequence.

 * Created: 07.06.2006 19:33:37
 */
public class CumulatedDoubleGenerator extends AbstractDoubleGenerator {

    public CumulatedDoubleGenerator() {
        this(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public CumulatedDoubleGenerator(double min, double max) {
        this(min, max, 1);
    }

    public CumulatedDoubleGenerator(double min, double max, double precision) {
        super(min, max, precision);
    }

    public Distribution getDistribution() {
        return Sequence.CUMULATED;
    }

    public float getAverage() {
        return (float)(max + min) / 2;
    }

    public Double generate() {
        RandomDoubleGenerator baseGen = new RandomDoubleGenerator(min, max);
        double exactValue = (baseGen.generate() + baseGen.generate() + baseGen.generate() + baseGen.generate() + baseGen.generate()) / 5.;
        return min + (int)(Math.round((exactValue - min) / precision)) * precision;
    }
}
