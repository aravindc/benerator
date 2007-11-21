/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator;

import org.databene.commons.ArrayFormat;

/**
 * Wraps several other generators (in a <i>sources</i> property) and refines a composite state from them.<br/>
 * <br/>
 * Created: 19.12.2006 07:05:29
 */
public abstract class MultiGeneratorWrapper<S, P> implements Generator<P> {

    protected Generator<S>[] sources;

    public MultiGeneratorWrapper(Generator<S> ... sources) {
        setSources(sources);
    }

    public void setSources(Generator<S> ... sources) {
        this.sources = sources;
    }

    public Generator<S> getSource(int index) {
        return sources[index];
    }

    public Generator<S>[] getSources() {
        return sources;
    }

    public void validate() {
        if (sources.length == 0)
            throw new InvalidGeneratorSetupException("sources", "is empty");
        for (Generator<S> source : sources)
            source.validate();
    }

    public boolean available() {
        for (Generator<S> source : sources)
            if (!source.available())
                return false;
        return true;
    }

    public void reset() {
        for (Generator<S> source : sources)
            source.reset();
    }

    public void close() {
        for (Generator<S> source : sources)
            source.close();
    }

    public String toString() {
        return getClass().getSimpleName() + '[' + ArrayFormat.format(sources) + ']';
    }

}
