/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
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

import java.io.Closeable;
import java.io.Flushable;

import org.databene.commons.Context;
import org.databene.commons.TypedIterable;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.Entity;

/**
 * Abstraction of an Entity storage system.<br/><br/>
 * <br/>
 * Created: 27.06.2007 23:02:21
 * @since 0.4.0
 * @author Volker Bergmann
 */
public interface StorageSystem extends DescriptorProvider, Closeable, Flushable {

    /** Returns a name that identifies the database */
    String getId();
    
    /** Creates an iterator that provides all entities of given type. */
    TypedIterable<Entity> queryEntities(String type, String selector, Context context);
    
    /** Queries for entity ids */
    <T> TypedIterable<T> queryEntityIds(String entityName, String selector, Context context);

    /** Creates an Iterable for repetitive iteration through the results of the specified query. */
    <T> TypedIterable<T> query(String selector, Context context);
    
    /** Persists a new entity. */
    void store(Entity entity);
    
    /** Updates an existing entity. */
    void update(Entity entity);
    
    /** Assures that all data that has been {@link #store(Entity)}d, is send to the target system. */
    void flush();
    
    /** Closes the database. */
    void close();
}
