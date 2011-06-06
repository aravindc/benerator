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

import java.util.Set;

import static org.databene.benerator.engine.DescriptorConstants.*;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.CascadeParent;
import org.databene.benerator.engine.statement.CascadeStatement;
import org.databene.benerator.engine.statement.MutatingTypeExpression;
import org.databene.benerator.engine.statement.TranscodeStatement;
import org.databene.commons.ArrayUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.xml.XMLUtil;
import org.w3c.dom.Element;

/**
 * Parses &lt;cascade ref="..."&gt; descriptors.<br/><br/>
 * Created: 18.04.2011 08:27:48
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class CascadeParser extends AbstractBeneratorDescriptorParser {

	private static final Set<String> MEMBER_ELEMENTS = CollectionUtil.toSet(
			EL_ID, EL_ATTRIBUTE, EL_REFERENCE);

	public CascadeParser() {
		super(EL_CASCADE, CollectionUtil.toSet(ATT_REF), null, 
				TranscodeStatement.class, CascadeStatement.class);
	}

	@Override
	public Statement doParse(Element element, Statement[] parentPath,
			BeneratorParseContext context) {
		CascadeParent parent = (CascadeParent) ArrayUtil.lastElementOf(parentPath);
		String ref = getRequiredAttribute("ref", element);
		CascadeStatement result = new CascadeStatement(ref, new MutatingTypeExpression(element, null), parent);
		Statement[] currentPath = context.createSubPath(parentPath, result);
	    for (Element child : XMLUtil.getChildElements(element)) {
	    	String childName = child.getNodeName();
	    	if (!MEMBER_ELEMENTS.contains(childName))
	    		result.addSubStatement(context.parseChildElement(child, currentPath));
	    	// The 'component' child elements (id, attribute, reference) are handled by the MutatingTypeExpression 
	    }
		return result;
	}

}
