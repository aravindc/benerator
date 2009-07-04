/*
 * (c) Copyright 2007, 2008 by Volker Bergmann. All rights reserved.
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

package org.databene.model.consumer;

import java.util.List;

import org.databene.commons.CollectionUtil;
import org.databene.commons.IOUtil;

/**
 * Combines several Processors under one Processor interface.
 * Each call to the Processor is forwarded to all sub Processors.<br/>
 * <br/>
 * Created: 26.08.2007 14:50:29
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class ConsumerChain<E> extends AbstractConsumer<E> {

    private List<Consumer<E>> components;

    // constructors ----------------------------------------------------------------------------------------------------

    public ConsumerChain(Consumer<E> ... components) {
    	setComponents(components);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setComponents(Consumer<E>... components) {
    	this.components = CollectionUtil.toList(components);
    }

    public void addComponent(Consumer<E> component) {
        this.components.add(component);
    }
    
    public Consumer<E> getComponent(int index) {
        return this.components.get(index);
    }
    
    public int componentCount() {
    	return components.size();
    }

    // Processor interface ---------------------------------------------------------------------------------------------

    public void startConsuming(E object) {
        for (Consumer<E> processor : components)
            processor.startConsuming(object);
    }

    @Override
	public void finishConsuming(E object) {
        for (Consumer<E> processor : components)
            processor.finishConsuming(object);
    }

    @Override
	public void flush() {
        for (Consumer<E> processor : components)
            processor.flush();
    }

    @Override
	public void close() {
        for (Consumer<E> consumer : components)
            IOUtil.close(consumer);
    }

    public List<Consumer<E>> getComponents() {
    	return components;
    }
}
