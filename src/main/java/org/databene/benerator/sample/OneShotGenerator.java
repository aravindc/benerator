/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.sample;

import org.databene.benerator.Generator;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.engine.BeneratorContext;

/**
 * Returns a value only once and then becomes unavailable immediately.<br/>
 * <br/>
 * Created at 23.09.2009 00:20:03
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class OneShotGenerator<E> implements Generator<E> {

	private E value;
	private boolean used;
	
    public OneShotGenerator(E value) {
	    this.value = value;
	    this.used = false;
    }

    public void close() {
    	used = true;
	    value = null;
    }

    public E generate() throws IllegalGeneratorStateException {
	    if (used)
	    	return null;
	    used = true;
	    return value;
    }

    @SuppressWarnings("unchecked")
    public Class<E> getGeneratedType() {
	    return (Class<E>) value.getClass();
    }

    public void init(BeneratorContext context) throws InvalidGeneratorSetupException {
	    if (value == null)
	    	throw new InvalidGeneratorSetupException("value is null");
    }

    public void reset() throws IllegalGeneratorStateException {
	    used = false;
    }

}
