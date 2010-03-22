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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.primitive.number.AbstractNumberGenerator;

/**
 * Long Generator that implements a 'cumulated' Long Sequence.
 * Uniqueness cannot be supported since it contradicts the 
 * purpose of this generator.<br/>
 * <br/>
 * Created: 07.06.2006 19:33:37
 * @since 0.1
 * @author Volker Bergmann
 */
public class CumulatedLongGenerator extends AbstractNumberGenerator<Long> {

    private static final long DEFAULT_MAX = Long.MAX_VALUE / 2;
	private static final long DEFAULT_MIN = Long.MIN_VALUE / 2;

    RandomLongGenerator baseGen;

    // constructors ----------------------------------------------------------------------------------------------------

	public CumulatedLongGenerator() {
        this(DEFAULT_MIN, DEFAULT_MAX);
    }

    public CumulatedLongGenerator(long min, long max) {
        this(min, max, 1);
    }

    public CumulatedLongGenerator(long min, long max, long precision) {
        super(Long.class, min, max, precision);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public float average() {
        return (float)(max + min) / 2;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public Long generate() {
        long exactValue = (baseGen.generate() + baseGen.generate() + baseGen.generate() + baseGen.generate() + baseGen.generate() + 2) / 5L;
        return min + (exactValue - min) / precision * precision;
    }
    
    @Override
    public void init(GeneratorContext context) {
        super.init(context);
        baseGen = new RandomLongGenerator(min, max);
    }
    
}
