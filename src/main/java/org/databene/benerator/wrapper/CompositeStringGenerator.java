/*
 * (c) Copyright 2007-2013 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;

/**
 * Uses n String generators and appends the output of each one in each generate() call.<br/>
 * <br/>
 * Created: 17.11.2007 17:33:21
 * @author Volker Bergmann
 */
public class CompositeStringGenerator extends GeneratorWrapper<String[], String> {
	
	protected boolean unique;

    // constructors ----------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
	public CompositeStringGenerator() {
        this(false);
    }

    public CompositeStringGenerator(boolean unique, Generator<String>... sources) {
        super(new MultiSourceArrayGenerator<String>(String.class, unique, sources));
        this.unique = unique;
    }
    
	public boolean isUnique() {
		return unique;
	}

	public void setSources(Generator<String>[] sources) {
		((MultiSourceArrayGenerator<String>) getSource()).setSources(sources);
	}
	
    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
	public Class<String> getGeneratedType() {
        return String.class;
    }

	@Override
	public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
        StringBuilder builder = new StringBuilder();
        ProductWrapper<String[]> parts = generateFromSource();
        if (parts == null)
        	return null;
        for (String part : parts.unwrap())
            builder.append(part);
        return wrapper.wrap(builder.toString());
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        Generator<String[]> source = getSource();
        return getClass().getSimpleName() + "[unique=" + unique + ", source=" + source + ']';
    }

    // private helpers -------------------------------------------------------------------------------------------------

    protected static Generator<String[]> wrap(boolean unique, Generator<?>... sources) {
        return new MultiSourceArrayGenerator<String>(String.class, unique, WrapperFactory.asStringGenerators(sources));
    }

}
