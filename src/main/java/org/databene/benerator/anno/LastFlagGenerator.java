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

package org.databene.benerator.anno;

import org.databene.benerator.Generator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * Proxies a {@link Generator}, examines its generated {@link ProductWrapper}s for the "last" tag and, if one is found,
 * replaces a boolean array value at a given index ({@link #indexOfLastFlag}) with true, otherwise with false.<br/>
 * <br/>
 * Created: 12.09.2011 12:27:34
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class LastFlagGenerator extends GeneratorProxy<Object[]> {
	
	private int indexOfLastFlag;

	public LastFlagGenerator(Generator<Object[]> source, int indexOfLastFlag) {
		super(source);
		this.indexOfLastFlag = indexOfLastFlag;
	}

	@Override
	public ProductWrapper<Object[]> generate(ProductWrapper<Object[]> wrapper) {
		ProductWrapper<Object[]> result = super.generate(wrapper);
		if (result != null)
			result.unwrap()[indexOfLastFlag] = ("true".equals(result.getTag("last")));
		return result;
	}
	
}
