/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.LocaleUtil;
import org.databene.commons.operation.FirstNonNullSelector;

/**
 * Describes a type.<br/><br/>
 * Created: 03.03.2008 08:37:30
 * @since 0.5.0
 * @author Volker Bergmann
 */
public abstract class TypeDescriptor extends FeatureDescriptor {

    // constraint names
    public static final String VALIDATOR    = "validator";
    public static final String FILTER       = "filter";
    public static final String CONDITION    = "condition";

    // config names
    public static final String GENERATOR    = "generator";
    public static final String CONVERTER    = "converter";
    public static final String PATTERN      = "pattern";
    public static final String SCRIPT       = "script";

    public static final String SOURCE       = "source";
    public static final String FORMAT       = "format";
    public static final String ROW_BASED    = "rowBased";
    public static final String SEGMENT      = "segment";
    public static final String OFFSET       = "offset";
    public static final String SELECTOR     = "selector";
    public static final String SUB_SELECTOR = "subSelector";
    public static final String ENCODING     = "encoding";
    public static final String SEPARATOR    = "separator";
    public static final String EMPTY_MARKER = "emptyMarker";
    public static final String NULL_MARKER  = "nullMarker";
    
    public static final String CYCLIC       = "cyclic";
    public static final String SCOPE        = "scope";

    public static final String LOCALE       = "locale";
    public static final String DATASET      = "dataset";
    public static final String NESTING      = "nesting";

    public static final String DISTRIBUTION = "distribution";
    
    // attributes ------------------------------------------------------------------------------------------------------
    
    protected String parentName;
    protected TypeDescriptor parent;
    
    // constructors ----------------------------------------------------------------------------------------------------
    
    public TypeDescriptor(String name, DescriptorProvider provider, TypeDescriptor parent) {
    	this(name, provider, (parent != null ? parent.getName() : null));
    	this.parent = parent;
    }

    public TypeDescriptor(String name, DescriptorProvider provider, String parentName) {
        super(name, provider);
        this.parentName = parentName;
        init();
    }

	protected void init() {
		// constraints
        addConstraint(VALIDATOR, String.class, new FirstNonNullSelector<String>()); 
        addConstraint(FILTER, String.class, new FirstNonNullSelector<String>()); 
        addConstraint(CONDITION, String.class, new FirstNonNullSelector<String>()); 
        
        // config
        addConfig(GENERATOR,      String.class);
        addConfig(CONVERTER,      String.class);
        addConfig(PATTERN,        String.class);
        addConfig(SCRIPT,         String.class);

        addConfig(SOURCE,         String.class);
        addConfig(FORMAT,         Format.class);
        addConfig(ROW_BASED,      Boolean.class);
        addConfig(SEGMENT,        String.class);
        addConfig(OFFSET,         Integer.class);
        addConfig(SELECTOR,       String.class);
        addConfig(SUB_SELECTOR,   String.class);
        addConfig(SEPARATOR,      String.class);
        addConfig(EMPTY_MARKER,   String.class);
        addConfig(NULL_MARKER,    String.class);
        addConfig(ENCODING,       String.class);
        addConfig(SCOPE,          String.class);
        addConfig(CYCLIC,         Boolean.class);
        // i18n config
        addConfig(LOCALE,         Locale.class);
        addConfig(DATASET,        String.class);
        addConfig(NESTING,        String.class);
        // distribution
        addConfig(DISTRIBUTION,   String.class);
	}
    
    // properties ------------------------------------------------------------------------------------------------------

    public String getParentName() {
        return parentName;
    }
    
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
    
    public Boolean isRowBased() {
        return (Boolean) getDetailValue(ROW_BASED);
    }
    
    public void setRowBased(Boolean rowBased) {
        setDetailValue(ROW_BASED, rowBased);
    }
    
    public String getValidator() {
        return (String) getDetailValue(VALIDATOR);
    }

    public void setValidator(String filter) {
        setDetailValue(VALIDATOR, filter);
    }

    public String getFilter() {
        return (String) getDetailValue(FILTER);
    }

    public void setFilter(String filter) {
        setDetailValue(FILTER, filter);
    }

    public String getCondition() {
        return (String) getDetailValue(CONDITION);
    }

    public void setCondition(String condition) {
        setDetailValue(CONDITION, condition);
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

    public Format getFormat() {
        return (Format) getDetailValue(FORMAT);
    }

    public void setFormat(Format format) {
        setDetailValue(FORMAT, format);
    }

    public String getSegment() {
        return (String) getDetailValue(SEGMENT);
    }

    public void setSegment(String segment) {
        setDetailValue(SEGMENT, segment);
    }

    public Integer getOffset() {
        return (Integer) getDetailValue(OFFSET);
    }

    public void setOffset(Integer offset) {
        setDetailValue(OFFSET, offset);
    }

    public String getSelector() {
        return (String) getDetailValue(SELECTOR);
    }

    public void setSelector(String selector) {
        setDetailValue(SELECTOR, selector);
    }

    public String getSubSelector() {
        return (String) getDetailValue(SUB_SELECTOR);
    }

    public void setSubSelector(String selector) {
        setDetailValue(SUB_SELECTOR, selector);
    }

    public String getSeparator() {
        return (String) getDetailValue(SEPARATOR);
    }

    public void setSeparator(String separator) {
        setDetailValue(SEPARATOR, separator);
    }

    public String getEmptyMarker() {
        return (String) getDetailValue(EMPTY_MARKER);
    }

    public void setEmptyMarker(String emptyMarker) {
        setDetailValue(EMPTY_MARKER, emptyMarker);
    }

    public String getNullMarker() {
        return (String) getDetailValue(NULL_MARKER);
    }

    public void setNullMarker(String nullMarker) {
        setDetailValue(NULL_MARKER, nullMarker);
    }

    public String getEncoding() {
        return (String) getDetailValue(ENCODING);
    }

    public void setEncoding(String encoding) {
        setDetailValue(ENCODING, encoding);
    }

    public String getScope() {
        return (String) getDetailValue(SCOPE);
    }

    public void setScope(String scope) {
        setDetailValue(SCOPE, scope);
    }

    public Boolean isCyclic() {
        return (Boolean) getDetailValue(CYCLIC);
    }

    public void setCyclic(boolean cyclic) {
        setDetailValue(CYCLIC, cyclic);
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
    
    public String getDistribution() {
        return (String) getDetailValue(DISTRIBUTION);
    }

    public void setDistribution(String distribution) {
        setDetailValue(DISTRIBUTION, distribution);
    }
    
    // literal construction helpers ------------------------------------------------------------------------------------
    
    public TypeDescriptor withSource(String source) {
        setSource(source);
        return this;
    }

    public TypeDescriptor withSeparator(String separator) {
        setSeparator(separator);
        return this;
    }

    public TypeDescriptor withGenerator(String generator) {
        setGenerator(generator);
        return this;
    }

    // generic functionality -------------------------------------------------------------------------------------------

    public TypeDescriptor getParent() {
        if (parent != null)
            return parent;
        if (parentName == null)
            return null;
        // TODO v0.7.1 the following is a workaround for name conflicts with types of same name in different name spaces, e.g. xs:string <-> ben.string
        TypeDescriptor candidate = getDataModel().getTypeDescriptor(parentName);
        if (candidate != this)
        	parent = candidate;
		return parent;
    }
    
    public void setParent(TypeDescriptor parent) {
        this.parent = parent;
    }
    
}
