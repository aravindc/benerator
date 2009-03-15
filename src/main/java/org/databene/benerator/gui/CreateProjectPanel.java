/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.DriverManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.databene.commons.FileUtil;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.ui.FileOperation;
import org.databene.commons.ui.FileTypeSupport;
import org.databene.commons.ui.I18NError;
import org.databene.commons.ui.I18NSupport;
import org.databene.gui.swing.AlignedPane;
import org.databene.gui.swing.SwingUtil;
import org.databene.gui.swing.delegate.PropertyCheckBox;
import org.databene.gui.swing.delegate.PropertyComboBox;
import org.databene.gui.swing.delegate.PropertyFileField;
import org.databene.gui.swing.delegate.PropertyFileList;
import org.databene.gui.swing.delegate.PropertyTextField;
import org.databene.gui.swing.ProgressMonitor;

/**
 * Lets the user enter benerator project data and 
 * calls the ArchetypeBuilder for creating a new benerator project.<br/>
 * <br/>
 * Created at 17.07.2008 08:00:00
 * @since 0.5.6
 * @author Volker Bergmann
 */
public class CreateProjectPanel extends JPanel {
	
	private static final long serialVersionUID = 167461075459757736L;

	private static final int WIDE = 30;
	
	Setup setup;
	I18NSupport i18n;
	PropertyFileField folderField;
	JButton createButton;
	JCheckBox dbProjectCheckBox;
	JTextField dbUrlField;
	JTextField dbDriverField;
	JTextField dbUserField;
	JTextField dbSchemaField;
	JTextField dbPasswordField;
	JComboBox dbSnapshotField;
	JButton testButton;
	
	public CreateProjectPanel(Setup setup, I18NSupport i18n) {
		super(new BorderLayout());
		this.setup = setup;
		this.i18n = i18n;
		
		setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		add(createPropertiesPane(), BorderLayout.CENTER);
		add(createButtonPane(), BorderLayout.SOUTH);
		
		// Exit the application if 'Escape' is pressed
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	private Component createButtonPane() {
		JPanel pane = new JPanel();

		createButton = createButton("create", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Creator()).start();
			}
		});
		pane.add(createButton);
		
		pane.add(createButton("cancel", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		}));
		
		return pane;
	}

	private Component createPropertiesPane() {
		AlignedPane pane = AlignedPane.createVerticalPane(4);
		
		// project properties
		createTextFieldRow("projectName", pane);
		folderField = new PropertyFileField(setup, "projectFolder", WIDE, 
				FileTypeSupport.directoriesOnly, FileOperation.save);
		folderField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File folder = CreateProjectPanel.this.folderField.getFile();
				if (! setup.isOverwrite() && folder.exists() && !FileUtil.isEmptyFolder(folder))
					showErrors(i18n.getString("error.projectFolderNotEmpty"));
			}
			
		});
		pane.addRow(i18n.getString("projectFolder"), folderField);
		pane.addSeparator();
		
		createTextField("groupId", pane);
		createTextField("version", pane);
		pane.endRow();
		
		createCheckBox("eclipseProject", pane);
		createCheckBox("overwrite", pane);
		pane.endRow();
		
		dbProjectCheckBox = createCheckBox("databaseProject", pane);
		dbProjectCheckBox.addActionListener(new DBProjectListener());
		
		createCheckBox("offline", pane);
		pane.endRow();
		pane.addSeparator();
		
		// db properties
		dbUrlField = createTextFieldRow("dbUrl", pane);
		dbDriverField = createTextFieldRow("dbDriver", pane);
		dbUserField = createTextField("dbUser", pane);
		dbSchemaField = createTextField("dbSchema", pane);
		pane.endRow();
		
		dbPasswordField = createTextField("dbPassword", pane);
		pane.addElement(new JLabel(""));
		testButton = createButton("testConnection", new TestConnectionListener());
		pane.addElement(testButton);
		pane.endRow();

		dbSnapshotField = createComboBox( "dbSnapshot", pane, "DbUnit", "none");
		pane.addSeparator();
		
		// import files
		PropertyFileList importList =  new PropertyFileList(setup, "importFiles", i18n);
		pane.addRow(i18n.getString("importFiles"), importList);
		pane.addSeparator();
		
		createTextField("encoding",      pane);
		createTextField("lineSeparator", pane);
		pane.endRow();

		createTextField("locale", pane);
		createTextField("dataset", pane);
		pane.endRow();
		pane.addSeparator();
		
		return pane;
	}

	private JCheckBox createCheckBox(String propertyName, AlignedPane pane) {
		PropertyCheckBox checkBox = new PropertyCheckBox(setup, propertyName, i18n.getString(propertyName));
		pane.addElement("", checkBox);
		return checkBox;
	}

	private JComboBox createComboBox(String propertyName, AlignedPane pane, String... options) {
		JComboBox comboBox = new PropertyComboBox(setup, propertyName, i18n, "dbSnapshot.", (Object[]) options);
		String label = i18n.getString(propertyName);
		pane.addRow(label, comboBox);
		return comboBox;
	}

	private JTextField createTextFieldRow(String propertyName, AlignedPane pane) {
		JTextField textfield = new PropertyTextField(setup, propertyName, WIDE);
		String label = i18n.getString(propertyName);
		pane.addRow(label, textfield);
		return textfield;
	}
	
	private JTextField createTextField(String propertyName, AlignedPane pane) {
		JTextField textfield = new PropertyTextField(setup, propertyName, WIDE / 2);
		String label = i18n.getString(propertyName);
		pane.addElement(label, textfield);
		return textfield;
	}
	
	private JButton createButton(String label, ActionListener listener) {
		JButton button = new JButton(i18n.getString(label));
		button.addActionListener(listener);
		return button;
	}
	
	public void exit() {
		JFrame frame = (JFrame) SwingUtilities.getRoot(this);
		frame.dispose();
		System.exit(0);
	}
	
	void showErrors(Object... errors) {
		String[] messages = new String[errors.length];
		for (int i = 0; i < errors.length; i++) {
			Object error = errors[i];
			if (error instanceof I18NError) {
				messages[i] = ((I18NError) error).renderMessage(i18n);
			} else
				messages[i] = ToStringConverter.convert(error, "null");
		}
		JOptionPane.showMessageDialog(CreateProjectPanel.this, messages, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	class DBProjectListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
	        boolean useDB = dbProjectCheckBox.isSelected();
        	dbUrlField.setEnabled(useDB);
        	dbDriverField.setEnabled(useDB);
        	dbUserField.setEnabled(useDB);
        	dbSchemaField.setEnabled(useDB);
        	dbPasswordField.setEnabled(useDB);
        	dbSnapshotField.setEnabled(useDB);
        	testButton.setEnabled(useDB);
        }
	}
	
	class Creator implements Runnable {

		public void run() {
			try {
				createButton.setEnabled(false);
				String taskName = i18n.format("message.project.create", setup.getProjectName());
				String message = i18n.getString("message.project.initializing");
				ProgressMonitor monitor = new ProgressMonitor(null, taskName, message, 0, 100);
				monitor.setMillisToDecideToPopup(10);
				monitor.setMillisToPopup(10);
				ArchetypeBuilder builder = new ArchetypeBuilder(setup, monitor);
				builder.run();
				String[] errors = builder.getErrors();
				if (errors.length > 0)
					showErrors((Object[]) errors);
				else {
					JOptionPane.showMessageDialog(CreateProjectPanel.this, "Done");
					exit();
				}
			} catch (I18NError e) {
				e.printStackTrace();
				showErrors(e);
			} catch (Exception e) {
				e.printStackTrace();
				showErrors(e);
			} finally {
				createButton.setEnabled(true);
				SwingUtil.repaintLater(CreateProjectPanel.this);
			}
		}
		
	}

	class TestConnectionListener implements ActionListener {

        public void actionPerformed(ActionEvent actionevent) {
	        try {
	        	Class.forName(setup.getDbDriver());
	        	DriverManager.getConnection(setup.getDbUrl(), setup.getDbUser(), setup.getDbPassword());
	        	JOptionPane.showMessageDialog(CreateProjectPanel.this, i18n.getString("message.connect.successful"));
	        } catch (Exception e) {
	        	showErrors(e.toString());
	        }
        }
		
	}
}