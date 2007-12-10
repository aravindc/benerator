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

import org.databene.commons.LocaleUtil;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.region.Region;
import org.databene.benerator.Distribution;
import org.databene.benerator.Sequence;
import org.databene.model.converter.ParseFormatConverter;
import org.databene.model.Converter;

import java.util.Locale;
import java.text.Format;

/**
 * Created: 30.06.2007 07:29:43
 */
public class AttributeDescriptor extends ComponentDescriptor {

    public AttributeDescriptor(String name) {
        this(name, null);
    }
    
    public AttributeDescriptor(ComponentDescriptor parent) {
        this(parent.getName(), parent);
    }
    
    protected AttributeDescriptor(String name, ComponentDescriptor parent) {
        super(name, parent);

        // general constraints
        addDetailConfig("nullable", Boolean.class, true, true);

        // association constraints
        //addDetailConfig("minCount", Integer.class, true, 1, new MaxOperation<Integer>()); // TODO v0.4 implement association handling
        //addDetailConfig("maxCount", Integer.class, true, 1, new MinOperation<Integer>());

        // string constraints
        addDetailConfig("minLength", Integer.class, true, 1/*, new MaxOperation<Integer>()*/);
        addDetailConfig("maxLength", Integer.class, true, null/*, new MinOperation<Integer>()*/);

        // number constraints
        addDetailConfig("min", String.class, true, "1");
        addDetailConfig("max", String.class, true, "9");
        addDetailConfig("precision", String.class, false, "1");

        // setups
        addDetailConfig("converter", Converter.class, false, null);
        addDetailConfig("nullQuota", Double.class, false, null);
        addDetailConfig("trueQuota", Double.class, false, 0.5);
        //addDetailConfig("lengthDistribution", Distribution.class);
        addDetailConfig("variation1", String.class, false, "1");
        addDetailConfig("variation2", String.class, false, "1");
        addDetailConfig("distribution", Distribution.class, false, null);
        //addDetailConfig("countDistribution", Distribution.class, null); // for v0.4

        // i18n setups
        addDetailConfig("region", Region.class, false, null);
        addDetailConfig("locale", Locale.class, false, null);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public Boolean isPrivacy() {
        return (Boolean)getDetailValue("private");
    }

    public void setPrivacy(Boolean privacy) {
        setDetail("private", privacy);
    }

    public Integer getMinLength() {
        return (Integer)getDetailValue("minLength");
    }

    public void setMinLength(Integer minLength) {
        setDetail("minLength", minLength);
    }

    public Integer getMaxLength() {
        return (Integer)getDetailValue("maxLength");
    }

    public void setMaxLength(Integer maxLength) {
        setDetail("maxLength", maxLength);
    }

    public Distribution getLengthDistribution() {
        return mapDistribution((String)getDetailValue("lengthDistribution"));
    }

    public String getMin() {
        return (String)getDetailValue("min");
    }

    public void setMin(String min) {
        setDetail("min", min);
    }

    public String getMax() {
        return (String)getDetailValue("max");
    }

    public void setMax(String max) {
        setDetail("max", max);
    }

    public String getPrecision() {
        return (String)getDetailValue("precision");
    }

    public void setPrecision(String precision) {
        setDetail("precision", precision);
    }

    public String getVariation1() {
        return (String)getDetailValue("variation1");
    }

    public void setVariation1(String variation1) {
        setDetail("variation1", variation1);
    }

    public String getVariation2() {
        return (String)getDetailValue("variation2");
    }

    public void setVariation2(String variation2) {
        setDetail("variation2", variation2);
    }

    public Distribution getDistribution() {
        return (Distribution) getDetailValue("distribution");
    }

    public void setDistribution(Distribution distribution) {
        setDetail("distribution", distribution);
    }

    public Region getRegion() {
        return (Region)getDetailValue("region");
    }

    public void setRegionId(String regionId) {
        setDetail("region", (regionId != null ? Region.getInstance(regionId) : null));
    }

    public Locale getLocale() {
        return (Locale)getDetailValue("locale");
    }

    public void setLocaleId(String localeId) {
        setDetail("locale", LocaleUtil.getLocale(localeId));
    }

    public Boolean isNullable() {
        return (Boolean)getDetailValue("nullable");
    }

    public void setNullable(Boolean nullable) {
        setDetail("nullable", nullable);
    }

    public Integer getMinCardinality() {
        return (Integer)getDetailValue("minCardinality");
    }

    public void setMinCardinality(Integer minCardinality) {
        setDetail("minCardinality", minCardinality);
    }

    public Integer getMaxCardinality() {
        return (Integer)getDetailValue("maxCardinality");
    }

    public void setMaxCardinality(Integer maxCardinality) {
        setDetail("maxCardinality", maxCardinality);
    }

    public Distribution getCardinalityDistribution() {
        return mapDistribution((String)getDetailValue("cardinalityDistribution"));
    }

    public void setCardinalityDistribution(String distribution) {
        setDetail("cardinalityDistribution", distribution);
    }

    public Double getNullQuota() {
        return (Double)getDetailValue("nullQuota");
    }

    public void setNullQuota(Double nullQuota) {
        setDetail("nullQuota", nullQuota);
    }

    public Double getTrueQuota() {
        return (Double) getDetailValue("trueQuota");
    }

    public void setTrueQuota(Double trueQuota) {
        setDetail("trueQuota", trueQuota);
    }

    public String getSource() {
        return (String) getDetailValue("source");
    }

    public void setUri(String source) {
        setDetail("source", source);
    }

    public Converter<? extends Object, ? extends Object> getConverter() {
        return (Converter<? extends Object, ? extends Object>) getDetailValue("converter");
    }

    public void setConverter(Converter<? extends Object, ? extends Object> converter) {
        setDetail("converter", converter);
    }

    // literate build helpers ------------------------------------------------------------------------------------------

    public AttributeDescriptor withPrivacy(Boolean privacy) {
        setPrivacy(privacy);
        return this;
    }

    public AttributeDescriptor withMinLength(Integer minLength) {
        setMinLength(minLength);
        return this;
    }

    public AttributeDescriptor withMaxLength(Integer maxLength) {
        setMaxLength(maxLength);
        return this;
    }

    public AttributeDescriptor withMin(String min) {
        setMin(min);
        return this;
    }

    public AttributeDescriptor withMax(String max) {
        setMax(max);
        return this;
    }

    public AttributeDescriptor withPrecision(String precision) {
        setPrecision(precision);
        return this;
    }

    public AttributeDescriptor withVariation1(String variation1) {
        setVariation1(variation1);
        return this;
    }

    public AttributeDescriptor withVariation2(String variation2) {
        setVariation2(variation2);
        return this;
    }

    public AttributeDescriptor withDistribution(Distribution distribution) {
        setDistribution(distribution);
        return this;
    }

    public AttributeDescriptor withRegionId(String regionId) {
        setRegionId(regionId);
        return this;
    }

    public AttributeDescriptor withLocaleId(String localeId) {
        setLocaleId(localeId);
        return this;
    }

    public AttributeDescriptor withNullable(Boolean nullable) {
        setNullable(nullable);
        return this;
    }

    public AttributeDescriptor withMinCardinality(Integer minCardinality) {
        setMinCardinality(minCardinality);
        return this;
    }

    public AttributeDescriptor withMaxCardinality(Integer maxCardinality) {
        setMaxCardinality(maxCardinality);
        return this;
    }

    public AttributeDescriptor withCardinalityDistribution(String distribution) {
        setCardinalityDistribution(distribution);
        return this;
    }

    public AttributeDescriptor withNullQuota(Double nullQuota) {
        setNullQuota(nullQuota);
        return this;
    }

    public AttributeDescriptor withTrueQuota(Double trueQuota) {
        setTrueQuota(trueQuota);
        return this;
    }

    public AttributeDescriptor withUri(String source) {
        setSource(source);
        return this;
    }

    public AttributeDescriptor withConverter(Converter converter) {
        setConverter(converter);
        return this;
    }


    // generic property access -----------------------------------------------------------------------------------------

    public void setDetail(String detailName, Object detailValue) {
        Class<? extends Object> targetType = getDetailType(detailName);
        if (targetType == Distribution.class && detailValue.getClass() == String.class)
            detailValue = mapDistribution((String) detailValue);
        if (targetType == Converter.class && detailValue.getClass() == String.class)
            detailValue = mapConverter((String) detailValue);
        super.setDetail(detailName, detailValue);
    }

// private helpers -------------------------------------------------------------------------------------------------

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

}
