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

package org.databene.task;

import java.util.concurrent.ExecutorService;

import org.databene.commons.Context;

/**
 * TODO document class TaskRunnerTask.<br/>
 * <br/>
 * Created at 23.07.2009 07:01:38
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class TaskRunnerTask extends TaskProxy {

    private int count;
    private int pageSize;
    private int threads;
    private PageListener pager;
    private ExecutorService executor;

	public TaskRunnerTask(Task realTask, int count, int pageSize, int threads, PageListener pager, ExecutorService executor) {
	    super(realTask);
	    this.count = count;
	    this.pageSize = pageSize;
	    this.threads = threads;
	    this.pager = pager;
	    this.executor = executor;
    }

	@Override
	public void run(Context context) {
	    TaskRunner.run(realTask, context, count, pager, pageSize, threads, executor);
	}
	
}
