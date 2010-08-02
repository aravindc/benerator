/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.*;

/**
 * Forwards the output of other generators in random order, but at most once.<br/>
 * <br/>
 * Created: 17.11.2007 17:12:07
 * @author Volker Bergmann
 */
public class UniqueAlternativeGenerator<E> extends MultiGeneratorWrapper<E, E> { // TODO v0.6.4 is this obsolete compared to AlternativeGenerator?

    private Class<E> targetType;

    // constructors ----------------------------------------------------------------------------------------------------

    public UniqueAlternativeGenerator() {
        this(null);
    }

    /** Initializes the generator to a collection of source generators */
    public UniqueAlternativeGenerator(Class<E> targetType, Generator<E>... sources) {
        super(sources);
        this.targetType = targetType;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    public Class<E> getGeneratedType() {
        return targetType;
    }

    /** @see org.databene.benerator.Generator#generate() */
    public E generate() {
        return generateFromRandomSource();
    }

}
