/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package org.databene.model.data;

import org.databene.commons.Converter;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.Operation;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.operation.FirstArgSelector;

/**
 * A FeatureDescriptor is composed og FeatureDetails, which have name, value, type and default value.<br/>
 * <br/>
 * Created: 03.08.2007 06:57:42
 * @author Volker Bergmann
 */
public class FeatureDetail<E> {
    
    // properties ------------------------------------------------------------------------------------------------------

    private String name;
    private Class<E> type;
    private E value;
    private E defaultValue;
    private Converter<String, E> converter;
    private Operation<E, E> combinator;
    private boolean restriction;
    
    // constructors ----------------------------------------------------------------------------------------------------

    public FeatureDetail(String name, Class<E> type, boolean restriction, E defaultValue) {
        this(name, type, restriction, defaultValue, new AnyConverter<String, E>(type));
    }

    public FeatureDetail(String name, Class<E> type, boolean restriction, E defaultValue, Converter<String, E> converter) {
        this(name, type, restriction, defaultValue, converter, new FirstArgSelector<E>());
    }

    public FeatureDetail(String name, Class<E> type, boolean restriction, E defaultValue, Converter<String, E> converter, Operation<E, E> combinator) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.value = null;
        this.restriction = restriction;
        this.combinator = combinator;
        this.converter = converter;
    }
    
    // interface -------------------------------------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public Class<E> getType() {
        return type;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        if (value != null && !(type.isAssignableFrom(value.getClass())))
            throw new IllegalArgumentException("Tried to assign a value of type '" + value.getClass().getName() 
                    + "'to detail '" + name + "' of type '" + type + "'");
        this.value = value;
    }

    public void setValueAsString(String value) {
        this.value = converter.convert(value);
    }

    public E getDefault() {
        return defaultValue;
    }

    public E combineWith(E otherValue) {
        return combinator.perform(this.value, otherValue);
    }

    public boolean isRestriction() {
        return restriction;
    }

    public String getDescription() {
        return name + '=' + value + " (" + type + ')';
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return name + '=' + value;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final FeatureDetail<E> that = (FeatureDetail<E>) o;
        return (name.equals(that.name) 
        	&& NullSafeComparator.equals(this.value, that.value));
    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 29 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

}
