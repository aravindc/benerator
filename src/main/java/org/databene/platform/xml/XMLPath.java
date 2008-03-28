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

package org.databene.platform.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.databene.commons.ArrayBuilder;
import org.databene.commons.ArrayUtil;
import org.databene.commons.NullSafeComparator;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.InstanceDescriptor;

/**
 * Holds the position in an XML structure and provides checking operations.<br/><br/>
 * Created: 24.03.2008 11:28:44
 * @author Volker Bergmann
 */
public class XMLPath {
    
    private String root;
    private Stack<EntityState> path;
    
    public XMLPath(String root) {
        path = new Stack<EntityState>();
        this.root = root;
    }
    
    public String getRoot() {
        return root;
    }
    
    public void openElement(Entity entity) {
        if (!path.isEmpty()) {
            EntityState parentState = path.peek();
            parentState.processingChild(entity.getName());
        }
        path.push(new EntityState(entity));
    }
    
    public void closeElement(Entity entity) {
        if (!NullSafeComparator.equals(entity, path.peek().entity))
            throw new IllegalStateException("Closing element failed. " +
            		"Found: " + entity.getName() + ", expected: " + path.peek().entity.getName());
        path.pop();
    }
    
    public void emptyElement(String name) {
        if (!path.isEmpty()) {
            EntityState parentState = path.peek();
            parentState.processingChild(name);
        }
    }

    public void keep(String key, Object value) {
        if (path.isEmpty())
            throw new UnsupportedOperationException();
        path.peek().keep(key, value);
    }
    
    public boolean isKept(String key) {
        if (path.isEmpty())
            throw new UnsupportedOperationException();
        return path.peek().isKept(key);
    }

    public Object getKept(String key) {
        if (path.isEmpty())
            throw new UnsupportedOperationException();
        return path.peek().getKept(key);
    }

    public InstanceDescriptor[] allowedChildren() {
        if (path.isEmpty())
            return new InstanceDescriptor[] { new InstanceDescriptor(root, root).withCount(1) };
        return path.peek().allowedChildren();
    }

    private static class EntityState {
        Entity entity;
        List<ComponentDescriptor> children;
        int position;
        int countAtPosition;
        Map<String, Object> kept;

        public EntityState(Entity entity) {
            this.entity = entity;
            this.position = 0;
            this.countAtPosition = 0;
            this.kept = new HashMap<String, Object>();
            this.children = new ArrayList<ComponentDescriptor>();
            for (ComponentDescriptor component : entity.getDescriptor().getComponents()) {
                if (component.getType() instanceof ComplexTypeDescriptor ||
                        "element".equals(component.getPSInfo(XMLSchemaDescriptorProvider.XML_REPRESENTATION)))
                    this.children.add(component);
            }
        }

        public Object getKept(String key) {
            Object object = kept.get(key);
            kept.remove(key);
            return object;
        }

        public boolean isKept(String key) {
            return kept.containsKey(key);
        }

        public void keep(String key, Object value) {
            kept.put(key, value);
        }

        public ComponentDescriptor[] allowedChildren() {
            if (position >= children.size())
                return new ComponentDescriptor[0];
            ArrayBuilder<ComponentDescriptor> builder = new ArrayBuilder<ComponentDescriptor>(ComponentDescriptor.class);
            ComponentDescriptor next = children.get(position);
            builder.append(next);
            if (countAtPosition >= next.getMinCount()) {
                int i = position + 1;
                do {
                    next = children.get(i++);
                    builder.append(next);
                } while (next.getMinCount() == 0);
            }
            return builder.toArray();
        }

        public void processingChild(String childName) {
            ComponentDescriptor[] allowedChildren = allowedChildren();
            if (ArrayUtil.isEmpty(allowedChildren))
                    return;
            for (int i = 0; i < allowedChildren.length; i++) {
                ComponentDescriptor allowedChild = allowedChildren[i];
                if (allowedChild.getName().equals(childName)) {
                    if (i == 0) {
                        countAtPosition++;
                        if (allowedChild.getMaxCount() == countAtPosition) {
                            position++;
                            countAtPosition = 0;
                        }
                    } else {
                        position += i;
                        countAtPosition = 0;
                    }
                    return;
                }
            }
            throw new IllegalStateException();
        }

        @Override
        public String toString() {
            return entity.toString();
        }
    }

}
