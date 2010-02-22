/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine;

import java.io.Closeable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.databene.commons.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a standard implementation of the {@link ResourceManager} interface.<br/>
 * <br/>
 * Created at 25.09.2009 09:19:41
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class ResourceManagerSupport implements ResourceManager{

	private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);
	
	private Set<Closeable> resources = new HashSet<Closeable>();

	public boolean addResource(Closeable resource) {
		if (resources.contains(resource))
			return false;
	    return resources.add(resource);
    }
	
	public Collection<? extends Closeable> getResources() {
		return resources;
	}

    public void close() {
		logger.debug("Closing resources: " + this);
    	IOUtil.closeAll(resources);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + resources;
    }
    
}
