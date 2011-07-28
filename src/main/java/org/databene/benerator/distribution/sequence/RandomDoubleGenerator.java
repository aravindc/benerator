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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.primitive.number.AbstractNonNullNumberGenerator;
import org.databene.benerator.util.RandomUtil;

/**
 * Double Generator that implements a 'random' Double Sequence.<br/>
 * <br/>
 * Created: 11.06.2006 07:55:54
 * @since 0.1
 * @author Volker Bergmann
 */
public class RandomDoubleGenerator extends AbstractNonNullNumberGenerator<Double> {
	
    public RandomDoubleGenerator() {
        this(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public RandomDoubleGenerator(double min, double max) {
        this(min, max, 1);
    }

    public RandomDoubleGenerator(double min, double max, double granularity) {
        super(Double.class, min, max, granularity);
    }

    // Generator interface ---------------------------------------------------------------------------------------------

	@Override
	public Double generate() {
        if (granularity == 0)
            return min + Math.random() * (max - min);
        int n = (int)((max - min) / granularity);
        return min + RandomUtil.randomInt(0, n) * granularity;
    }

}
