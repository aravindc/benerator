/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

import java.util.Map;

import org.databene.benerator.engine.BeneratorRootStatement;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.IfStatement;
import org.databene.benerator.engine.statement.MemStoreStatement;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ConversionException;
import org.databene.commons.StringUtil;
import org.databene.commons.xml.XMLUtil;
import org.w3c.dom.Element;

/**
 * Parses a &lt;memstore%gt; statement.<br/><br/>
 * Created: 08.03.2011 13:28:55
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class MemStoreParser extends AbstractBeneratorDescriptorParser {
	
	public MemStoreParser() {
	    super(EL_MEMSTORE, CollectionUtil.toSet(ATT_ID), null, BeneratorRootStatement.class, IfStatement.class);
    }

	@Override
    public MemStoreStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		checkAttributeSupport(XMLUtil.getAttributes(element));
		try {
			String id = getAttribute(ATT_ID, element);
			return new MemStoreStatement(id, context.getResourceManager());
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
    }

	private void checkAttributeSupport(Map<String, String> attributes) {
		if (StringUtil.isEmpty(attributes.get(ATT_ID)))
			throw new ConfigurationError("No id specified for <store>");
		for (String key : attributes.keySet())
			if (!"id".equals(key))
				throw new ConfigurationError("Not a supported attribute of <store>: " + key);
	}

}
