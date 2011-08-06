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
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.wrapper.WrapperFactory;

/**
 * Parent class for facilitating individual {@link ComponentBuilder} implementation.<br/><br/>
 * Created: 30.04.2010 09:34:42
 * @since 0.6.1
 * @author Volker Bergmann
 */
public abstract class AbstractComponentBuilder<E> implements ComponentBuilder<E> {

	protected Generator<?> source;
	
    public AbstractComponentBuilder(Generator<?> source, double nullQuota) {
		this(WrapperFactory.injectNulls(source, nullQuota));
	}
    
    public Generator<?> getSource() {
    	return source;
    }

    public AbstractComponentBuilder(Generator<?> source) {
		this.source = source;
	}

	public Class<?> getGeneratedType() {
	    return source.getGeneratedType();
	}
	
	public void close() {
    	source.close();
	}

	public void init(GeneratorContext context) {
		source.init(context);
	}

	public void reset() {
		source.reset();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + '{' + source + '}';
	}

	public boolean isParallelizable() {
	    return source.isParallelizable();
    }

	public boolean isThreadSafe() {
	    return source.isThreadSafe();
    }
	
}
