/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.nullable;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;

/**
 * Proxy for a {@link NullableGenerator}.<br/><br/>
 * Created: 18.02.2010 11:21:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class NullableGeneratorWrapper<S, P> extends AbstractNullableGenerator<P> {
	
	protected NullableGenerator<S> source;

	public NullableGeneratorWrapper(NullableGenerator<S> source) {
	    this.source = source;
    }

	@Override
	public void close() {
		source.close();
		super.close();
    }

	@Override
	public void reset() {
		source.reset();
		super.reset();
    }

	@Override
    public void init(GeneratorContext context) throws InvalidGeneratorSetupException {
		source.init(context);
	    super.init(context);
    }

	public boolean isThreadSafe() {
	    return source.isThreadSafe();
	}
	
	public boolean isParallelizable() {
	    return source.isParallelizable();
	}
	
}
