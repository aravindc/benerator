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

package org.databene.benerator.sample;

import org.databene.benerator.util.LightweightGenerator;

import java.util.Collection;

/**
 * Generates values from an unweighted list of samples.<br/>
 * <br/>
 * Created: 07.06.2006 19:04:08
 */
public abstract class AbstractSampleGenerator<E> extends LightweightGenerator<E> {

    public AbstractSampleGenerator(Class<E> generatedType) {
		super(generatedType);
	}

    /** Adds values to the sample list */
    public void setValues(Collection<E> values) {
        clear();
        if (values != null)
            for (E value : values)
                addValue(value);
    }

    /** Sets the sample list to the specified values */
    public void setValues(E ... values) {
        clear();
        if (values != null)
            for (E value : values)
                addValue(value);
    }

	/** Adds values to the sample list */
    public void addValues(E ... values) {
        if (values != null)
            for (E value : values)
                addValue(value);
    }

    /** Adds values to the sample list */
    public void addValues(Collection<E> values) {
        if (values != null)
            for (E value : values)
                addValue(value);
    }

    /** Adds a value to the sample list */
    public abstract void addValue(E value);

    /** Removes all values from the sample list */
    public abstract void clear();
}
