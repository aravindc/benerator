/*
 * (c) Copyright 2013 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.databene.benerator.BeneratorFactory;
import org.databene.benerator.factory.DefaultsProvider;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.commons.Context;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.TypeDescriptor;

/**
 * TODO Document class.<br/><br/>
 * Created: 26.01.2013 13:14:37
 * @since TODO version
 * @author Volker Bergmann
 */
public abstract class AbstractBeneratorSubContext implements BeneratorSubContext {

	protected BeneratorContext parent;
	protected String currentProductName;
	private Context localContext;
	
	public AbstractBeneratorSubContext(String productName, BeneratorContext parent) {
		this.currentProductName = productName;
		this.parent = parent;
		this.localContext = BeneratorFactory.getInstance().createGenerationContext();
	}
	
	public BeneratorContext getParent() {
		return parent;
	}

	public String getDefaultEncoding() {
		return parent.getDefaultEncoding();
	}
	
	// simple delegates ------------------------------------------------------------------------------------------------
	
	public String getDefaultLineSeparator() {
		return parent.getDefaultLineSeparator();
	}

	public Locale getDefaultLocale() {
		return parent.getDefaultLocale();
	}

	public void remove(String key) {
		parent.remove(key);
	}

	public String getDefaultDataset() {
		return parent.getDefaultDataset();
	}

	public long getDefaultPageSize() {
		return parent.getDefaultPageSize();
	}

	public String getDefaultScript() {
		return parent.getDefaultScript();
	}

	public boolean isDefaultNull() {
		return parent.isDefaultNull();
	}

	public char getDefaultSeparator() {
		return parent.getDefaultSeparator();
	}

	public String getDefaultErrorHandler() {
		return parent.getDefaultErrorHandler();
	}

	public String getContextUri() {
		return parent.getContextUri();
	}

	public boolean isValidate() {
		return parent.isValidate();
	}

	public Long getMaxCount() {
		return parent.getMaxCount();
	}

	public GeneratorFactory getGeneratorFactory() {
		return parent.getGeneratorFactory();
	}

	public void setGeneratorFactory(GeneratorFactory generatorFactory) {
		parent.setGeneratorFactory(generatorFactory);
	}

	public Object getGlobal(String name) {
		return parent.getGlobal(name);
	}

	public DefaultsProvider getDefaultsProvider() {
		return parent.getDefaultsProvider();
	}

	public void setDefaultsProvider(DefaultsProvider defaultsProvider) {
		parent.setDefaultsProvider(defaultsProvider);
	}

	public Class<?> forName(String className) {
		return parent.forName(className);
	}

	public ExecutorService getExecutorService() {
		return parent.getExecutorService();
	}

	public void setGlobal(String name, Object value) {
		parent.setGlobal(name, value);
	}

	public String resolveRelativeUri(String relativeUri) {
		return parent.resolveRelativeUri(relativeUri);
	}

	public void close() {
		parent.close();
	}

	public void importClass(String className) {
		parent.importClass(className);
	}

	public void importPackage(String packageName) {
		parent.importPackage(packageName);
	}

	public void importDefaults() {
		parent.importDefaults();
	}

	public void setDefaultEncoding(String defaultEncoding) {
		parent.setDefaultEncoding(defaultEncoding);
	}

	public void setDefaultLineSeparator(String defaultLineSeparator) {
		parent.setDefaultLineSeparator(defaultLineSeparator);
	}

	public void setDefaultLocale(Locale defaultLocale) {
		parent.setDefaultLocale(defaultLocale);
	}

	public void setDefaultDataset(String defaultDataset) {
		parent.setDefaultDataset(defaultDataset);
	}

	public void setDefaultPageSize(long defaultPageSize) {
		parent.setDefaultPageSize(defaultPageSize);
	}

	public void setDefaultScript(String defaultScript) {
		parent.setDefaultScript(defaultScript);
	}

	public void setDefaultNull(boolean defaultNull) {
		parent.setDefaultNull(defaultNull);
	}

	public void setDefaultSeparator(char defaultSeparator) {
		parent.setDefaultSeparator(defaultSeparator);
	}

	public ComponentDescriptor getDefaultComponentConfig(String name) {
		return parent.getDefaultComponentConfig(name);
	}

	public void setDefaultComponentConfig(ComponentDescriptor component) {
		parent.setDefaultComponentConfig(component);
	}

	public void setDefaultErrorHandler(String defaultErrorHandler) {
		parent.setDefaultErrorHandler(defaultErrorHandler);
	}

	public void setContextUri(String contextUri) {
		parent.setContextUri(contextUri);
	}

	public void setValidate(boolean validate) {
		parent.setValidate(validate);
	}

	public void setMaxCount(Long maxCount) {
		parent.setMaxCount(maxCount);
	}

	public boolean isDefaultOneToOne() {
		return parent.isDefaultOneToOne();
	}

	public void setDefaultOneToOne(boolean defaultOneToOne) {
		parent.setDefaultOneToOne(defaultOneToOne);
	}

	public boolean isAcceptUnknownSimpleTypes() {
		return parent.isAcceptUnknownSimpleTypes();
	}

	public void setAcceptUnknownSimpleTypes(boolean acceptUnknownSimpleTypes) {
		parent.setAcceptUnknownSimpleTypes(acceptUnknownSimpleTypes);
	}

	public boolean isDefaultImports() {
		return parent.isDefaultImports();
	}

	public void setDefaultImports(boolean defaultImports) {
		parent.setDefaultImports(defaultImports);
	}

	public DataModel getDataModel() {
		return parent.getDataModel();
	}

	public void setDataModel(DataModel dataModel) {
		parent.setDataModel(dataModel);
	}

	public DescriptorProvider getLocalDescriptorProvider() {
		return parent.getLocalDescriptorProvider();
	}
	
	public void addLocalType(TypeDescriptor type) {
		parent.addLocalType(type);
	}
	
	
	
	// Functional interface --------------------------------------------------------------------------------------------

	public boolean hasProductNameInScope(String currentProductName) {
		return (this.currentProductName != null && this.currentProductName.equals(currentProductName))
			|| (parent != null && parent.hasProductNameInScope(currentProductName));
	}
	
	public boolean contains(String key) {
		return key != null && (key.equalsIgnoreCase(currentProductName) || "this".equalsIgnoreCase(key) || 
				localContext.contains(key) || parent.contains(key));
	}

	public Object get(String key) {
		if (key == null)
			return null;
		else if (localContext.contains(key))
			return localContext.get(key);
		else if (key.equalsIgnoreCase(currentProductName) || "this".equalsIgnoreCase(key))
			return getCurrentProduct().unwrap();
		else
			return parent.get(key);
	}
	
	public void set(String key, Object value) {
		localContext.set(key, value);
	}
	
	public Set<String> keySet() {
        Set<String> keySet = new HashSet<String>(parent.keySet());
        keySet.addAll(localContext.keySet());
        return keySet;
	}

	public Set<Entry<String, Object>> entrySet() {
		Set<Entry<String, Object>> entrySet = new HashSet<Entry<String,Object>>(parent.entrySet());
		entrySet.addAll(localContext.entrySet());
		return entrySet;
	}

	
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + currentProductName + ")";
	}
	
}
