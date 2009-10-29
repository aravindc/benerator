/*
 * (c) Copyright 2008, 2009 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.engine.expression.ScriptedLiteral;
import org.databene.commons.Expression;
import org.databene.commons.expression.ConstantExpression;
import org.databene.commons.expression.TypeConvertingExpression;
import org.databene.commons.operation.AndOperation;
import org.databene.commons.operation.OrOperation;

/**
 * Describes generation of (several) entities of a type by uniqueness, 
 * nullability and count characteristics.<br/><br/>
 * Created: 03.03.2008 07:55:45
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class InstanceDescriptor extends FeatureDescriptor {
    
    public static final String TYPE               = "type";
    
    // restrictions
    public static final String UNIQUE             = "unique";
    public static final String NULLABLE           = "nullable";
    public static final String MIN_COUNT          = "minCount";
    public static final String MAX_COUNT          = "maxCount";
    
    // configs
    public static final String COUNT_PRECISION    = "countPrecision";
    public static final String COUNT_DISTRIBUTION = "countDistribution";
    public static final String COUNT              = "count";
    public static final String NULL_QUOTA         = "nullQuota";
    
    private InstanceDescriptor parent;
    private String typeName;
    private TypeDescriptor localType;
    
    // constructors ----------------------------------------------------------------------------------------------------

    public InstanceDescriptor(String name) {
        this(name, null, null);
    }

    public InstanceDescriptor(String name, String typeName) {
        this(name, typeName, null);
    }

    public InstanceDescriptor(String name, TypeDescriptor localType) {
        this(name, null, localType);
    }

    protected InstanceDescriptor(String name, String typeName, TypeDescriptor localType) {
        super(name);
        this.typeName = typeName;
        this.localType = localType;

        // restrictions
        addRestriction(UNIQUE,        Boolean.class, false, new OrOperation());
        addRestriction(NULLABLE,      Boolean.class, false, new AndOperation());
        addRestriction(MIN_COUNT,     Expression.class, null, null); // TODO combination operation?
        addRestriction(MAX_COUNT,     Expression.class, null, null); // TODO combination operation?
        
        // configs
        addConfig(COUNT,              Expression.class, null);
        addConfig(COUNT_PRECISION,    Expression.class, new ConstantExpression<Long>(1L));
        addConfig(COUNT_DISTRIBUTION, String.class, null);
        addConfig(NULL_QUOTA,         Double.class,     0.);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setParent(InstanceDescriptor parent) {
    	this.parent = parent;
    }
    
    public String getTypeName() {
        return (typeName == null && parent != null ? parent.getTypeName() : typeName);
    }
    
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
    public TypeDescriptor getType() {
        if (getLocalType() != null)
            return getLocalType();
        TypeDescriptor type = null;
        if (getTypeName() != null) {
            type = DataModel.getDefaultInstance().getTypeDescriptor(typeName);
        }
        return type;
    }
    
    public TypeDescriptor getLocalType() {
    	if (localType == null && parent != null && parent.getLocalType() != null)
    		localType = getLocalType(parent.getLocalType() instanceof ComplexTypeDescriptor);
    	return localType;
    }
    
    public TypeDescriptor getLocalType(boolean complexType) {
        if (localType != null)
            return localType;
        if (complexType)
            localType = new ComplexTypeDescriptor(getName(), getTypeName());
        else
            localType = new SimpleTypeDescriptor(getName(), getTypeName());
        typeName = null;
        return localType;
    }
    
    public void setLocalType(TypeDescriptor localType) {
        this.localType = localType;
        if (localType != null)
        	typeName = null;
    }
    
    public Boolean isUnique() {
        return (Boolean)getDetailValue(UNIQUE);
    }

    public void setUnique(Boolean unique) {
        setDetailValue(UNIQUE, unique);
    }

    public Boolean isNullable() {
        return (Boolean)getDetailValue(NULLABLE);
    }
    
    public void setNullable(Boolean nullable) {
        setDetailValue(NULLABLE, nullable);
    }
    
    @SuppressWarnings("unchecked")
    public Expression<Long> getMinCount() {
        return (Expression<Long>) getDetailValue(MIN_COUNT);
    }
    
    public void setMinCount(Expression<Long> minCount) {
        setDetailValue(MIN_COUNT, minCount);
    }

    @SuppressWarnings("unchecked")
    public Expression<Long> getMaxCount() {
        return (Expression<Long>) getDetailValue(MAX_COUNT);
    }
    
    public void setMaxCount(Expression<Long> maxCount) {
        setDetailValue(MAX_COUNT, maxCount);
    }

    @SuppressWarnings("unchecked")
    public Expression<Long> getCount() {
        return (Expression) getDetailValue(COUNT);
    }
    
    public void setCount(Expression<Long> count) {
        setDetailValue(COUNT, count);
    }

    public String getCountDistribution() {
        return (String) getDetailValue(COUNT_DISTRIBUTION);
    }
    
    public void setCountDistribution(String distribution) {
        setDetailValue(COUNT_DISTRIBUTION, distribution);
    }

    @SuppressWarnings("unchecked")
    public Expression<Long> getCountPrecision() {
        return (Expression<Long>) getDetailValue(COUNT_PRECISION);
    }
    
    public void setCountPrecision(Expression<Long> distribution) {
        setDetailValue(COUNT_PRECISION, distribution);
    }

    public Double getNullQuota() {
        return (Double)getDetailValue(NULL_QUOTA);
    }
    
    public void setNullQuota(Double nullQuota) {
        setDetailValue(NULL_QUOTA, nullQuota);
    }

    @Override
    public Object getDetailValue(String name) {
        Object value = super.getDetailValue(name);
        if (value == null && parent != null && parent.supportsDetail(name)) {
            FeatureDetail<? extends Object> detail = parent.getDetail(name);
            if (detail.isRestriction())
                value = detail.getValue();
        }
        if (value == null)
            value = getDetailDefault(name);
        return value;
    }
    
    @Override
    public void setDetailValue(String detailName, Object detailValue) {
    	if (COUNT.equals(detailName) || MIN_COUNT.equals(detailName) || MAX_COUNT.equals(detailName) || COUNT_PRECISION.equals(detailName)) {
            FeatureDetail<Object> detail = getDetail(detailName);
            if (detail == null)
                throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support detail type: " + detailName);
            if (detailValue instanceof Expression)
            	detail.setValue(new TypeConvertingExpression<Long>((Expression<?>) detailValue, Long.class));
            else if (detailValue == null)
                detail.setValue(null);
    	    else if ((detailValue instanceof String) && ((String) detailValue).startsWith("{") && ((String) detailValue).endsWith("}"))
                detail.setValue(new TypeConvertingExpression<Long>(new ScriptedLiteral<Long>((String) detailValue), Long.class));
    	    else
    	    	detail.setValue(new TypeConvertingExpression<Long>(new ConstantExpression<Long>((Long) detailValue), Long.class));
    	} else
    		super.setDetailValue(detailName, detailValue);
    }
    
    // convenience 'with...' methods -----------------------------------------------------------------------------------
    
    public InstanceDescriptor withCount(long count) {
        setCount(new ConstantExpression<Long>(count));
        return this;
    }
    
    public InstanceDescriptor withMinCount(long minCount) {
        setMinCount(new ConstantExpression<Long>(minCount));
        return this;
    }
    
    public InstanceDescriptor withMaxCount(long maxCount) {
        setMaxCount(new ConstantExpression<Long>(maxCount));
        return this;
    }
    
    public InstanceDescriptor withNullQuota(double nullQuota) {
    	setNullQuota(nullQuota);
    	return this;
    }

    public InstanceDescriptor withUnique(boolean unique) {
    	setUnique(unique);
    	return this;
    }

	public boolean overwritesParent() {
		return parent != null;
	}
}
