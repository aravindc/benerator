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

package org.databene.benerator.factory;

import org.databene.commons.SystemInfo;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.script.ScriptUtil;

/**
 * Simple implementation of the GenerationSetup interface.<br/><br/>
 * Created: 28.02.2008 22:40:55
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class SimpleGenerationSetup implements GenerationSetup {
    
    protected String  defaultEncoding     = SystemInfo.fileEncoding();
    protected int     defaultPagesize     = 1;
    protected boolean defaultNull         = true;
    protected char    defaultSeparator    = ',';
    protected String  defaultErrorHandler = "fatal";
    protected String  contextUri          = "./";
    
    protected ComplexTypeDescriptor defaultComponent = new ComplexTypeDescriptor("benerator:defaultComponent");

    public SimpleGenerationSetup(String contextUri) {
    	this.contextUri = contextUri;
    }
    
    public String getDefaultEncoding() {
        return defaultEncoding;
    }
    
    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }
    
    public int getDefaultPagesize() {
        return defaultPagesize;
    }
    
    public void setDefaultPagesize(int defaultPagesize) {
        this.defaultPagesize = defaultPagesize;
    }
    
    public String getDefaultScript() {
        return ScriptUtil.getDefaultScriptEngine();
    }
    
    public void setDefaultScript(String defaultScript) {
        ScriptUtil.setDefaultScriptEngine(defaultScript);
    }
    
    public boolean isDefaultNull() {
        return defaultNull;
    }
    
    public void setDefaultNull(boolean defaultNull) {
        this.defaultNull = defaultNull;
    }
    
	public char getDefaultSeparator() {
		return defaultSeparator;
	}

	public void setDefaultSeparator(char defaultSeparator) {
		this.defaultSeparator = defaultSeparator;
	}

	public ComponentDescriptor getDefaultComponentConfig(String name) {
		return defaultComponent.getComponent(name);
	}

	public String getDefaultErrorHandler() {
		return defaultErrorHandler;
	}

	public void setDefaultErrorHandler(String defaultErrorHandler) {
		this.defaultErrorHandler = defaultErrorHandler;
	}

	public String getContextUri() {
		return contextUri;
	}

	public void setContextUri(String contextUri) {
		this.contextUri = contextUri;
	}

}
