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
 * @param <E>
 */
public class TypeMapper<E> {

    private Map<String, E> abstractToConcrete;
    private Map<E, String> concreteToAbstract;
    
    public TypeMapper(Object ... types) {
        this.abstractToConcrete = new HashMap<String, E>();
        this.concreteToAbstract = new HashMap<E, String>();
        for (int i = 0; i < types.length; i += 2) {
            String abstractType = (String)types[i];
            Object concreteType = types[i + 1];
            abstractToConcrete.put((String)abstractType, (E)concreteType);
        }
    }
    
    public void map(String abstactType, E concreteType) {
        abstractToConcrete.put(abstactType, concreteType);
        concreteToAbstract.put(concreteType, abstactType);
    }
    
    public E concreteType(String abstractType) {
        return abstractToConcrete.get(abstractType);
    }
    
    public String abstractType(E concreteType) {
        return concreteToAbstract.get(concreteType);
    }
}
