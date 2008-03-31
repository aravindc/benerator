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

package org.databene.dataset;

import java.util.HashSet;
import java.util.Set;

/**
 * Defines a data set that may be nested.<br/><br/>
 * Created: 21.03.2008 12:31:13
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class Dataset {
    
    private String id;
    private String type;
    private String name;
    private Set<Dataset> subSets;

    Dataset(String type, String name) {
        if (type == null)
            throw new IllegalArgumentException("type is null");
        if (name == null)
            throw new IllegalArgumentException("name is null");
        this.id = type + ':' + name;
        this.type = type;
        this.name = name;
        this.subSets = new HashSet<Dataset>();
    }
    
    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    
    public void addSubSet(Dataset subSet) {
        subSets.add(subSet);
    }
    
    public Set<Dataset> getSubSets() {
        return subSets;
    }
    
    public Set<Dataset> getAtomicSubSets() {
        Set<Dataset> atomicSubSets = new HashSet<Dataset>();
        for (Dataset set : subSets) {
            if (set.getSubSets().size() == 0)
                atomicSubSets.add(set);
            else
                atomicSubSets.addAll(set.getAtomicSubSets());
        }
        return atomicSubSets;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final Dataset that = (Dataset) obj;
        return this.id.equals(that.id);
    }

}
