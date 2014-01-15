/*
 * (c) Copyright 2014 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

package org.databene.benerator.engine;

import static org.junit.Assert.*;

import java.util.Set;

import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.commons.CollectionUtil;
import org.databene.commons.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Integration-tests Benerator's XML features.<br/><br/>
 * Created: 14.01.2014 10:09:40
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class XmlIntegrationTest extends BeneratorIntegrationTest {
	
	@Test
	public void testAnonymization() throws Exception {
		parseAndExecuteFile("org/databene/benerator/engine/xml/anonymize-xml.ben.xml");
		resourceManager.close();
		
		Set<String> anonNames = CollectionUtil.toSet("Michael", "Maria", "Miles", "Manfred");
		Set<String> anonCities = CollectionUtil.toSet("Munich", "Michigan", "Madrid", "Milano");
		Document document = XMLUtil.parse("target/test-classes/teamplayers-anon.xml");
		NodeList names = XMLUtil.queryNodes(document, "//name/text()");
		for (int i = 0; i < names.getLength(); i++) {
			String name = names.item(i).getTextContent();
			assertTrue("Not an anonymized name: " + name, anonNames.contains(name));
		}
		NodeList cities = XMLUtil.queryNodes(document, "//city/text()");
		for (int i = 0; i < cities.getLength(); i++) {
			String city = cities.item(i).getTextContent();
			assertTrue("not an anonymized city: " + city, anonCities.contains(city));
		}
	}
	
}
