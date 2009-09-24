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

package org.databene.platform.xml;

import java.io.IOException;
import java.util.List;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.file.XMLFileGenerator;
import org.databene.model.data.AlternativeGroupDescriptor;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;

import junit.framework.TestCase;

/**
 * Tests the XMLDescriptorProvider.<br/><br/>
 * Created: 26.02.2008 21:05:23
 * @since 0.5.0

 * @author Volker Bergmann
 */
public class XMLSchemaDescriptorProviderTest extends TestCase {
    
    private static final String SIMPLE_ELEMENT_TEST_FILE = "org/databene/platform/xml/simple-element-test.xsd";
    private static final String NESTING_TEST_FILE = "org/databene/platform/xml/nesting-test.xsd";
    private static final String ANNOTATION_TEST_FILE = "org/databene/platform/xml/annotation-test.xsd";
    private static final String CHOICE_TEST_FILE = "org/databene/platform/xml/choice-test.xsd";

    public void testSimpleTypeElement() {
        XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(SIMPLE_ELEMENT_TEST_FILE, new BeneratorContext(null));
        ComplexTypeDescriptor rootDescriptor = (ComplexTypeDescriptor) provider.getTypeDescriptor("root");
        // check root
        assertNotNull(rootDescriptor);
        assertEquals(2, rootDescriptor.getComponents().size());
        // check inline 
        assertComplexComponentWithSimpleContent("inline", rootDescriptor);
        // check external
        assertComplexComponentWithSimpleContent("external", rootDescriptor);
    }

    public void testNesting() {
        XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(NESTING_TEST_FILE, new BeneratorContext(null));
        ComplexTypeDescriptor rootDescriptor = (ComplexTypeDescriptor) provider.getTypeDescriptor("root");
        // check root
        assertNotNull(rootDescriptor);
        assertEquals(4, rootDescriptor.getComponents().size());
        ComponentDescriptor rootAtt1 = rootDescriptor.getComponent("rootAtt1");
        assertNotNull(rootAtt1);
        // check c1
        ComponentDescriptor c1 = rootDescriptor.getComponent("c1");
        assertNotNull(c1);
        // check number
        ComponentDescriptor number = rootDescriptor.getComponent("number");
        assertNotNull(number);
        assertEquals(Long.valueOf(1), number.getMinCount().evaluate(null));
        assertEquals(Long.valueOf(1), number.getMaxCount().evaluate(null));
        // check c2
        ComponentDescriptor c2 = rootDescriptor.getComponent("c2");
        assertNotNull(c2);
    }

    public void testAnnotations() throws IOException {
        XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(ANNOTATION_TEST_FILE, new BeneratorContext(null));
        ComplexTypeDescriptor rootDescriptor = (ComplexTypeDescriptor) provider.getTypeDescriptor("root");
        // check root
        assertNotNull(rootDescriptor);
        assertEquals(2, rootDescriptor.getComponents().size());
        
        // check component root.simple-type
        ComponentDescriptor simpleTypeComponent = rootDescriptor.getComponent("simple-type");
        assertNotNull(simpleTypeComponent);
        
        // check simple-type
        SimpleTypeDescriptor simpleType = (SimpleTypeDescriptor) provider.getTypeDescriptor("simple-type");
        assertNotNull(simpleType);
        assertEquals("Alice,Bob", simpleType.getValues());
        
        // check component root.complex-type
        ComponentDescriptor complexTypeComponent = rootDescriptor.getComponent("complex-type");
        assertNotNull(complexTypeComponent);
        
        // check complex-type
        ComplexTypeDescriptor complexType = (ComplexTypeDescriptor) provider.getTypeDescriptor("complex-type");
        assertNotNull(complexType);
        assertEquals("org/databene/platform/xml/person.csv", complexType.getSource());
        
        XMLFileGenerator g = new XMLFileGenerator(ANNOTATION_TEST_FILE, "root", "test{0}.xml");
        g.generate();
        g.generate();
    }

    public void testChoice() {
        XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(CHOICE_TEST_FILE, new BeneratorContext(null));
        ComplexTypeDescriptor rootDescriptor = (ComplexTypeDescriptor) provider.getTypeDescriptor("root");
        // check root
        assertNotNull(rootDescriptor);
        List<ComponentDescriptor> components = rootDescriptor.getComponents();
		assertEquals(2, components.size());
        
        // check choice a/b
        ComponentDescriptor choiceAB = components.get(0);
        assertNotNull(choiceAB);
        assertEquals(1, choiceAB.getMinCount().evaluate(null).intValue());
        assertEquals(1, choiceAB.getMaxCount().evaluate(null).intValue());
        AlternativeGroupDescriptor choiceABType = (AlternativeGroupDescriptor) choiceAB.getType();
        assertEquals(2, choiceABType.getComponents().size());
        
        // check choice x/y/z
        ComponentDescriptor choiceXYZ = components.get(1);
        assertNotNull(choiceXYZ);
        assertEquals(0, choiceXYZ.getMinCount().evaluate(null).intValue());
        assertEquals(2, choiceXYZ.getMaxCount().evaluate(null).intValue());
        AlternativeGroupDescriptor choiceXYZType = (AlternativeGroupDescriptor) choiceXYZ.getType();
        assertEquals(3, choiceXYZType.getComponents().size());
    }

	private void assertComplexComponentWithSimpleContent(String name, ComplexTypeDescriptor rootDescriptor) {
		ComponentDescriptor stComponent = rootDescriptor.getComponent(name);
        assertNotNull(stComponent);
        assertTrue(stComponent instanceof PartDescriptor);
        ComplexTypeDescriptor stType = (ComplexTypeDescriptor) stComponent.getType();
		ComponentDescriptor content = stType.getComponent(ComplexTypeDescriptor.__SIMPLE_CONTENT);
        assertNotNull(content);
        SimpleTypeDescriptor contentType = (SimpleTypeDescriptor) content.getType();
        assertEquals("string", contentType.getPrimitiveType().getName());
	}
}
