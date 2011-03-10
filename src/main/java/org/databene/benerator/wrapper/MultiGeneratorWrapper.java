/*
 * (c) Copyright 2006-2010 by Volker Bergmann. All rights reserved.
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

import java.lang.reflect.Array;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.AbstractGenerator;
import org.databene.benerator.util.RandomUtil;
import org.databene.commons.ArrayFormat;
import org.databene.commons.CollectionUtil;

/**
 * Parent class for wrapping several other generators (in a <i>sources</i> property) 
 * and refining a composite state from them.<br/>
 * <br/>
 * Created: 19.12.2006 07:05:29
 * @since 0.1
 * @author Volker Bergmann
 */
public abstract class MultiGeneratorWrapper<S, P> extends AbstractGenerator<P> {

	protected Class<P> generatedType;
    protected Generator<? extends S>[] sources;
    protected List<Generator<? extends S>> availableSources;
    
    public MultiGeneratorWrapper(Class<P> generatedType, Generator<? extends S> ... sources) {
    	this.generatedType = generatedType;
        setSources(sources);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public Generator<? extends S>[] getSources() {
        return sources;
    }
    
    public synchronized void setSources(Generator<? extends S> ... sources) {
        this.sources = sources;
        this.availableSources = CollectionUtil.toList(sources);
    }

    public Generator<? extends S> getSource(int index) {
        return sources[index];
    }
    
    @SuppressWarnings("unchecked")
    public synchronized void addSource(Generator<S> source) {
    	Generator<S>[] newSources = new Generator[sources.length + 1];
    	System.arraycopy(sources, 0, newSources, 0, sources.length);
    	newSources[sources.length] = source;
    	setSources(newSources);
    }

    // Generator interface implementation ------------------------------------------------------------------------------

	public Class<P> getGeneratedType() {
		return generatedType;
	}
	
    @Override
    public synchronized void init(GeneratorContext context) {
    	assertNotInitialized();
        if (sources.length == 0)
            throw new InvalidGeneratorSetupException("sources", "is empty");
        for (Generator<? extends S> source : sources)
            source.init(context);
        super.init(context);
    }

    @Override
    public synchronized void reset() {
        for (Generator<? extends S> source : sources)
            source.reset();
        this.availableSources = CollectionUtil.toList(sources);
    	super.reset();
    }

    @Override
    public synchronized void close() {
        for (Generator<? extends S> source : sources)
            source.close();
        this.availableSources.clear();
    	super.close();
    }
    
    public boolean isThreadSafe() {
    	for (Generator<? extends S> source : sources)
    		if (!source.isThreadSafe())
    			return false;
        return true;
    }
    
    public boolean isParallelizable() {
    	for (Generator<? extends S> source : sources)
    		if (!source.isParallelizable())
    			return false;
        return true;
    }
    
    // helpers ---------------------------------------------------------------------------------------------------------
    
    protected synchronized S generateFromRandomSource() {
    	assertInitialized();
    	if (availableSources.size() == 0)
    		return null;
    	S product;
    	do {
        	int sourceIndex = RandomUtil.randomIndex(availableSources);
    		product = availableSources.get(sourceIndex).generate();
    		if (product == null)
    			availableSources.remove(sourceIndex);
    	} while (product == null && availableSources.size() > 0);
    	return product;
    }

    @SuppressWarnings("unchecked")
    protected synchronized S[] generateFromAllSources(Class<S> componentType) {
    	assertInitialized();
    	if (availableSources.size() < sources.length)
    		return null;
    	S[] result = (S[]) Array.newInstance(componentType, sources.length);
    	for (int i = 0; i < sources.length; i++) {
    		S product = sources[i].generate();
    		if (product == null) {
    			availableSources.remove(i);
    			return null;
    		}
    		result[i] = product;
    	}
    	return result;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
    public synchronized String toString() {
        return getClass().getSimpleName() + "[" + ArrayFormat.format(sources) + "]";
    }

}
