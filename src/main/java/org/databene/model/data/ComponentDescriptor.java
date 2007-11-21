/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.StringUtil;

/**
 * Describes a component of an Entity.<br/>
 * <br/>
 * Created: 16.08.2007 18:37:33
 */
public abstract class ComponentDescriptor extends FeatureDescriptor {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public ComponentDescriptor(String name) {
        this(name, null);
    }

    public ComponentDescriptor(String name, ComponentDescriptor parent) {
        super(name, parent);
        addDetailConfig("type", String.class, true, null);
        addDetailConfig("values", String.class, false, null);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getType() {
        return (String)getDetailValue("type");
    }

    public void setType(String type) {
        setDetail("type", type);
    }

    public String[] getValues() {
        String valuesString = (String) getDetailValue("values");
        if (valuesString == null)
            return EMPTY_STRING_ARRAY;
        return StringUtil.tokenize(valuesString, ',');
    }

    public void setValues(String values) {
        setDetail("values", values);
    }

    // literate construction helpers -----------------------------------------------------------------------------------

    public ComponentDescriptor ofType(String type) {
        // TODO make this usable in sub classes like new AttributeDescriptor().ofType().setMin()
        setDetail("type", type);
        return this;
    }

    public ComponentDescriptor withValues(String values) {
        setDetail("values", values);
        return this;
    }

}
