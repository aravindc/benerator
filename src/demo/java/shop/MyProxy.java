/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

package shop;

import org.databene.benerator.Consumer;
import org.databene.benerator.consumer.ConsumerProxy;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple {@link Consumer} proxy implementation that logs an entity before it is forwarded to the target consumer.<br/>
 * <br/>
 * Created: 26.08.2007 14:47:40
 */
public class MyProxy extends ConsumerProxy {

    private static Logger logger = LoggerFactory.getLogger(MyProxy.class);

    public MyProxy() {
        this(null);
    }

    public MyProxy(Consumer target) {
        super(target);
    }
    
    // Consumer interface ----------------------------------------------------------------------------------------------

    @Override
	public void startConsuming(ProductWrapper<?> wrapper) {
        logger.info(wrapper.toString());
        target.startConsuming(wrapper);
    }

    @Override
	public void finishConsuming(ProductWrapper<?> wrapper) {
        logger.info(wrapper.toString());
        target.finishConsuming(wrapper);
    }
    
    @Override
    public void flush() {
        target.flush();
    }

    @Override
    public void close() {
    	IOUtil.close(target);
    }

}
