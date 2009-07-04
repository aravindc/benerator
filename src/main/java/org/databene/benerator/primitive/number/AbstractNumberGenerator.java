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

package org.databene.benerator.primitive.number;

import org.databene.benerator.util.LightweightGenerator;
import org.databene.commons.comparator.NumberComparator;
import org.databene.commons.converter.NumberToNumberConverter;

/**
 * Abstract parent class for all number-conversion adapters.
 * It hosts a distribution and defines abstract properties to be implemented by child classes.<br/>
 * <br/>
 * Created: 10.09.2006 19:47:32
 * @author Volker Bergmann
 */
public abstract class AbstractNumberGenerator<E extends Number> extends LightweightGenerator<E> {

	protected Class<E> generatedType;

	protected E min;
    protected E max;
    protected E precision;

    protected boolean dirty;

    // constructors ----------------------------------------------------------------------------------------------------

    public AbstractNumberGenerator(Class<E> generatedType, E min, E max, E precision) {
    	if (min != null && max != null && NumberComparator.compareNumbers(min, max) > 0)
    		throw new IllegalArgumentException("min (" + min + ") is greater than max (" + max + ")");
    	this.generatedType = generatedType;
        setMin(min);
        setMax(max);
        setPrecision(precision);
        this.dirty = true;
    }

    // config properties -----------------------------------------------------------------------------------------------

    public E getMin() {
        return NumberToNumberConverter.convert(min, generatedType);
    }

    public void setMin(E min) {
        this.min = min;
        this.dirty = true;
    }

    public E getMax() {
        return NumberToNumberConverter.convert(max, generatedType);
    }

    public void setMax(E max) {
        this.max = max;
        this.dirty = true;
    }

    public E getPrecision() {
        return NumberToNumberConverter.convert(precision, generatedType);
    }

    public void setPrecision(E precision) {
        this.precision = precision;
        this.dirty = true;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public Class<E> getGeneratedType() {
    	return generatedType;
    }
    
}
