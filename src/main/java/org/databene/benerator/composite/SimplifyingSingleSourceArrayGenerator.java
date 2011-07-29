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

package org.databene.benerator.composite;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.benerator.wrapper.SingleSourceArrayGenerator;

/**
 * Creates a stochastic number of instances of a type. The number of elements is determined by the values 
 * minCount, maxCount, countDistribution. 
 * If the number of items is not one, an array of respective size is returned, 
 * otherwise a single object.<br/><br/>
 * Created: 06.03.2008 15:43:54
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class SimplifyingSingleSourceArrayGenerator<S> extends SingleSourceArrayGenerator<S, Object> {
    
    public SimplifyingSingleSourceArrayGenerator(Generator<S> source, NonNullGenerator<Integer> countGenerator) {
        super(source, source.getGeneratedType(), countGenerator);
    }
    
	@Override
	public ProductWrapper<Object> generate(ProductWrapper<Object> wrapper) {
		Object[] array = (Object[]) super.generate();
		if (array == null)
			return null;
        if (array.length == 1)
            return wrapper.wrap(array[0]);
        else
        	return wrapper.wrap(array);
    }

}
