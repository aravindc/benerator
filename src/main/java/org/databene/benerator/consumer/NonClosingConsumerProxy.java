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

package org.databene.benerator.consumer;

import org.databene.benerator.Consumer;
import org.databene.benerator.engine.ResourceManager;

/**
 * {@link Consumer} proxy that prevents its delegate from being closed.
 * Note: Users of this class must ensure that the delegate is closed 
 * a different way (e.g. by a {@link ResourceManager}).<br/><br/>
 * Created: 14.04.2011 11:36:07
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class NonClosingConsumerProxy extends ConsumerProxy {

	public NonClosingConsumerProxy(Consumer target) {
		super(target);
	}

	@Override
	public void close() {
		// don't close the target here, that's the job of the target's originator
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + '[' + target + ']';
	}
	
}
