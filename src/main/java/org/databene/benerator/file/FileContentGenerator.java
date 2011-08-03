/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.file;

import java.io.File;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.wrapper.NonNullGeneratorWrapper;

/**
 * Abstract parent class for generators that generate products based on concrete files.<br/><br/>
 * Created: 24.02.2010 08:45:14
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class FileContentGenerator<E> extends NonNullGeneratorWrapper<File, E> {

	protected String uri;
	protected String filter;
	protected boolean recursive;
	
	public FileContentGenerator() {
	    super(null);
    }

	public void setUri(String uri) {
    	this.uri = uri;
    }

	public void setFilter(String filter) {
    	this.filter = filter;
    }

	public void setRecursive(boolean recursive) {
    	this.recursive = recursive;
    }

	@Override
    public void init(GeneratorContext context) {
		assertNotInitialized();
	    setSource(new FileGenerator(uri, filter, recursive, true, false));
	    super.init(context);
    }

}