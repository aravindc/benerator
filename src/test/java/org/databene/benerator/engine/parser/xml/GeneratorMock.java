/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.parser.xml;

import org.databene.benerator.util.LightweightGenerator;
import org.databene.commons.Assert;
import org.databene.commons.Context;
import org.databene.commons.context.ContextAware;
import org.databene.model.data.Entity;

/**
 * Mock implementation of {@link org.databene.benerator.Generator} and {@link ContextAware}.<br/><br/>
 * Created: 16.02.2010 12:16:33
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class GeneratorMock extends LightweightGenerator<Entity> implements ContextAware {

	public Context context;
	
	public void setContext(Context context) {
		this.context = context;
	}

	public Class<Entity> getGeneratedType() {
		return Entity.class;
    }

	public Entity generate() {
		Assert.notNull(context, "context");
		return new Entity("Dummy");
    }

}
