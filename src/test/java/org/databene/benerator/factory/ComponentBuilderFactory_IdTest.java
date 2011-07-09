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

package org.databene.benerator.factory;

import static junit.framework.Assert.assertEquals;

import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.model.data.Entity;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.Uniqueness;
import org.junit.Test;

/**
 * Tests the id builder creation of the {@link ComponentBuilderFactory}.<br/><br/>
 * Created: 06.07.2011 15:46:50
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class ComponentBuilderFactory_IdTest {

    @Test
	public void testDefault() {
		IdDescriptor id = new IdDescriptor("id", "int");
		ComponentBuilder<Entity> generator = createAndInitBuilder(id);
		Entity entity = new Entity("Person");
		generator.buildComponentFor(entity);
		assertEquals(1, entity.get("id"));
	}
    
    // TODO test other id generation options

    @SuppressWarnings("unchecked")
	private ComponentBuilder<Entity> createAndInitBuilder(IdDescriptor id) {
		BeneratorContext context = new BeneratorContext(null);
		ComponentBuilder<Entity> builder = (ComponentBuilder<Entity>) ComponentBuilderFactory.createComponentBuilder(id, Uniqueness.NONE, context);
		builder.init(context);
		return builder;
	}

}
