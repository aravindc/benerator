/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.script.WeightedTransition;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConversionException;
import org.databene.commons.Converter;
import org.databene.commons.converter.AnyConverter;

/**
 * Converter implementation that maps input values in a 'Map' style.<br/><br/>
 * Created: 24.10.2009 09:05:58
 * @since 0.6.0
 * @author Volker Bergmann
 */
@SuppressWarnings("unchecked")
public class ValueMapper implements Converter {

	private Map mappings;
	private boolean lenient;
	private Class<?> targetType;
	private Class<?> sourceType;
	
	public ValueMapper() {
		try {
	        init(null, false);
        } catch (ParseException e) {
	        // this cannot happen
        }
	}

	public ValueMapper(String mappingSpec) throws ParseException {
		this(mappingSpec, false);
	}

	public ValueMapper(String mappingSpec, boolean lenient) throws ParseException {
		init(mappingSpec, lenient);
	}

	private void init(String mappingSpec, boolean lenient) throws ParseException {
	    this.mappings = new HashMap<Object, Object>();
		setMappings(mappingSpec);
		this.lenient = lenient;
    }

	public void setMappings(String mappingSpec) throws ParseException {
		if (mappingSpec != null) {
			WeightedTransition[] tl = BeneratorScriptParser.parseTransitionList(mappingSpec);
			for (WeightedTransition t : tl)
				mappings.put(t.getFrom(), t.getTo());
			sourceType = BeanUtil.commonSubType(mappings.keySet());
			targetType = BeanUtil.commonSuperType(mappings.values());
		} else
			mappings.clear();
	}
	
	public Class<?> getTargetType() {
	    return targetType;
    }
	
	public boolean canConvert(Object sourceValue) {
	    return (lenient || mappings.containsKey(AnyConverter.convert(sourceValue, sourceType)));
    }

	public Object convert(Object sourceValue) throws ConversionException {
		sourceValue = AnyConverter.convert(sourceValue, sourceType);
		if (!mappings.containsKey(sourceValue))
			if (lenient)
				return sourceValue;
			else
				throw new IllegalArgumentException("Cannot convert value: " + sourceValue);
		else 
			return mappings.get(sourceValue);
    }

}
