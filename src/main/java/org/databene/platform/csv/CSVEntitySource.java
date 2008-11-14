package org.databene.platform.csv;

import java.io.FileNotFoundException;

import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.NoOpConverter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;

public class CSVEntitySource implements EntitySource {
	
    private String uri;
    private char   separator;
    private String encoding;
    private Converter<String, String> preprocessor;

    private ComplexTypeDescriptor entityDescriptor; // TODO is this used/useful?

    // constructors ----------------------------------------------------------------------------------------------------

    public CSVEntitySource() {
        this(null, null);
    }

    public CSVEntitySource(String uri, String entityName) {
        this(uri, entityName, ',', SystemInfo.fileEncoding());
    }

    public CSVEntitySource(String uri, String entityName, char separator) {
        this(uri, entityName, separator, SystemInfo.fileEncoding());
    }

    public CSVEntitySource(String uri, String entityName, char separator, String encoding) {
        this(uri, new ComplexTypeDescriptor(entityName), new NoOpConverter<String>(), separator, encoding);
    }

    public CSVEntitySource(String uri, String entityName, Converter<String, String> preprocessor, 
    		char separator, String encoding) {
        this(uri, new ComplexTypeDescriptor(entityName), preprocessor, separator, encoding);
    }

    public CSVEntitySource(String uri, ComplexTypeDescriptor descriptor, Converter<String, String> preprocessor, 
    		char separator, String encoding) {
        this.uri = uri;
        this.separator = separator;
        this.encoding = encoding;
        this.entityDescriptor = descriptor;
        this.preprocessor = preprocessor;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEntityName() {
        return entityDescriptor.getName();
    }

    public void setEntityName(String entityName) {
        this.entityDescriptor = new ComplexTypeDescriptor(entityName);
    }

    // EntityIterable interface ----------------------------------------------------------------------------------------

    public Class<Entity> getType() {
        return Entity.class;
    }

    public HeavyweightIterator<Entity> iterator() {
        try {
			return new CSVEntityIterator(uri, entityDescriptor, preprocessor, separator, encoding);
		} catch (FileNotFoundException e) {
			throw new ConfigurationError("Cannot create iterator. ", e);
		}
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + "[uri=" + uri + ", encoding=" + encoding + ", separator=" + separator +
                ", entityName=" + entityDescriptor.getName() + "]";
    }
    
}
