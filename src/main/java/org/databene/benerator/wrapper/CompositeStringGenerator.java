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
 * Takes the output of several source {@link Generator}s and combines them to a result String.
 * If the source generators generate unique data, the CompositeStringGenerator is able to combine 
 * their output to unique values when setting its 'unique' property to 'true'.<br/><br/>
 * Created: 28.07.2010 21:53:41
 * @since 0.6.3
 * @author Volker Bergmann
 */
public abstract class CompositeStringGenerator extends GeneratorWrapper<Object[], String>{
	
	private boolean unique;

	public CompositeStringGenerator() {
	    this(false);
    }

	public CompositeStringGenerator(boolean unique) {
		super(null);
	    this.unique = unique;
    }
	
    public boolean isUnique() {
    	return unique;
    }

	public void setUnique(boolean unique) {
    	this.unique = unique;
    }

	final public Class<String> getGeneratedType() {
	    return String.class;
    }

    @Override
	public synchronized void init(GeneratorContext context) {
		Generator<? extends Object>[] sources = initSources(context, unique);
		super.setSource(new CompositeArrayGenerator<Object>(Object.class, unique, sources));
	    super.init(context);
	}
	
	public String generate() {
		assertInitialized();
		Object[] parts = source.generate();
		if (parts == null)
			return null;
		StringBuilder builder = new StringBuilder();
		for (Object part : parts) {
			if (part == null)
				return null;
			builder.append(part);
		}
		return builder.toString();
    }

    protected abstract Generator<?>[] initSources(GeneratorContext context, boolean unique);

}
