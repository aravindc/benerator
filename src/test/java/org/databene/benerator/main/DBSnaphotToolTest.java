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

package org.databene.benerator.main;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.databene.commons.db.DBUtil;
import org.databene.commons.db.hsql.HSQLUtil;
import org.databene.commons.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

/**
 * Tests the DBSnapshotTool.<br/><br/>
 * Created at 03.05.2008 11:39:01
 * @since 0.5.3
 * @author Volker Bergmann
 */
public class DBSnaphotToolTest extends TestCase {

	private static final String SCRIPT = "org/databene/benerator/main/create_tables.hsql.sql";
	private static final String SNAPSHOT = "target/test.snapshot.dbunit.xml";
	private static final String ENCODING = "iso-8859-15";
	
	public void testMissingUrl() {
		try {
			System.setProperty(DBSnapshotTool.DB_URL, "");
			System.setProperty(DBSnapshotTool.DB_DRIVER, HSQLUtil.DRIVER);
			DBSnapshotTool.main(new String[0]);
			fail("Expected " + IllegalArgumentException.class.getSimpleName());
		} catch (IllegalArgumentException e) {
			// this is required
		}
	}

	public void testMissingDriver() {
		try {
			System.setProperty(DBSnapshotTool.DB_URL, HSQLUtil.IN_MEMORY_URL_PREFIX + "benerator");
			System.setProperty(DBSnapshotTool.DB_DRIVER, "");
			DBSnapshotTool.main(new String[0]);
			fail("Expected " + IllegalArgumentException.class.getSimpleName());
		} catch (IllegalArgumentException e) {
			// this is required
		}
	}
	
	public void testSuccess() throws SQLException, IOException {
		// prepare DB
		String db = getClass().getSimpleName();
		Connection connection = HSQLUtil.connectInMemoryDB(db);
		DBUtil.runScript(SCRIPT, ENCODING, connection, true, false);
		// prepare snapshot
		System.setProperty(DBSnapshotTool.DB_URL, HSQLUtil.IN_MEMORY_URL_PREFIX + db);
		System.setProperty(DBSnapshotTool.DB_DRIVER, HSQLUtil.DRIVER);
		System.setProperty(DBSnapshotTool.DB_USER, HSQLUtil.DEFAULT_USER);
		System.setProperty(DBSnapshotTool.DB_SCHEMA, HSQLUtil.DEFAULT_SCHEMA);
		System.setProperty(DBSnapshotTool.DB_SCHEMA, HSQLUtil.DEFAULT_SCHEMA);
		System.setProperty("file.encoding", ENCODING);
		// create snapshot
		DBSnapshotTool.main(new String[] { SNAPSHOT });
		File file = new File(SNAPSHOT);
		assertTrue(file.exists());
		Document document = XMLUtil.parse(SNAPSHOT);
		assertTrue(ENCODING.equalsIgnoreCase(document.getXmlEncoding()));
		Element root = document.getDocumentElement();
		assertEquals("dataset", root.getNodeName());
		assertEquals(1, XMLUtil.getChildElements(root).length);
		Element child = XMLUtil.getChildElement(root, false, true, "T1");
		assertEquals("1", child.getAttribute("ID"));
		assertEquals("R&B", child.getAttribute("NAME"));
	}
}
