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
import org.databene.benerator.engine.task.GenerateAndConsumeEntityTask;
import org.databene.benerator.engine.task.LazyTask;
import org.databene.benerator.engine.task.SerialTask;
import org.databene.model.data.Entity;
import org.databene.task.Task;
import org.databene.task.TaskProxy;

/**
 * The root {@link Task} for executing descriptor file based data generation.<br/><br/>
 * Created: 24.10.2009 11:08:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class BeneratorMainTask extends SerialTask {

	@SuppressWarnings("unchecked")
    public Generator<Entity> getGenerator(String name, BeneratorContext context) {
		for (Task subTask : subTasks) {
			if (match(name, subTask))
				return ((GenerateAndConsumeEntityTask) subTask).getEntityGenerator();
			Task tmp = subTask;
			while (tmp instanceof TaskProxy || tmp instanceof LazyTask) {
				if (tmp instanceof TaskProxy)
					tmp = ((TaskProxy) tmp).getRealTask();
				else
					tmp = (Task) ((LazyTask) tmp).getTargetExpression().evaluate(null);
				if (match(name, tmp))
					return ((GenerateAndConsumeEntityTask) tmp).getEntityGenerator();
			}
			subTask.run(context);
		}
		throw new IllegalArgumentException("Generator not found: " + name);
	}

	private boolean match(String name, Task task) {
	    return (task instanceof GenerateAndConsumeEntityTask && name.equals(task.getTaskName()));
    }
	
	@Override
	public void close() {
	    super.close();
	    // TODO close resources
	}
}
