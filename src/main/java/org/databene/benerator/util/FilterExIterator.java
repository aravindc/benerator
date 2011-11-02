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

package org.databene.benerator.util;

import java.util.Iterator;

import org.databene.commons.Context;
import org.databene.script.Expression;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.util.DataIteratorProxy;

/**
 * {@link Iterator} proxy which filters its source's output with a (boolean) filter expression.<br/><br/>
 * Created: 08.03.2011 11:51:51
 * @since 0.5.8
 * @author Volker Bergmann
 */
public class FilterExIterator<E> extends DataIteratorProxy<E> {
	
	Expression<Boolean> filterEx;
	Context context;

	public FilterExIterator(DataIterator<E> source, Expression<Boolean> filterEx, Context context) {
		super(source);
		this.filterEx = filterEx;
		this.context = context;
	}

	@Override
	public DataContainer<E> next(DataContainer<E> wrapper) {
		DataContainer<E> tmp;
		while ((tmp = super.next(wrapper)) != null) {
			context.set("_candidate", tmp.getData());
			if (filterEx.evaluate(context))
				return tmp;
		}
		return null;
	}
	
}
