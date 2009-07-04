/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.address;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorClassTest;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.InstanceGeneratorFactory;
import org.databene.benerator.parser.ModelParser;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Tests the AddressGenerator<br/><br/>
 * Created: 12.06.2007 06:45:41
 * @author Volker Bergmann
 */
public class AddressGeneratorTest extends GeneratorClassTest {

    private static final Logger logger = LoggerFactory.getLogger(AddressGeneratorTest.class);

    public AddressGeneratorTest() {
        super(AddressGenerator.class);
    }
    
    // tests -----------------------------------------------------------------------------------------------------------

    public void testGermany() {
        check(Country.GERMANY, true);
    }

    public void testUS() {
        check(Country.US, true);
    }

    public void testSingapore() {
        check(Country.SINGAPORE, false);
    }
    
    public void testDefaultDescriptorMapping() throws Exception {
    	Country country = Country.getDefault();
    	try {
    		Country.setDefault(Country.GERMANY);
    		checkDescriptorMapping(null);
    	} finally {
    		Country.setDefault(country);
    	}
    }
    
    public void testUSDescriptorMapping() throws Exception {
    	checkDescriptorMapping(Country.US);
    }
    
    public void testDEDescriptorMapping() throws Exception {
    	checkDescriptorMapping(Country.GERMANY);
    }
    
    // helper ----------------------------------------------------------------------------------------------------------

    private void check(Country country, boolean supported) {
        AddressGenerator generator = new AddressGenerator(country);
        for (int i = 0; i < 100; i++) {
            Address address = generator.generate();
            assertNotNull(address);
            if (supported)
            	assertEquals(country, address.getCountry());
            else
            	assertEquals(Country.US, address.getCountry());
            logger.debug(address.toString());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void checkDescriptorMapping(Country country) throws Exception {
    	String xml = 
    		"<variable name='x' " +
    			"generator='org.databene.domain.address.AddressGenerator' ";
    	if (country != null)
    		xml += "dataset='" + country.getIsoCode() + "'";
    	xml += "/>";
		Element element = XMLUtil.parseStringAsElement(xml);
    	BeneratorContext context = new BeneratorContext(".");
		ModelParser parser = new ModelParser(context);
    	ComplexTypeDescriptor parent = new ComplexTypeDescriptor("y");
		InstanceDescriptor descriptor = parser.parseVariable(element, parent);
		Generator<Address> generator = (Generator<Address>) InstanceGeneratorFactory.createInstanceGenerator(descriptor, context);
        Country generatedCountry = generator.generate().getCountry();
		if (country == null) {
	        assertEquals(Country.GERMANY, generatedCountry);
        } else
			assertEquals(country, generatedCountry);
    }
    

}