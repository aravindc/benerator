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

package org.databene.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.document.csv.CSVLineIterator;
import org.databene.commons.BeanUtil;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Runs a task from its instance or from a config file.<br/>
 * <br/>
 * Created: 06.07.2007 06:37:20
 */
public class TaskRunner {

    private static final Log logger = LogFactory.getLog(TaskRunner.class);

    public static void run(Task task, TaskContext context, long invocations,
                           PageListener pager, long pageSize, int threadCount, ExecutorService executor) {
        if (logger.isInfoEnabled()) {
            String invocationInfo = (invocations == 1 ? "once" :
                    invocations + " times with page size " + pageSize + " in " + threadCount + " threads");
            logger.info("Running task " + task + " " + invocationInfo);
        }
        PagedTask pagedTask = new PagedTask(task, invocations, pager, pageSize, threadCount, executor);
        pagedTask.init(context);
        try {
            pagedTask.run();
        } finally {
            pagedTask.destroy();
        }
    }

    /** @deprecated this is no longer supported */
    public static void runFromConfigFile(String urn) throws IOException {
        TaskContext context = new TaskContext();
        CSVLineIterator iterator = new CSVLineIterator(urn, ',');
        while (iterator.hasNext()) {
            String[] cells = iterator.next();
            if (cells.length == 0)
                continue;
            Class<Task> taskClass = (Class<Task>) BeanUtil.forName(cells[0]);
            Task task = BeanUtil.newInstance(taskClass);
            int invocations = Integer.parseInt(cells[1]);
            int pageSize = Integer.parseInt(cells[2]);
            int threadCount = Integer.parseInt(cells[3]);
            run(task, context, invocations, null, pageSize, threadCount, Executors.newCachedThreadPool());
        }
        iterator.close();
    }

}
