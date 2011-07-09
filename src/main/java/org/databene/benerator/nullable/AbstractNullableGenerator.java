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

package org.databene.benerator.nullable;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.GeneratorState;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;

/**
 * Abstract {@link NullableGenerator} implementation which holds a state and state management methods.<br/><br/>
 * Created: 24.02.2010 15:22:42
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class AbstractNullableGenerator<E> implements NullableGenerator<E> { // TODO create common concept with AbstractGenerator
	
	protected GeneratorState state = GeneratorState.CREATED;

	public void init(GeneratorContext context) throws InvalidGeneratorSetupException {
	    state = GeneratorState.RUNNING;
    }

	public void reset() {
		state = GeneratorState.RUNNING;
	}
	
	public void close() {
		state = GeneratorState.CLOSED;
	}
	
	// internal helpers ------------------------------------------------------------------------------------------------
    
    protected final void assertNotInitialized() {
	    if (state != GeneratorState.CREATED)
    		throw new IllegalGeneratorStateException("Trying to initialize generator in state " + state);
    }

    protected final void assertInitialized() {
    	if (state != GeneratorState.RUNNING)
    		throw new IllegalGeneratorStateException("Generator was not initialized: " + this);
    }
    
}
