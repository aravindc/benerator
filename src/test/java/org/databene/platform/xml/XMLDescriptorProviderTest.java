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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.commons.context.DefaultContext;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.TypeDescriptor;

import junit.framework.TestCase;

/**
 * Tests the XMLDescriptorProvider.<br/><br/>
 * Created: 26.02.2008 21:05:23
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class XMLDescriptorProviderTest extends TestCase {
    
    private static final Log logger = LogFactory.getLog(XMLDescriptorProviderTest.class);
    
    private static final String NESTING_TEST_FILE = "org/databene/platform/xml/simple_type_element_test.xsd";

    public void testNesting() throws IOException {
        XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(NESTING_TEST_FILE, new DefaultContext());

        for (TypeDescriptor descriptor : provider.getTypeDescriptors())
            logger.debug(descriptor);

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
        assertEquals(Long.valueOf(1), number.getMinCount());
        assertEquals(Long.valueOf(1), number.getMaxCount());
        // check c2
        ComponentDescriptor c2 = rootDescriptor.getComponent("c2");
        assertNotNull(c2);
    }

}
