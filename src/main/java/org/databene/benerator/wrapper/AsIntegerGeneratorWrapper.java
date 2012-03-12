/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

/**
 * Converts the {@link Number} products of another {@link Generator} to {@link Integer}.<br/>
 * <br/>
 * Created at 23.06.2009 22:58:26
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class AsIntegerGeneratorWrapper<E extends Number> extends GeneratorWrapper<E, Integer> {

    public AsIntegerGeneratorWrapper(Generator<E> source) {
	    super(source);
    }

	public Class<Integer> getGeneratedType() {
	    return Integer.class;
    }

	public ProductWrapper<Integer> generate(ProductWrapper<Integer> wrapper) {
    	assertInitialized();
    	ProductWrapper<E> tmp = generateFromSource();
    	if (tmp == null)
    		return null;
	    E unwrappedValue = tmp.unwrap();
		return wrapper.wrap(unwrappedValue != null ? unwrappedValue.intValue() : null);
    }

}
