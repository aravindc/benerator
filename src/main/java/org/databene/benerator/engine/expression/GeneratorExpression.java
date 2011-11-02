/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.expression;

import org.databene.benerator.Generator;
import org.databene.benerator.util.WrapperProvider;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Context;
import org.databene.script.Expression;
import org.databene.script.expression.DynamicExpression;

/**
 * {@link Expression} implementation that evaluates to a {@link Generator}.<br/>
 * <br/>
 * Created at 23.07.2009 15:17:04
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class GeneratorExpression<E> extends DynamicExpression<E> {

	private Generator<E> generator;
	private WrapperProvider<E> wrapperProvider = new WrapperProvider<E>();
	
    public GeneratorExpression(Generator<E> generator) {
	    this.generator = generator;
    }

	public E evaluate(Context context) {
		ProductWrapper<E> result = generator.generate(wrapperProvider.get());
		return (result != null ? result.unwrap() : null);
    }

}
