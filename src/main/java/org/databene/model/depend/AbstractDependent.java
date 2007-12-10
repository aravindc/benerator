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

import org.databene.model.Dependent;

/**
 * Abstract class that provides partial featur implementation of the Dependent interface.
 * @author Volker Bergmann
 * @since 0.3.04
 * @param <E>
 */
public abstract class AbstractDependent<E extends Dependent<E>> implements Dependent<E> {

    protected List<ProviderInfo<E>> providers;

    public AbstractDependent(E ... requiredProviders) {
        this.providers = new ArrayList<ProviderInfo<E>>();
        for (E requiredProvider : requiredProviders)
            addRequiredProvider(requiredProvider);
    }
    
    public void addRequiredProvider(E provider) {
        providers.add(new ProviderInfo<E>(provider, true));
    }

    public void addOptionalProvider(E provider) {
        providers.add(new ProviderInfo<E>(provider, false));
    }

    // Dependent interface --------------------------------------------------------------------------
    
    public int countProviders() {
        return providers.size();
    }

    public E getProvider(int index) {
        return providers.get(index).getProvider();
    }

    public boolean requiresProvider(int index) {
        return providers.get(index).isRequired();
    }

}