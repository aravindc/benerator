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
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.commons.ArrayUtil;
import org.databene.commons.ConversionException;
import org.databene.commons.Converter;

/**
 * Reads products from a source Generator and applies a Converter to transform them into the target products.<br/>
 * <br/>
 * Created: 12.06.2006 19:02:30
 * @author Volker Bergmann
 */
public class ConvertingGenerator<S, T> extends GeneratorWrapper<S, T> {

    /** The converter to apply to the source's products */
    protected Converter<?, ?>[] converters;

    /** Initializes all attributes */
    public ConvertingGenerator(Generator<S> source, Converter<?, ?>... converters) {
        super(source);
        this.converters = converters;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
    public void init(GeneratorContext context) {
        if (source == null)
            throw new InvalidGeneratorSetupException("source", "is null");
        if (ArrayUtil.isEmpty(converters))
            throw new InvalidGeneratorSetupException("converters", "is empty");
        super.init(context);
    }

    @SuppressWarnings("unchecked")
    public Class<T> getGeneratedType() {
    	if (converters.length > 0)
    		return (Class<T>) converters[converters.length - 1].getTargetType();
    	else
    		return (Class<T>) source.getGeneratedType();
    }

    /** @see org.databene.benerator.Generator#generate() */
    @SuppressWarnings("unchecked")
    public T generate() {
        try {
            Object tmp = source.generate();
            for (Converter converter : converters)
            	tmp = converter.convert(tmp);
            return (T) tmp;
        } catch (ConversionException e) {
            throw new IllegalGeneratorStateException(e);
        }
    }
    
}
