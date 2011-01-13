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

import static org.databene.benerator.engine.DescriptorConstants.EL_SETUP;

import java.util.List;

import org.databene.benerator.engine.BeneratorRootStatement;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.XMLNameNormalizer;
import org.databene.commons.xml.XMLUtil;
import org.databene.webdecs.xml.XMLElementParser;
import org.w3c.dom.Element;

/**
 * {@link XMLElementParser} implementation for parsing a Benerator descriptor file's root XML element.<br/><br/>
 * Created: 14.12.2010 19:48:00
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class SetupParser extends AbstractBeneratorDescriptorParser {

	public SetupParser() {
		super(EL_SETUP);
	}

	@Override
	public Statement parse(Element element, Statement[] parentPath, BeneratorParseContext context) {
	    XMLUtil.mapAttributesToProperties(element, context, true, new XMLNameNormalizer());
	    BeneratorRootStatement rootStatement = new BeneratorRootStatement();
	    Statement[] currentPath = context.createSubPath(parentPath, rootStatement);
		List<Statement> subStatements = context.parseChildElementsOf(element, currentPath);
	    rootStatement.setSubStatements(subStatements);
	    return rootStatement;
	}

}
