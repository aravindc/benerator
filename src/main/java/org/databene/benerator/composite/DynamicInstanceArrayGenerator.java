/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.composite;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.wrapper.GeneratorWrapper;
import org.databene.commons.ArrayUtil;
import org.databene.commons.Assert;

/**
 * Array generator that allows for dynamic change of the array length algorithm.<br/><br/>
 * Created: 21.10.2009 10:35:43
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DynamicInstanceArrayGenerator extends GeneratorWrapper<Object, Object> {
	
	private Generator<Long> countGenerator;

    public DynamicInstanceArrayGenerator(Generator<Object> source, Generator<Long> countGenerator, BeneratorContext context) {
        super(source, context);
        Assert.notNull(source, "source");
        Assert.notNull(countGenerator, "countGenerator");
        this.countGenerator = countGenerator;
    }
    
	public Class<Object> getGeneratedType() {
	    return Object.class;
    }

	@Override
	public synchronized void init(GeneratorContext context) {
	    countGenerator.init(context);
	    super.init(context);
	}
	
    public Object generate() {
    	assertInitialized();
        int count = countGenerator.generate().intValue();
        if (count == 0)
            return new Object[0];
        if (count == 1)
            return source.generate();
        else {
            Object[] result = ArrayUtil.newInstance(source.getGeneratedType(), count);
            for (int i = 0; i < count; i++) {
                result[i] = source.generate();
                if (result[i] == null) {
                    // if source generator went unavailable report unavailability 
                    return null;
                }
            }
            return result;
        }
    }

    @Override
    public void reset() {
        super.reset();
        countGenerator.reset();
    }
    
    @Override
    public void close() {
    	countGenerator.close();
    	super.close();
    }
    
    @Override
    public boolean isThreadSafe() {
        return countGenerator.isThreadSafe() && super.isThreadSafe();
    }

    @Override
    public boolean isParallelizable() {
        return countGenerator.isParallelizable() && super.isParallelizable();
    }
    
	@Override
	public String toString() {
	    return getClass().getSimpleName() + "[" + source + ", " + countGenerator + "]";
	}

}
