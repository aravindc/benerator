/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

package org.databene.benerator.primitive;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.distribution.sequence.BitReverseNaturalNumberGenerator;
import org.databene.benerator.wrapper.NonNullGeneratorProxy;

/**
 * Creates unique pseudo-random int arrays.<br/><br/>
 * Created: 01.08.2011 17:00:57
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class UniqueIntsGenerator extends NonNullGeneratorProxy<int[]> {

    private int[] digits;
    private int[] displayColumn;
    private int[] digitOffsets;
    private int cycleCounter;

    public UniqueIntsGenerator(int radix, int length) {
    	super(new IncrementalIntsGenerator(radix, length));
    	this.displayColumn = new int[length];
    	this.digitOffsets = new int[length];
	}

	@Override
	public IncrementalIntsGenerator getSource() {
		return (IncrementalIntsGenerator) super.getSource();
	}
	
    public int getRadix() {
		return getSource().getRadix();
	}

	public int getLength() {
		return getSource().getLength();
	}
	
    @Override
    public synchronized void init(GeneratorContext context) {
    	assertNotInitialized();
    	int length = getLength();
    	int radix = getRadix();
		NonNullGenerator<Long> colGen = new BitReverseNaturalNumberGenerator(length - 1);
        colGen.init(context);
        for (int i = 0; i < length; i++) {
            this.displayColumn[i] = colGen.generate().intValue();
            this.digitOffsets[i] = (length - 1 - this.displayColumn[i]) % radix;
        }
        resetMembers();
        super.init(context);
    }
    
	@Override
	public int[] generate() {
        if (digits == null)
            return null;
        int length = getLength();
        int radix = getRadix();
        int[] buffer = new int[length];
        for (int i = 0; i < digits.length; i++)
            buffer[displayColumn[i]] = (digits[i] + digitOffsets[i] + cycleCounter) % radix;
        if (cycleCounter < radix - 1 && length > 0) {
            cycleCounter++;
        } else {
            digits = super.generate();
            if (radix == 1 || (digits != null && digits[0] > 0)) {
                // counter + cycle have run through all combinations
                digits = null;
            }
            cycleCounter = 0;
        }
        return buffer;
    }

	@Override
    public void reset() {
        super.reset();
        resetMembers();
    }

	private void resetMembers() {
		this.digits = super.generate().clone();
        this.cycleCounter = 0;
    }

}
