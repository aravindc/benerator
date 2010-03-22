/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.db;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.commons.StringUtil;
import org.databene.commons.TypedIterable;
import org.databene.model.storage.StorageSystem;

/**
 * Generates values based on a database query.<br/>
 * <br/>
 * Created at 06.07.2009 08:02:21
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class QueryGenerator<E> extends GeneratorProxy<E> {
	
	private StorageSystem storage;
	private String selector;
	
    public QueryGenerator(String selector, StorageSystem storage) {
		this(selector, storage, null);
	}

    @SuppressWarnings("unchecked")
    public QueryGenerator(String selector, StorageSystem source, BeneratorContext context) {
		this.storage = source;
		this.selector = selector;
		this.source = new IteratingGenerator<E>((TypedIterable<E>) source.query(selector, context));
	}

    @Override
    public void init(GeneratorContext context) throws InvalidGeneratorSetupException {
    	assertNotInitialized();
	    if (storage == null)
	    	throw new InvalidGeneratorSetupException("source is null");
	    if (StringUtil.isEmpty(selector))
	    	throw new InvalidGeneratorSetupException("no query defined");
	    super.init(context);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + selector + "]";
    }
    
}
