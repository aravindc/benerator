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

package org.databene.benerator.xml;

import java.io.File;
import java.io.IOException;

import org.databene.benerator.file.XMLFileGenerator;
import org.databene.commons.xml.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

/**
 * Tests the XMLFileGenerator.<br/><br/>
 * Created: 06.03.2008 11:16:45
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class XMLFileGeneratorTest extends TestCase {
    
    private static final Logger logger = LoggerFactory.getLogger(XMLFileGeneratorTest.class);
    
/*
    private static final String ROOT_ELEMENT = "product";
    private static final String SCHEMA_FILE = "demo/shop/product-simple.xsd";
    private static final String SCHEMA_FILE = "demo/shop/product-annotated.xsd";
*/
/*
    public void testSimpleTypeElement() throws IOException {
        createXMLFile("org/databene/platform/xml/simple-element-test.xsd", "root", "target/simple-element-test.xml");
    }
*/    
    public void testBean() throws IOException {
        Document document = createXMLFile("org/databene/benerator/xml/bean_test.xsd", "root", "target/bean_test.xml");
        Element root = document.getDocumentElement();
        assertEquals("root", root.getNodeName());
        Element[] children = XMLUtil.getChildElements(root);
        assertEquals(1, children.length);
        assertElementNameAndText(children[0], "result", "OK");
    }

    public void testVariables() throws IOException {
        Document document = createXMLFile("org/databene/benerator/xml/variable_test.xsd", "root", "target/variable_test.xml");
        Element root = document.getDocumentElement();
        assertEquals("root", root.getNodeName());
        assertEquals(0, root.getChildNodes().getLength());
        assertEquals("OK", root.getAttribute("string_att"));
        assertEquals("Bob", root.getAttribute("bean_att"));
        assertEquals("Alice", root.getAttribute("entity_att"));
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    private void assertElementNameAndText(Element child, String name, String text) {
        assertNotNull(child);
        assertEquals(name, child.getNodeName());
        assertEquals(text, XMLUtil.getText(child));
    }

    private Document createXMLFile(String schemaUri, String root, String filename) throws IOException {
        XMLFileGenerator generator = new XMLFileGenerator(schemaUri, root, filename);
        File file = generator.generate();
        assertNotNull(file);
        logger.debug("Generated " + file);
        generator.close();
        // validate the generated file
        Document document = XMLUtil.parse(file.getAbsolutePath());
        return document;
    }

}
