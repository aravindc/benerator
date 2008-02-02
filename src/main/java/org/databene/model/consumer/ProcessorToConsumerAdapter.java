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

package org.databene.model.consumer;

import org.databene.model.Processor;

/**
 * Wraps an implementor of the deprecated {@link Processor} interface, providing a {@link Consumer} interface.
 * This class is only provided for easing migration from 0.3.x to 0.4.x and will be removed in future releases.<br/><br/>
 * Created: 01.02.2008 16:23:46
 * @since 0.4.0
 * @author Volker Bergmann
 * @deprecated This class is only provided for easing migration from 0.3.x to 0.4.x 
 * and will be removed in future releases - use a {@link Consumer} instead of a {@link Processor}.
 */
@Deprecated
public class ProcessorToConsumerAdapter implements Consumer {

    private Processor processor;
    
    public ProcessorToConsumerAdapter(Processor processor) {
        this.processor = processor;
    }

    /**
     * @see org.databene.model.consumer.Consumer#startConsuming(java.lang.Object)
     */
    public void startConsuming(Object object) {
        processor.process(object);
    }

    /**
     * @see org.databene.model.consumer.Consumer#finishConsuming(java.lang.Object)
     */
    public void finishConsuming(Object object) {
        // ignored
    }

    /**
     * @see org.databene.model.consumer.Consumer#flush()
     */
    public void flush() {
        processor.flush();
    }

    public void close() {
        processor.close();
    }
}
