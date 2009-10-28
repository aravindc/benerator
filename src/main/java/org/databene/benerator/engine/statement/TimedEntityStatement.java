/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {Task} implementation that acts as a proxy to another tasks, forwards calls to it, 
 * measures execution times and logs them.<br/>
 * <br/>
 * Created at 23.07.2009 06:55:46
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class TimedEntityStatement extends StatementProxy {
	
	private static final Logger logger = LoggerFactory.getLogger(TimedEntityStatement.class);

    public TimedEntityStatement(Statement realStatement) {
    	super(realStatement);
    }

    @Override
    public void execute(BeneratorContext context) {
	    long t0 = System.currentTimeMillis();
		super.execute(context);
		long dc = context.getLatestGenerationCount();
		long dt = System.currentTimeMillis() - t0;
		String taskId = "TODO"; // realStatement.getTaskName(); TODO
		if (dc == 0)
			logger.info("No entities created from '" + taskId + "' setup");
		else if (dt > 0)
			logger.info("Created " + dc + " entities from '"
					+ taskId + "' setup in " + dt + " ms ("
					+ (dc * 1000 / dt) + "/s)");
		else
			logger.info("Created " + dc + " entities from '" + taskId);
    }

}
