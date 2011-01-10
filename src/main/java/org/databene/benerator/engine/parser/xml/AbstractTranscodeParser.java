/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.parseAttribute;
import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptAttribute;

import org.databene.commons.Expression;
import org.databene.commons.expression.ConvertingExpression;
import org.databene.platform.db.DBSystem;
import org.databene.text.SplitStringConverter;
import org.w3c.dom.Element;

/**
 * TODO Document class.<br/><br/>
 * Created: 11.09.2010 07:12:55
 * @since 0.6.4
 * @author Volker Bergmann
 */
public abstract class AbstractTranscodeParser extends AbstractBeneratorDescriptorParser {

	public AbstractTranscodeParser(String elementName, Class<?>... supportedParentTypes) {
	    super(elementName, supportedParentTypes);
    }

	@SuppressWarnings("unchecked")
    protected Expression<Integer> parsePageSize(Element element) {
	    return (Expression<Integer>) parseScriptAttribute("pageSize", element);
    }

	@SuppressWarnings("unchecked")
    protected Expression<DBSystem> parseTarget(Element element) {
	    return (Expression<DBSystem>) parseScriptAttribute("target", element);
    }

	@SuppressWarnings("unchecked")
    protected Expression<DBSystem> parseSource(Element element) {
	    Expression<DBSystem> sourceEx = (Expression<DBSystem>) parseScriptAttribute("source", element);
	    return sourceEx;
    }

	protected Expression<String[]> parseIrrelevantColumns(Element element) {
	    return new ConvertingExpression<String, String[]>(
	    		parseAttribute("irrelevantColumns", element), new SplitStringConverter(','));
    }

}
