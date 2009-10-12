/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.parser;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.Expression;
import org.databene.commons.xml.XMLUtil;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link ModelParser}.<br/>
 * <br/>
 * Created at 12.03.2009 18:19:12
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class ModelParserTest {

	@Test
    public void testParseBeanClass() throws Exception {
		BeneratorContext context = new BeneratorContext(".");
		ModelParser parser = new ModelParser(context);
		String beanXML = "<bean id='id' class='" + TestBean.class.getName() + "' />";
		Expression<?> ex = parser.parseBean(XMLUtil.parseStringAsElement(beanXML));
		Object bean = ex.evaluate(context);
		assertEquals(TestBean.class, bean.getClass());
		assertEquals(1, ((TestBean) bean).n);
	}
	
	@Test
	public void testParseBeanSpec() throws Exception {
		BeneratorContext context = new BeneratorContext(".");
		ModelParser parser = new ModelParser(context);
		String beanXML = "<bean id='id' spec='new " + TestBean.class.getName() + "(2)' />";
		Expression<?> ex = parser.parseBean(XMLUtil.parseStringAsElement(beanXML));
		Object bean = ex.evaluate(context);
		assertEquals(TestBean.class, bean.getClass());
		assertEquals(2, ((TestBean) bean).n);
	}

}
