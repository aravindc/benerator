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

package org.databene.benerator.engine;

import org.databene.benerator.factory.GenerationSetup;
import org.databene.benerator.factory.SimpleGenerationSetup;
import org.databene.commons.bean.ClassCache;
import org.databene.commons.bean.ClassProvider;
import org.databene.commons.context.CaseInsensitiveContext;
import org.databene.commons.context.ContextStack;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.context.PropertiesContext;

/**
 * A BeneratorContext.<br/><br/>
 * Created at 20.04.2008 06:41:04
 * @since 0.5.2
 * @author Volker Bergmann
 *
 */
public class BeneratorContext extends ContextStack implements ClassProvider {
	
	private DefaultContext properties;
	private ClassCache classCache;
	
	public BeneratorContext(String contextUri) {
		properties = new DefaultContext();
		push(new PropertiesContext(java.lang.System.getenv()));
		push(new PropertiesContext(java.lang.System.getProperties()));
		push(properties);
		push(new CaseInsensitiveContext(true));
		set("benerator", new SimpleGenerationSetup(contextUri));
		classCache = new ClassCache(); // TODO initialize ClassCache
	}
	
	@Override
    public synchronized Object get(String key) {
        for (int i = contexts.size() - 1; i >= 0; i--) {
            Object result = contexts.get(i).get(key);
            if (result != null)
                return result;
        }
        return null;
    }
	
	public void setProperty(String name, Object value) {
		properties.set(name, value);
	}
	
	public GenerationSetup getGenerationSetup() {
		return (GenerationSetup) get("benerator");
	}

	public void importClass(String className) {
		classCache.importClass(className);
	}

	public void importPackage(String packageName) {
		classCache.importPackage(packageName);
	}

	public Class forName(String className) {
		return classCache.forName(className);
	}
}
