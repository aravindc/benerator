/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.*;

import java.util.Locale;

import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.expression.BeneratorLocaleExpression;
import org.databene.benerator.engine.statement.DBSanityStatement;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.converter.String2EnumConverter;
import org.databene.commons.expression.ConvertingExpression;
import org.databene.dbsanity.ExecutionMode;
import org.w3c.dom.Element;

/**
 * Parses Benerator's &lt;dbsanity&gt; descriptor XML element and maps it to a {@link DBSanityStatement}.<br/><br/>
 * Created: 29.11.2010 11:09:28
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class DbSanity4BeneratorParser extends AbstractBeneratorDescriptorParser {
	
	public DbSanity4BeneratorParser() {
	    super("dbsanity");
    }

	@Override
	public DBSanityStatement parse(Element element, Statement[] parentPath, BeneratorParseContext context) {
        Expression<String> envEx = parseScriptableStringAttribute("environment", element);
        if (envEx == null)
        	throw new ConfigurationError("no environment specified in <dbsanity> element");
        Expression<String> inEx = parseScriptableStringAttribute("in", element);
		Expression<String> outEx = parseScriptableStringAttribute("out", element);
        Expression<String[]> tablesEx = parseScriptableStringArrayAttribute("tables", element);
		Expression<String> skinEx = parseScriptableStringAttribute("skin", element); // online or offline
		Expression<Locale> localeEx = new BeneratorLocaleExpression(parseScriptableStringAttribute("locale", element)); // 2-letter-ISO code
		Expression<String> modeNameEx = parseScriptableStringAttribute("mode", element); // verbose, quiet or default
		Expression<ExecutionMode> modeEx = new ConvertingExpression<String, ExecutionMode>(
				modeNameEx, new String2EnumConverter<ExecutionMode>(ExecutionMode.class));
		Expression<ErrorHandler> errHandlerEx = parseOnErrorAttribute(element, "dbsanity");
		return new DBSanityStatement(envEx, inEx, outEx, tablesEx, skinEx, localeEx, modeEx, errHandlerEx);
    }

}
