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

/**
 * Describes a reference to an instance of a complex type (see {@link ComplexTypeDescriptor}).<br/>
 * <br/>
 * Created: 27.02.2008 16:28:22
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class ReferenceDescriptor extends ComponentDescriptor {
	
	// TODO v0.7.1 the class implies a reference to a primary key, but it could be an arbitrary column

    private static final String TARGET_TYPE = "targetType";
    
    // constructors ----------------------------------------------------------------------------------------------------

    public ReferenceDescriptor(String name) {
        this(name, null);
    }

    public ReferenceDescriptor(String name, String typeName) {
        this(name, typeName, null);
    }

    public ReferenceDescriptor(String name, String typeName, String targetType) {
        super(name, typeName);
        addConstraint(TARGET_TYPE, String.class, new FirstArgSelector<String>());
        setTargetType(targetType);
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public String getTargetType() {
        return (String) getDetailValue(TARGET_TYPE);
    }

    public void setTargetType(String targetType) {
        setDetailValue(TARGET_TYPE, targetType);
    }
    
    // convenience-with-methods for construction -----------------------------------------------------------------------
    
    public ReferenceDescriptor withTargetTye(String targetType) {
        setTargetType(targetType);
        return this;
    }
}