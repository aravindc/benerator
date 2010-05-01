/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

/**
 * Maps abstract types to concrete types and vice versa.
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class TypeMapper {

    private Map<String, Class<?>> abstractToConcrete;
    private Map<Class<?>, String> concreteToAbstract;
    
    /**
     * @param typeMappings name-class pairs that list the mappings to be defined
     */
    public TypeMapper(Object ... typeMappings) {
        this.abstractToConcrete = new HashMap<String, Class<?>>();
        this.concreteToAbstract = new HashMap<Class<?>, String>();
        for (int i = 0; i < typeMappings.length; i += 2) {
            String abstractType = (String) typeMappings[i];
            Object concreteType = typeMappings[i + 1];
            map(abstractType, (Class<?>) concreteType);
        }
    }
    
    public void map(String abstractType, Class<?> concreteType) {
        abstractToConcrete.put(abstractType, concreteType);
        concreteToAbstract.put(concreteType, abstractType);
    }
    
    public Class<?> concreteType(String abstractType) {
        return abstractToConcrete.get(abstractType);
    }
    
    public String abstractType(Class<?> concreteType) {
        return concreteToAbstract.get(concreteType);
    }
}
