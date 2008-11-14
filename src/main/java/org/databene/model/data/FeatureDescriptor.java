package org.databene.model.data;

import org.databene.commons.Converter;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.Operation;
import org.databene.commons.StringUtil;
import org.databene.commons.collection.OrderedNameMap;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ConverterManager;
import org.databene.commons.converter.String2ConverterConverter;
import org.databene.commons.converter.ToStringConverter;
import org.databene.model.function.String2DistributionConverter;

import java.util.List;

/**
 * Common parent class of all descriptors.<br/><br/>
 * Created: 17.07.2006 21:30:45
 * @author Volker Bergmann
 */
public class FeatureDescriptor {

    public static final String NAME = "name";

    static {
        ConverterManager converterManager = ConverterManager.getInstance();
        converterManager.register(new String2DistributionConverter());
        converterManager.register(new String2ConverterConverter());
    }

    protected OrderedNameMap<FeatureDetail<? extends Object>> details;

    // constructor -----------------------------------------------------------------------------------------------------

    public FeatureDescriptor(String name) {
        this.details = new OrderedNameMap<FeatureDetail<? extends Object>>();
        addRestriction(NAME, String.class, null, null);
        setName(name);
    }
    
    // typed interface -------------------------------------------------------------------------------------------------
/*
    public void setParent(FeatureDescriptor parent) {
        this.parent = parent;
    }
*/
    public String getName() {
        return (String) getDetailValue(NAME);
    }

    public void setName(String name) {
        setDetailValue(NAME, name);
    }

    // generic detail access -------------------------------------------------------------------------------------------

    /**
     * @see org.databene.model.data.FeatureDescriptor#supportsDetail(java.lang.String)
     */
    public boolean supportsDetail(String name) {
        return (details.get(name) != null);

    }

    public Object getDeclaredDetailValue(String name) {
        return getDetail(name).getValue();
    }

    public Object getDetailValue(String name) {
        FeatureDetail detail = getDetail(name);
        Object value = detail.getValue();
        return value;
    }

    public <T> void setDetailValue(String detailName, T detailValue) {
        FeatureDetail<T> detail = getDetail(detailName);
        if (detail == null)
            throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support detail type: " + detailName);
        detail.setValue(AnyConverter.convert(detailValue, detail.getType()));
    }

    public <T> void setDetailValueAsString(String detailName, String detailValue) {
        FeatureDetail<T> detail = getDetail(detailName);
        if (detail == null)
            throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support detail type: " + detailName);
        detail.setValueAsString(detailValue);
    }

    public <T> T getDetailDefault(String name) {
        FeatureDetail<T> detail = getDetail(name);
        return detail.getDefault();
    }

    public List<FeatureDetail<? extends Object>> getDetails() {
        return details.values();
    }

    // java.lang.io overrides ------------------------------------------------------------------------------------------

    public String toString() {
        String name = getName();
        if (StringUtil.isEmpty(name))
            name = "anonymous";
        StringBuilder buffer = new StringBuilder(name).append("[");
        boolean empty = true;
        for (FeatureDetail<? extends Object> descriptor : details.values())
            if (descriptor.getValue() != null && !NAME.equals(descriptor.getName())) {
                if (!empty)
                    buffer.append(", ");
                empty = false;
                buffer.append(descriptor.getName()).append("=");
                buffer.append(ToStringConverter.convert(descriptor.getValue(), "[null]"));
            }
        return buffer.append("]").toString();
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final FeatureDescriptor that = (FeatureDescriptor) o;
        for (FeatureDetail<? extends Object> detail : details.values()) {
            String detailName = detail.getName();
            if (!NullSafeComparator.equals(detail.getValue(), that.getDetailValue(detailName)))
                return false;
        }
        return true;
    }

    public int hashCode() {
        return (getClass().hashCode() * 29 /*+ (parent != null ? parent.hashCode() : 0)*/) * 29 + details.hashCode();
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    protected Class<? extends Object> getDetailType(String detailName) {
        FeatureDetail<? extends Object> detail = details.get(detailName);
        if (detail == null)
            throw new UnsupportedOperationException("Feature detail not supported: " + detailName);
        return detail.getType();
    }

    protected <T> void addConfig(String name, Class<T> type, T defaultValue) {
        addDetailConfig(name, type, false, defaultValue);
    }

    protected <T> void addRestriction(String name, Class<T> type, T defaultValue, Operation<T, T> combinator) {
        addDetailConfig(name, type, true, defaultValue, null, combinator);
    }

    protected <T> void addDetailConfig(String detailName, Class<T> detailType, boolean constraint, T defaultValue) {
        addDetailConfig(detailName, detailType, constraint, defaultValue, new AnyConverter<String, T>(detailType));
    }

    protected <T> void addDetailConfig(String detailName, Class<T> detailType, boolean constraint, T defaultValue, Converter<String, T> converter) {
        addDetailConfig(detailName, detailType, constraint, defaultValue, converter, null);
    }

    protected <T> void addDetailConfig(String detailName, Class<T> detailType, boolean constraint, T defaultValue, Operation<T,T> combinator) {
        addDetailConfig(detailName, detailType, constraint, defaultValue, new AnyConverter<String, T>(detailType), combinator);
    }

    protected <T> void addDetailConfig(String detailName, Class<T> detailType, boolean constraint, T defaultValue, Converter<String, T> converter, Operation<T,T> combinator) {
        this.details.put(detailName, new FeatureDetail<T>(detailName, detailType, constraint, defaultValue, converter, combinator));
    }

    // generic property access -----------------------------------------------------------------------------------------

    protected <T> FeatureDetail<T> getDetail(String name) {
        FeatureDetail<T> detail = (FeatureDetail<T>) details.get(name);
        if (detail == null)
            throw new UnsupportedOperationException("Feature detail '" + name + "' not supported in feature type: " 
                    + getClass().getName());
        return detail;
    }
}
