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

package org.databene.benerator.test;

import java.util.List;

import org.databene.commons.CollectionUtil;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.iterator.HeavyweightIteratorProxy;
import org.databene.model.data.AbstractEntitySource;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;

/**
 * {@link EntitySource} implementation for testing.<br/><br/>
 * Created: 11.03.2010 12:42:48
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PersonIterable extends AbstractEntitySource {

	public static final ComplexTypeDescriptor PERSON_DESCRIPTOR = new ComplexTypeDescriptor("Person")
		.withComponent(new ComponentDescriptor("name", "string"))
		.withComponent(new ComponentDescriptor("age", "int"));
	
	public static final Entity ALICE = new Entity(PERSON_DESCRIPTOR, "name", "Alice", "age", "23");
	public static final Entity BOB = new Entity(PERSON_DESCRIPTOR, "name", "Bob", "age", "34");

	public static final List<Entity> PERSONS = CollectionUtil.toList(ALICE, BOB);

	public HeavyweightIterator<Entity> iterator() {
	    return new HeavyweightIteratorProxy<Entity>(PERSONS.iterator());
    }

}
