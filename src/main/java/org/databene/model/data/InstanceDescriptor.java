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

import org.databene.commons.ConfigurationError;
import org.databene.commons.operation.AndOperation;
import org.databene.commons.operation.MaxOperation;
import org.databene.commons.operation.MinOperation;
import org.databene.commons.operation.OrOperation;
import org.databene.model.function.Distribution;

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
    public static final String COUNT_DISTRIBUTION = "countDistribution";
    public static final String COUNT_VARIATION1   = "countVariation1";
    public static final String COUNT_VARIATION2   = "countVariation2";
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
        addRestriction(MIN_COUNT,     Long.class,    null, new MaxOperation<Long>());
        addRestriction(MAX_COUNT,     Long.class,    null, new MinOperation<Long>());
        
        // configs
        addConfig(COUNT,              Long.class,         null);
        addConfig(COUNT_DISTRIBUTION, Distribution.class, null);
        addConfig(COUNT_VARIATION1,   Long.class,         null);
        addConfig(COUNT_VARIATION2,   Long.class,         null);
        addConfig(NULL_QUOTA,         Double.class,       null);
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
        if (getTypeName() == null)
            throw new ConfigurationError("Type not defined for " + getName());
        TypeDescriptor type = DataModel.getDefaultInstance().getTypeDescriptor(typeName);
        if (type == null)
            throw new ConfigurationError("Type of " + getName() + " not found: " + typeName);
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
    
    public Long getMinCount() {
        return (Long)getDetailValue(MIN_COUNT);
    }
    
    public void setMinCount(Long minCount) {
        setDetailValue(MIN_COUNT, minCount);
    }

    public Long getMaxCount() {
        return (Long)getDetailValue(MAX_COUNT);
    }
    
    public void setMaxCount(Long maxCount) {
        setDetailValue(MAX_COUNT, maxCount);
    }

    public Long getCount() {
        return (Long) getDetailValue(COUNT);
    }
    
    public void setCount(Long count) {
        setDetailValue(COUNT, count);
    }

    public Distribution getCountDistribution() {
        return (Distribution) getDetailValue(COUNT_DISTRIBUTION);
    }
    
    public void setCountDistribution(Distribution distribution) {
        setDetailValue(COUNT_DISTRIBUTION, distribution);
    }

    public Long getCountVariation1() {
        return (Long)getDetailValue(COUNT_VARIATION1);
    }
    
    public void setCountVariation1(Long countVariation1) {
        setDetailValue(COUNT_VARIATION1, countVariation1);
    }

    public Long getCountVariation2() {
        return (Long)getDetailValue(COUNT_VARIATION2);
    }
    
    public void setCountVariation2(Long countVariation2) {
        setDetailValue(COUNT_VARIATION2, countVariation2);
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
    
    // convenience with... methods -------------------------------------------------------------------------------------
    
    public InstanceDescriptor withCount(long count) {
        setCount(count);
        return this;
    }
    
}
