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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;

/**
 * {@link Generator} proxy which hides the first products of its source generator.<br/><br/>
 * Created: 23.07.2011 10:00:49
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class OffsetBasedGenerator<E> extends GeneratorProxy<E> {

	private int offset;

	public OffsetBasedGenerator() {
		this(null, 0);
	}

	public OffsetBasedGenerator(Generator<E> source, int offset) {
		super(source);
		this.offset = offset;
	}
	
	public int getOffset() {
		return offset;
	}
	
	@Override
	public synchronized void init(GeneratorContext context) {
		super.init(context);
		advanceToOffset();
	}

	@Override
	public void reset() {
		super.reset();
		advanceToOffset();
	}
	
	private void advanceToOffset() {
		ProductWrapper<E> wrapper = getSourceWrapper();
		for (int i = 0; i < offset; i++)
			super.generate(wrapper);
	}
	
}
