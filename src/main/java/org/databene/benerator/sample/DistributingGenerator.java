/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.sample;

import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.BeanUtil;
import org.databene.model.function.Distribution;
import org.databene.model.function.IndividualWeight;
import org.databene.model.function.Sequence;
import org.databene.model.function.WeightFunction;

/**
 * Takes the output of another generator and applies a {@link Distribution} to the values.<br/>
 * <br/>
 * Created at 04.11.2008 16:25:42
 * @since 0.5.6
 * @author Volker Bergmann
 */
public class DistributingGenerator<E> extends GeneratorProxy<E>{
	
	private Distribution distribution;
	private String variation1;
	private String variation2;
	private Generator dataProvider;

	public DistributingGenerator(Generator<E> dataProvider, Distribution distribution, String variation1, String variation2) {
		super(null);
		this.dataProvider = dataProvider;
		this.distribution = distribution;
		this.variation1 = variation1;
		this.variation2 = variation2;
	}
	
	@Override
	public Class<E> getGeneratedType() {
		return dataProvider.getGeneratedType();
	}
	
	@Override
	public void validate() {
		if (dirty) {
			List<Object> values = new ArrayList<Object>();
			while (dataProvider.available())
			    values.add(dataProvider.generate());
			if (distribution instanceof Sequence)
				source = new SequencedSampleGenerator(dataProvider.getGeneratedType(), (Sequence) distribution, values);
			else if (distribution instanceof WeightFunction || distribution instanceof IndividualWeight)
				source = new WeightedSampleGenerator(dataProvider.getGeneratedType(), distribution, values);
			else
			    throw new UnsupportedOperationException("Distribution type not supported: " + distribution.getClass());
			if (variation1!= null)
				BeanUtil.setPropertyValue(source, "variation1", variation1, false);
			if (variation2 != null)
				BeanUtil.setPropertyValue(source, "variation2", variation2, false);
			super.validate();
		}
	}
	
	@Override
	public void reset() {
		super.close();
		dataProvider.reset();
		dirty = true;
	}

}
