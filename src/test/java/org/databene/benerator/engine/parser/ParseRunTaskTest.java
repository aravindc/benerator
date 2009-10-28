/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.parser;

import static org.junit.Assert.*;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManagerSupport;
import org.databene.benerator.engine.parser.xml.RunTaskParser;
import org.databene.benerator.engine.statement.RunTaskStatement;
import org.databene.commons.xml.XMLUtil;
import org.databene.task.PageListenerMock;
import org.databene.task.TaskMock;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * TODO Document class.<br/><br/>
 * Created: 26.10.2009 07:07:40
 * @since TODO version
 * @author Volker Bergmann
 */
public class ParseRunTaskTest {

    @Test
	public void test() throws Exception {
        String uri = "string://" +
        		"<run-task id='myId' class='org.databene.task.TaskMock' count='5' pagesize='2' " +
        		"      pager='new org.databene.task.PageListenerMock(1)'>" +
        		"  <property name='intProp' value='42' />" +
        		"</run-task>";
        Document doc = XMLUtil.parse(uri);
        RunTaskParser parser = new RunTaskParser();
		RunTaskStatement task = parser.parse(doc.getDocumentElement(), new ResourceManagerSupport());
		BeneratorContext context = new BeneratorContext();
		assertEquals(5, task.getCount().evaluate(context));
		assertEquals(2, task.getPageSize().evaluate(context));
		assertEquals(new PageListenerMock(1), task.getPager().evaluate(context));
		task.execute(new BeneratorContext());
		assertEquals(5, TaskMock.count.get());
	}
	
}
