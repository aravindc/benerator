/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.StorageSystem;
import org.databene.benerator.wrapper.DataSourceGenerator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.StringUtil;

/**
 * Generates values based on a database query.<br/>
 * <br/>
 * Created at 06.07.2009 08:02:21
 * @since 0.6.0
 * @author Volker Bergmann
 */

@SuppressWarnings("rawtypes")
public class QueryGenerator<E> extends GeneratorProxy<E> {
	
	private StorageSystem target;
	private String selector;
	private boolean simplifying;
	
    public QueryGenerator() {
		this(null, null, true);
	}

    @SuppressWarnings("unchecked")
	public QueryGenerator(String selector, StorageSystem target, boolean simplifying) {
    	super((Class<E>) Object.class);
		this.target = target;
		this.selector = selector;
		this.simplifying = simplifying;
	}

	public void setTarget(StorageSystem storage) {
		this.target = storage;
	}
	
	public void setSelector(String selector) {
		this.selector = selector;
	}
	
    @SuppressWarnings("unchecked")
	@Override
    public void init(GeneratorContext context) throws InvalidGeneratorSetupException {
    	
    	// check preconditions
    	assertNotInitialized();
	    if (target == null)
	    	throw new InvalidGeneratorSetupException("source is null");
	    if (StringUtil.isEmpty(selector))
	    	throw new InvalidGeneratorSetupException("no query defined");
	    
	    // initialize
		setSource(new DataSourceGenerator(target.query(selector, simplifying, context)));
	    super.init(context);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + selector + "]";
    }
    
}
