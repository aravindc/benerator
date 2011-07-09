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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;

/**
 * Keeps an array of generators, of which it combines the products to an array.<br/><br/>
 * Created: 28.07.2010 19:10:53
 * @since 0.1
 * @author Volker Bergmann
 */
public class CompositeArrayGenerator<S> extends GeneratorProxy<S[]> {

	private Class<S> targetType;
    private boolean unique;
    private Generator<? extends S>[] sources;
    
	public CompositeArrayGenerator(Class<S> targetType, boolean unique, Generator<? extends S>... sources) {
	    this.targetType = targetType;
	    this.unique = unique;
	    this.sources = sources;
    }

	public void setUnique(boolean unique) {
    	this.unique = unique;
    }
	
	public Generator<? extends S>[] getSources() {
		return sources;
	}
    
    @Override
    public synchronized void init(GeneratorContext context) {
		if (unique)
			super.setSource(new UniqueCompositeArrayGenerator<S>(targetType, sources));
		else
			super.setSource(new SimpleCompositeArrayGenerator<S>(targetType, sources));
	    super.init(context);
    }
    
}
