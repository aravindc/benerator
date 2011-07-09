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
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.util.ThreadSafeGenerator;

/**
 * Creates Strings from a {@link Character} {@link Generator} with a length defined by a number Generator.<br/>
 * <br/>
 * Created: 04.07.2011 01:35:15
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class DistributedLengthStringGenerator extends ThreadSafeGenerator<String> {
	
	private Generator<Character> charGenerator;
	private Generator<Integer> lengthGenerator;

	public DistributedLengthStringGenerator(Generator<Character> charGenerator, Generator<Integer> lengthGenerator) {
		this.charGenerator = charGenerator;
		this.lengthGenerator = lengthGenerator;
	}

	public Class<String> getGeneratedType() {
		return String.class;
	}
	
	@Override
	public synchronized void init(GeneratorContext context) {
		this.charGenerator.init(context);
		this.lengthGenerator.init(context);
		super.init(context);
	}

	public String generate() {
		Integer length = lengthGenerator.generate();
		if (length == null)
			return null;
		char[] buffer = new char[length];
		for (int i = 0; i < length; i++)
			buffer[i] = charGenerator.generate();
		return new String(buffer);
	}

}
