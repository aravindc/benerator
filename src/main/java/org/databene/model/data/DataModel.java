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

import java.util.HashMap;
import java.util.Map;

import org.databene.commons.ConfigurationError;

/**
 * Merges and organizes entity definitions of different systems.<br/><br/>
 * Created: 25.08.2007 20:40:17
 * @since 0.3
 * @author Volker Bergmann
 */
public class DataModel {
    
    private static final DataModel defaultInstance = new DataModel();

    private Map<String, DescriptorProvider> providers;

    public DataModel() {
        this.providers = new HashMap<String, DescriptorProvider>();
        clear();
    }

    public void addDescriptorProvider(DescriptorProvider provider) {
        addDescriptorProvider(provider, true);
    }
    
    public void addDescriptorProvider(DescriptorProvider provider, boolean validate) {
        providers.put(provider.getId(), provider);
        if (validate)
            validate();
    }
    
    public TypeDescriptor getTypeDescriptor(String typeId) {
    	if (typeId == null)
    		return null;
        String ns = null;
        String name = typeId;
        if (typeId.contains(":")) {
            int i = typeId.indexOf(':');
            ns = typeId.substring(0, i);
            name = typeId.substring(i + 1);
        }
        
        if (ns != null) {
            DescriptorProvider provider = providers.get(ns);
            if (provider == null)
                throw new ConfigurationError("No provider found for namespace: " + ns);
            // first, search case-sensitive
            TypeDescriptor typeDescriptor = provider.getTypeDescriptor(name);
            if (typeDescriptor != null)
                return typeDescriptor;
            else {
                // not found yet, try it case-insensitive
                return searchCaseInsensitive(provider, name);
            }
        } else {
            // first, search case-sensitive
            for (DescriptorProvider provider : providers.values()) {
                TypeDescriptor descriptor = provider.getTypeDescriptor(name);
                if (descriptor != null)
                    return descriptor;
            }
            // not found yet, try it case-insensitive
            for (DescriptorProvider provider : providers.values()) {
                TypeDescriptor descriptor = searchCaseInsensitive(provider, name);
                if (descriptor != null)
                    return descriptor;
            }
        }
        return null;
    }
    
    public void validate() {
        for (DescriptorProvider provider : providers.values()) {
            for (TypeDescriptor desc : provider.getTypeDescriptors())
                validate(desc);
        }
    }
    
    public static DataModel getDefaultInstance() {
        return defaultInstance;
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    private TypeDescriptor searchCaseInsensitive(DescriptorProvider provider, String name) {
        for (TypeDescriptor type : provider.getTypeDescriptors())
            if (type.getName().equals(name))
                return type;
        return null;
    }

    private void validate(TypeDescriptor type) {
        if (type instanceof SimpleTypeDescriptor) {
            validate((SimpleTypeDescriptor) type);
        } else if (type instanceof ComplexTypeDescriptor) {
            validate((ComplexTypeDescriptor) type);
        } else
            throw new UnsupportedOperationException("Descriptor type not supported: " + type.getClass());
    }

    private void validate(ComplexTypeDescriptor desc) {
        for (ComponentDescriptor component : desc.getComponents()) {
            TypeDescriptor type = component.getType();
            if (!(type instanceof ComplexTypeDescriptor))
                validate(type);
        }
    }

    private void validate(SimpleTypeDescriptor desc) {
        PrimitiveType<? extends Object> primitiveType = desc.getPrimitiveType();
        if (primitiveType == null)
            throw new ConfigurationError("No primitive type defined for simple type: " + desc.getName());
    }

    public void clear() {
        providers.clear();
        addDescriptorProvider(new BasicDescriptorProvider(), false);
    }

}
