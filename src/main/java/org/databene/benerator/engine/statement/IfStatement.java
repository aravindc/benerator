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

package org.databene.benerator.engine.statement;

import java.util.List;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.commons.Expression;

/**
 * {@link CompositeStatement} that executes it parts 
 * only if a condition is matched.<br/><br/>
 * Created: 19.02.2010 09:13:30
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class IfStatement extends ConditionStatement {
	
	private CompositeStatement thenBranch;
	private CompositeStatement elseBranch;

	public IfStatement(Expression<Boolean> condition) {
	    super(condition);
    }

	public IfStatement(Expression<Boolean> condition, 
			List<Statement> thenStatements, List<Statement> elseStatements) {
	    super(condition);
	    setThenStatements(thenStatements);
	    setElseStatements(elseStatements);
    }

    public void execute(BeneratorContext context) {
	    if (condition.evaluate(context))
	    	thenBranch.execute(context);
	    else
	    	elseBranch.execute(context);
    }

	public void setThenStatements(List<Statement> thenStatements) {
	    this.thenBranch = new SequentialStatement(thenStatements);
    }

	public void setElseStatements(List<Statement> elseStatements) {
	    this.elseBranch = new SequentialStatement(elseStatements);
    }

}
