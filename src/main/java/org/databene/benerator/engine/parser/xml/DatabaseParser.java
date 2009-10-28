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

package org.databene.benerator.engine.parser.xml;

import static org.databene.benerator.engine.DescriptorConstants.*;

import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.expression.TypedScriptExpression;
import org.databene.benerator.engine.statement.DefineDatabaseStatement;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ConversionException;
import org.databene.commons.Expression;
import org.databene.commons.expression.StringExpression;
import org.w3c.dom.Element;

/**
 * TODO Document class.<br/><br/>
 * Created: 25.10.2009 00:40:56
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DatabaseParser extends AbstractDescriptorParser {
	
	public DatabaseParser() {
	    super(EL_DATABASE);
    }

	public DefineDatabaseStatement parse(Element element, ResourceManager resourceManager) {
		try {
			StringExpression  id       = parseStringAttr(ATT_ID,       element);
			StringExpression  url      = parseStringAttr(ATT_URL,      element);
			StringExpression  driver   = parseStringAttr(ATT_DRIVER,   element);
			StringExpression  user     = parseStringAttr(ATT_USER,     element);
			StringExpression  password = parseStringAttr(ATT_PASSWORD, element);
			StringExpression  schema   = parseStringAttr(ATT_SCHEMA,   element);
			Expression batch     = new TypedScriptExpression(element.getAttribute(ATT_BATCH), Boolean.class, false);
			Expression fetchSize = new TypedScriptExpression(element.getAttribute(ATT_FETCH_SIZE), Integer.class, 100);
			Expression readOnly  = new TypedScriptExpression(element.getAttribute(ATT_READ_ONLY), Boolean.class, false);
			return new DefineDatabaseStatement(id, url, driver, user, password, schema, batch, fetchSize, readOnly, resourceManager);
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
    }

}
