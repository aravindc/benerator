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

package org.databene.benerator.util;

import java.util.List;
import java.util.Random;

/**
 * Provides utility functions for generating numbers in an interval.<br/>
 * <br/>
 * Created: 03.09.2006 13:23:02
 * @since 0.1
 * @author Volker Bergmann
 */
public class RandomUtil {

    /** The basic random provider */
    private static Random random = new Random();

    /** Generates a random long value in the range from min to max */
    public static long randomLong(long min, long max) {
        if (min > max)
            throw new IllegalArgumentException("min > max: " + min + " > " + max);
        long range = max - min + 1;
        long result;
        if (range != 0)
            result = min + (random.nextLong() % range);
        else
            result = random.nextLong();
        if (result < min)
            result += range;
        return result;
    }

    /** Generates a random int value in the range from min to max */
    public static int randomInt(int min, int max) {
        if (min > max)
            throw new IllegalArgumentException("min > max: " + min + " > " + max);
        int range = max - min + 1;
        int result;
        if (range != 0)
            result = min + (random.nextInt() % range);
        else
            result = random.nextInt();
        if (result < min)
            result += range;
        return result;
    }

    public static <T> T randomElement(T ... values) {
    	if (values.length == 0)
    		throw new IllegalArgumentException("Cannot choose random value from an empty array");
        return values[random.nextInt(values.length)];
    }
    
    public static <T> T randomElement(List<T> values) {
    	if (values.size() == 0)
    		throw new IllegalArgumentException("Cannot choose random value from an empty array");
        return values.get(random.nextInt(values.size()));
    }

    /**
     * Calculates the last digit expected for a number that passes the Luhn test,
     * ignoring the last digit. This is useful for creating Luhn numbers.
     * The actual evaluation if a number passes the test is done by 
     * {@link #luhnValid(CharSequence)}.
     * @see "http://en.wikipedia.org/wiki/Luhn_algorithm"
     */
	public static char requiredLuhnDigit(CharSequence number) {
		int sum = 0;
		int multiplier = 2;
		for (int i = number.length() - 2; i >= 0; i--) {
			int digit = number.charAt(i) - '0';
			int partialSum = digit * multiplier;
			sum += (partialSum > 9 ? 1 + (partialSum % 10) : partialSum);
			multiplier = 1 + (multiplier % 2);
		}
		return (char) ('0' + (10 - sum % 10) % 10); 
	}
	
    /**
     * Tests a number against the Luhn algorithm
     * @see #requiredLuhnDigit(CharSequence)
     * @see "http://en.wikipedia.org/wiki/Luhn_algorithm"
     */
	public static boolean luhnValid(CharSequence number) {
		return (requiredLuhnDigit(number) == number.charAt(number.length() - 1)); 
	}
	
}
