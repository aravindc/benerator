/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.operation.FirstArgSelector;
import org.databene.commons.operation.MaxNumberLiteral;
import org.databene.commons.operation.MaxOperation;
import org.databene.commons.operation.MinNumberLiteral;
import org.databene.commons.operation.MinOperation;
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;

/**
 * Describes a simple type.<br/><br/>
 * Created: 03.03.2008 08:58:58
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class SimpleTypeDescriptor extends TypeDescriptor {

    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String MIN_EXCLUSIVE = "minExclusive";
    public static final String MAX_EXCLUSIVE = "maxExclusive";

    public static final String TOTAL_DIGITS = "totalDigits";
    public static final String FRACTION_DIGITS = "fractionDigits";
    public static final String PRECISION = "precision";

    public static final String TRUE_QUOTA = "trueQuota";
    public static final String MIN_LENGTH = "minLength";
    public static final String MAX_LENGTH = "maxLength";

    public static final String LENGTH_DISTRIBUTION = "lengthDistribution";
    
    public SimpleTypeDescriptor(String name, String parent) {
        super(name, parent);
        // number setup
        addRestriction(MIN,             String.class,   "1", new MaxNumberLiteral());
        addRestriction(MAX,             String.class,   "9", new MinNumberLiteral());
        addRestriction(MIN_EXCLUSIVE,   String.class,   "0", new MaxNumberLiteral());
        addRestriction(MAX_EXCLUSIVE,   String.class,  "10", new MinNumberLiteral());
        addRestriction(TOTAL_DIGITS,    String.class,  "10", new FirstArgSelector<String>());
        addRestriction(FRACTION_DIGITS, String.class,   "0", new FirstArgSelector<String>());
        addConfig(PRECISION,            String.class,   "1");
        // boolean setup
        addConfig(TRUE_QUOTA,           Double.class,   0.5);
        // string setup
        addRestriction(MIN_LENGTH,      Integer.class,     1, new MaxOperation<Integer>());
        addRestriction(MAX_LENGTH,      Integer.class,    30, new MinOperation<Integer>());
        // other config
        addConfig(LENGTH_DISTRIBUTION,  Distribution.class, Sequence.RANDOM);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public <T> PrimitiveType<T> getPrimitiveType() {
        SimpleTypeDescriptor parent = this;
        while (parent.getParent() != null) {
            PrimitiveType<T> primitiveType = PrimitiveType.getInstance(parent.getParentName());
            if (primitiveType != null)
                return primitiveType;
            parent = (SimpleTypeDescriptor) parent.getParent();
        }
        return null;
    }
    
    public String getMin() {
        return (String) getDetailValue(MIN);
    }

    public void setMin(String min) {
        setDetailValue(MIN, min);
    }

    public String getMax() {
        return (String) getDetailValue(MAX);
    }

    public void setMax(String max) {
        setDetailValue(MAX, max);
    }

    public String getMinExclusive() {
        return (String) getDetailValue(MIN_EXCLUSIVE);
    }

    public void setMinExclusive(String minExclusive) {
        setDetailValue(MIN, minExclusive);
    }

    public String getMaxExclusive() {
        return (String) getDetailValue(MAX_EXCLUSIVE);
    }

    public void setMaxExclusive(String maxExclusive) {
        setDetailValue(MAX_EXCLUSIVE, maxExclusive);
    }

    public String getTotalDigits() {
        return (String) getDetailValue(TOTAL_DIGITS);
    }

    public void setTotalDigits(String totalDigits) {
        setDetailValue(TOTAL_DIGITS, totalDigits);
    }

    public String getFractionDigits() {
        return (String) getDetailValue(FRACTION_DIGITS);
    }

    public void setFractionDigits(String fractionDigits) {
        setDetailValue(FRACTION_DIGITS, fractionDigits);
    }

    public String getPrecision() {
        return (String) getDetailValue(PRECISION);
    }

    public void setPrecision(String precision) {
        setDetailValue(PRECISION, precision);
    }

    public Double getTrueQuota() {
        return (Double) getDetailValue(TRUE_QUOTA);
    }

    public void setTrueQuota(Double trueQuota) {
        setDetailValue(TRUE_QUOTA, trueQuota);
    }
    
    public Integer getMinLength() {
        return (Integer) getDetailValue(MIN_LENGTH);
    }

    public void setMinLength(Integer minLength) {
        setDetailValue(MIN_LENGTH, minLength);
    }

    public Integer getMaxLength() {
        return (Integer) getDetailValue(MAX_LENGTH);
    }

    public void setMaxLength(Integer maxLength) {
        setDetailValue(MAX_LENGTH, maxLength);
    }

    public Distribution getLengthDistribution() {
        return (Distribution) getDetailValue(LENGTH_DISTRIBUTION);
    }
    
    public void setLengthDistribution(Distribution lengthDistribution) {
        setDetailValue(LENGTH_DISTRIBUTION, lengthDistribution);
    }

    // literate build helpers ------------------------------------------------------------------------------------------

    public SimpleTypeDescriptor withMin(String min) {
        setMin(min);
        return this;
    }

    public SimpleTypeDescriptor withMax(String max) {
        setMax(max);
        return this;
    }

    public SimpleTypeDescriptor withPrecision(String precision) {
        setPrecision(precision);
        return this;
    }

    public SimpleTypeDescriptor withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    public SimpleTypeDescriptor withVariation1(String variation1) {
        setVariation1(variation1);
        return this;
    }

    public SimpleTypeDescriptor withVariation2(String variation2) {
        setVariation2(variation2);
        return this;
    }

    public SimpleTypeDescriptor withDistribution(Distribution distribution) {
        setDistribution(distribution);
        return this;
    }

    public SimpleTypeDescriptor withDataSet(String dataSet) {
        setDataSet(dataSet);
        return this;
    }

    public SimpleTypeDescriptor withLocaleId(String localeId) {
        setLocaleId(localeId);
        return this;
    }

    public SimpleTypeDescriptor withTrueQuota(Double trueQuota) {
        setTrueQuota(trueQuota);
        return this;
    }

    public SimpleTypeDescriptor withUri(String source) {
        setSource(source);
        return this;
    }

    // generic property access -----------------------------------------------------------------------------------------

    public void setDetail(String detailName, Object detailValue) {
/*
        Class<? extends Object> targetType = getDetailType(detailName);
        if (targetType == Distribution.class && detailValue.getClass() == String.class)
            detailValue = mapDistribution((String) detailValue);
        else if (targetType == Converter.class && detailValue.getClass() == String.class)
            detailValue = mapConverter((String) detailValue);
        super.setDetailValue(detailName, detailValue);
*/
    }

// private helpers -------------------------------------------------------------------------------------------------
/*
    private Converter<? extends Object, ? extends Object> mapConverter(String converterString) {
        Object result = BeanUtil.newInstance(converterString);
        if (result instanceof Format)
            result = new ParseFormatConverter(Object.class, (Format) result);
        else if (!(result instanceof Converter))
            throw new ConfigurationError("Class is no Converter: " + result.getClass());
        return (Converter<? extends Object, ? extends Object>) result;
    }

    private static Distribution mapDistribution(String distributionName) {
        if (distributionName == null)
            return null;
        try {
            return Sequence.getInstance(distributionName);
        } catch (Exception e) {
            return (Distribution) BeanUtil.newInstance(distributionName);
        }
    }
*/

}
