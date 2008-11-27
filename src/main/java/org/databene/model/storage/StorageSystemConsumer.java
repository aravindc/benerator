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

package org.databene.model.storage;

import org.databene.model.consumer.AbstractConsumer;
import org.databene.model.data.Entity;

/**
 * Stores an Entity in the associated {@link StorageSystem}. It replaces the class SystemProcessor.<br/>
 * <br/>
 * Created: 29.01.2008 09:35:07
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class StorageSystemConsumer extends AbstractConsumer<Entity> {

    private StorageSystem system;
    private boolean insert;

    public StorageSystemConsumer(StorageSystem system, boolean insert) {
        this.system = system;
        this.insert = insert;
    }

    public void startConsuming(Entity entity) {
    	if (insert)
    		system.store(entity);
    	else
    		system.update(entity);
    }

    @Override
    public void flush() {
        system.flush();
    }

    @Override
    public void close() {
        system.close();
    }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + "[" + system + "]";
    }
}

