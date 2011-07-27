/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive;

import org.databene.benerator.*;
import org.databene.benerator.distribution.sequence.BitReverseNaturalNumberGenerator;
import org.databene.benerator.util.RandomUtil;
import org.databene.benerator.util.ThreadSafeNonNullGenerator;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ArrayFormat;
import org.databene.commons.CustomCounter;

import java.util.Set;

/**
 * Generates unique strings of fixed length.<br/>
 * <br/>
 * Created: 15.11.2007 14:07:49
 * @author Volker Bergmann
 */
public class UniqueFixedLengthStringGenerator extends ThreadSafeNonNullGenerator<String> { // TODO compare and merge with DigitsGenerator?

    public static final Set<Character> DEFAULT_CHAR_SET
            = CollectionUtil.toSet('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    public static final Set<Character> ORDERED_CHAR_SET
            = CollectionUtil.toSortedSet('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    private static final int DEFAULT_LENGTH = 4;

    private int radix;
    private int length;
    private CustomCounter counter;
    private char[] digitSymbols;
    private int[] displayColumn;
    private int[] seed;
    private int cycleCounter;

    public UniqueFixedLengthStringGenerator() {
        this(DEFAULT_LENGTH, DEFAULT_CHAR_SET);
    }

    public UniqueFixedLengthStringGenerator(int length, Set<Character> charSet) {
        this(length, CollectionUtil.toCharArray(charSet));
    }

    public UniqueFixedLengthStringGenerator(int length, char ... chars) {
        radix = chars.length;
        digitSymbols = chars;
        this.length = length;
        this.displayColumn = new int[length];
        this.seed = new int[length];
    }

    // Generator interface ---------------------------------------------------------------------------------------------

	public Class<String> getGeneratedType() {
	    return String.class;
    }

    @Override
    public synchronized void init(GeneratorContext context) {
    	assertNotInitialized();
    	BitReverseNaturalNumberGenerator gen = new BitReverseNaturalNumberGenerator(length - 1);
        gen.init(context);
        for (int i = 0; i < length; i++) {
            this.displayColumn[i] = gen.generate().intValue();
            this.seed[i] = RandomUtil.randomInt(0, length - 1);
        }
        resetMembers();
        super.init(context);
    }
    
	@Override
	public String generate() {
        if (counter == null)
            return null;
        int[] digits = counter.getDigits();
        char[] tmp = new char[length];
        for (int i = 0; i < digits.length; i++)
            tmp[displayColumn[i]] = digitSymbols[(seed[i] + digits[i] + cycleCounter) % radix];
        String result = new String(tmp);
        if (cycleCounter < radix - 1 && length > 0) {
            cycleCounter++;
        } else {
            counter.increment();
            cycleCounter = 0;
            if (counter.hasOverrun() || radix == 1 || digits[length - 1] > 0) {
                // counter + cycle have run through all combinations
                counter = null;
            }
        }
        return result;
    }

    @Override
    public void reset() {
        super.reset();
        resetMembers();
    }

	private void resetMembers() {
	    this.counter = new CustomCounter(radix, length);
        this.cycleCounter = 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[length=" + length + ", charset=" +
                ArrayFormat.formatChars(",", digitSymbols) + ']'; 
    }

}
