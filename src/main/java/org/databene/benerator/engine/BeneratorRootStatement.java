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

package org.databene.benerator.engine;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.statement.CompositeStatement;
import org.databene.benerator.engine.statement.GenerateAndConsumeEntityTask;
import org.databene.benerator.engine.statement.LazyStatement;
import org.databene.benerator.engine.statement.StatementProxy;
import org.databene.model.data.Entity;

/**
 * The root {@link Statement} for executing descriptor file based data generation.<br/><br/>
 * Created: 24.10.2009 11:08:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class BeneratorRootStatement extends CompositeStatement {

    public Generator<Entity> getGenerator(String name, BeneratorContext context) {
		// TODO use visitor pattern
		for (Statement subStatement : subStatements) {
			if (match(name, subStatement))
				return ((GenerateAndConsumeEntityTask) subStatement).getEntityGenerator();
			Statement tmp = subStatement;
			while (tmp instanceof StatementProxy || tmp instanceof LazyStatement) {
				if (tmp instanceof StatementProxy)
					tmp = ((StatementProxy) tmp).getRealStatement();
				else
					tmp = (Statement) ((LazyStatement) tmp).getTargetExpression().evaluate(null);
				if (match(name, tmp))
					return ((GenerateAndConsumeEntityTask) tmp).getEntityGenerator();
			}
			subStatement.execute(context);
		}
		throw new IllegalArgumentException("Generator not found: " + name);
	}

	private boolean match(String name, Statement statement) {
		return false;
// TODO	    return (statement instanceof GenerateAndConsumeEntityTask && name.equals(statement.getTaskName()));
    }

}
