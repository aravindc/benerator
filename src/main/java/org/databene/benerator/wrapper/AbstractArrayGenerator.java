/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.commons.ArrayUtil;

/**
 * Common abstraction for all generators that create arrays.<br/>
 * <br/>
 * Created at 13.07.2008 15:51:59
 * @since 0.5.4
 * @author Volker Bergmann
 */
public abstract class AbstractArrayGenerator<E, A> extends CardinalGenerator<E, A>{

    private Class<E> componentType;
    private Class<A> generatedType;

    public AbstractArrayGenerator(Generator<E> source, Class<E> componentType, Class<A> generatedType, 
    		int minLength, int maxLength, Distribution lengthDistribution) {
        super(source, minLength, maxLength, 1, SequenceManager.RANDOM_SEQUENCE);
        this.componentType = componentType;
        this.generatedType = generatedType;
    }

    // configuration properties ----------------------------------------------------------------------------------------

    public Class<A> getGeneratedType() {
        return generatedType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(GeneratorContext context) {
        super.init(context);
        if (generatedType == null) {
            Class<E> cType = (componentType != null ? componentType : source.getGeneratedType());
            this.generatedType = ArrayUtil.arrayType(cType);
        }
    }

    @SuppressWarnings("unchecked")
    public ProductWrapper<A> generate(ProductWrapper<A> wrapper) {
    	Integer size = generateCount();
    	if (size == null)
    		return null;
        E[] array = ArrayUtil.newInstance(componentType, size.intValue());
        for (int i = 0; i < size; i++) {
            ProductWrapper<E> component = generateFromSource();
            if (component == null)
            	return null;
			array[i] = component.unwrap();
        } 
        return wrapper.wrap((A) array);
    }

}
