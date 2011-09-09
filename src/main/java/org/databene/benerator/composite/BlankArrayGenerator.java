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

import org.databene.benerator.util.ThreadSafeNonNullGenerator;

/**
 * Generates an array of fixed size filled with nulls.<br/><br/>
 * Created: 30.04.2010 08:36:00
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class BlankArrayGenerator extends ThreadSafeNonNullGenerator<Object[]> {

    private static final Class<Object[]> GENERATED_TYPE = Object[].class;

    private int length;

    public BlankArrayGenerator(int length) {
        this.length = length;
    }

	public Class<Object[]> getGeneratedType() {
        return GENERATED_TYPE;
    }

	@Override
	public Object[] generate() {
        return new Object[length];
    }

	@Override
	public String toString() {
		return getClass().getSimpleName() + '[' + length + ']';
	}
	
}
