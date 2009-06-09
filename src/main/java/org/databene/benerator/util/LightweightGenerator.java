/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.util;

import org.databene.benerator.Generator;
import org.databene.benerator.IllegalGeneratorStateException;

/**
 * Helper for lightweight generators that do not have a life cycle.
 * For these, the close() and reset() methods are implemented with
 * an empty body.<br/>
 * <br/>
 * Created: 15.08.2006 09:12:55
 */
public abstract class LightweightGenerator<E> implements Generator<E> {

    private Class<E> generatedType;

    protected LightweightGenerator() { // TODO v0.6 remove this constructor or attribute and getGeneratedType()
        this((Class<E>) Object.class);
    }

    protected LightweightGenerator(Class<E> generatedType) {
        this.generatedType = generatedType;
    }

    public Class<E> getGeneratedType() {
        return generatedType;
    }
    
	public void setGeneratedType(Class<E> generatedType) {
		this.generatedType = generatedType;
	}

	public void validate() {
    }

    /** Empty implementation */
    public void reset() {
    }

    /** Empty implementation */
    public void close() {
    }

    public boolean available() {
        return true;
    }

    // protected helpers for child classes -----------------------------------------------------------------------------

    protected static IllegalGeneratorStateException stateException(Generator generator) {
        return GeneratorUtil.stateException(generator);
    }

}
