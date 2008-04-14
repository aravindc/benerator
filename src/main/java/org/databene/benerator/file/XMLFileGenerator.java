package org.databene.benerator.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.databene.benerator.Generator;
import org.databene.benerator.factory.SimpleGenerationSetup;
import org.databene.benerator.factory.TypeGeneratorFactory;
import org.databene.benerator.primitive.IncrementGenerator;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.IOUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.context.ContextStack;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.context.PropertiesContext;
import org.databene.commons.converter.MessageConverter;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.ModelParser;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.model.data.TypeDescriptor;
import org.databene.platform.xml.XMLEntityExporter;
import org.databene.platform.xml.XMLSchemaDescriptorProvider;

public class XMLFileGenerator extends LightweightGenerator<File> {
	
    private String encoding = SystemInfo.fileEncoding();
    private String root;
    private String filenamePattern;
    private Generator<String> fileNameGenerator;
    private Generator<? extends Object> contentGenerator;
    private DataModel dataModel = DataModel.getDefaultInstance();
    
    public XMLFileGenerator(String schemaUri, String root, String filenamePattern, String... propertiesFiles) throws IOException {
        super(File.class);
        dataModel.clear();
        this.root = root;
        this.filenamePattern = filenamePattern;
        
        // create context
        // TODO v0.5.2 simplify & encapsulate
        ContextStack context = new ContextStack();
        context.push(new PropertiesContext(java.lang.System.getenv()));
        context.push(new PropertiesContext(java.lang.System.getProperties()));
        context.push(new DefaultContext());
        context.set("benerator", new SimpleGenerationSetup());

        // parse schema
        XMLSchemaDescriptorProvider xsdProvider = new XMLSchemaDescriptorProvider(schemaUri, context);
		dataModel.addDescriptorProvider(xsdProvider);
        // set up file name generator
        this.fileNameGenerator = new ConvertingGenerator<Long, String>(
                new IncrementGenerator(), 
                new MessageConverter<Long>(filenamePattern, Locale.US));
        // parse properties files
        ModelParser parser = new ModelParser();
        for (String propertiesFile : propertiesFiles)
            parser.importProperties(propertiesFile, context);

        // set up content generator
        TypeDescriptor rootDescriptor = DataModel.getDefaultInstance().getTypeDescriptor(root);
        if (rootDescriptor == null)
            throw new ConfigurationError("Type '" + root + "' not found in schema: " + schemaUri);
        contentGenerator = TypeGeneratorFactory.createTypeGenerator(
                rootDescriptor, false, context, new SimpleGenerationSetup());
    }

    @Override
    public boolean available() {
        return super.available() && contentGenerator.available();
    }
    
    @Override
    public void validate() {
        super.validate();
        contentGenerator.validate();
    }
    
    public File generate() {
        Object content = contentGenerator.generate();
        return persistContent(content);
    }

    private File persistContent(Object content) {
        File file = new File(fileNameGenerator.generate());
        if (content instanceof Entity)
            persistRootEntity((Entity) content, file);
        else
            persistRootObject(content, file);
        return file;
    }

    private void persistRootEntity(Entity entity, File file) {
        //entity.setComponentValue("xmlns", "http://databene.org/shop-0.5.1.xsd");
        entity.setComponent("elementFormDefault", "unqualified");
        XMLEntityExporter exporter = null;
        try {
            exporter = new XMLEntityExporter(file.getAbsolutePath(), encoding);
            process(entity, exporter);
        } finally {
            if (exporter != null)
                exporter.close();
        }
    }

    private void process(Entity entity, XMLEntityExporter exporter) {
        exporter.startConsuming(entity);
        for (Object component : entity.getComponents().values()) {
        	if (component == null)
        		continue;
            if (component instanceof Entity)
                process((Entity) component, exporter);
            else if (component.getClass().isArray()) {
                Object[] array = (Object[]) component;
                for (Object element : array)
                    if (element instanceof Entity)
                        process((Entity) element, exporter);
            }
        }
        exporter.finishConsuming(entity);
    }

    private void persistRootObject(Object content, File file) {
        PrintWriter printer = null;
        try {
            printer = XMLUtil.createXMLFile(file.getAbsolutePath(), encoding);
            printer.println("<" + root + ">" + content + "</" + root + ">");
        } catch (FileNotFoundException e) {
            throw new ConfigurationError(e);
        } catch (UnsupportedEncodingException e) {
            throw new ConfigurationError(e);
        } finally {
            IOUtil.close(printer);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + filenamePattern + ']';
    }
}
