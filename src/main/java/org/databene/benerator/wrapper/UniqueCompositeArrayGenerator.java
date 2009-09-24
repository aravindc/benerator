/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.commons.ArrayUtil;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.ArrayFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;

/**
 * Creates arrays of unique combinations of the output of other generators.
 * Each array element is filled from an own generator,
 * each used generator is supposed to generate unique values itself.<br/>
 * <br/>
 * Created: 17.11.2007 13:37:37
 * @author Volker Bergmann
 */
public class UniqueCompositeArrayGenerator<S> extends MultiGeneratorWrapper<S, S[]> {

    private static final Logger logger = LoggerFactory.getLogger(UniqueCompositeArrayGenerator.class);

    private Class<S> componentType;
    private Object[] products;
    private boolean dirty;

    // constructors ----------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public UniqueCompositeArrayGenerator() {
        super();
        dirty = true;
    }

    /**
     * Initializes the generator to an array of source generators
     */
    public UniqueCompositeArrayGenerator(Class<S> componentType, Generator<S> ... sources) {
        super(sources);
        this.componentType = componentType;
        dirty = true;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<S[]> getGeneratedType() {
        return (Class<S[]>) Array.newInstance(componentType, 0).getClass();
    }

    @Override
    public void validate() {
        if (dirty) {
            super.validate();
            if (sources.length == 0)
                throw new InvalidGeneratorSetupException("source", "is null");
            products = new Object[sources.length];
            for (int i = 0; i < products.length; i++)
                if (sources[i].available())
                    products[i] = sources[i].generate();
                else
                    throw new InvalidGeneratorSetupException("Sub generator not available: " + sources[i]);
            dirty = false;
        }
    }

    @Override
    public boolean available() {
        if (dirty)
            validate();
        return (products != null);
    }

    /**
     * @see org.databene.benerator.Generator#generate()
     */
    @SuppressWarnings("cast")
    public S[] generate() {
        if (!available())
            throw new IllegalGeneratorStateException("Generator is not available");
        S[] result = (S[]) ArrayUtil.copyOfRange(products, 0, products.length, componentType);
        fetchNext(0);
        if (logger.isDebugEnabled())
            logger.debug("generated: " + ArrayFormat.format(result));
        return result;
    }

    @Override
    public void reset() {
        super.reset();
        dirty = true;
    }

    private void fetchNext(int index) {
        // check for overrun
        if (index >= sources.length) {
            products = null;
            return;
        }
        // if available, fetch the digit's next value
        boolean rep = false;
        Generator<S> gen = sources[index];
        if (gen.available()) {
            Object tmp = gen.generate();
            if (!NullSafeComparator.equals(products[index], tmp)) {
                products[index] = tmp;
                return;
            } else
                rep = true;
        }
        // sources[index] was not available or returned the same value as before
        fetchNext(index + 1);
        if (products != null && !rep) {
            gen.reset();
            products[index] = gen.generate();
        }
    }
}
