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

package org.databene.benerator.wrapper;

import java.util.HashMap;
import java.util.Map;

import org.databene.benerator.Generator;

/**
 * Helper class for the {@link Generator} class.<br/><br/>
 * Created: 26.01.2010 10:53:53
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ProductWrapper<E> {
	
	private E product;
	private boolean wrapped;
	private Map<String, String> tags;
	
	public ProductWrapper() {
		this.wrapped = false;
		this.tags = null;
	}
	
	public ProductWrapper<E> wrap(E product) {
		this.product = product;
		this.wrapped = true;
		if (tags != null)
			tags.clear();
		return this;
	}
	
	public E unwrap() {
		if (!wrapped)
			throw new IllegalStateException("Tried to unwrap a product twice");
		E result = this.product;
		this.product = null;
		this.wrapped = false;
		return result;
	}
	
	public String getTag(String key) {
		return (tags != null ? tags.get(key) : null);
	}
	
	public ProductWrapper<E> setTag(String key, String value) {
		if (tags == null)
			tags = new HashMap<String, String>();
		tags.put(key, value);
		return this;
	}

	@Override
	public String toString() {
	    return product.toString();
	}

	public static Object unwrap(ProductWrapper<?> wrapper) {
	    return (wrapper != null ? wrapper.product : null);
    }
	
}
