/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.InvalidGeneratorSetupException;

/**
 * Wraps several other generators (in a <i>sources</i> property) and refines a composite state from them.<br/>
 * <br/>
 * Created: 19.12.2006 07:05:29
 * @since 0.1
 * @author Volker Bergmann
 */
public abstract class MultiGeneratorWrapper<S, P> implements Generator<P> {

    protected Generator<S>[] sources;
    protected boolean dirty;
    
    public MultiGeneratorWrapper(Generator<S> ... sources) {
        setSources(sources);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public Generator<S>[] getSources() {
        validate();
        return sources;
    }
    
    public void setSources(Generator<S> ... sources) {
        dirty = true;
        this.sources = sources;
    }

    public Generator<S> getSource(int index) {
        validate();
        return sources[index];
    }
    
    @SuppressWarnings("unchecked")
    public void addSource(Generator<S> source) {
        dirty = true;
    	Generator<S>[] newSources = new Generator[sources.length + 1];
    	System.arraycopy(sources, 0, newSources, 0, sources.length);
    	newSources[sources.length] = source;
    	sources = newSources;
    }

    // Generator interface implementation ------------------------------------------------------------------------------

    public void validate() {
        if (dirty) {
            if (sources.length == 0)
                throw new InvalidGeneratorSetupException("sources", "is empty");
            for (Generator<S> source : sources)
                source.validate();
            dirty = false;
        }
    }

    public boolean available() {
        validate();
        for (Generator<S> source : sources)
            if (!source.available())
                return false;
        return true;
    }

    public void reset() {
        validate();
        dirty = true;
        for (Generator<S> source : sources)
            source.reset();
    }

    public void close() {
        validate();
        for (Generator<S> source : sources)
            source.close();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + sources;
    }

}
