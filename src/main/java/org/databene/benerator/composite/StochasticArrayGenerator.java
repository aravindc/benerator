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

package org.databene.benerator.composite;

import java.lang.reflect.Array;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.benerator.wrapper.CompositeGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.benerator.wrapper.ThreadLocalProductWrapper;
import org.databene.commons.ArrayUtil;

/**
 * Uses n {@link NullableGenerator}s for generating random arrays of n elements.<br/><br/>
 * Created: 05.07.2011 18:15:23
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class StochasticArrayGenerator<T> extends CompositeGenerator<T[]> { // TODO clean up array generator hierarchy
	
	private ThreadLocalProductWrapper<T> threadLocalWrapper = new ThreadLocalProductWrapper<T>();
	private Class<T> componentType;
	private NullableGenerator<? extends T>[] sources;
	
	@SuppressWarnings("unchecked")
	public StochasticArrayGenerator(Class<T> componentType, NullableGenerator<? extends T>[] sources) {
		super(ArrayUtil.arrayType(componentType));
		this.componentType = componentType;
		this.sources = sources;
		registerComponents(sources);
	}
	
	@Override
	public synchronized void init(GeneratorContext context) {
	    GeneratorUtil.initAll(sources, context);
	    super.init(context);
	}

	@Override
	public void reset() {
		GeneratorUtil.resetAll(sources);
	    super.reset();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public T[] generate() {
		assertInitialized();
		T[] result = (T[]) Array.newInstance(componentType, sources.length);
		ProductWrapper wrapper = threadLocalWrapper.get();
		for (int i = 0; i < sources.length; i++) {
			wrapper = sources[i].generate(wrapper);
			if (wrapper == null)
				return null;
			result[i] = (T) wrapper.product;
		}
	    return result;
    }

}
