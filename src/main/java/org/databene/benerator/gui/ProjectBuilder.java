/*
 * (c) Copyright 2008-2010 by Volker Bergmann. All rights reserved.
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

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.sax.TransformerHandler;

import org.databene.benerator.archetype.FolderLayout;
import static org.databene.benerator.engine.DescriptorConstants.*;
import org.databene.benerator.main.DBSnapshotTool;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.Expression;
import org.databene.commons.FileUtil;
import org.databene.commons.IOUtil;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.OrderedMap;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.commons.maven.MavenUtil;
import org.databene.commons.ui.I18NError;
import org.databene.commons.ui.ProgressMonitor;
import org.databene.html.DefaultHTMLTokenizer;
import org.databene.html.HTMLTokenizer;
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
import org.databene.text.LFNormalizingStringBuilder;

/**
 * Creates benerator project archetypes.<br/>
 * Created at 30.11.2008 17:59:18
 * @since 0.5.6
 * @author Volker Bergmann
 */

public class ProjectBuilder implements Runnable {

	private static final String TAB = "    ";
    private static final String DBUNIT_SNAPSHOT_FILENAME = "base.dbunit.xml";
	private static final String COMMENT_SNAPSHOT = "Create a valid predefined base data set for regression testing " +
			"by importing a snapshot file";
	private static final String COMMENT_DROP_TABLES = "Drop the current tables and sequences if they already exist";
	private static final String COMMENT_CREATE_TABLES = "Create the tables and sequences";

	
    private final static Set<String> DB_CONSTRAINT_NAMES = CollectionUtil.toSet("nullable", "maxLength", "type");
    private static ToStringConverter toStringConverter = new ToStringConverter("");

	protected Setup setup;
	protected TypeDescriptor[]  descriptors;
	protected TransformerHandler handler;
	protected DBSystem db;
    private List<String> errors;
    private ProgressMonitor monitor;
    private FolderLayout folderLayout;
	
	public ProjectBuilder(Setup setup, FolderLayout folderLayout, ProgressMonitor monitor) {
		this.setup = setup;
		this.errors = new ArrayList<String>();
		this.monitor = monitor;
		this.descriptors = new TypeDescriptor[0];
		this.folderLayout = folderLayout;
	}

	public void run() {
		try {
	        // read data model
			if (setup.isDatabaseProject()) {
		        parseDatabaseMetaData();
		        if (monitor != null)
		        	monitor.setMaximum(5 + descriptors.length);
		        advanceMonitor();
			} else
				monitor.setMaximum(5);
	        
	        createFolderLayout();
			applyArchetype(setup, monitor);
			createPOM();
			createProjectPropertiesFile();
			
			// copy import files
			copyImportFiles();
			
			// create db snapshot project.dbunit.xml
			Exception exception = null;
			if (setup.isDatabaseProject() && "DbUnit".equals(setup.getDbSnapshot())) {
				try {
					createDbUnitSnapshot();
				} catch (Exception e) {
					exception = e;
				}
			}			
			
			// create project.ben.xml (including imports)
			createBeneratorXml(setup, monitor);

	        createEclipseProject();
	        
	        if (exception != null)
	        	throw exception;
		} catch (Exception e) {
			errors.add(e.getMessage());
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
			if (monitor != null)
				monitor.setProgress(monitor.getMaximum());
		}
	}

	private void parseDatabaseMetaData() {
	    noteMonitor("scanning database");
	    if (monitor != null)
	    	monitor.setProgress(0);
	    db = new DBSystem("db", setup.getDbUrl(), setup.getDbDriver(), setup.getDbUser(), setup.getDbPassword());
	    db.setSchema(setup.getDbSchema());
	    DataModel.getDefaultInstance().addDescriptorProvider(db);
	    if (setup.getDbSchema() != null)
	        db.setSchema(setup.getDbSchema());
	    descriptors = db.getTypeDescriptors();
    }

	private void createFolderLayout() {
	    String groupId = setup.getGroupId();
	    String pkgFolder = "/" + (StringUtil.isEmpty(groupId) ? "" : groupId.replace('.', '/') + '/') + setup.getProjectName();
	    haveSubFolder("src/main/java" + pkgFolder);
	    haveSubFolder("src/main/resources" + pkgFolder);
	    haveSubFolder("src/test/java" + pkgFolder);
	    haveSubFolder("src/test/resources" + pkgFolder);
    }

	private void applyArchetype(Setup setup, ProgressMonitor monitor) throws IOException {
	    // create project files
	    monitor.setNote("Creating files...");
	    setup.getArchetype().copyFilesTo(setup.getProjectFolder(), folderLayout);
	}

	private void createEclipseProject() {
		setup.projectFile(".project"); // call this for existence check and overwrite error
		if (setup.isEclipseProject()) {
			noteMonitor("Creating Eclipse project");
			MavenUtil.invoke("eclipse:eclipse", setup.getProjectFolder(), !setup.isOffline()); 
		}
		advanceMonitor();
	}

	private void haveSubFolder(String relativePath) {
		FileUtil.ensureDirectoryExists(setup.subDirectory(folderLayout.mapSubFolder(relativePath)));
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
		copyImportFile(setup.getDropScriptFile());
		copyImportFile(setup.getCreateScriptFile());
	}

	private void copyImportFile(File importFile) {
	    if (importFile == null)
	    	return;
    	if (importFile.exists()) {
		    noteMonitor("Importing " + importFile);
	    	File copy = setup.projectFile(importFile.getName());
	    	try {
	    		IOUtil.copyFile(importFile.getAbsolutePath(), copy.getAbsolutePath());
	    	} catch (IOException e) {
	    		throw new I18NError("ErrorCopying", e, importFile.getAbsolutePath(), copy);
	    	}
	    } else
	    	errors.add("File not found: " + importFile);
	    advanceMonitor();
    }

	private File createDbUnitSnapshot() {
		File file = setup.projectFile(setup.getProjectName() + ".dbunit.xml");
		if (!setup.isOverwrite() && file.exists())
			throw new I18NError("FileAlreadyExists", null, file.getAbsolutePath());
		DBSnapshotTool.export(setup.getDbUrl(), setup.getDbDriver(), setup.getDbSchema(), 
				setup.getDbUser(), setup.getDbPassword(), file.getAbsolutePath(), "dbunit", null, monitor);
		return file;
	}

	private File createPOM() {
        noteMonitor("creating pom");
		File file = new File(setup.getProjectFolder(), "pom.xml");
		try {
			String content = IOUtil.getContentOfURI(file.getAbsolutePath());
			content = replaceVariables(content);
			file.delete();
			IOUtil.writeTextFile(file.getAbsolutePath(), content, setup.getEncoding());
		} catch (IOException e) {
			throw new I18NError("ErrorCreatingFile", e, file);
		}
		advanceMonitor();
		return file;	
	}

	private File createProjectPropertiesFile() {
		String filename = "benerator.properties";
		noteMonitor("creating " + filename);
		File file = setup.projectFile(filename);
		PrintWriter writer = null;
		try { // TODO parse existing benerator.properties file and replace ${setup.} expressions instead of overwriting it
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
	
    public static void createBeneratorXml(Setup setup, ProgressMonitor monitor) 
    		throws IOException, ParseException {
    	File descriptorFile = new File(setup.getProjectFolder(), "benerator.xml");
    	if (descriptorFile.exists()) { // not applicable for XML schema based generation
	    	BufferedReader reader = IOUtil.getReaderForURI(descriptorFile.getAbsolutePath());
			DefaultHTMLTokenizer tokenizer = new DefaultHTMLTokenizer(reader);
	    	String lineSeparator = setup.getLineSeparator();
	    	if (lineSeparator == null)
	    		lineSeparator = SystemInfo.getLineSeparator();
			LFNormalizingStringBuilder writer = new LFNormalizingStringBuilder(lineSeparator);
	    	while (tokenizer.nextToken() != HTMLTokenizer.END)
	    		processToken(setup, tokenizer, writer);
	    	IOUtil.writeTextFile(descriptorFile.getAbsolutePath(), writer.toString(), "UTF-8");
    	}
    	monitor.advance();
    }

    private static void processToken(Setup setup, 
    		DefaultHTMLTokenizer tokenizer, LFNormalizingStringBuilder writer) 
    			throws FileNotFoundException, IOException, ParseException {

    	switch (tokenizer.tokenType()) {
    		case HTMLTokenizer.START_TAG: {
    			String nodeName = tokenizer.name();
				if (EL_SETUP.equals(nodeName))
    				appendStartTag(nodeName, renderSetupAttributes(tokenizer, setup), writer, true);
				else if (EL_COMMENT.equals(nodeName))
    				processComment(tokenizer, setup, writer);
    			else
    				writer.append(tokenizer.text());
    			break;
    		}
    		case HTMLTokenizer.CLOSED_TAG: {
    			String nodeName = tokenizer.name();
    			if (EL_DATABASE.equals(nodeName) && setup.isDatabaseProject()) {
    				appendElement(nodeName, renderDbAttributes(setup, tokenizer), writer, false);
    			} else if (EL_EXECUTE.equals(nodeName)) {
    				Map<String, String> attributes = tokenizer.attributes();
    				String uri = attributes.get("uri");
    				if ("{drop_tables.sql}".equals(uri)) {
    			    	if (setup.getDropScriptFile() != null) {
    			    		File dropScriptFile = setup.getDropScriptFile();
    						copyToProject(dropScriptFile, setup.getProjectFolder());
    						attributes.put("uri", dropScriptFile.getName());
    						appendElement(nodeName, attributes, writer, false);
    			    	}
    				} else if ("{create_tables.sql}".equals(uri)) {
    			    	if (setup.getCreateScriptFile() != null) {
    			    		File createScriptFile = setup.getCreateScriptFile();
    						copyToProject(createScriptFile, setup.getProjectFolder());
    						attributes.put("uri", createScriptFile.getName());
    						appendElement(nodeName, attributes, writer, false);
    			    	}
    				} else
    					writer.append(tokenizer.text());
    			} else if (EL_GENERATE.equals(nodeName)) {
    				Map<String, String> attributes = tokenizer.attributes();
    				if (DBUNIT_SNAPSHOT_FILENAME.equals(attributes.get(ATT_SOURCE))) {
	    		    	if (setup.getDbSnapshot() != null)
	    		    		appendElement(nodeName, attributes, writer, false);
    				} else if ("tables".equals(attributes.get(ATT_TYPE))) {
    					generateTables(setup, writer);
    				} else
    					writer.append(tokenizer.text());
    			} else
    				writer.append(tokenizer.text());
    			break;
    		}
    		default: writer.append(tokenizer.text());
    	}
    }
    
	private static Map<String, String> renderDbAttributes(Setup setup, DefaultHTMLTokenizer tokenizer) {
	    Map<String, String> attributes = tokenizer.attributes();
	    attributes.put("url", setup.getDbUrl());	    		
	    attributes.put("driver", setup.getDbDriver());
	    if (!StringUtil.isEmpty(setup.getDbSchema()))
	    	attributes.put("schema", setup.getDbSchema());
	    attributes.put("user", setup.getDbUser());
	    if (!StringUtil.isEmpty(setup.getDbPassword()))
	    	attributes.put("password", setup.getDbPassword());
	    return attributes;
    }

	private static void appendStartTag(String nodeName, Map<String, String> attributes, 
			LFNormalizingStringBuilder writer, boolean wrapLines) {
	    writer.append('<').append(nodeName);
	    writeAttributes(attributes, writer, wrapLines);
	    writer.append('>');
    }

    private static Map<String, String> renderSetupAttributes(DefaultHTMLTokenizer tokenizer,
            Setup setup) {
	    Map<String, String> attributes = tokenizer.attributes();
	    if (setup.getEncoding() != null)
	    	attributes.put("defaultEncoding", setup.getEncoding());
	    if (setup.getDataset() != null)
	    	attributes.put("defaultDataset", setup.getDataset());
	    if (setup.getLocale() != null)
	    	attributes.put("defaultLocale", setup.getLocale().toString());
	    if (setup.getLineSeparator() != null)
	    	attributes.put("defaultLineSeparator", StringUtil.escape(setup.getLineSeparator()));
	    return attributes;
    }
	
    private static void appendElement(String nodeName, Map<String, String> attributes, LFNormalizingStringBuilder writer, boolean wrapLines) {
	    writer.append('<').append(nodeName);
	    writeAttributes(attributes, writer, wrapLines);
	    writer.append("/>");
    }

	private static void writeAttributes(Map<String, String> attributes, LFNormalizingStringBuilder writer, boolean wrapLines) {
		int i = 0;
	    for (Map.Entry<String, String> attribute : attributes.entrySet()) {
	    	if (wrapLines && i > 0) 
	    		writer.append(TAB).append(TAB);
	    	else
	    		writer.append(' ');
	    	writer.append(attribute.getKey()).append("=\"").append(attribute.getValue()).append("\"");
	    	if (i < attributes.size() - 1)
	    		writer.append('\n');
	    	i++;
	    }
    }

	private static void copyToProject(File srcFile, File projectFolder) throws FileNotFoundException, IOException {
        File dstFile = new File(projectFolder, srcFile.getName());
    	FileUtil.copy(srcFile, dstFile, true);
    }
	
    private static void processComment(DefaultHTMLTokenizer tokenizer, Setup setup, 
    		LFNormalizingStringBuilder writer) throws IOException, ParseException {
    	String startText = tokenizer.text();
	    int nextToken = tokenizer.nextToken();
	    if (nextToken == HTMLTokenizer.END_TAG) {
	    	writer.append(startText).append(tokenizer.text());
	    	return;
	    }
		if (nextToken != HTMLTokenizer.TEXT)
	    	throw new ParseException("Text expected in comment", -1);
		String commentText = tokenizer.text().trim();
		if (   (COMMENT_DROP_TABLES.equals(commentText) && setup.getDropScriptFile() == null)
			|| (COMMENT_CREATE_TABLES.equals(commentText) && setup.getCreateScriptFile() == null) 
			|| (COMMENT_SNAPSHOT.equals(commentText) && setup.getDbSnapshot() == null)) {
				while (tokenizer.nextToken() != HTMLTokenizer.END_TAG) {
					// skip all elements until comment end
				}
		} else {
			// write comment start and content
			writer.append(startText).append(tokenizer.text());
		}
    }
	
    private static void generateTables(Setup setup, LFNormalizingStringBuilder writer) {
    	final DBSystem db = getDBSystem(setup);
    	TypeDescriptor[] descriptors = db.getTypeDescriptors();
		for (TypeDescriptor descriptor : descriptors) {
          	ComplexTypeDescriptor complexType = (ComplexTypeDescriptor) descriptor;
          	final String name = complexType.getName();
          	InstanceDescriptor iDesc = new InstanceDescriptor(name, name);
          	if (setup.getDbSnapshot() != null)
          		iDesc.setCount(ExpressionUtil.constant(0L));
          	else
          		iDesc.setCount(ExpressionUtil.constant(db.countEntities(name)));
			generate(iDesc, writer);
       }
	}

    private static DBSystem getDBSystem(Setup setup) {
		DBSystem db = new DBSystem("db", setup.getDbUrl(), setup.getDbDriver(), setup.getDbUser(), setup.getDbPassword());
		if (setup.getDbSchema() != null)
			db.setSchema(setup.getDbSchema());
		DataModel.getDefaultInstance().addDescriptorProvider(db);
		return db;
    }
	
	@SuppressWarnings("unchecked")
    private static void generate(InstanceDescriptor descriptor, LFNormalizingStringBuilder writer) {
        ComplexTypeDescriptor type = (ComplexTypeDescriptor) descriptor.getTypeDescriptor();
        Map<String, String> attributes = new OrderedMap<String, String>();
        for (FeatureDetail<? extends Object> detail : descriptor.getDetails()) {
            Object value = detail.getValue();
            if (value != null && !NullSafeComparator.equals(value, detail.getDefault())) {
            	if (value instanceof Expression)
            		value = ((Expression) value).evaluate(null);
                attributes.put(detail.getName(), toStringConverter.convert(value));
            }
        }
        attributes.put(ATT_CONSUMER, "db");
        appendStartTag(EL_GENERATE, attributes, writer, false);
        writer.append('\n');
        
        for (ComponentDescriptor cd : type.getComponents())
            addAttribute(cd, writer);
        writer.append(TAB);
        appendEndElement(EL_GENERATE, writer);
        writer.append("\n\n").append(TAB);
    }
	
    private static void appendEndElement(String nodeName, LFNormalizingStringBuilder writer) {
	    writer.append("</").append(nodeName).append(">");
    }

	private static void addAttribute(ComponentDescriptor component, LFNormalizingStringBuilder writer) {
    	// normalize
    	boolean nullable = (component.isNullable() == null || component.isNullable());
		if (component.getMaxCount() != null && component.getMaxCount().evaluate(null) == 1)
			component.setMaxCount(null);
		if (component.getMinCount() != null && component.getMinCount().evaluate(null) == 1)
			component.setMinCount(null);
		if (nullable)
			component.setNullable(null);

        String elementName = null;
        if (component instanceof PartDescriptor)
            elementName = EL_ATTRIBUTE;
        else if (component instanceof ReferenceDescriptor)
        	elementName = EL_REFERENCE;
        else if (component instanceof IdDescriptor)
        	elementName = EL_ID;
        else
            throw new UnsupportedOperationException("Component descriptor type not supported: " + 
                    component.getClass().getSimpleName());
        
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(ATT_NAME, component.getName());
        SimpleTypeDescriptor type = (SimpleTypeDescriptor) (component.getType() != null ? 
        		DataModel.getDefaultInstance().getTypeDescriptor(component.getType()) : 
        		component.getLocalType());
        if (type != null) {
			for (FeatureDetail<? extends Object> detail : type.getDetails())
	            format(detail, attributes);
	    }
        for (FeatureDetail<? extends Object> detail : component.getDetails())
            format(detail, attributes);
        if (nullable)
        	attributes.put("nullQuota", "1");

        writer.append(TAB).append(TAB).append("<!--").append(elementName);
    	for (Map.Entry<String, String> entry : attributes.entrySet())
    		writer.append(' ').append(entry.getKey()).append("=\"").append(entry.getValue()).append('"');
    	writer.append(" /-->");
    	writer.append('\n');
    }
    
	private static void format(FeatureDetail<? extends Object> detail, Map<String, String> attributes) {
		if (!ATT_NAME.equals(detail.getName()) && detail.getValue() != null && !isDbConstraint(detail.getName()))
		    attributes.put(detail.getName(), toStringConverter.convert(detail.getValue()));
	}
	
	private static boolean isDbConstraint(String name) {
		return DB_CONSTRAINT_NAMES.contains(name);
	}

	private String replaceVariables(String text) {
		for (PropertyDescriptor property : BeanUtil.getPropertyDescriptors(Setup.class)) {
			String propertyName = property.getName();
			String var = "${setup." + propertyName + "}";
			Object propertyValue = BeanUtil.getPropertyValue(setup, propertyName);
			String propertyString = toStringConverter.convert(propertyValue);
			text = text.replace(var, propertyString);
		}
		return text;
	}

}
