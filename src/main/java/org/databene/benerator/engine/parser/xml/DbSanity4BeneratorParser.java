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
import static org.databene.benerator.engine.DescriptorConstants.*;

import java.util.Locale;

import org.databene.benerator.engine.BeneratorRootStatement;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.expression.BeneratorLocaleExpression;
import org.databene.benerator.engine.statement.DBSanityStatement;
import org.databene.benerator.engine.statement.IfStatement;
import org.databene.benerator.engine.statement.WhileStatement;
import org.databene.commons.CollectionUtil;
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
	    super(EL_DBSANITY, 
	    		CollectionUtil.toSet(ATT_ENVIRONMENT), 
	    		CollectionUtil.toSet(ATT_IN, ATT_OUT, ATT_TABLES, ATT_SKIN, ATT_LOCALE, ATT_MODE, ATT_ON_ERROR),
	    		BeneratorRootStatement.class, IfStatement.class, WhileStatement.class);
    }

	@Override
	public DBSanityStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
        Expression<String> envEx = parseScriptableStringAttribute(ATT_ENVIRONMENT, element);
        if (envEx == null)
        	throw new ConfigurationError("no environment specified in <dbsanity> element");
        Expression<String> appVersionEx = parseScriptableStringAttribute(ATT_APPVERSION, element);
        Expression<String> inEx = parseScriptableStringAttribute(ATT_IN, element);
		Expression<String> outEx = parseScriptableStringAttribute(ATT_OUT, element);
        Expression<String[]> tablesEx = parseScriptableStringArrayAttribute(ATT_TABLES, element);
		Expression<String> skinEx = parseScriptableStringAttribute(ATT_SKIN, element); // online or offline
		Expression<Locale> localeEx = new BeneratorLocaleExpression(parseScriptableStringAttribute(ATT_LOCALE, element)); // 2-letter-ISO code
		Expression<String> modeNameEx = parseScriptableStringAttribute(ATT_MODE, element); // verbose, quiet or default
		Expression<ExecutionMode> modeEx = new ConvertingExpression<String, ExecutionMode>(
				modeNameEx, new String2EnumConverter<ExecutionMode>(ExecutionMode.class));
		Expression<ErrorHandler> errHandlerEx = parseOnErrorAttribute(element, EL_DBSANITY);
		return new DBSanityStatement(envEx, appVersionEx, inEx, outEx, tablesEx, skinEx, localeEx, modeEx, errHandlerEx);
    }

}
