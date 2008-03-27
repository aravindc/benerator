package org.databene.model.data;

public class IdDescriptor extends ComponentDescriptor {

    private static final String STRATEGY = "strategy";
    private static final String SCOPE    = "scope";
    private static final String PARAM    = "param";

    public IdDescriptor(String name) {
        this(name, null);
    }

    public IdDescriptor(String name, String type) {
        super(name, type);
        addDetailConfig(STRATEGY, String.class, false, null);
        addDetailConfig(SCOPE,    String.class, false, null);
        addDetailConfig(PARAM,    String.class, false, null);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getStrategy() {
        return (String) getDetailValue(STRATEGY);
    }

    public void setStrategy(String strategy) {
        setDetailValue(STRATEGY, strategy);
    }

    public String getScope() {
        return (String) getDetailValue(SCOPE);
    }

    public void setScope(String scope) {
        setDetailValue(SCOPE, scope);
    }

    public String getParam() {
        return (String)getDetailValue(PARAM);
    }

    public void setParam(String param) {
        setDetailValue(PARAM, param);
    }

    // literate build helpers ------------------------------------------------------------------------------------------

    public IdDescriptor withStrategy(String strategy) {
        setStrategy(strategy);
        return this;
    }

    public IdDescriptor withScope(String scope) {
        setScope(scope);
        return this;
    }

    public IdDescriptor withParam(String param) {
        setParam(param);
        return this;
    }

    // generic property access -----------------------------------------------------------------------------------------
/*
    public void setDetail(String detailName, Object detailValue) {
        Class<? extends Object> targetType = getDetailType(detailName);
        detailValue = AnyConverter.convert(detailValue, targetType);
        super.setDetailValue(detailName, detailValue);
    }
*/
}