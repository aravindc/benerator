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

package org.databene.benerator.main;

import java.util.Map;

import org.databene.commons.Context;
import org.databene.commons.ConversionException;
import org.databene.commons.Converter;
import org.databene.commons.collection.MapEntry;
import org.databene.commons.mutator.AnyMutator;

/**
 * Converts Map entries by first applying a preprocessor to the value, 
 * then (if possible) converting the result to a number or boolean.<br/><br/>
 * Created: 01.02.2008 14:40:43
 * @author Volker Bergmann
 */
public class DefaultEntryConverter implements Converter<Map.Entry, Map.Entry> {
    // TODO v0.4.1 make it more general and move to commons
    private Context context;
    private Converter<String, String> preprocessor;
    private boolean putEntriesToContext;
    
    public DefaultEntryConverter(Converter<String, String> preprocessor, Context context, boolean putEntriesToContext) {
        this.preprocessor = preprocessor;
        this.context = context;
        this.putEntriesToContext = putEntriesToContext;
    }

    public Class<Map.Entry> getTargetType() {
        return Map.Entry.class;
    }

    public Map.Entry convert(Map.Entry entry) throws ConversionException {
        String key = String.valueOf(entry.getKey());
        String sourceValue = String.valueOf(entry.getValue());
        sourceValue = preprocessor.convert(sourceValue);
        Object result = sourceValue;
        if (isNumber(sourceValue))
            result = Integer.parseInt(sourceValue);
        else if ("false".equals(sourceValue.trim().toLowerCase()))
                result = Boolean.FALSE;
        else if ("true".equals(sourceValue.trim().toLowerCase()))
                result = Boolean.TRUE;
        if (putEntriesToContext)
            AnyMutator.setValue(context, key, result, true);
        return new MapEntry<String, Object>(key, result);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
