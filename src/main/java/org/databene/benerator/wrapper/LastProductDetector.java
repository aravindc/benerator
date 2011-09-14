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
 * Wraps another {@link Generator}, finds out which is the last generated object and tags that with "last"="true".<br/>
 * <br/>
 * Created: 12.09.2011 12:06:26
 * @see ProductWrapper#getTag(String)
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class LastProductDetector<E> extends GeneratorProxy<E> {

	private ProductWrapper<E> next;
	
	public LastProductDetector(Generator<E> source) {
		super(source);
	}
	
	@Override
	public synchronized void init(GeneratorContext context) {
		super.init(context);
		this.next = generateFromSource();
	}

	@Override
	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
		if (next == null)
			return null;
		wrapper.wrap(next.unwrap());
		next = super.generate(next);
		if (next == null)
			wrapper.setTag("last", "true");
		return wrapper;
	}
	
	@Override
	public void reset() {
		super.reset();
		this.next = generateFromSource();
	}

	@Override
	public void close() {
		super.close();
		this.next = null;
	}
}
