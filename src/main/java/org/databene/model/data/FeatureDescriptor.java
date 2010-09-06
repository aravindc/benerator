package org.databene.model.data;

import org.databene.commons.Named;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.Operation;
import org.databene.commons.StringUtil;
import org.databene.commons.collection.OrderedNameMap;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ConverterManager;
import org.databene.commons.converter.String2ConverterConverter;
import org.databene.commons.converter.ToStringConverter;

import java.util.List;

/**
 * Common parent class of all descriptors.<br/><br/>
 * Created: 17.07.2006 21:30:45
 * @since 0.1
 * @author Volker Bergmann
 */
public class FeatureDescriptor implements Named {

    public static final String NAME = "name";

    static {
        ConverterManager.getInstance().registerConverterClass(String2ConverterConverter.class);
    }

    protected OrderedNameMap<FeatureDetail<?>> details;

    // constructor -----------------------------------------------------------------------------------------------------

    public FeatureDescriptor(String name) {
        this.details = new OrderedNameMap<FeatureDetail<?>>();
        addConstraint(NAME, String.class, null, null);
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

    public boolean supportsDetail(String name) {
        return details.containsKey(name);
    }

    public Object getDeclaredDetailValue(String name) {
        return getConfiguredDetail(name).getValue();
    }

    public Object getDetailValue(String name) {
        FeatureDetail<Object> detail = getConfiguredDetail(name);
        return detail.getValue();
    }

    public void setDetailValue(String detailName, Object detailValue) {
        FeatureDetail<Object> detail = getConfiguredDetail(detailName);
        detail.setValue(AnyConverter.convert(detailValue, detail.getType()));
    }

    public <T> T getDetailDefault(String name) {
        FeatureDetail<T> detail = getConfiguredDetail(name);
        return detail.getDefault();
    }

    public List<FeatureDetail<?>> getDetails() {
        return details.values();
    }

    // java.lang overrides ---------------------------------------------------------------------------------------------

    @Override
	public String toString() {
        String name = getName();
        if (StringUtil.isEmpty(name))
            name = "anonymous";
        StringBuilder buffer = new StringBuilder(name).append("[");
        boolean empty = true;
        for (FeatureDetail<?> descriptor : details.values())
            if (descriptor.getValue() != null && !NAME.equals(descriptor.getName())) {
                if (!empty)
                    buffer.append(", ");
                empty = false;
                buffer.append(descriptor.getName()).append("=");
                buffer.append(ToStringConverter.convert(descriptor.getValue(), "[null]"));
            }
        return buffer.append("]").toString();
    }

    @Override
	public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final FeatureDescriptor that = (FeatureDescriptor) o;
        for (FeatureDetail<?> detail : details.values()) {
            String detailName = detail.getName();
            if (!NullSafeComparator.equals(detail.getValue(), that.getDetailValue(detailName)))
                return false;
        }
        return true;
    }

    @Override
	public int hashCode() {
        return (getClass().hashCode() * 29 /*+ (parent != null ? parent.hashCode() : 0)*/) * 29 + details.hashCode();
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    protected Class<?> getDetailType(String detailName) {
        FeatureDetail<?> detail = details.get(detailName);
        if (detail == null)
            throw new UnsupportedOperationException("Feature detail not supported: " + detailName);
        return detail.getType();
    }

    protected <T> void addConfig(String name, Class<T> type, T defaultValue) {
    	addConfig(name, type, defaultValue, false);
    }

    protected <T> void addConfig(String name, Class<T> type, T defaultValue, boolean deprecated) {
        addDetail(name, type, false, defaultValue, deprecated, null);
    }

    protected <T> void addConstraint(String name, Class<T> type, T defaultValue, Operation<T, T> combinator) {
        addDetail(name, type, true, defaultValue, false, combinator);
    }

    protected <T> void addDetail(String detailName, Class<T> detailType, boolean constraint, T defaultValue, 
    		boolean deprecated, Operation<T,T> combinator) {
        this.details.put(detailName, new FeatureDetail<T>(detailName, detailType, constraint, defaultValue, combinator));
    }

    // generic property access -----------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <T> FeatureDetail<T> getConfiguredDetail(String name) {
    	if (!supportsDetail(name))
            throw new UnsupportedOperationException("Feature detail '" + name + 
            		"' not supported in feature type: " + getClass().getName());
        return (FeatureDetail<T>) details.get(name);
    }
    
}
