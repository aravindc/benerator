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

package org.databene.benerator.engine.statement;

import java.io.Closeable;
import java.io.IOException;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.commons.Expression;

/**
 * {@link Statement} implementation that evaluates an {@link Expression} 
 * which returns a Task and executes the returned Task.<br/>
 * <br/>
 * Created: 27.10.2009 16:09:20
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class LazyStatement implements Statement, Closeable { // TODO v0.7 remove this class

	private Expression<Statement> targetExpression;
	private Statement target;

    public LazyStatement(Expression<Statement> targetExpression) {
	    this.targetExpression = targetExpression;
	    this.target = null;
    }

	public Expression<Statement> getTargetExpression() {
	    return targetExpression;
    }

	public Statement getTarget(BeneratorContext context) {
	    if (target == null)
	    	target = targetExpression.evaluate(context);
	    return target;
	}
	
	public void execute(BeneratorContext context) {
	    getTarget(context).execute(context);
    }

	@Override
	public String toString() {
	    return getClass().getSimpleName() + '(' + (target != null ? target : targetExpression) + ')';
	}

	public void close() throws IOException {
		if (target instanceof Closeable)
			((Closeable) target).close();
	}

}
