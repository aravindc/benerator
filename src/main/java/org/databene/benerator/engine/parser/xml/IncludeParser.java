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

import org.databene.benerator.engine.DescriptorConstants;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.expression.ScriptableExpression;
import org.databene.benerator.engine.statement.IncludeStatement;
import org.databene.commons.Expression;
import org.databene.commons.expression.StringExpression;
import org.w3c.dom.Element;

/**
 * Parses an <lt;include&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:32:02
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class IncludeParser extends AbstractDescriptorParser {

	public IncludeParser() {
	    super(DescriptorConstants.EL_INCLUDE);
    }

	public IncludeStatement parse(Element element, ResourceManager resourceManager) {
        String uriAttr = element.getAttribute(DescriptorConstants.ATT_URI);
		Expression<String> uriEx = new StringExpression(new ScriptableExpression(uriAttr, null));
        return new IncludeStatement(uriEx);
    }

}
