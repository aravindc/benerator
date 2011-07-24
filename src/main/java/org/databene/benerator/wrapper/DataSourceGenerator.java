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

package org.databene.benerator.wrapper;

import java.io.Closeable;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.AbstractGenerator;
import org.databene.commons.IOUtil;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.DataSource;

/**
 * TODO Document class.<br/><br/>
 * Created: 24.07.2011 08:58:09
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class DataSourceGenerator<E> extends AbstractGenerator<E> {

    private DataSource<E> source;
    private DataIterator<E> iterator;

    // constructors ----------------------------------------------------------------------------------------------------

    public DataSourceGenerator() {
        this(null);
    }

    public DataSourceGenerator(DataSource<E> source) {
        this.source = source;
        this.iterator = null;
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public DataSource<E> getSource() {
        return source;
    }

    public void setSource(DataSource<E> source) {
        if (this.source != null)
        	throw new IllegalGeneratorStateException("Mutating an initialized generator");
        this.source = source;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

	public boolean isParallelizable() { // TODO?
	    return true;
    }

	public boolean isThreadSafe() { // TODO?
	    return true;
    }
    
    public Class<E> getGeneratedType() {
        return source.getType();
    }

    @Override
    public void init(GeneratorContext context) {
    	if (source == null)
    		throw new InvalidGeneratorSetupException("source", "is null");
    	super.init(context);
		createIterator();
    }

    public E generate() {
        try {
            assertInitialized();
            if (iterator == null)
            	return null;
        	E result = iterator.next();
            if (result == null)
            	closeIterator();
			return result;
        } catch (Exception e) {
        	throw new IllegalGeneratorStateException("Generation failed: ", e);
        }
    }

	@Override
    public void reset() {
        closeIterator();
        super.reset();
        createIterator();
    }

    @Override
    public void close() {
        closeIterator();
        super.close();
        if (source instanceof Closeable)
        	IOUtil.close((Closeable) source);
    }

    // private helpers -------------------------------------------------------------------------------------------------    
    
	private void createIterator() {
	    iterator = source.iterator();
    }

    private void closeIterator() {
		if (iterator != null) {
            iterator.close();
            iterator = null;
        }
	}

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + source + ']';
    }

}