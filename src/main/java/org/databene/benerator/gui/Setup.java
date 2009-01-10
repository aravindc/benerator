/*
 * (c) Copyright 2008, 2009 by Volker Bergmann. All rights reserved.
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Locale;

import org.databene.commons.FileUtil;
import org.databene.commons.LocaleUtil;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.bean.ObservableBean;
import org.databene.commons.ui.I18NError;

/**
 * Assembles all data useful for creating benerator archetypes.<br/>
 * <br/>
 * Created at 29.11.2008 22:44:12
 * @since 0.5.6
 * @author Volker Bergmann
 */

public class Setup implements ObservableBean {
	
	private static final String DEFAULT_PROJECT_NAME = "myproject";
	private static final String DEFAULT_GROUP_ID = "com.my";
	private static final String DEFAULT_PROJECT_VERSION = "1.0";

	private static final String DEFAULT_DB_URL = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
	private static final String DEFAULT_DB_DRIVER = "oracle.jdbc.driver.OracleDriver";

	private PropertyChangeSupport changeSupport;
	
	private String  projectName;
	private String  groupId;
	private String  version;
	private File    projectFolder;
	
	private boolean eclipseProject;
	private boolean offline;
	private boolean overwrite;

	private String encoding;
	private String lineSeparator;

	private String locale;
	private String dataset;

	private String dbUrl;
	private String dbDriver;
	private String dbPassword;
	private String dbSchema;
	private String dbUser;
	
	private String dbSnapshot;
	
	private MavenDependency[] dbDependencies;

	private File[] importFiles;
	
	public Setup() {
		this.changeSupport = new PropertyChangeSupport(this);
		
		projectFolder = new File(SystemInfo.currentDir());
		setProjectName(DEFAULT_PROJECT_NAME);
		setGroupId(DEFAULT_GROUP_ID);
		setVersion(DEFAULT_PROJECT_VERSION);
		eclipseProject = true;
		offline = false;
		overwrite = false;
		
		setEncoding(SystemInfo.fileEncoding());
		setLineSeparator(SystemInfo.lineSeparator());
		setLocale(Locale.getDefault().toString());
		setDataset(LocaleUtil.getDefaultCountryCode());

		String url = System.getenv("DEFAULT_DATABASE");
		if (StringUtil.isEmpty(url))
			url = DEFAULT_DB_URL;
		setDbUrl(url);
		setDbDriver(DEFAULT_DB_DRIVER);
		setDbUser(SystemInfo.userName());
		setDbSnapshot("DbUnit");
		this.importFiles = new File[0];
		this.dbDependencies = new MavenDependency[0]; // TODO v0.5.8 handle maven dependencies
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		String oldName = this.projectName;
		this.projectName = projectName;
		changeSupport.firePropertyChange("projectName", oldName, this.projectName);
		// if user had no value or the same value before, update it
		if (this.dbSchema == null || NullSafeComparator.equals(oldName, this.dbUser))
			setDbUser(projectName);
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isEclipseProject() {
		return eclipseProject;
	}

	public void setEclipseProject(boolean eclipseProject) {
		boolean oldValue = this.eclipseProject;
		this.eclipseProject = eclipseProject;
		changeSupport.firePropertyChange("eclipseProject", oldValue, this.eclipseProject);
	}

	public boolean isOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public File getProjectFolder() {
		return projectFolder;
	}

	public void setProjectFolder(File projectFolder) {
		File oldValue = this.projectFolder;
		this.projectFolder = projectFolder;
		changeSupport.firePropertyChange("projectFolder", oldValue, this.projectFolder);
		if (DEFAULT_PROJECT_NAME.equals(projectName))
			setProjectName(projectFolder.getName());
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		String oldValue = this.encoding;
		this.encoding = encoding;
		changeSupport.firePropertyChange("encoding", oldValue, this.encoding);
	}

	public String getLineSeparator() {
		return lineSeparator;
	}

	public void setLineSeparator(String lineSeparator) {
		String oldValue = this.lineSeparator;
		this.lineSeparator = lineSeparator;
		changeSupport.firePropertyChange("lineSeparator", oldValue, this.lineSeparator);
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		String oldValue = this.locale;
		this.locale = locale;
		changeSupport.firePropertyChange("locale", oldValue, this.locale);
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		String oldValue = this.dataset;
		this.dataset = dataset;
		changeSupport.firePropertyChange("dataset", oldValue, this.dataset);
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		String oldValue = this.dbUrl;
		this.dbUrl = dbUrl;
		changeSupport.firePropertyChange("dbUrl", oldValue, this.dbUrl);
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public void setDbDriver(String dbDriver) {
		String oldValue = this.dbDriver;
		this.dbDriver = dbDriver;
		changeSupport.firePropertyChange("dbDriver", oldValue, this.dbDriver);
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		String oldValue = this.dbPassword;
		this.dbPassword = dbPassword;
		changeSupport.firePropertyChange("dbPassword", oldValue, this.dbPassword);
	}

	public String getDbSchema() {
		return dbSchema;
	}

	public void setDbSchema(String dbSchema) {
		String oldValue = this.dbSchema;
		this.dbSchema = dbSchema;
		changeSupport.firePropertyChange("dbSchema", oldValue, this.dbSchema);
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		String oldValue = this.dbUser;
		this.dbUser = dbUser;
		changeSupport.firePropertyChange("dbUser", oldValue, this.dbUser);
		// if schema or password had no value or the same value before, update it
		if (this.dbSchema == null || NullSafeComparator.equals(oldValue, this.dbSchema))
			setDbSchema(dbUser);
		if (this.dbPassword == null || NullSafeComparator.equals(oldValue, this.dbPassword))
			setDbPassword(dbUser);
	}

	public MavenDependency[] getDbDependencies() {
		return dbDependencies;
	}

	public void setDbDependencies(MavenDependency[] dbDependencies) {
		this.dbDependencies = dbDependencies;
	}

	public String getDbSnapshot() {
		return dbSnapshot;
	}

	public void setDbSnapshot(String dbSnapshot) {
		this.dbSnapshot = dbSnapshot;
	}
	
	public File[] getImportFiles() {
		return importFiles;
	}

	public void setImportFiles(File... importFiles) {
		File[] oldValue = this.importFiles;
		this.importFiles = importFiles;
		changeSupport.firePropertyChange("importFiles", oldValue, this.importFiles);
	}

	public void addImportFile(File importFile) {
		File[] oldValue = this.importFiles;
		this.importFiles = new File[oldValue.length + 1];
		System.arraycopy(oldValue, 0, this.importFiles, 0, oldValue.length);
		this.importFiles[this.importFiles.length - 1] = importFile;
		changeSupport.firePropertyChange("importFiles", oldValue, this.importFiles);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public File projectFile(String filename) {
		File file = new File(getProjectFolder(), filename);
		if (!overwrite && file.exists())
			throw new I18NError("FileAlreadyExists", null, filename);
		return file;
	}

	public File subDirectory(String relativePath) {
		return new File(projectFolder, FileUtil.nativePath(relativePath));
	}

}
