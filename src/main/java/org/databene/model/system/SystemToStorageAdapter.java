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

package org.databene.model.system;

import org.databene.commons.TypedIterable;
import org.databene.model.data.Entity;
import org.databene.model.data.EntityDescriptor;
import org.databene.model.storage.StorageSystem;

/**
 * Lets benerator use a service provider class that implements the deprecated interface {@link System} 
 * by wrapping it with an adapter that implements the now required {@link StorageSystem} interface.
 * This class is for smoother migration of existing customizations only! 
 * For new customizations implement the {@link StorageSystem} interface!<br/>
 * <br/>
 * Created: 27.01.2008 07:28:59
 * @since 0.4.0
 * @author Volker Bergmann
 * @deprecated this is just an adapter for the deprecated interface {@link System}
 * for a smoother migration, on new customizations implement the {@link StorageSystem} instead. 
 */
@Deprecated
public class SystemToStorageAdapter implements StorageSystem {
    
    private System system;
    
    public SystemToStorageAdapter(System system) {
        super();
        this.system = system;
    }

    // StorageSystem interface implementation --------------------------------------------------------------------------
    
    public String getId() {
        return system.getId();
    }

    public EntityDescriptor getTypeDescriptor(String typeName) {
        return system.getTypeDescriptor(typeName);
    }

    public EntityDescriptor[] getTypeDescriptors() {
        return system.getTypeDescriptors();
    }

    public TypedIterable<Entity> queryEntities(String type, String selector) {
        return system.getEntities(type);
    }

    public <T> TypedIterable<T> queryEntityIds(String entityName, String selector) {
        return (TypedIterable<T>) system.getIds(entityName, selector);
    }

    public <T> TypedIterable<T> query(String selector) {
        return (TypedIterable<T>) system.getBySelector(selector);
    }
    
    public void store(Entity entity) {
        system.store(entity);
    }

    public void flush() {
        system.flush();
    }

    public void close() {
        system.close();
    }
}
