/*
 * (c) Copyright 2008-2013 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.consumer;

import org.databene.benerator.Consumer;
import org.databene.benerator.wrapper.ProductWrapper;


/**
 * Abstract implementation of the Consumer interface. 
 * Custom implementations should rather inherit from this class 
 * than implement the Consumer interface directly.
 * This increases the chance to keep custom consumers compatible 
 * with future versions.<br/><br/>
 * Created: 25.01.2008 22:37:42
 * @since 0.4.0
 * @author Volker Bergmann
 */
public abstract class AbstractConsumer implements Consumer {
	
	@Override
	public void startConsuming(ProductWrapper<?> wrapper) {
		startProductConsumption(wrapper.unwrap());
	}
	
	@Override
	public void finishConsuming(ProductWrapper<?> wrapper) {
		finishProductConsumption(wrapper.unwrap());
	}

	public abstract void startProductConsumption(Object object);
	
	public void finishProductConsumption(Object object) { }
	
    @Override
	public void flush() { }
    
    @Override
	public void close() { }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName();
    }
    
}