/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.csv;

import java.io.File;
import java.io.IOException;

import org.databene.commons.IOUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;

import junit.framework.TestCase;

/**
 * Tests the {@link CSVEntityExporter}.<br/>
 * <br/>
 * Created at 14.03.2009 06:10:37
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class CSVEntityExporterTest extends TestCase {
	
	private static final File DEFAULT_FILE = new File("export.csv");
	
	private final File customFile = new File("target" + File.separator + getClass().getSimpleName() + ".csv");
	
	private ComplexTypeDescriptor descriptor;
	private Entity alice;
	private Entity bob;
	
	@Override
	protected void setUp() throws Exception {
		// create descriptor for 'Person' entities
		descriptor = new ComplexTypeDescriptor("Person", "entity");
		descriptor.addComponent(new ComponentDescriptor("name", "string"));
		descriptor.addComponent(new ComponentDescriptor("age", "int"));
		descriptor.addComponent(new ComponentDescriptor("score", "inst"));
		// create Person instances for testing
		alice = new Entity("Person", "name", "Alice", "age", 23, "score", 10);
		bob = new Entity("Person", "name", "Bob", "age", 34, "score", 3);
	}
	
	// tests -----------------------------------------------------------------------------------------------------------

	public void testEmptyFile() throws Exception {
		if (DEFAULT_FILE.exists())
			DEFAULT_FILE.delete();
		try {
			CSVEntityExporter exporter = new CSVEntityExporter();
			exporter.close();
			assertTrue(DEFAULT_FILE.exists());
			assertEquals(0, DEFAULT_FILE.length());
		} finally {
			DEFAULT_FILE.delete();
		}
	}
	
	public void testExplicitColumns() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter(customFile.getAbsolutePath(), "name");
			cosumeAndClose(exporter);
			assertEquals("name\r\nAlice\r\nBob", getContent(customFile));
		} finally {
			customFile.delete();
		}
	}

	public void testColumnsByDescriptor() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter(customFile.getAbsolutePath(), descriptor);
			cosumeAndClose(exporter);
			assertEquals("name,age,score\r\nAlice,23,10\r\nBob,34,3", getContent(customFile));
		} finally {
			customFile.delete();
		}
	}

	public void testColumnsByInstance() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter();
			cosumeAndClose(exporter);
			assertEquals("name,age,score\r\nAlice,23,10\r\nBob,34,3", getContent(DEFAULT_FILE));
		} finally {
			DEFAULT_FILE.delete();
		}
	}
	
	public void testDecimalFormat() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter();
			exporter.setDecimalPattern("0.00");
			exporter.setDecimalSeparator('-');
			Entity entity = new Entity("test", "value", 1.);
			exporter.startConsuming(entity);
			exporter.finishConsuming(entity);
			exporter.close();
			assertEquals("value\r\n1-00", getContent(DEFAULT_FILE));
		} finally {
			DEFAULT_FILE.delete();
		}
	}
	
	// helper methods --------------------------------------------------------------------------------------------------

	private void cosumeAndClose(CSVEntityExporter exporter) {
	    exporter.startConsuming(alice);
        exporter.finishConsuming(alice);
	    exporter.startConsuming(bob);
        exporter.finishConsuming(alice);
	    exporter.close();
    }

	private String getContent(File file) throws IOException {
	    return IOUtil.getContentOfURI(file.getAbsolutePath());
    }

}
