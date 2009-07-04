/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.distribution.Distribution;
import org.databene.commons.ArrayUtil;

/**
 * Common abstraction for all generators that create arrays.<br/>
 * <br/>
 * Created at 13.07.2008 15:51:59
 * @since 0.5.4
 * @author Volker Bergmann
 */
public abstract class AbstractArrayGenerator<E, A> extends GeneratorWrapper<E, A>{

    /** The generator that creates the array length */
    protected Generator<Integer> sizeGenerator;

    private Class<E> componentType;
    private Class<A> generatedType;

    public AbstractArrayGenerator(Generator<E> source, Class<E> componentType, Class<A> generatedType, 
    		int minLength, int maxLength, Distribution lengthDistribution) {
        super(source);
        this.componentType = componentType;
        this.generatedType = generatedType;
        this.sizeGenerator = lengthDistribution.createGenerator(Integer.class, minLength, maxLength, 1);
    }

    // configuration properties ----------------------------------------------------------------------------------------

    public Class<A> getGeneratedType() {
        return generatedType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validate() {
        if (dirty) {
            super.validate();
            sizeGenerator.validate();
            if (source == null)
                throw new InvalidGeneratorSetupException("source", " is null");
            if (generatedType == null) {
	            Class<E> cType = (componentType != null ? componentType : source.getGeneratedType());
	            this.generatedType = (Class<A>) ArrayUtil.arrayType(cType);
            }
            dirty = false;
        }
    }
    
    @Override
    public boolean available() {
        validate();
        return (super.available() && sizeGenerator.available());
    }

    @SuppressWarnings("unchecked")
    public A generate() {
        int length = sizeGenerator.generate().intValue();
        E[] array = ArrayUtil.newInstance(componentType, length);
        for (int i = 0; i < length; i++)
            array[i] = source.generate();
        return (A) array;
    }

}
