/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.GeneratorState;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * TODO Document class.<br/><br/>
 * Created: 18.05.2011 14:22:43
 * @since TODO version
 * @author Volker Bergmann
 */
public class CyclicNullableGeneratorProxy<E> extends NullableGeneratorProxy<E> {
	
    public CyclicNullableGeneratorProxy(NullableGenerator<E> source) {
        super(source);
    }

    @Override
    public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    	if (source == null || state == GeneratorState.CLOSED)
    		return null;
    	ProductWrapper<E> generation = source.generate(wrapper);
        if (generation == null) {
            reset();
            generation = source.generate(wrapper);
        }
        return generation;
    }
    
}
