/*
 * (c) Copyright 2008-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.composite;

import java.util.List;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.util.RandomUtil;
import org.databene.commons.ArrayFormat;
import org.databene.commons.CollectionUtil;
import org.databene.model.data.Entity;

/**
 * Abstract parent class for all builders that relate to a group of components.<br/><br/>
 * Created at 09.05.2008 13:38:33
 * @since 0.5.4
 * @author Volker Bergmann
 */
public abstract class MultiComponentBuilder implements ComponentBuilder {
	
	protected ComponentBuilder[] builders;
	private List<ComponentBuilder> avalailableBuilders;

	public MultiComponentBuilder(ComponentBuilder[] builders) {
		this.builders = builders;
		this.avalailableBuilders = CollectionUtil.toList(builders);
	}
	
	// Generator interface ---------------------------------------------------------------------------------------------

	public Class<?> getGeneratedType() {
	    return (builders != null && builders.length > 0 ? builders[0].getGeneratedType() : Object.class);
	}
	
	public void init(GeneratorContext context) {
		for (ComponentBuilder builder : builders)
			builder.init(context);
	}

	public void reset() {
		for (ComponentBuilder builder : builders)
			builder.reset();
		this.avalailableBuilders = CollectionUtil.toList(builders);
	}

	public void close() {
		for (ComponentBuilder builder : builders)
			builder.close();
		this.avalailableBuilders.clear();
	}
	
	public boolean buildRandomComponentFor(Entity entity) {
		if (avalailableBuilders.size() == 0)
			return false;
		boolean success;
		do {
			int builderIndex = RandomUtil.randomIndex(avalailableBuilders);
			success = avalailableBuilders.get(builderIndex).buildComponentFor(entity);
		} while (!success && avalailableBuilders.size() > 0);
	    return success;
	}
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ArrayFormat.format(builders);
	}
	
}
