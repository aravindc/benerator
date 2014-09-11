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

import org.databene.benerator.Consumer;
import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.commons.IOUtil;
import org.databene.platform.template.TemplateFileEntityExporter;
import org.junit.Test;

/**
 * Tests the {@link TemplateFileEntityExporter}.<br/><br/>
 * Created: 27.06.2014 17:42:34
 * @since 0.9.7
 * @author Volker Bergmann
 */

public class TemplateFileEntityExporterIntegrationTest extends BeneratorIntegrationTest {

	@Test
	public void testCountries() throws Exception {
		String uri = "target/" + getClass().getName() + ".txt";
		String templateUri = "org/databene/benerator/engine/template/countries.ftl";
		BeneratorContext context = parseAndExecute(
			"<setup>" +
			"<bean id='con' class='TemplateFileEntityExporter'>" + 
			"	<property name='uri' value='" + uri + "'/>" + 
			"	<property name='templateUri' value='" + templateUri + "'/>" + 
			"</bean>" +
			"<generate type='countries' count='3' consumer='con'>" +
	    	"	<attribute name='name' pattern='[A-Z]{5,10}' />" + 
	    	"	<attribute name='population' type='int' min='1000000' max='100000000' />" + 
			"   <generate type='states' minCount='3' maxCount='7' consumer='con'>" +
	    	"      <attribute name='name' pattern='[A-Z]{5,10}' />" + 
	    	"   </generate>" +
	    	"</generate>" +
	    	"</setup>");
		closeCon(context);
		String content = IOUtil.getContentOfURI(uri);
		System.out.println(content);
	}

	@Test
	public void testIFTDGN1() throws Exception {
		BeneratorContext context = parseAndExecuteFile("org/databene/benerator/engine/template/IFTDGN1.ben.xml");
		closeCon(context);
		String content = IOUtil.getContentOfURI("target/IFTDGN1.edi");
		System.out.println(content);
	}

	@Test
	public void testIFTDGN2() throws Exception {
		BeneratorContext context = parseAndExecuteFile("org/databene/benerator/engine/template/IFTDGN2.ben.xml");
		closeCon(context);
		IOUtil.close(context);
		String content = IOUtil.getContentOfURI("target/IFTDGN2.edi");
		System.out.println(content);
	}

	private static void closeCon(BeneratorContext context) {
		Consumer con = (Consumer) context.get("con");
		con.close();
	}

}
