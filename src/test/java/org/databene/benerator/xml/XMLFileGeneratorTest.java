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

package org.databene.benerator.xml;

import java.io.File;
import java.io.IOException;

import org.databene.benerator.file.XMLFileGenerator;
import org.databene.commons.xml.XMLUtil;

import junit.framework.TestCase;

/**
 * Tests the XMLFileGenerator.<br/><br/>
 * Created: 06.03.2008 11:16:45
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class XMLFileGeneratorTest extends TestCase {
    
    // todo create createXML.bat

    private static final String ROOT_ELEMENT = "root";
    private static final String SCHEMA_FILE = "org/databene/platform/xml/xsdtest.xsd";
/*
    private static final String ROOT_ELEMENT = "product";
    private static final String SCHEMA_FILE = "demo/shop/product-simple.xsd";
    private static final String SCHEMA_FILE = "demo/shop/product-annotated.xsd";

    private static final String OUTPUT_FILE_PREFIX = "setup";
    private static final String SCHEMA_FILE = "org/databene/benerator/benerator-0.5.0.xsd";
*/
    public void test() throws IOException {
        XMLFileGenerator generator = new XMLFileGenerator(SCHEMA_FILE, ROOT_ELEMENT);
        File file = generator.generate();
        System.out.println("Generated " + file);
        XMLUtil.parse(file.getAbsolutePath()); // validate the generated file
    }
}
