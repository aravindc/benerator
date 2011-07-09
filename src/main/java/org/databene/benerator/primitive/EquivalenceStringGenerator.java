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
 * {@link Generator} which generates {@link String}s by first generating a part and a part count
 * and the repeating the part the generated number of times.<br/><br/>
 * Created: 08.07.2011 06:20:42
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class EquivalenceStringGenerator<E> extends ThreadSafeGenerator<String> {

	Generator<E> partGenerator;
	Generator<Integer> lengthGenerator;
	
	Integer currentLength;
	
	public EquivalenceStringGenerator(Generator<E> charGenerator,
			Generator<Integer> lengthGenerator) {
		this.partGenerator = charGenerator;
		this.lengthGenerator = lengthGenerator;
	}

	public Class<String> getGeneratedType() {
		return String.class;
	}
	
	@Override
	public synchronized void init(GeneratorContext context) {
		partGenerator.init(context);
		lengthGenerator.init(context);
		currentLength = lengthGenerator.generate();
		super.init(context);
	}

	public String generate() {
		assertInitialized();
		if (currentLength == null)
			return null;
		E part = partGenerator.generate();
		if (part == null) {
			currentLength = lengthGenerator.generate();
			if (currentLength == null)
				return null;
			partGenerator.reset();
			part = partGenerator.generate();
		}
		return createString(part, currentLength);
	}
	
	@Override
	public void reset() {
		partGenerator.reset();
		lengthGenerator.reset();
		currentLength = lengthGenerator.generate();
		super.reset();
	}

	@Override
	public void close() {
		partGenerator.close();
		lengthGenerator.close();
		super.close();
	}
	
	private String createString(E part, Integer length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++)
			builder.append(part);
		return builder.toString();
	}

}
