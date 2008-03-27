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

package org.databene.id;

import org.databene.commons.ConfigurationError;

/**
 * Creates global {@link IdProvider}s.<br/><br/>
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class GlobalIdProviderFactory implements IdProviderFactory {

    public static final IdStrategy<String> UUID = new IdStrategy<String>("uuid", String.class);
    public static final IdStrategy<Long> INCREMENT = new IdStrategy<Long>("increment", Long.class);
    
    public static final UUIDProvider uuidProvider = new UUIDProvider();
    
    public <T> IdProvider<T> idProvider(IdStrategy<T> strategy, String param, String scope) {
        if (strategy == INCREMENT) 
            return (IdProvider<T>) createIncrementIdProvider(param);
        else if (strategy == UUID) 
            return (IdProvider<T>) uuidProvider;
        else
            throw new ConfigurationError(
                    "IdStrategy '" + strategy + "' is not supported by " + getClass().getName());
    }

    public IdStrategy<? extends Object>[] getIdStrategies() {
        return new IdStrategy[] { INCREMENT, UUID };
    }
    
    private IncrementIdProvider createIncrementIdProvider(String param) {
        long initialValue = 1;
        if (param != null)
            initialValue = Long.parseLong(param);
        return new IncrementIdProvider(initialValue);
    }


}
