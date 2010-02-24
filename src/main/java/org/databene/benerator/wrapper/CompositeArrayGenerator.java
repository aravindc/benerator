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

import org.databene.benerator.Generator;

import java.lang.reflect.Array;

/**
 * Keeps an array of generators, of which it combines the products to an array.<br/>
 * <br/>
 * Created: 26.08.2006 09:37:55
 * @since 0.1
 * @author Volker Bergmann
 */
public class CompositeArrayGenerator<S> extends MultiGeneratorWrapper<S, S[]> {

    private Class<S> componentType;

    // constructors ----------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public CompositeArrayGenerator() {
        super();
    }

    /** Initializes the generator to an array of source generators */
    public CompositeArrayGenerator(Class<S> componentType, Generator<S> ... sources) {
        super(sources);
        this.componentType = componentType;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<S[]> getGeneratedType() {
        return (Class<S[]>) Array.newInstance(componentType, 0).getClass();
    }

    /** @see org.databene.benerator.Generator#generate() */
    @SuppressWarnings("unchecked")
    public S[] generate() {
        S[] array = (S[]) Array.newInstance(componentType, sources.length);
        for (int i = 0; i < array.length; i++) {
            try {
                array[i] = sources[i].generate();
            } catch (Exception e) {
                throw new RuntimeException("Generation failed for generator #" + i + ": " + sources[i], e);
            }
        }
        return array;
    }

}
