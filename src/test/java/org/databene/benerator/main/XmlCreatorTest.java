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

import java.io.IOException;

import junit.framework.TestCase;

import org.databene.commons.xml.XMLUtil;
import org.w3c.dom.Document;

/**
 * Tests the {@link XmlCreator}.<br/><br/>
 * Created at 05.05.2008 16:53:29
 * @since 0.5.3
 * @author Volker Bergmann
 */
public class XmlCreatorTest extends TestCase {

	private static final String SCHEMA_FILE = "org/databene/platform/xml/simple_type_element_test.xsd";

	public void testSimpleTypeElement() throws IOException {
        createXMLFile(SCHEMA_FILE, "root", "target/" + getClass().getSimpleName() + ".xml");
    }

    private Document createXMLFile(String schemaUri, String root, String filename) throws IOException {
    	String[] args = new String[] { schemaUri, root, filename, "1" };
        XmlCreator.main(args);
        Document document = XMLUtil.parse(filename);
        return document;
    }
}
