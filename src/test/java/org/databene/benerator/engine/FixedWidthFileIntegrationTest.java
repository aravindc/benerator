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

import static org.junit.Assert.assertEquals;

import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.commons.IOUtil;
import org.databene.commons.SystemInfo;
import org.databene.platform.fixedwidth.FixedWidthEntityExporter;
import org.junit.Test;

/**
 * Integration tests for the {@link FixedWidthEntityExporter}.<br/><br/>
 * Created: 25.03.2014 10:44:49
 * @since 0.9.2
 * @author Volker Bergmann
 */

public class FixedWidthFileIntegrationTest extends BeneratorIntegrationTest {

	@Test
	public void testNullColumns() throws Exception {
		String fileName = "target/" + getClass().getName() + ".fcw";
		parseAndExecute(
			"<setup>" +
			"<bean id='fcw' class='FixedWidthEntityExporter'>" + 
			"	<property name='uri' value='" + fileName + "'/>" + 
			"	<property name='columns' value='x[3],y[1]'/>" + 
			"</bean>" +
			"<generate type='t' count='1' consumer='fcw'>" +
	    	"	<attribute name='x' type='string' nullQuota='1' />" + 
	    	"	<attribute name='y' type='string' constant='Y' />" + 
	    	"</generate>" +
	    	"</setup>");
		String content = IOUtil.getContentOfURI(fileName);
		assertEquals("   Y" + SystemInfo.getLineSeparator(), content);
		
	}

}
