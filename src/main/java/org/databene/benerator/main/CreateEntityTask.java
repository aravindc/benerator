/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.main;

import java.util.Collection;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.commons.Escalator;
import org.databene.commons.LoggerEscalator;
import org.databene.model.consumer.Consumer;
import org.databene.model.data.Entity;
import org.databene.task.AbstractTask;
import org.databene.task.Task;
import org.databene.task.ThreadSafe;

/**
 * Task that creates one entity instance in a run() invocation.<br/><br/>
 * Created: 01.02.2008 14:39:11
 * @author Volker Bergmann
 */
public  class CreateEntityTask extends AbstractTask implements ThreadSafe {

    private String entityName;
    private Generator<Entity> generator;
    private Collection<Consumer<Entity>> consumers;
    private Escalator noConsumerHandler;
//    private int generationCount;
    private List<? extends Task> subTasks;
    private boolean isSubTask;
    
    public CreateEntityTask(String entityName, Generator<Entity> generator, 
            Collection<Consumer<Entity>> consumers, List<? extends Task> subTasks, boolean isSubTask) {
        this.entityName = entityName;
        this.generator = generator;
        this.consumers = consumers;
        this.noConsumerHandler = new LoggerEscalator();
//        this.generationCount = 0;
        this.subTasks = subTasks;
        this.isSubTask = isSubTask;
    }
    
    public String getEntityName() {
        return entityName;
    }

    @Override
    public boolean wantsToRun() {
        return generator.available();
    }
    
    public void run() {
        Entity entity = null;
        synchronized (generator) {
            if (generator.available())
                entity = generator.generate();
            else
                return;
        }
        if (consumers.size() == 0)
            noConsumerHandler.escalate("No consumers defined ", this, null);
        if (entity != null) {
            context.set(entityName, entity);
//            generationCount++;
            for (Consumer<Entity> consumer : consumers)
                consumer.startConsuming(entity);
            for (Task subTask : subTasks) {
                if (subTask instanceof PagedCreateEntityTask)
                    ((PagedCreateEntityTask)subTask).reset();
                subTask.init(context);
                subTask.run();
                subTask.destroy();
            }
            for (Consumer<Entity> consumer : consumers)
                consumer.finishConsuming(entity);
        }
    }
    
    @Override
    public void destroy() {
        if (!isSubTask)
            for (Consumer<Entity> consumer : consumers)
                consumer.flush();
        super.destroy();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + entityName + ']';
    }
}
