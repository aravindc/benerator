/*
 * (c) Copyright 2009-2013 by Volker Bergmann. All rights reserved.
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

/**
 * Proxy for a {@link Statement}.<br><br>
 * Created: 27.10.2009 16:06:04
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class StatementProxy implements Statement, Closeable {
	
	protected Statement realStatement;

	public StatementProxy(Statement realStatement) {
	    this.realStatement = realStatement;
    }

	@Override
	public boolean execute(BeneratorContext context) {
	    return realStatement.execute(context);
    }

	public Statement getRealStatement(BeneratorContext context) {
	    return realStatement;
    }

	@Override
	public void close() throws IOException {
		if (realStatement instanceof Closeable)
			((Closeable) realStatement).close();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + realStatement + "]";
	}
	
}
