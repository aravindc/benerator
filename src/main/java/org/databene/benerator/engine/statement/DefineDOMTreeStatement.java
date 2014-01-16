/*
 * (c) Copyright 2014 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.statement;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.Statement;
import org.databene.commons.ConfigurationError;
import org.databene.platform.xml.DOMTree;
import org.databene.script.Expression;
import org.databene.script.expression.ExpressionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Statement} for creating a {@link DOMTree} element 
 * and assigning it with context and resource manager.<br/><br/>
 * Created: 16.01.2014 16:07:06
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class DefineDOMTreeStatement implements Statement {
	
	private static Logger logger = LoggerFactory.getLogger(DefineDOMTreeStatement.class);
	
	private ResourceManager resourceManager;
	
	private Expression<String>  id;
	private Expression<String>  inputUri;
	private Expression<String>  outputUri;
	private Expression<Boolean> namespaceAware;
	
	public DefineDOMTreeStatement(Expression<String> id, Expression<String> inputUri, 
			Expression<String> outputUri, Expression<Boolean> namespaceAware, ResourceManager resourceManager) {
		if (id == null)
			throw new ConfigurationError("No DOMTree id defined");
		this.id = id;
		this.inputUri = inputUri;
	    this.outputUri = outputUri;
	    this.namespaceAware = namespaceAware;
	    this.resourceManager = resourceManager;
    }

	@Override
    public boolean execute(BeneratorContext context) {
	    logger.debug("Instantiating database with id '" + id + "'");
	    String idValue = id.evaluate(context);
		String inputUriValue = ExpressionUtil.evaluate(inputUri, context);
	    DOMTree domTree = new DOMTree(inputUriValue, context);
		
		String outputUriValue = ExpressionUtil.evaluate(outputUri, context);
		if (outputUriValue != null)
			domTree.setOutputUri(outputUriValue);
		Boolean namespaceAwareValue = ExpressionUtil.evaluate(namespaceAware, context);
		if (namespaceAware != null)
			domTree.setNamespaceAware(namespaceAwareValue);

	    // register this object on all relevant managers and in the context
	    context.setGlobal(idValue, domTree);
	    context.getDataModel().addDescriptorProvider(domTree, context.isValidate());
	    resourceManager.addResource(domTree);
    	return true;
    }

}
