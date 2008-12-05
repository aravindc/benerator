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

package org.databene.model.data;

import java.util.Locale;

import org.databene.commons.ArrayUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.LocaleUtil;
import org.databene.commons.operation.FirstNonNullSelector;
import org.databene.model.function.Distribution;

/**
 * Describes a type.<br/><br/>
 * Created: 03.03.2008 08:37:30
 * @since 0.5.0
 * @author Volker Bergmann
 */
public abstract class TypeDescriptor extends FeatureDescriptor {

    // restriction names
    public static final String VALUES       = "values";
    public static final String VALIDATOR    = "validator";

    // config names
    public static final String GENERATOR    = "generator";
    public static final String CONVERTER    = "converter";
    public static final String PATTERN      = "pattern";
    public static final String SCRIPT       = "script";

    public static final String SOURCE       = "source";
    public static final String SELECTOR     = "selector";
    public static final String ENCODING     = "encoding";
    public static final String SEPARATOR    = "separator";
    
    public static final String CYCLIC       = "cyclic";
    public static final String PROXY        = "proxy";
    public static final String PROXY_PARAM1 = "proxy-param1";
    public static final String PROXY_PARAM2 = "proxy-param2";

    public static final String LOCALE       = "locale";
    public static final String DATASET      = "dataset";
    public static final String NESTING      = "nesting";

    public static final String DISTRIBUTION = "distribution";
    public static final String VARIATION1   = "variation1";
    public static final String VARIATION2   = "variation2";
    
    // attributes ------------------------------------------------------------------------------------------------------
    
    protected String parentName;
    protected TypeDescriptor parent;
    
    // constructors ----------------------------------------------------------------------------------------------------
    
    public TypeDescriptor(String name) {
        this(name, (String) null);
    }

    public TypeDescriptor(String name, TypeDescriptor parent) {
    	this(name, parent.getName());
    	this.parent = parent;
    }

    public TypeDescriptor(String name, String parentName) {
        super(name);
        this.parentName = parentName;
        init();
    }

	protected void init() {
		// restrictions
        addRestriction(VALIDATOR, String.class, null, new FirstNonNullSelector<String>()); 
            // For performance reasons, retain only the first non-null validator
        
        // config
        addConfig(VALUES,         String[].class,     null);
        addConfig(GENERATOR,      String.class,       null);
        addConfig(CONVERTER,      String.class,       null);
        addConfig(PATTERN,        String.class,       null);
        addConfig(SCRIPT,         String.class,       null);
        addConfig(SOURCE,         String.class,       null);
        addConfig(SELECTOR,       String.class,       null);
        addConfig(SEPARATOR,      String.class,       null);
        addConfig(ENCODING,       String.class,       null);
        addConfig(CYCLIC,         Boolean.class,      null);
        addConfig(PROXY,          Iteration.class,    null);
        addConfig(PROXY_PARAM1,   Long.class,         null);
        addConfig(PROXY_PARAM2,   Long.class,         null);
        // i18n config
        addConfig(LOCALE,         Locale.class,       null);
        addConfig(DATASET,        String.class,       null);
        addConfig(NESTING,        String.class,       null);
        // distribution
        addConfig(DISTRIBUTION,   Distribution.class, null);
        addConfig(VARIATION1,     String.class,        "1");
        addConfig(VARIATION2,     String.class,        "1");
	}
    
    // properties ------------------------------------------------------------------------------------------------------

    public String getParentName() {
        return parentName;
    }
    
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
    
    public String[] getValues() {
        return (String[]) getDetailValue(VALUES);
    }

    public void setValues(String... values) {
        setDetailValue(VALUES, values);
    }

    public void addValue(String value) {
        setValues(ArrayUtil.append(getValues(), value));
    }

    public String getValidator() {
        return (String) getDetailValue(VALIDATOR);
    }

    public void setValidator(String validator) {
        setDetailValue(VALIDATOR, validator);
    }

    public String getGenerator() {
        return (String) getDetailValue(GENERATOR);
    }

    public void setGenerator(String generatorName) {
        setDetailValue(GENERATOR, generatorName);
    }

    public String getConverter() {
        return (String) getDetailValue(CONVERTER);
    }

    public void setConverter(String converter) {
        setDetailValue(CONVERTER, converter);
    }
    
    public String getPattern() {
        return (String)getDetailValue(PATTERN);
    }

    public void setPattern(String pattern) {
        setDetailValue(PATTERN, pattern);
    }
    
    public String getScript() {
        return (String) getDetailValue(SCRIPT);
    }

    public void setScript(String script) {
        setDetailValue(SCRIPT, script);
    }

    public String getSource() {
        return (String) getDetailValue(SOURCE);
    }

    public void setSource(String source) {
        setDetailValue(SOURCE, source);
    }

    public String getSelector() {
        return (String) getDetailValue(SELECTOR);
    }

    public void setSelector(String selector) {
        setDetailValue(SELECTOR, selector);
    }

    public String getSeparator() {
        return (String) getDetailValue(SEPARATOR);
    }

    public void setSeparator(String separator) {
        setDetailValue(SEPARATOR, separator);
    }

    public String getEncoding() {
        return (String) getDetailValue(ENCODING);
    }

    public void setEncoding(String encoding) {
        setDetailValue(ENCODING, encoding);
    }

    public Boolean isCyclic() {
        return (Boolean) getDetailValue(CYCLIC);
    }

    public void setCyclic(boolean cyclic) {
        setDetailValue(CYCLIC, cyclic);
    }

    public Iteration getProxy() {
        return (Iteration) getDetailValue(PROXY);
    }

    public void setProxy(Iteration iteration) {
        setDetailValue(PROXY, iteration);
    }

    public Long getProxyParam1() {
        return (Long) getDetailValue(PROXY_PARAM1);
    }

    public void setProxyParam1(Long param) {
        setDetailValue(PROXY_PARAM1, param);
    }

    public Long getProxyParam2() {
        return (Long) getDetailValue(PROXY_PARAM2);
    }

    public void setProxyParam2(Long param) {
        setDetailValue(PROXY_PARAM2, param);
    }

    public String getDataset() {
        return (String) getDetailValue(DATASET);
    }

    public void setDataset(String dataset) {
        setDetailValue(DATASET, dataset);
    }

    public String getNesting() {
        return (String) getDetailValue(NESTING);
    }

    public void setNesting(String nesting) {
        setDetailValue(NESTING, nesting);
    }

    public Locale getLocale() {
        return (Locale)getDetailValue(LOCALE);
    }

    public void setLocaleId(String localeId) {
        setDetailValue(LOCALE, LocaleUtil.getLocale(localeId));
    }
    
    public Distribution getDistribution() {
        return (Distribution) getDetailValue(DISTRIBUTION);
    }

    public void setDistribution(Distribution distribution) {
        setDetailValue(DISTRIBUTION, distribution);
    }
    
    public String getVariation1() {
        return (String)getDetailValue(VARIATION1);
    }

    public void setVariation1(String variation1) {
        setDetailValue(VARIATION1, variation1);
    }

    public String getVariation2() {
        return (String)getDetailValue(VARIATION2);
    }

    public void setVariation2(String variation2) {
        setDetailValue(VARIATION2, variation2);
    }

    // literal construction helpers ------------------------------------------------------------------------------------
    
    public FeatureDescriptor withValues(String... values) {
    	this.setValues(values);
    	return this;
    }

    public FeatureDescriptor withSource(String source) {
        setSource(source);
        return this;
    }

/*
    public FeatureDescriptor withParent(DefaultFeatureDescriptor parent) {
        this.parent = parent;
        return this;
    }

    public FeatureDescriptor withName(String name) {
        setName(name);
        return this;
    }

    public FeatureDescriptor withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    public FeatureDescriptor withUnique(Boolean unique) {
        setDetailValue("unique", unique);
        return this;
    }

    public FeatureDescriptor withSelector(String selector) {
        setSelector(selector);
        return this;
    }

    public FeatureDescriptor withMode(Mode mode) {
        setMode(mode);
        return this;
    }

    public FeatureDescriptor withCyclic(boolean cyclic) {
        setCyclic(cyclic);
        return this;
    }

    public FeatureDescriptor withGenerator(String generatorName) {
        setGenerator(generatorName);
        return this;
    }

    public FeatureDescriptor withValidator(String validatorName) {
        setValidator(validatorName);
        return this;
    }

    public FeatureDescriptor withProxyParam1(Long param) {
        setProxyParam1(param);
        return this;
    }

    public FeatureDescriptor withProxyParam2(Long param) {
        setProxyParam2(param);
        return this;
    }
*/

    
    @Override
    public Object getDetailValue(String name) {
        Object value = super.getDetailValue(name);
        if (value == null && getParent() != null && PrimitiveType.getInstance(parentName) == null) {
            TypeDescriptor parentDescriptor = getParent();
            if (parentDescriptor == null)
                throw new ConfigurationError("Unknown type: " + parentName);
            if (parentDescriptor.supportsDetail(name)) {
                FeatureDetail<? extends Object> detail = parentDescriptor.getDetail(name);
                if (detail.isRestriction())
                    value = detail.getValue();
            }
        }
        return value;
    }

    public TypeDescriptor getParent() {
        if (parent != null)
            return parent;
        if (parentName == null)
            return null;
        return DataModel.getDefaultInstance().getTypeDescriptor(parentName);
    }
    
    public void setParent(TypeDescriptor parent) {
        this.parent = parent;
    }
}
