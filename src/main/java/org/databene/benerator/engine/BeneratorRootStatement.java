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

package org.databene.benerator.engine;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.statement.GeneratorStatement;
import org.databene.benerator.engine.statement.LazyStatement;
import org.databene.benerator.engine.statement.SequentialStatement;
import org.databene.benerator.engine.statement.StatementProxy;
import org.databene.commons.Expression;
import org.databene.commons.Visitor;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.model.data.Entity;

/**
 * The root {@link Statement} for executing descriptor file based data generation.<br/><br/>
 * Created: 24.10.2009 11:08:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class BeneratorRootStatement extends SequentialStatement {

    public Generator<Entity> getGenerator(String name, BeneratorContext context) {
    	GenVisitor visitor = new GenVisitor(name, context);
    	accept(visitor);
    	if (visitor.getResult() == null)
    		throw new IllegalArgumentException("Generator not found: " + name);
    	return visitor.getResult();
	}

	class GenVisitor implements Visitor<Statement> {
		
		private String name;
		private BeneratorContext context;
		private Generator<Entity> result;
		
		public GenVisitor(String name, BeneratorContext context) {
	        super();
	        this.name = name;
	        this.context = context;
        }

		public Generator<Entity> getResult() {
        	return result;
        }

		public void visit(Statement element) {
			if (result != null)
				return;
			if (element instanceof GeneratorStatement) {
				GeneratorStatement candidate = (GeneratorStatement) element;
				GeneratorTask target = candidate.getTarget();
				if (name.equals(target.getTaskName())) {
					result = target.getGenerator();
					return;
				}
			} else if (element instanceof StatementProxy)
				visit(((StatementProxy) element).getRealStatement());
			else if (element instanceof LazyStatement) {
	            Expression<Statement> targetExpression = ((LazyStatement) element).getTargetExpression();
	            visit(ExpressionUtil.evaluate(targetExpression, context));
            }
			element.execute(context);
        }
	}
	
}
