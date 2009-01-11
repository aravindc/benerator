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

import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ConversionException;
import org.databene.commons.StringUtil;
import org.databene.commons.converter.BidirectionalConverter;

/**
 * Converts Strings to Distributions and vice versa.<br/><br/>
 * Created: 13.03.2008 22:33:19
 * @author Volker Bergmann
 * TODO v0.5.8 merge this with new object construction concept
 */
public class String2DistributionConverter implements BidirectionalConverter<String, Distribution> {

    public Class<String> getSourceType() {
        return String.class;
    }

    public Class<Distribution> getTargetType() {
        return Distribution.class;
    }

    public Distribution convert(String sourceValue) throws ConversionException {
        return parse(sourceValue);
    }

    public String revert(Distribution target) throws ConversionException {
        if (target instanceof Sequence)
            return ((Sequence) target).getName();
        else if (target instanceof WeightFunction)
            return target.getClass().getName();
        else
            throw new UnsupportedOperationException("Not a supported distribution type: " + target);
    }

    public static Distribution parse(String sourceValue) throws ConversionException {
    	if (StringUtil.isEmpty(sourceValue))
    		return null;
    	if (sourceValue.startsWith("weighted[") && sourceValue.endsWith("]"))
    		return new FeatureWeight(sourceValue.substring("weighted[".length(), sourceValue.length() - 1));
    	else if ("weighted".equals(sourceValue))
    		return new FeatureWeight(null);
        Distribution result = Sequence.getInstance(sourceValue, false);
        if (result == null)
            result = (Distribution) BeanUtil.newInstance(sourceValue);
        if (result == null)
        	throw new ConfigurationError("Distribution not found: " + sourceValue);
        return result;
    }

}
