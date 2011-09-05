/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.statement.GeneratorStatement;
import org.databene.benerator.engine.statement.IncludeStatement;
import org.databene.benerator.engine.statement.LazyStatement;
import org.databene.benerator.engine.statement.SequentialStatement;
import org.databene.benerator.engine.statement.StatementProxy;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.wrapper.NShotGeneratorProxy;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Expression;
import org.databene.commons.Visitor;
import org.databene.commons.expression.ExpressionUtil;

/**
 * The root {@link Statement} for executing descriptor file based data generation.<br/><br/>
 * Created: 24.10.2009 11:08:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class BeneratorRootStatement extends SequentialStatement {

	private Map<String, String> attributes;
	
    public BeneratorRootStatement(Map<String, String> attributes) {
    	this.attributes = new HashMap<String, String>(attributes);
	}

    @Override
    public boolean execute(BeneratorContext context) {
    	mapAttributesTo(context);
    	if (context.isDefaultImports())
    		context.importDefaults();
    	super.execute(context);
    	return true;
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public Generator<?> getGenerator(String name, BeneratorContext context) {
    	GeneratorStatement statement = getGeneratorStatement(name, context);
    	Generator<?> generator = new TaskBasedGenerator(statement.getTarget());
		return new NShotGeneratorProxy(generator, statement.generateCount(context));
	}

    public GeneratorStatement getGeneratorStatement(String name, BeneratorContext context) {
    	BeneratorVisitor visitor = new BeneratorVisitor(name, context);
    	accept(visitor);
    	GeneratorStatement statement = visitor.getResult();
		if (statement == null)
    		throw new IllegalArgumentException("Generator not found: " + name);
    	return statement;
	}

	protected void mapAttributesTo(BeneratorContext context) {
		for (Entry<String, String> attribute : attributes.entrySet()) {
    		String key = attribute.getKey();
			String value = attribute.getValue();
			Object result;
			if ("generatorFactory".equals(key))
    			result = BeneratorScriptParser.parseBeanSpec(value).evaluate(context);
			else 
				result = value;
			BeanUtil.setPropertyValue(context, key, result, true, true);
    	}
	}
    
	class BeneratorVisitor implements Visitor<Statement> {
		
		private String name;
		private BeneratorContext context;
		private GeneratorStatement result;
		
		public BeneratorVisitor(String name, BeneratorContext context) {
	        this.name = name;
	        this.context = context;
        }

		public GeneratorStatement getResult() {
        	return result;
        }

		public void visit(Statement statement) {
			if (result != null)
				return;
			if (statement instanceof GeneratorStatement) {
				GeneratorStatement generatorStatement = (GeneratorStatement) statement;
				GeneratorTask target = generatorStatement.getTarget();
				if (name.equals(target.getTaskName())) {
					result = generatorStatement;
					return;
				}
			} else if (statement instanceof StatementProxy)
				visit(((StatementProxy) statement).getRealStatement(context));
			else if (statement instanceof LazyStatement) {
	            Expression<Statement> targetExpression = ((LazyStatement) statement).getTargetExpression();
	            visit(ExpressionUtil.evaluate(targetExpression, context));
            } else if (statement instanceof IncludeStatement) {
                String uri = ((IncludeStatement) statement).getUri().evaluate(context);
                if (uri != null && uri.toLowerCase().endsWith(".xml")) {
	                DescriptorRunner descriptorRunner = new DescriptorRunner(context.resolveRelativeUri(uri), context);
	            	try {
		                BeneratorRootStatement rootStatement = descriptorRunner.parseDescriptorFile();
		                result = rootStatement.getGeneratorStatement(name, context);
		                return;
	                } catch (IOException e) {
		                throw new ConfigurationError("error parsing file " + uri, e);
	                }
                }
            } else if (!(statement instanceof BeneratorRootStatement))
            	statement.execute(context);
        }
	}
	
	@Override
	public String toString() {
	    return "root";
	}
	
}
