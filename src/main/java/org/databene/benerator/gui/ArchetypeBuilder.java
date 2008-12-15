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

package org.databene.benerator.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.main.DBSnapshotTool;
import org.databene.benerator.parser.ModelParser;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ErrorHandler;
import org.databene.commons.FileUtil;
import org.databene.commons.IOUtil;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.ShellUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.ui.I18NError;
import org.databene.commons.ui.ProgressMonitor;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.FeatureDetail;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.platform.db.DBSystem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Creates benerator project archetypes.<br/>
 * <br/>
 * Created at 30.11.2008 17:59:18
 * @since 0.5.6
 * @author Volker Bergmann
 */

public class ArchetypeBuilder implements Runnable {
	
	private static final String TEMPLATE_BEN_XML = "org/databene/benerator/gui/template.ben.xml";
	private static final String DEFAULTS_XML = "org/databene/benerator/gui/defaults.xml";
	private static final char[] LF_TAB = StringUtil.getChars(SystemInfo.lineSeparator() + "\t");
	private static final char[] LF_TWO_TABS = StringUtil.getChars(SystemInfo.lineSeparator() + "\t\t");
    private final static Set<String> DB_CONSTRAINT_NAMES = CollectionUtil.toSet("nullable", "maxLength", "type");

	protected Setup setup;
	protected TypeDescriptor[]  descriptors;
	protected TransformerHandler handler;
	protected DBSystem db;
    private ModelParser parser = new ModelParser(new BeneratorContext("."));
    private Map<String, ComponentDescriptor> defaults = new HashMap<String, ComponentDescriptor>();
    private ToStringConverter<Object> toStringConverter = new ToStringConverter<Object>();
    private List<String> errors;
    private ProgressMonitor monitor;
	
	public ArchetypeBuilder(Setup setup, ProgressMonitor monitor) {
		this.setup = setup;
		this.errors = new ArrayList<String>();
		this.monitor = monitor;
	}

	public void run() {
		try {
	        // read data model
	        noteMonitor("scanning database");
	        if (monitor != null)
	        	monitor.setProgress(0);
            db = new DBSystem("db", setup.getDbUrl(), setup.getDbDriver(), setup.getDbUser(), setup.getDbPassword());
            db.setSchema(setup.getDbSchema());
            DataModel.getDefaultInstance().addDescriptorProvider(db);
            if (setup.getDbSchema() != null)
                db.setSchema(setup.getDbSchema());
	        descriptors = db.getTypeDescriptors();
	        if (monitor != null)
	        	monitor.setMaximum(5 + setup.getImportFiles().length + descriptors.length);
	        advanceMonitor();
	        
	        String groupId = setup.getGroupId();
			String pkgFolder = "/" + (StringUtil.isEmpty(groupId) ? "" : groupId.replace('.', '/') + '/') + setup.getProjectName();
			haveSubFolder("src/main/java" + pkgFolder);
			haveSubFolder("src/main/resources" + pkgFolder);
			haveSubFolder("src/test/java" + pkgFolder);
			haveSubFolder("src/test/resources" + pkgFolder);
	        
	        // create pom.xml, project.properties
			createPOM();
			createProjectPropertiesFile();
			
			// copy import files
			copyImportFiles();
			
			// create db snapshot project.dbunit.xml
			File snapshotFile = createDbUnitSnapshot();
			if (snapshotFile != null)
				setup.addImportFile(snapshotFile);
			
			// create project.ben.xml (including imports)
			createDescriptorFile();

	        createEclipseProject();
	        
		} finally {
			if (db != null)
				db.close();
			if (monitor != null)
				monitor.setProgress(monitor.getMaximum());
		}
	}

	private void createEclipseProject() {
		setup.projectFile(".project"); // call this for existence check and overwrite error
		if (setup.isEclipseProject()) {
			noteMonitor("Creating Eclipse project");
			String command = "mvn" + (setup.isOffline() ? " -o" : "") + " eclipse:eclipse";
			ShellUtil.runShellCommand(command, setup.getProjectFolder(), new Handler());
		}
		advanceMonitor();
	}

	private void haveSubFolder(String relativePath) {
		FileUtil.ensureDirectoryExists(setup.subDirectory(relativePath));
	}
	
	public String[] getErrors() {
		return CollectionUtil.toArray(errors, String.class);
	}

	private void advanceMonitor() {
		if (monitor != null)
			monitor.advance();
	}
	
	private void noteMonitor(String note) {
		if (monitor != null)
			monitor.setNote(note);
	}
	
	private void copyImportFiles() {
		for (File importFile : setup.getImportFiles()) {
			noteMonitor("Importing " + importFile);
			if (importFile.exists()) {
				File copy = setup.projectFile(importFile.getName());
				try {
					IOUtil.copy(importFile.getAbsolutePath(), copy.getAbsolutePath());
				} catch (IOException e) {
					throw new I18NError("ErrorCopying", e, importFile.getAbsolutePath(), copy);
				}
			} else
				errors.add("File not found: " + importFile);
			advanceMonitor();
		}
	}

	private File createDbUnitSnapshot() {
		if ("DbUnit".equals(setup.getDbSnapshot())) {
			File file = setup.projectFile(setup.getProjectName() + ".dbunit.xml");
			if (!setup.isOverwrite() && file.exists())
				throw new I18NError("FileAlreadyExists", null, file.getAbsolutePath());
			DBSnapshotTool.export(setup.getDbUrl(), setup.getDbDriver(), setup.getDbSchema(), 
					setup.getDbUser(), setup.getDbPassword(), file.getAbsolutePath(), monitor);
			return file;
		} else
			return null;
	}

	private File createPOM() {
        noteMonitor("creating pom");
		File file = setup.projectFile("pom.xml");
		try {
			String content = IOUtil.getContentOfURI("org/databene/benerator/gui/template-pom.xml");
			content = content.replace("${setup.projectName}", setup.getProjectName());
			content = content.replace("${setup.groupId}",     setup.getGroupId());
			content = content.replace("${setup.version}",     setup.getVersion());
			content = content.replace("${setup.encoding}",    setup.getEncoding());
			content = content.replace("${setup.dbUrl}",       setup.getDbUrl());
			content = content.replace("${setup.dbDriver}",    setup.getDbDriver());
			content = content.replace("${setup.dbUser}",      setup.getDbUser());
			content = content.replace("${setup.dbPassword}",  setup.getDbPassword());
			content = content.replace("${setup.dbSchema}",    setup.getDbSchema());
			IOUtil.writeTextFile(file.getAbsolutePath(), content, setup.getEncoding());
		} catch (IOException e) {
			throw new I18NError("ErrorCreatingFile", e, file);
		}
		advanceMonitor();
		return file;	
	}

	private File createProjectPropertiesFile() {
		String filename = setup.getProjectName() + ".properties";
		noteMonitor("creating " + filename);
		File file = setup.projectFile(filename);
		PrintWriter writer = null;
		try { 
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			writer.println("benerator.defaultEncoding=" + setup.getEncoding());
		} catch (IOException e) {
			throw new I18NError("ErrorCreatingFile", e, file);
		} finally {
			IOUtil.close(writer);
		}
		advanceMonitor();
		return file;
	}
	
	private File createDescriptorFile() {
		String filename = setup.getProjectName() + ".ben.xml";
		noteMonitor("Creating " + filename);
		File file = setup.projectFile(filename);
        try {
			// create file and write header
			SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			handler = tf.newTransformerHandler();
			
			Transformer serializer = handler.getTransformer();
			serializer.setOutputProperty(OutputKeys.METHOD, "xml");
			serializer.setOutputProperty(OutputKeys.ENCODING, setup.getEncoding());
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			
		    OutputStream out = new FileOutputStream(file);
			handler.setResult(new StreamResult(out));
			handler.startDocument();
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			InputStream in = IOUtil.getInputStreamForURI(TEMPLATE_BEN_XML);
			saxParser.parse(in, new SaxHandler(handler));
		} catch (TransformerConfigurationException e) {
			throw new ConfigurationError(e);
		} catch (SAXException e) {
			throw new ConfigurationError("Error in initializing XML file", e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error writing file " + file, e);
		} catch (IOException e) {
			throw new RuntimeException("Error writing file " + file, e);
		} catch (ParserConfigurationException e) {
			throw new ConfigurationError("Error in initializing SAX parser", e);
		}
		advanceMonitor();
		return file;
	}
	
	class SaxHandler extends DefaultHandler {
		
		private static final String CREATE_ENTITIES = "${create-entities}";
		private static final String IMPORT_FILES = "${import-files}";
		private TransformerHandler handler;
		private boolean comment;

		public SaxHandler(TransformerHandler handler) {
			this.handler = handler;
			this.comment = false;
		}

		@Override
		public void startDocument() throws SAXException {
			handler.startDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) 
				throws SAXException {
			if ("comment".equals(qName))
				comment = true;
			else {
				AttributesImpl out = new AttributesImpl();
				for (int i = 0; i < attributes.getLength(); i++) {
					String value = attributes.getValue(i);
					value = value.replace("${setup.projectName}", setup.getProjectName());
					out.addAttribute(attributes.getURI(i), attributes.getLocalName(i), attributes.getQName(i), 
							attributes.getType(i), value);
				}
				handler.startElement(uri, localName, qName, out);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (comment)
				handler.comment(ch, start, length);
			else {
				String text = new String(ch, start, length);
				if (text.contains("${setup.projectName}")) {
					writeText(text.replace("${setup.projectName}", setup.getProjectName()));
				} else if (text.contains(IMPORT_FILES)) {
					int index = text.indexOf(IMPORT_FILES, start);
					handler.characters(ch, start, index - start);
					importFiles();
					handler.characters(ch, index + IMPORT_FILES.length(), length - index - IMPORT_FILES.length());
				} else if (text.contains(CREATE_ENTITIES)) {
					int index = text.indexOf(CREATE_ENTITIES, start);
					handler.characters(ch, start, index - start);
					createEntities();
					handler.characters(ch, index + CREATE_ENTITIES.length(), length - index - CREATE_ENTITIES.length());
				} else
					handler.characters(ch, start, length);
			}
		}

		private void writeText(String text) throws SAXException {
			char[] out = StringUtil.getChars(text);
			handler.characters(out, 0, out.length);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if ("comment".equals(qName))
				comment = false;
			else
				handler.endElement(uri, localName, qName);
		}
		
		@Override
		public void ignorableWhitespace(char[] ch, int start, int length)
				throws SAXException {
			handler.ignorableWhitespace(ch, start, length);
		}

		@Override
		public void endDocument() throws SAXException {
			handler.endDocument();
		}
	}

	void importFiles() throws SAXException {
		File[] importFiles = setup.getImportFiles();
		if (importFiles != null)
			for (File importFile : importFiles)
				importFile(importFile);
	}

	private void importFile(File importFile) throws SAXException {
		writeLfTab();
		String filename = importFile.getName();
		if (filename.toLowerCase().endsWith(".properties"))
			writeElement("include", "uri", filename);
		else if (filename.toLowerCase().endsWith(".dbunit.xml"))
			writeElement("create-entities", "source", filename, "consumer", "db");
		else if (filename.toLowerCase().endsWith(".sql"))
			writeElement("execute", "uri", filename, "target", "db");
		else
			writeElement("execute", "uri", filename);
	}

	private void writeElement(String name, String ... keyValuePairs) throws SAXException {
		if (keyValuePairs.length % 2 == 1)
			throw new IllegalArgumentException();
		writeLfTab();
		AttributesImpl attributes = new AttributesImpl();
		for (int i = 0; i < keyValuePairs.length; i += 2)
			attributes.addAttribute("", "", keyValuePairs[i], "CDATA", keyValuePairs[i + 1]);
		handler.startElement("", "", name, attributes);
		handler.endElement("", "", name);
	}

	private void writeLfTab() throws SAXException {
		handler.characters(LF_TAB, 0, LF_TAB.length);
	}

	void createEntities() throws SAXException {
		parseDefaults();
		for (TypeDescriptor descriptor : descriptors) {
          	ComplexTypeDescriptor complexType = (ComplexTypeDescriptor) descriptor;
          	String name = complexType.getName();
          	applyDefaults(complexType);
          	InstanceDescriptor iDesc = new InstanceDescriptor(name, complexType.getName());
			long count = db.countEntities(name);
			iDesc.setCount(count);
			createEntity(iDesc);
       }
	}
	
	private void createEntity(InstanceDescriptor descriptor) throws SAXException {
		descriptor.setCount(0L);
		AttributesImpl attributes = new AttributesImpl();
        for (FeatureDetail<? extends Object> detail : descriptor.getDetails()) {
            Object value = detail.getValue();
            if (value != null && !NullSafeComparator.equals(value, detail.getDefault()))
                attributes.addAttribute("", "", detail.getName(), "CDATA", toStringConverter.convert(value));
        }
        attributes.addAttribute("", "", "consumer", "CDATA", "db");
		writeLfTab();
		handler.startElement("", "", "create-entities", attributes);
        ComplexTypeDescriptor type = (ComplexTypeDescriptor) descriptor.getType();
		if (type.getComponents().size() > 0)
            for (ComponentDescriptor cd : type.getComponents())
                attribute(cd);
			writeLfTab();
		handler.endElement("", "", "create-entities");
		writeLfTab();
    }

    private void attribute(ComponentDescriptor component) throws SAXException {
    	// normalize
    	boolean nullable = (component.isNullable() == null || component.isNullable());
		if (component.getMaxCount() != null && component.getMaxCount() == 1)
			component.setMaxCount(null);
		if (component.getMinCount() != null && component.getMinCount() == 1)
			component.setMinCount(null);
		if (nullable)
			component.setNullable(null);

        String elementName = null;
        if (component instanceof PartDescriptor)
            elementName = "attribute";
        else if (component instanceof ReferenceDescriptor)
        	elementName = "reference";
        else if (component instanceof IdDescriptor)
        	elementName = "id";
        else
            throw new UnsupportedOperationException("Component descriptor type not supported: " + 
                    component.getClass().getSimpleName());
        
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", "name", "CDATA", component.getName());
        SimpleTypeDescriptor type = (SimpleTypeDescriptor)(component.getTypeName() != null ? 
        		DataModel.getDefaultInstance().getTypeDescriptor(component.getTypeName()) : 
        		component.getLocalType());
        if (type != null) {
			for (FeatureDetail<? extends Object> detail : type.getDetails())
	            format(detail, attributes);
	    }
        for (FeatureDetail<? extends Object> detail : component.getDetails())
            format(detail, attributes);
        if (nullable)
        	attributes.addAttribute("", "", "nullQuota", "CDATA", "1");

        handler.characters(LF_TWO_TABS, 0, LF_TWO_TABS.length);
//        if (nullable) {
        	StringBuilder builder = new StringBuilder();
        	builder.append(elementName);
        	for (int i = 0; i < attributes.getLength(); i++)
        		builder.append(' ').append(attributes.getQName(i)).append("=\"").append(attributes.getValue(i)).append('"');
        	builder.append(" /");
        	char[] chars = StringUtil.getChars(builder);
        	handler.comment(chars, 0, builder.length());
/*
    	} else {
	        handler.startElement("", "", elementName, attributes);
	        handler.endElement("", "", elementName);
        }
*/
    }

	private void format(FeatureDetail<? extends Object> detail, AttributesImpl attributes) {
		if (!"name".equals(detail.getName()) && detail.getValue() != null && !dbConstraint(detail.getName()))
		    attributes.addAttribute("", "", detail.getName(), "CDATA", toStringConverter.convert(detail.getValue()));
	}
	
	private boolean dbConstraint(String name) {
		return DB_CONSTRAINT_NAMES.contains(name);
	}
	
	private void parseDefaults() {
		 try {
			Document document = XMLUtil.parse(IOUtil.getInputStreamForURI(DEFAULTS_XML));
			 Element root = document.getDocumentElement();
			 Element[] childElements = XMLUtil.getChildElements(root);
			 String projectPath = setup.getProjectFolder().getCanonicalPath();
			BeneratorContext context = new BeneratorContext(projectPath);
			 for (Element node : childElements) {
				 ComponentDescriptor component = parser.parseSimpleTypeComponent(node, null, context);
				 defaults.put(component.getName(), component);
			 }
		} catch (IOException e) {
			throw new ConfigurationError(e);
		}
	}

	private void applyDefaults(ComplexTypeDescriptor complexType) {
		List<ComponentDescriptor> overrides = new ArrayList<ComponentDescriptor>();
		for (ComponentDescriptor component : complexType.getComponents()) {
			String name = component.getName();
			if (defaults.containsKey(name))
				overrides.add(defaults.get(name));
		}
		for (ComponentDescriptor override : overrides)
			complexType.setComponent(override);
	}

	class Handler extends ErrorHandler {
		
		public Handler() {
			super(ArchetypeBuilder.class);
		}

		@Override
		public void handleError(String message) {
			errors.add(message);
		}
		
		@Override
		public void handleError(String message, Throwable t) {
			errors.add(message + ": " + t.toString());
		}
	}
}
