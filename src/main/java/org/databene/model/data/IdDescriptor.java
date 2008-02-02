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

import org.databene.commons.converter.AnyConverter;

/**
 * Describes an ID generation setup.<br/><br/>
 * Created: 29.01.2008 21:20:59
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class IdDescriptor extends ComponentDescriptor {

    public IdDescriptor(String name) {
        this(name, null);
    }
    
    public IdDescriptor(ComponentDescriptor parent) {
        this(parent.getName(), parent);
    }
    
    protected IdDescriptor(String name, ComponentDescriptor parent) {
        super(name, parent);
        addDetailConfig("strategy", String.class, false, null);
        addDetailConfig("scope", String.class, false, null);
        addDetailConfig("param", String.class, false, "");
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getStrategy() {
        return (String) getDetailValue("strategy");
    }

    public void setStrategy(String strategy) {
        setDetail("strategy", strategy);
    }

    public String getScope() {
        return (String) getDetailValue("scope");
    }

    public void setScope(String scope) {
        setDetail("scope", scope);
    }

    public String getParam() {
        return (String)getDetailValue("param");
    }

    public void setParam(String param) {
        setDetail("param", param);
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

    public void setDetail(String detailName, Object detailValue) {
        Class<? extends Object> targetType = getDetailType(detailName);
        detailValue = AnyConverter.convert(detailValue, targetType);
        super.setDetail(detailName, detailValue);
    }

}
