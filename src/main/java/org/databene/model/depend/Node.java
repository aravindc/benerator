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

package org.databene.model.depend;

import java.util.ArrayList;
import java.util.List;

import static org.databene.model.depend.NodeState.*;

/**
 * Helper class for calculating dependencies.
 * @author Volker Bergmann
 * @since 0.3.04
 * @param <E>
 */
class Node<E extends Dependent<E>> {
    
    private NodeState state;
    
    private E subject;
    private List<Node<E>> providers;
    private List<Boolean> providerRequired;
    private List<Node<E>> clients;
    
    public Node(E subject) {
        super();
        this.subject = subject;
        this.providers = new ArrayList<Node<E>>();
        this.providerRequired = new ArrayList<Boolean>();
        this.clients = new ArrayList<Node<E>>();
        this.state = INITIALIZABLE;
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    /**
     * @return the subject
     */
    public E getSubject() {
        return subject;
    }
    
    /**
     * @return the state
     */
    public NodeState getState() {
        return state;
    }

    public boolean requires(Node<E> provider) {
        return providerRequired.get(providers.indexOf(provider));
    }
    
    public List<Node<E>> getProviders() {
        return providers;
    }
    
    public Node<E> addProvider(Node<E> provider, boolean required) {
        this.state = INACTIVE;
        if (this.providers.contains(provider)) {
            if (required && !required(provider)) {
                providerRequired.set(providers.indexOf(provider), Boolean.TRUE);
                providersChanged();
            }
        } else {
            this.providers.add(provider);
            this.providerRequired.add(required);
            provider.addClient(this);
            providersChanged();
        }
        return this;
    }
    
    public boolean hasProviders() {
        return (providers.size() > 0);
    }
    
    public boolean required(Node<E> provider) {
        return providerRequired.get(providers.indexOf(provider));
    }
    
    public List<Node<E>> getClients() {
        return clients;
    }

    public void addClient(Node<E> client) {
        if (!this.clients.contains(client))
            this.clients.add(client);
    }
    
    public boolean hasClients() {
        return (clients.size() > 0);
    }
    
    // interface -------------------------------------------------------------------------------------------------------

    void providersChanged() {
        if (state == INITIALIZABLE || state == INITIALIZED)
            return;
        // check initializability
        boolean initializable = true;
        boolean partiallyInitializable = true;
        for (Node<E> provider : providers)
            if (!allowsClientInitialization(provider.getState())) {
                initializable = false;
                if (required(provider))
                    partiallyInitializable = false;
            }
        if (initializable) {
            this.state = INITIALIZABLE;
            return;
        }
        if (state == PARTIALLY_INITIALIZED)
            return;
        if (partiallyInitializable) {
            this.state = PARTIALLY_INITIALIZABLE;
            return;
        }
        if (state != INACTIVE)
            return;
        if (!hasProviders())
            this.state = FORCEABLE;
        for (Node<E> provider : providers)
            if (provider.getState() == INITIALIZED) {
                this.state = FORCEABLE;
                return;
            }
    }

    private boolean allowsClientInitialization(NodeState providerState) {
        return providerState == INITIALIZED || providerState == FORCED || providerState == PARTIALLY_INITIALIZED;
    }
/*
    private boolean allProvidersInState(NodeState state) {
        for (Node<E> provider : providers)
            if (provider.getState() != state)
                return false;
        return true;
    }
*/
    public void initialize() {
        if (state != INITIALIZABLE)
            throw new IllegalStateException("Node not initializable: " + this);
        setState(INITIALIZED);
    }

    private void setState(NodeState state) {
        this.state = state;
        for (Node<E> client : clients)
            client.providersChanged();
    }
    
    public void initializePartially() {
        if (state != PARTIALLY_INITIALIZABLE)
            throw new IllegalStateException("Node not partially initializable: " + this);
        setState(PARTIALLY_INITIALIZED);
    }
    
    public void force() {
        setState(FORCED);
    }
    
    void assertState(NodeState state) {
        if (this.state != state)
            throw new IllegalStateException("Expected to be in state '" + state + "', " 
                    + "found: '" + this.state + "'");
    }
    
    // java.lang.Object ------------------------------------------------------------------------------------------------
    
    @Override
    public int hashCode() {
        return subject.hashCode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final Node that = (Node) obj;
        return (this.subject != null ? this.subject.equals(that.subject) : that.subject == null);
    }
    
    @Override
    public String toString() {
        return subject.toString();
    }

}
