/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.Generator;
import org.databene.benerator.factory.GeneratorFactoryUtil;
import org.databene.benerator.util.WrapperProvider;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Mutator;

/**
 * Helper class for simple creation of custom {@link ComponentBuilder}s which uses a {@link Mutator}
 * object for abstracting the target object type.<br/><br/>
 * Created: 30.04.2010 09:40:40
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class DefaultComponentBuilder<E> extends AbstractComponentBuilder<E> {

	protected Mutator mutator;
	private WrapperProvider<Object> productWrapper = new WrapperProvider<Object>();
	
    public DefaultComponentBuilder(Generator<?> source, Mutator mutator, double nullQuota) {
		this(GeneratorFactoryUtil.injectNulls(source, nullQuota), mutator);
	}

    public DefaultComponentBuilder(Generator<?> source, Mutator mutator) {
		super(source);
		this.mutator = mutator;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean buildComponentFor(E target) {
		ProductWrapper<?> wrapper = source.generate((ProductWrapper) productWrapper.get());
		if (wrapper == null)
			return false;
		mutator.setValue(target, wrapper.unwrap());
		return true;
	}
	
}
