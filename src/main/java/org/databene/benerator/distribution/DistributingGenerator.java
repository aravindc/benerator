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

package org.databene.benerator.distribution;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.wrapper.GeneratorProxy;

/**
 * General purpose generator proxy which is supposed to work with any distribution. 
 * The behavior on a reset is up to the generator created by the distribution.<br/>
 * <br/>
 * Created: 22.03.2010 10:45:48
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DistributingGenerator<E> extends GeneratorProxy<E> {
	
	private Generator<E> dataProvider;
	private Distribution distribution;
	private boolean unique;

	public DistributingGenerator(Generator<E> dataProvider, Distribution distribution, boolean unique) {
		super(dataProvider.getGeneratedType());
		this.dataProvider = dataProvider;
		this.distribution = distribution;
		this.unique = unique;
    }
	
	@Override
	public void init(GeneratorContext context) {
		dataProvider.init(context);
		setSource(distribution.applyTo(dataProvider, unique));
	    super.init(context);
	}
	
}
