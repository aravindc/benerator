/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

import java.util.Locale;

import org.databene.commons.LocaleUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.bean.ClassCache;
import org.databene.commons.bean.ClassProvider;
import org.databene.commons.context.CaseInsensitiveContext;
import org.databene.commons.context.ContextStack;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.context.PropertiesContext;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.script.ScriptUtil;

/**
 * A BeneratorContext.<br/><br/>
 * Created at 20.04.2008 06:41:04
 * @since 0.5.2
 * @author Volker Bergmann
 */
public class BeneratorContext extends ContextStack implements ClassProvider {
	
    private DefaultContext properties;
	private ClassCache classCache;
	
    protected String  defaultEncoding      = SystemInfo.getFileEncoding();
    protected String  defaultDataset       = LocaleUtil.getDefaultCountryCode();
    protected int     defaultPagesize      = 1;
    protected boolean defaultNull          = true;
    protected char    defaultSeparator     = ',';
    protected String  defaultErrorHandler  = "fatal";
    protected String  contextUri           = "./";
    public    boolean validate             = true;
    public    Long    maxCount             = null;

    protected ComplexTypeDescriptor defaultComponent = new ComplexTypeDescriptor("benerator:defaultComponent");
	
	public BeneratorContext(String contextUri) {
		this.contextUri = contextUri;
		validate = !("false".equals(System.getProperty("benerator.validate")));
		properties = new DefaultContext();
		push(new PropertiesContext(java.lang.System.getenv()));
		push(new PropertiesContext(java.lang.System.getProperties()));
		push(properties);
		push(new CaseInsensitiveContext(true));
		set("benerator", this);
		classCache = new ClassCache();
	}
	
	// interface -------------------------------------------------------------------------------------------------------
	
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
	
	@SuppressWarnings("unchecked")
    public Class forName(String className) {
		return classCache.forName(className);
	}
	
	public void importClass(String className) {
		classCache.importClass(className);
	}

	public void importPackage(String packageName) {
		classCache.importPackage(packageName);
	}

	public void importDefaults() {
		importPackage("org.databene.benerator.primitive.datetime");
		importPackage("org.databene.platform.flat");
		importPackage("org.databene.platform.csv");
		importPackage("org.databene.platform.dbunit");
		importPackage("org.databene.platform.xls");
		importPackage("org.databene.model.consumer");
		importPackage("org.databene.benerator.wrapper");
		importPackage("org.databene.commons.converter");
	}

	// properties ------------------------------------------------------------------------------------------------------
	
    public String getDefaultEncoding() {
        return defaultEncoding;
    }
    
    public void setDefaultEncoding(String defaultEncoding) {
    	System.setProperty("file.encoding", defaultEncoding);
        this.defaultEncoding = defaultEncoding;
    }
    
    public String getDefaultLineSeparator() {
		return SystemInfo.getLineSeparator();
	}

	public void setDefaultLineSeparator(String defaultLineSeparator) {
    	System.setProperty("line.separator", defaultLineSeparator);
	}

	public Locale getDefaultLocale() {
		return Locale.getDefault();
	}

	public void setDefaultLocale(Locale defaultLocale) {
		Locale.setDefault(defaultLocale);
	}

	public String getDefaultDataset() {
		return defaultDataset;
	}

	public void setDefaultDataset(String defaultDataset) {
		this.defaultDataset = defaultDataset;
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

	public void setDefaultComponentConfig(ComponentDescriptor component) {
		defaultComponent.addComponent(component);
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

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}
	
	public Long getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(Long maxCount) {
		this.maxCount = maxCount;
	}

}
