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

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.wrapper.CardinalGenerator;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * Creates Strings from a {@link Character} {@link Generator} with a length defined by a number Generator.<br/>
 * <br/>
 * Created: 04.07.2011 01:35:15
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class DistributedLengthStringGenerator extends CardinalGenerator<Character, String> 
		implements NonNullGenerator<String>{
	
	public DistributedLengthStringGenerator(Generator<Character> charGenerator, NonNullGenerator<Integer> lengthGenerator) {
		super(charGenerator, false, lengthGenerator);
	}

	public Class<String> getGeneratedType() {
		return String.class;
	}
	
	public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
		return wrapper.wrap(generate());
	}

	public String generate() {
		Integer length = generateCount();
		if (length == null)
			return null;
		char[] buffer = new char[length];
		for (int i = 0; i < length; i++) {
			ProductWrapper<Character> charWrapper = generateFromSource();
			if (charWrapper == null)
				return null;
			buffer[i] = charWrapper.unwrap();
		}
		return new String(buffer);
	}

}
