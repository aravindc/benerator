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
 * Abstract implementation of the {@link BeneratorSubContext} interface.<br/><br/>
 * Created: 26.01.2013 13:14:37
 * @since 0.8.0
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
	
	@Override
	public BeneratorContext getParent() {
		return parent;
	}

	@Override
	public String getDefaultEncoding() {
		return parent.getDefaultEncoding();
	}
	
	// simple delegates ------------------------------------------------------------------------------------------------
	
	@Override
	public String getDefaultLineSeparator() {
		return parent.getDefaultLineSeparator();
	}

	@Override
	public Locale getDefaultLocale() {
		return parent.getDefaultLocale();
	}

	@Override
	public void remove(String key) {
		parent.remove(key);
	}

	@Override
	public String getDefaultDataset() {
		return parent.getDefaultDataset();
	}

	@Override
	public long getDefaultPageSize() {
		return parent.getDefaultPageSize();
	}

	@Override
	public String getDefaultScript() {
		return parent.getDefaultScript();
	}

	@Override
	public boolean isDefaultNull() {
		return parent.isDefaultNull();
	}

	@Override
	public char getDefaultSeparator() {
		return parent.getDefaultSeparator();
	}

	@Override
	public String getDefaultErrorHandler() {
		return parent.getDefaultErrorHandler();
	}

	@Override
	public String getContextUri() {
		return parent.getContextUri();
	}

	@Override
	public boolean isValidate() {
		return parent.isValidate();
	}

	@Override
	public Long getMaxCount() {
		return parent.getMaxCount();
	}

	@Override
	public GeneratorFactory getGeneratorFactory() {
		return parent.getGeneratorFactory();
	}

	@Override
	public void setGeneratorFactory(GeneratorFactory generatorFactory) {
		parent.setGeneratorFactory(generatorFactory);
	}

	@Override
	public Object getGlobal(String name) {
		return parent.getGlobal(name);
	}

	@Override
	public DefaultsProvider getDefaultsProvider() {
		return parent.getDefaultsProvider();
	}

	@Override
	public void setDefaultsProvider(DefaultsProvider defaultsProvider) {
		parent.setDefaultsProvider(defaultsProvider);
	}

	@Override
	public Class<?> forName(String className) {
		return parent.forName(className);
	}

	@Override
	public ExecutorService getExecutorService() {
		return parent.getExecutorService();
	}

	@Override
	public void setGlobal(String name, Object value) {
		parent.setGlobal(name, value);
	}

	@Override
	public String resolveRelativeUri(String relativeUri) {
		return parent.resolveRelativeUri(relativeUri);
	}

	@Override
	public void close() {
		parent.close();
	}

	@Override
	public void importClass(String className) {
		parent.importClass(className);
	}

	@Override
	public void importPackage(String packageName) {
		parent.importPackage(packageName);
	}

	@Override
	public void importDefaults() {
		parent.importDefaults();
	}

	@Override
	public void setDefaultEncoding(String defaultEncoding) {
		parent.setDefaultEncoding(defaultEncoding);
	}

	@Override
	public void setDefaultLineSeparator(String defaultLineSeparator) {
		parent.setDefaultLineSeparator(defaultLineSeparator);
	}

	@Override
	public void setDefaultLocale(Locale defaultLocale) {
		parent.setDefaultLocale(defaultLocale);
	}

	@Override
	public void setDefaultDataset(String defaultDataset) {
		parent.setDefaultDataset(defaultDataset);
	}

	@Override
	public void setDefaultPageSize(long defaultPageSize) {
		parent.setDefaultPageSize(defaultPageSize);
	}

	@Override
	public void setDefaultScript(String defaultScript) {
		parent.setDefaultScript(defaultScript);
	}

	@Override
	public void setDefaultNull(boolean defaultNull) {
		parent.setDefaultNull(defaultNull);
	}

	@Override
	public void setDefaultSeparator(char defaultSeparator) {
		parent.setDefaultSeparator(defaultSeparator);
	}

	@Override
	public ComponentDescriptor getDefaultComponentConfig(String name) {
		return parent.getDefaultComponentConfig(name);
	}

	@Override
	public void setDefaultComponentConfig(ComponentDescriptor component) {
		parent.setDefaultComponentConfig(component);
	}

	@Override
	public void setDefaultErrorHandler(String defaultErrorHandler) {
		parent.setDefaultErrorHandler(defaultErrorHandler);
	}

	@Override
	public void setContextUri(String contextUri) {
		parent.setContextUri(contextUri);
	}

	@Override
	public void setValidate(boolean validate) {
		parent.setValidate(validate);
	}

	@Override
	public void setMaxCount(Long maxCount) {
		parent.setMaxCount(maxCount);
	}

	@Override
	public boolean isDefaultOneToOne() {
		return parent.isDefaultOneToOne();
	}

	@Override
	public void setDefaultOneToOne(boolean defaultOneToOne) {
		parent.setDefaultOneToOne(defaultOneToOne);
	}

	@Override
	public boolean isAcceptUnknownSimpleTypes() {
		return parent.isAcceptUnknownSimpleTypes();
	}

	@Override
	public void setAcceptUnknownSimpleTypes(boolean acceptUnknownSimpleTypes) {
		parent.setAcceptUnknownSimpleTypes(acceptUnknownSimpleTypes);
	}

	@Override
	public boolean isDefaultImports() {
		return parent.isDefaultImports();
	}

	@Override
	public void setDefaultImports(boolean defaultImports) {
		parent.setDefaultImports(defaultImports);
	}

	@Override
	public DataModel getDataModel() {
		return parent.getDataModel();
	}

	@Override
	public void setDataModel(DataModel dataModel) {
		parent.setDataModel(dataModel);
	}

	@Override
	public DescriptorProvider getLocalDescriptorProvider() {
		return parent.getLocalDescriptorProvider();
	}
	
	@Override
	public void addLocalType(TypeDescriptor type) {
		parent.addLocalType(type);
	}
	
	
	
	// Functional interface --------------------------------------------------------------------------------------------

	@Override
	public boolean hasProductNameInScope(String currentProductName) {
		return (this.currentProductName != null && this.currentProductName.equals(currentProductName))
			|| (parent != null && parent.hasProductNameInScope(currentProductName));
	}
	
	@Override
	public boolean contains(String key) {
		return key != null && (key.equalsIgnoreCase(currentProductName) || "this".equalsIgnoreCase(key) || 
				localContext.contains(key) || parent.contains(key));
	}

	@Override
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
	
	@Override
	public void set(String key, Object value) {
		localContext.set(key, value);
	}
	
	@Override
	public Set<String> keySet() {
        Set<String> keySet = new HashSet<String>(parent.keySet());
        keySet.addAll(localContext.keySet());
        return keySet;
	}

	@Override
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
