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

package org.databene.model.function;

import org.databene.benerator.parser.BasicParser;
import org.databene.commons.Context;
import org.databene.commons.ConversionException;
import org.databene.commons.StringUtil;
import org.databene.commons.bean.ClassProvider;
import org.databene.commons.converter.AbstractBidirectionalConverter;

/**
 * Converts Strings to Distributions and vice versa.<br/><br/>
 * Created: 13.03.2008 22:33:19
 * @author Volker Bergmann
 */
public class String2DistributionConverter extends AbstractBidirectionalConverter<String, Distribution> {
	
	private static BasicParser parser;

	public String2DistributionConverter() {
		super(String.class, Distribution.class);
		parser = new BasicParser();
	}

    public Distribution convert(String sourceValue) throws ConversionException {
        return parse(sourceValue, null, null);
    }

    public String revert(Distribution target) throws ConversionException {
        if (target instanceof Sequence)
            return ((Sequence) target).getName();
        else if (target instanceof WeightFunction)
            return target.getClass().getName();
        else
            throw new UnsupportedOperationException("Not a supported distribution type: " + target);
    }

    public static Distribution parse(String sourceValue, ClassProvider classProvider, Context context) throws ConversionException {
    	if (StringUtil.isEmpty(sourceValue))
    		return null;
    	if (sourceValue.startsWith("weighted[") && sourceValue.endsWith("]"))
    		return new FeatureWeight<Object>(sourceValue.substring("weighted[".length(), sourceValue.length() - 1));
    	else if ("weighted".equals(sourceValue))
    		return new FeatureWeight<Object>(null);
        Distribution result = Sequence.getInstance(sourceValue, false);
        if (result == null)
            result = (Distribution) parser.resolveConstructionOrReference(sourceValue, classProvider, context);
        if (result == null)
        	throw new ConversionException("Distribution not found: " + sourceValue);
        return result;
    }

}
