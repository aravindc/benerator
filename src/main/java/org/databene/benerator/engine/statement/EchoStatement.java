/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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
import org.databene.benerator.engine.Statement;
import org.databene.commons.SpeechUtil;
import org.databene.script.Expression;
import org.databene.script.expression.ExpressionUtil;

/**
 * Prints out a message to the console.<br/>
 * <br/>
 * Created at 22.07.2009 07:13:28
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class EchoStatement implements Statement {
	
	private final Expression<String> messageEx;
	private final Expression<String> typeEx;

    public EchoStatement(Expression<String> messageEx, Expression<String> typeEx) {
	    this.messageEx = messageEx;
	    this.typeEx = typeEx;
    }

	public Expression<?> getExpression() {
    	return messageEx;
    }

	public boolean execute(BeneratorContext context) {
		String message = ExpressionUtil.evaluate(messageEx, context);
		String type = ExpressionUtil.evaluate(typeEx, context);
		if ("speech".equals(type) && SpeechUtil.speechSupported())
			SpeechUtil.say(message);
		else
			System.out.println(message);
    	return true;
    }

}
