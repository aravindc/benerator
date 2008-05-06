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

package org.databene.platform.dbunit;

import java.io.IOException;

import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.Entity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

/**
 * Tests the {@link DbUnitEntityExporter}.<br/><br/>
 * Created at 03.05.2008 13:47:13
 * @since 0.5.3
 * @author Volker Bergmann
 */
public class DbUnitEntityExporterTest extends TestCase {
	
	public static final String URI = "target/" 
		+ DbUnitEntityExporterTest.class.getSimpleName() + ".dbunit.xml";
	
	public static final String ENCODING = "ISO-8859-1";
	
	public void test() throws IOException {
		DbUnitEntityExporter exporter = new DbUnitEntityExporter(URI, ENCODING);
		try {
			Entity entity = new Entity("E", "name", "R&B");
			exporter.startConsuming(entity);
			exporter.finishConsuming(entity);
		} finally {
			exporter.close();
		}
		Document document = XMLUtil.parse(URI);
		assertEquals(ENCODING, document.getXmlEncoding());
		Element root = document.getDocumentElement();
		assertEquals(1, XMLUtil.getChildElements(root).length);
		Element child = XMLUtil.getChildElement(root, false, true, "E");
		assertEquals("E", child.getNodeName());
		assertEquals("R&B", child.getAttribute("name"));
	}
}
