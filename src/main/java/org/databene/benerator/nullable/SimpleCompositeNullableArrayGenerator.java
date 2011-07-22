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

import java.lang.reflect.Array;

import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.benerator.wrapper.ThreadLocalProductWrapper;
import org.databene.commons.ArrayUtil;

/**
 * TODO Document class.<br/><br/>
 * Created: 22.07.2011 11:42:11
 * @since TODO version
 * @author Volker Bergmann
 */
public class SimpleCompositeNullableArrayGenerator<S> extends MultiNullableGeneratorWrapper<S, S[]> {

    private Class<S> componentType;
    private boolean available;
	private ThreadLocalProductWrapper<S> threadLocalWrapper = new ThreadLocalProductWrapper<S>();

    // constructors ----------------------------------------------------------------------------------------------------

    /** Initializes the generator to an array of source generators */
    @SuppressWarnings("unchecked")
	public SimpleCompositeNullableArrayGenerator(Class<S> componentType, NullableGenerator<? extends S> ... sources) {
        super(ArrayUtil.arrayType(componentType), sources);
        this.componentType = componentType;
        this.available = true;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    /** @see org.databene.benerator.Generator#generate() */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public synchronized S[] generate() {
    	if (!available)
    		return null;
        S[] array = (S[]) Array.newInstance(componentType, sources.length);
        for (int i = 0; i < array.length; i++) {
            try {
                ProductWrapper elementWrapper = threadLocalWrapper.get();
                elementWrapper = sources[i].generate(elementWrapper);
                if (elementWrapper == null) {
                	available = false;
                	return null;
                }
				array[i] = (S) elementWrapper.product;
            } catch (Exception e) {
                throw new RuntimeException("Generation failed for generator #" + i + ": " + sources[i], e);
            }
        }
        return array;
    }

    @Override
    public synchronized void reset() {
    	super.reset();
    	this.available = true;
    }
    
    @Override
    public synchronized void close() {
    	super.close();
    	this.available = false;
    }
    
}
