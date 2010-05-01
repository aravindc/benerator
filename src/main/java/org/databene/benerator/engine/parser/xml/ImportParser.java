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

import java.util.Set;

import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.ImportStatement;
import org.databene.commons.ArrayBuilder;
import org.databene.commons.CollectionUtil;
import org.databene.commons.StringUtil;
import org.w3c.dom.Element;
import static org.databene.benerator.engine.DescriptorConstants.*;

/**
 * Parses an &lt;import&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:53:06
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ImportParser extends AbstractDescriptorParser {
	
	private static final Set<String> SUPPORTED_ATTRIBUTES = CollectionUtil.toSet(ATT_CLASS, ATT_DEFAULTS, ATT_DOMAINS, ATT_PLATFORMS);

	public ImportParser() {
	    super(EL_IMPORT);
    }

	public ImportStatement parse(Element element, Statement[] parentPath, ResourceManager resourceManager) {
		
		checkAttributes(element, SUPPORTED_ATTRIBUTES);
		
		ArrayBuilder<String> classImports = new ArrayBuilder<String>(String.class); 
		ArrayBuilder<String> domainImports = new ArrayBuilder<String>(String.class); 
		ArrayBuilder<String> platformImports = new ArrayBuilder<String>(String.class); 
		
		// defaults import
		boolean defaults = ("true".equals(element.getAttribute("defaults")));
		
		// check class import
		String attribute = element.getAttribute("class");
		if (!StringUtil.isEmpty(attribute))
			classImports.add(attribute);
		
		// (multiple) domain import
		attribute = element.getAttribute("domains");
		if (!StringUtil.isEmpty(attribute))
			domainImports.addAll(StringUtil.trimAll(StringUtil.tokenize(attribute, ',')));
		
		// (multiple) platform import
		attribute = element.getAttribute("platforms");
		if (!StringUtil.isEmpty(attribute))
			platformImports.addAll(StringUtil.trimAll(StringUtil.tokenize(attribute, ',')));
		
		return new ImportStatement(defaults, classImports.toArray(), 
				domainImports.toArray(), platformImports.toArray());
	}

}
