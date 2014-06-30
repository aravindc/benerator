/*
 * (c) Copyright 2011-2013 by Volker Bergmann. All rights reserved.
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

import java.io.File;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.databene.benerator.BeneratorFactory;
import org.databene.benerator.engine.parser.String2DistributionConverter;
import org.databene.benerator.factory.DefaultsProvider;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.factory.StochasticGeneratorFactory;
import org.databene.benerator.script.BeneratorScriptFactory;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.IOUtil;
import org.databene.commons.Level;
import org.databene.commons.LocaleUtil;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.SystemInfo;
import org.databene.commons.bean.ClassCache;
import org.databene.commons.context.ContextStack;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.context.SimpleContextStack;
import org.databene.commons.converter.ConverterManager;
import org.databene.commons.file.FileSuffixFilter;
import org.databene.domain.address.Country;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.DefaultDescriptorProvider;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.TypeDescriptor;
import org.databene.script.ScriptUtil;

/**
 * Default implementation of {@link BeneratorContext}.<br/><br/>
 * Created: 02.09.2011 14:36:58
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class DefaultBeneratorContext implements BeneratorContext {
	
	// constants -------------------------------------------------------------------------------------------------------
	
    public static final String CELL_SEPARATOR_SYSPROP = "cell.separator";
 	public static final char DEFAULT_CELL_SEPARATOR = ',';
 	
 	
 	
 	// attributes -------------------------------------------------------------------------------------------------------
 	
	private GeneratorFactory generatorFactory;
    private DefaultContext settings;
	private ClassCache classCache;
	private ContextStack contextStack;
	
    protected String  defaultEncoding      = SystemInfo.getFileEncoding();
    protected String  defaultDataset       = LocaleUtil.getDefaultCountryCode();
    protected long    defaultPageSize      = 1;
    protected boolean defaultNull          = true;
    protected String  contextUri           = "./";
    public    Long    maxCount             = null;
    public    boolean defaultOneToOne      = false;
    public    boolean defaultImports       = true;
    public    boolean acceptUnknownSimpleTypes = false;


    protected ComplexTypeDescriptor defaultComponent;
    protected ExecutorService executorService;

	private ProductWrapper<?> currentProduct;

	private DataModel dataModel;
	private DefaultDescriptorProvider localDescriptorProvider;
	
	protected String currentProductName;
	
	
	
	// construction ----------------------------------------------------------------------------------------------------
	
    static {
    	ScriptUtil.addFactory("ben", new BeneratorScriptFactory());
    	ScriptUtil.setDefaultScriptEngine("ben");
    	ConverterManager.getInstance().registerConverterClass(String2DistributionConverter.class); // TODO is this required any longer?
    }
    
	public DefaultBeneratorContext() {
		this(".");
	}
	
	public DefaultBeneratorContext(String contextUri) {
		if (contextUri == null)
			throw new ConfigurationError("No context URI specified");
		this.contextUri = contextUri;
		this.executorService = createExecutorService();
		this.dataModel = new DataModel();
		this.localDescriptorProvider = new DefaultDescriptorProvider("ctx", dataModel);
		this.defaultComponent = new ComplexTypeDescriptor("benerator:defaultComponent", localDescriptorProvider);
		this.generatorFactory = new StochasticGeneratorFactory();
		settings = new DefaultContext();
		this.contextStack = createContextStack(
			new DefaultContext(java.lang.System.getenv()),
			new DefaultContext(java.lang.System.getProperties()),
			settings,
			BeneratorFactory.getInstance().createGenerationContext()
		);
		set("context", this);
		if (IOUtil.isFileUri(contextUri))
			addLibFolderToClassLoader();
		classCache = new ClassCache();
	}
	
	
	
	// properties ------------------------------------------------------------------------------------------------------
	
	@Override
	public GeneratorFactory getGeneratorFactory() {
		return generatorFactory;
	}

	@Override
	public void setGeneratorFactory(GeneratorFactory generatorFactory) {
		this.generatorFactory = generatorFactory;
	}
	
	@Override
	public DescriptorProvider getLocalDescriptorProvider() {
		return localDescriptorProvider;
	}
	
	@Override
	public void setDefaultsProvider(DefaultsProvider defaultsProvider) {
		this.generatorFactory.setDefaultsProvider(defaultsProvider);
	}
	
    @Override
	public String getDefaultEncoding() {
        return defaultEncoding;
    }
    
    @Override
	public void setDefaultEncoding(String defaultEncoding) {
    	SystemInfo.setFileEncoding(defaultEncoding);
        this.defaultEncoding = defaultEncoding;
    }
    
    @Override
	public String getDefaultLineSeparator() {
		return SystemInfo.getLineSeparator();
	}

	@Override
	public void setDefaultLineSeparator(String defaultLineSeparator) {
    	SystemInfo.setLineSeparator(defaultLineSeparator);
	}

	@Override
	public Locale getDefaultLocale() {
		return Locale.getDefault();
	}

	@Override
	public void setDefaultLocale(Locale defaultLocale) {
		Locale.setDefault(defaultLocale);
	}

	@Override
	public String getDefaultDataset() {
		return defaultDataset;
	}

	@Override
	public void setDefaultDataset(String defaultDataset) {
		this.defaultDataset = defaultDataset;
		Country country = Country.getInstance(defaultDataset, false);
		if (country != null)
			Country.setDefault(country);
	}

	@Override
	public long getDefaultPageSize() {
        return defaultPageSize;
    }
    
    @Override
	public void setDefaultPageSize(long defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }
    
    @Override
	public String getDefaultScript() {
        return ScriptUtil.getDefaultScriptEngine();
    }
    
    @Override
	public void setDefaultScript(String defaultScript) {
        ScriptUtil.setDefaultScriptEngine(defaultScript);
    }
    
    @Override
	public boolean isDefaultNull() {
        return defaultNull;
    }
    
    @Override
	public void setDefaultNull(boolean defaultNull) {
        this.defaultNull = defaultNull;
    }
    
	@Override
	public char getDefaultSeparator() {
		return getDefaultCellSeparator();
	}

	@Override
	public void setDefaultSeparator(char defaultSeparator) {
		System.setProperty(CELL_SEPARATOR_SYSPROP, String.valueOf(defaultSeparator));
	}

	@Override
	public ComponentDescriptor getDefaultComponentConfig(String name) {
		return defaultComponent.getComponent(name);
	}

	@Override
	public void setDefaultComponentConfig(ComponentDescriptor component) {
		defaultComponent.addComponent(component);
	}

	@Override
	public String getDefaultErrorHandler() {
		return ErrorHandler.getDefaultLevel().name();
	}

	@Override
	public void setDefaultErrorHandler(String defaultErrorHandler) {
		ErrorHandler.setDefaultLevel(Level.valueOf(defaultErrorHandler));
	}

	@Override
	public String getContextUri() {
		return contextUri;
	}

	@Override
	public void setContextUri(String contextUri) {
		this.contextUri = contextUri;
	}

	@Override
	public boolean isValidate() {
		return BeneratorOpts.isValidating();
	}

	@Override
	public void setValidate(boolean validate) {
		BeneratorOpts.setValidating(validate);
	}
	
	@Override
	public Long getMaxCount() {
		return maxCount;
	}

	@Override
	public void setMaxCount(Long maxCount) {
		this.maxCount = maxCount;
	}
	
	@Override
	public ExecutorService getExecutorService() {
    	return executorService;
    }

	@Override
	public boolean isDefaultOneToOne() {
    	return defaultOneToOne;
    }

	@Override
	public void setDefaultOneToOne(boolean defaultOneToOne) {
    	this.defaultOneToOne = defaultOneToOne;
    }

	@Override
	public boolean isAcceptUnknownSimpleTypes() {
    	return acceptUnknownSimpleTypes;
    }

	@Override
	public void setAcceptUnknownSimpleTypes(boolean acceptUnknownSimpleTypes) {
    	this.acceptUnknownSimpleTypes = acceptUnknownSimpleTypes;
    	dataModel.setAcceptUnknownPrimitives(acceptUnknownSimpleTypes);
    }
    
	public static char getDefaultCellSeparator() {
		String tmp = System.getProperty(CELL_SEPARATOR_SYSPROP);
		if (tmp == null)
			return DEFAULT_CELL_SEPARATOR;
		if (tmp.length() != 1)
			throw new ConfigurationError("Cell separator has illegal length: '" + tmp + "'");
		return tmp.charAt(0);
	}

	@Override
	public DefaultsProvider getDefaultsProvider() {
		return getGeneratorFactory().getDefaultsProvider();
	}

	@Override
	public void setDefaultImports(boolean defaultImports) {
		this.defaultImports = defaultImports;
	}
	
	@Override
	public boolean isDefaultImports() {
		return defaultImports;
	}

	@Override
	public ProductWrapper<?> getCurrentProduct() {
		return currentProduct;
	}

	@Override
	public void setCurrentProduct(ProductWrapper<?> currentProduct) {
		this.currentProduct = currentProduct;
	}

	@Override
	public DataModel getDataModel() {
		return dataModel;
	}

	@Override
	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}
	
	
	
	// Context interface -----------------------------------------------------------------------------------------------
	
	@Override
	public Object get(String key) {
		if (contextStack.contains(key))
			return contextStack.get(key);
		else if (key.equalsIgnoreCase(currentProductName) || "this".equalsIgnoreCase(key))
			return currentProduct.unwrap();
		else
			return null;
	}

	@Override
	public void set(String key, Object value) {
		contextStack.set(key, value);
	}

	@Override
	public void remove(String key) {
		contextStack.remove(key);
	}

	@Override
	public Set<String> keySet() {
		return contextStack.keySet();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return contextStack.entrySet();
	}

	@Override
	public boolean contains(String key) {
		return (key != null && (key.equalsIgnoreCase(currentProductName) || "this".equalsIgnoreCase(key) || contextStack.contains(key)));
	}

	
	
	// class-loading interface -----------------------------------------------------------------------------------------
	
    @Override
	public Class<?> forName(String className) {
		return classCache.forName(className);
	}
	
	@Override
	public void importClass(String className) {
		classCache.importClass(className);
	}

	@Override
	public void importPackage(String packageName) {
		classCache.importPackage(packageName);
	}

	@Override
	public void importDefaults() {
		// import frequently used Benerator packages
		importPackage("org.databene.benerator.consumer");
		importPackage("org.databene.benerator.primitive");
		importPackage("org.databene.benerator.primitive.datetime");
		importPackage("org.databene.benerator.distribution.sequence");
		importPackage("org.databene.benerator.distribution.function");
		importPackage("org.databene.benerator.distribution.cumulative");
		importPackage("org.databene.benerator.sample");
		// import ConsoleExporter and LoggingConsumer
		importPackage("org.databene.model.consumer");
		// import formats, converters and validators from commons
		importPackage("org.databene.commons.converter");
		importPackage("org.databene.commons.format");
		importPackage("org.databene.commons.validator");
		// import standard platforms
		importPackage("org.databene.platform.fixedwidth");
		importPackage("org.databene.platform.csv");
		importPackage("org.databene.platform.dbunit");
		importPackage("org.databene.platform.xls");
		importPackage("org.databene.platform.template");
	}
	
	
	
	// other interface methods -----------------------------------------------------------------------------------------

	@Override
	public void setGlobal(String name, Object value) {
		settings.set(name, value);
	}
	
	@Override
	public Object getGlobal(String name) {
		return settings.get(name);
	}
	
	@Override
	public void close() {
		executorService.shutdownNow();
	}
	
	@Override
	public void addLocalType(TypeDescriptor type) {
		localDescriptorProvider.addTypeDescriptor(type);
	}

	@Override
	public BeneratorContext createSubContext(String productName) {
		return new DefaultBeneratorSubContext(productName, this);
	}
	
	public void setCurrentProduct(ProductWrapper<?> currentProduct, String currentProductName) {
		this.currentProductName = currentProductName;
		setCurrentProduct(currentProduct);
	}
	
	@Override
	public boolean hasProductNameInScope(String productName) {
		return (NullSafeComparator.equals(this.currentProductName, productName));
	}
	
	@Override
	public String resolveRelativeUri(String relativeUri) {
	    return IOUtil.resolveRelativeUri(relativeUri, contextUri);
    }

	
	
	// non-public helper methods ---------------------------------------------------------------------------------------

	private void addLibFolderToClassLoader() {
		File libFolder = new File(contextUri, "lib");
		if (libFolder.exists()) {
			Thread.currentThread().setContextClassLoader(BeanUtil.createDirectoryClassLoader(libFolder));
			for (File jarFile : libFolder.listFiles(new FileSuffixFilter("jar", false))) {
				ClassLoader classLoader = BeanUtil.createJarClassLoader(jarFile);
				Thread.currentThread().setContextClassLoader(classLoader);
			}
		}
	}

	protected ExecutorService createExecutorService() {
		return Executors.newSingleThreadExecutor();
	}

	protected ContextStack createContextStack(Context... contexts) {
		return new SimpleContextStack(contexts);
	}
	
	
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return getClass() + "[" + currentProductName + "]";
	}
	
}
