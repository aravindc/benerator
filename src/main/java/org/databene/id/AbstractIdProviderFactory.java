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

import java.util.HashMap;
import java.util.Map;

import org.databene.commons.CollectionUtil;

/**
 * Helper class for inheriting custom {@link IdProviderFactory}s.<br/>
 * <br/>
 * Created at 05.11.2008 08:46:51
 * @since 0.5.6
 * @author Volker Bergmann
 */
public abstract class AbstractIdProviderFactory implements IdProviderFactory {
	
	private Map<String, IdProvider> providers = new HashMap<String, IdProvider>();

	public <T> IdProvider<T> idProvider(IdStrategy<T> strategy, String param, String scope) {
		if (scope == null || "local".equals(scope))
			return createIdProvider(strategy, param);
		IdProvider provider = providers.get(scope);
		if (provider == null) {
			provider = createIdProvider(strategy, param);
			providers.put(scope, provider);
		}
		return provider;
	}

	public abstract <T> IdProvider<T> createIdProvider(IdStrategy<T> strategy, String param);

	public IdStrategy<? extends Object>[] getIdStrategies() {
		return (IdStrategy<? extends Object>[]) CollectionUtil.toArray(providers.values());
	}

}
