/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.Generator;
import org.databene.benerator.util.SimpleRandom;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.commons.CollectionUtil;
import org.databene.commons.CharSet;
import org.databene.commons.ArrayFormat;

import java.util.Set;

/**
 * Generates unique strings of variable length.<br/>
 * <br/>
 * Created: 16.11.2007 11:56:15
 * @author Volker Bergmann
 */
public class UniqueStringGenerator implements Generator<String> {

    private int minLength;
    private int maxLength;
    private char[] charSet;
    private Generator<String>[] subGens;
    private boolean dirty;

    // constructors ----------------------------------------------------------------------------------------------------

    public UniqueStringGenerator() {
        this(4, 8, new CharSet('A', 'Z').getSet());
    }

    public UniqueStringGenerator(int minLength, int maxLength, Set<Character> charSet) {
        this(minLength, maxLength, CollectionUtil.toArray(charSet));
    }

    @SuppressWarnings("unchecked")
    public UniqueStringGenerator(int minLength, int maxLength, char ... charSet) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.charSet = charSet;
        this.subGens = new Generator[0];
        dirty = true;
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
        dirty = true;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        dirty = true;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public Class<String> getGeneratedType() {
        return String.class;
    }

    @SuppressWarnings("unchecked")
    public void validate() {
        if (dirty) {
            this.subGens = new Generator[maxLength - minLength + 1];
            for (int i = minLength; i <= maxLength; i++)
                subGens[i - minLength] = new UniqueFixedLengthStringGenerator(i, charSet);
            dirty = false;
        }
    }

    public boolean available() {
        if (dirty)
            validate();
        if (subGens == null)
        	return false;
        for (int i = maxLength - minLength; i >= 0; i--)
            if (subGens[i].available())
                return true;
        return false;
    }

    public String generate() {
        if (!available())
            throw new IllegalGeneratorStateException("Generator is no longer available");
        int generatorIndex;
        do {
            generatorIndex = SimpleRandom.randomInt(0, maxLength - minLength);
        } while (!subGens[generatorIndex].available());
        return subGens[generatorIndex].generate();
    }

    public void reset() {
    	if (subGens != null)
	        for (Generator<String> generator : subGens)
	            generator.reset();
        dirty = true;
    }

    public void close() {
    	if (subGens != null) {
	        for (Generator<String> generator : subGens)
	            generator.close();
	        subGens = null;
    	}
        dirty = true;
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + minLength + "<=length<=" + maxLength + ", " +
                "charSet=[" + ArrayFormat.formatChars(", ", charSet) + "]]";
    }
    
}
