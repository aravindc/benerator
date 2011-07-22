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

package org.databene.benerator.nullable;

import java.lang.reflect.Array;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.AbstractGenerator;
import org.databene.benerator.util.RandomUtil;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.ArrayFormat;
import org.databene.commons.CollectionUtil;

/**
 * Abstract {@link Generator} class which wraps multiple source generators 
 * and provides utility methods for child classes.<br/><br/>
 * Created: 22.07.2011 11:50:21
 * @since 0.7.0
 * @author Volker Bergmann
 */
public abstract class MultiNullableGeneratorWrapper<S, P> extends AbstractGenerator<P> {
	// TODO v0.7 common concept with MultiGeneratorWrapper
	// TODO v0.7 test

	protected Class<P> generatedType;
    protected NullableGenerator<? extends S>[] sources;
    protected List<NullableGenerator<? extends S>> availableSources;
    
    public MultiNullableGeneratorWrapper(Class<P> generatedType, NullableGenerator<? extends S> ... sources) {
    	this.generatedType = generatedType;
        setSources(sources);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public NullableGenerator<? extends S>[] getSources() {
        return sources;
    }
    
    public synchronized void setSources(NullableGenerator<? extends S> ... sources) {
        this.sources = sources;
        this.availableSources = CollectionUtil.toList(sources);
    }

    public NullableGenerator<? extends S> getSource(int index) {
        return sources[index];
    }
    
    @SuppressWarnings("unchecked")
    public synchronized void addSource(NullableGenerator<S> source) {
    	NullableGenerator<S>[] newSources = new NullableGenerator[sources.length + 1];
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
        for (NullableGenerator<? extends S> source : sources)
            source.init(context);
        super.init(context);
    }

    @Override
    public synchronized void reset() {
        for (NullableGenerator<? extends S> source : sources)
            source.reset();
        this.availableSources = CollectionUtil.toList(sources);
    	super.reset();
    }

    @Override
    public synchronized void close() {
        for (NullableGenerator<? extends S> source : sources)
            source.close();
        this.availableSources.clear();
    	super.close();
    }
    
    public boolean isThreadSafe() {
    	for (NullableGenerator<? extends S> source : sources)
    		if (!source.isThreadSafe())
    			return false;
        return true;
    }
    
    public boolean isParallelizable() {
    	for (NullableGenerator<? extends S> source : sources)
    		if (!source.isParallelizable())
    			return false;
        return true;
    }
    
    // helpers ---------------------------------------------------------------------------------------------------------
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected synchronized ProductWrapper<S> generateFromRandomSource(ProductWrapper<S> wrapper) {
    	assertInitialized();
    	if (availableSources.size() == 0)
    		return null;
		ProductWrapper tmp = wrapper;
    	do {
        	int sourceIndex = RandomUtil.randomIndex(availableSources);
    		tmp = availableSources.get(sourceIndex).generate(tmp);
    		if (tmp == null)
    			availableSources.remove(sourceIndex);
    	} while (tmp == null && availableSources.size() > 0);
    	return tmp;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected synchronized S[] generateFromAllSources(ProductWrapper<S> wrapper, Class<S> componentType) {
    	assertInitialized();
    	if (availableSources.size() < sources.length)
    		return null;
    	S[] result = (S[]) Array.newInstance(componentType, sources.length);
		ProductWrapper tmp = wrapper;
    	for (int i = 0; i < sources.length; i++) {
    		tmp = sources[i].generate(tmp);
    		if (tmp == null) {
    			availableSources.remove(i);
    			return null;
    		}
    		result[i] = (S) tmp.product;
    	}
    	return result;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
    public synchronized String toString() {
        return getClass().getSimpleName() + "[" + ArrayFormat.format(sources) + "]";
    }

}
