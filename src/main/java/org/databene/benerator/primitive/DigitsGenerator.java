/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.util.RandomUtil;
import org.databene.benerator.util.ThreadSafeGenerator;

/**
 * Generates {@link String}s composed of numerical digits.<br/><br/>
 * Created: 16.10.2009 07:31:16
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DigitsGenerator extends ThreadSafeGenerator<String> {
	
	// TODO v0.6.7 support length and digit distribution?

	private int minLength;
	private int maxLength;
	private int minInitial;
	private String prefix;
	
	public DigitsGenerator(int length) {
	    this(length, length, 0);
    }

	public DigitsGenerator(int minLength, int maxLength, int minInitial) {
	    this.minLength = minLength;
	    this.maxLength = maxLength;
	    this.minInitial = minInitial;
	    this.prefix = "";
    }

	public DigitsGenerator(int minLength, int maxLength, String prefix) {
	    this.minLength = minLength;
	    this.maxLength = maxLength;
	    this.minInitial = 0;
	    this.prefix = prefix;
    }
	
	// properties ------------------------------------------------------------------------------------------------------

	public int getMinLength() {
    	return minLength;
    }

	public void setMinLength(int minLength) {
    	this.minLength = minLength;
    }

	public int getMaxLength() {
    	return maxLength;
    }

	public void setMaxLength(int maxLength) {
    	this.maxLength = maxLength;
    }

	public int getMinInitial() {
    	return minInitial;
    }

	public void setMinInitial(int minInitial) {
    	this.minInitial = minInitial;
    }

	public String getPrefix() {
    	return prefix;
    }

	public void setPrefix(String prefix) {
    	this.prefix = prefix;
    }

	// Generator interface implementation ------------------------------------------------------------------------------

	public Class<String> getGeneratedType() {
	    return String.class;
    }
	
	public String generate() {
	    return generate(prefix);
    }
	
	public String generate(String prefix) throws IllegalGeneratorStateException {
		int length = RandomUtil.randomInt(minLength, maxLength);
		StringBuilder builder = new StringBuilder(prefix);
		if (prefix.length() == 0)
			builder.append(RandomUtil.randomDigit(minInitial));
		for (int i = builder.length(); i < length; i++)
			builder.append(RandomUtil.randomDigit(0));
	    return builder.toString();
    }

	public static String generate(int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++)
			builder.append(RandomUtil.randomDigit(0));
	    return builder.toString();
    }

}
