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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.OrderedMap;
import org.databene.commons.xml.XMLUtil;

/**
 * Default implementation of the DescriptorProvider interface.<br/><br/>
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class DefaultDescriptorProvider implements DescriptorProvider {
    
    private static Log logger = LogFactory.getLog(DefaultDescriptorProvider.class);
    
    private Map<String, TypeDescriptor> typeMap;
    private final String id;
    
    public DefaultDescriptorProvider(String id) {
        typeMap = new OrderedMap<String, TypeDescriptor>();
        this.id = id;
    }

    protected void addDescriptor(TypeDescriptor descriptor) {
        if (typeMap.get(descriptor.getName()) != null)
            throw new ConfigurationError("Type has already been defined: " + descriptor.getName());
        typeMap.put(descriptor.getName(), descriptor);
        logger.debug("added type descriptor: " + descriptor);
    }
    
    public String getId() {
        return id;
    }

    public TypeDescriptor getTypeDescriptor(String typeName) {
        String localName = XMLUtil.localName(typeName);
        return typeMap.get(localName);
    }

    public TypeDescriptor[] getTypeDescriptors() {
        return CollectionUtil.toArray(typeMap.values(), TypeDescriptor.class);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + id + ')';
    }
}
