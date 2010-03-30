/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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
import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.*;

import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.statement.DefineDatabaseStatement;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ConversionException;
import org.databene.commons.Expression;
import org.w3c.dom.Element;

/**
 * Parses a &lt;database&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:40:56
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DatabaseParser extends AbstractDescriptorParser {
	
	// TODO v1.0 define parser extension mechanism and move DatabaseParser and DefineDatabaseStatement to DB package?
	
	public DatabaseParser() {
	    super(EL_DATABASE);
    }

	public DefineDatabaseStatement parse(Element element, final Element parent, ResourceManager resourceManager) {
		try {
			Expression<String>  id          = parseAttribute(ATT_ID, element);
			Expression<String>  url         = parseScriptableStringAttribute(ATT_URL,      element);
			Expression<String>  driver      = parseScriptableStringAttribute(ATT_DRIVER,   element);
			Expression<String>  user        = parseScriptableStringAttribute(ATT_USER,     element);
			Expression<String>  password    = parseScriptableStringAttribute(ATT_PASSWORD, element);
			Expression<String>  schema      = parseScriptableStringAttribute(ATT_SCHEMA,   element);
			Expression<String>  tableFilter = parseScriptableStringAttribute(ATT_TABLE_FILTER, element);
			Expression<Boolean> batch       = parseBooleanExpressionAttribute(ATT_BATCH, element, false);
			Expression<Integer> fetchSize   = parseIntAttribute(ATT_FETCH_SIZE, element, 100);
			Expression<Boolean> readOnly    = parseBooleanExpressionAttribute(ATT_READ_ONLY, element, false);
			return new DefineDatabaseStatement(id, url, driver, user, password, schema, tableFilter, batch, fetchSize, readOnly, resourceManager);
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
    }

}
