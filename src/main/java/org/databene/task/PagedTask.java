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
import org.databene.commons.ConfigurationError;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Task implementation that provides for repeated, paged, multithreaded Task execution.<br/>
 * <br/>
 * Created: 16.07.2007 19:25:30
 */
public class PagedTask extends AbstractTask implements Thread.UncaughtExceptionHandler {

    private static final Log logger = LogFactory.getLog(PagedTask.class);

    private Task realTask;
    private PageListener listener;

    private long totalInvocations;
    private long pageSize;
    private int threadCount;
    
    private ExecutorService executor;

    private Throwable exception;

    // constructors ----------------------------------------------------------------------------------------------------

    public PagedTask() {
        this(null);
    }

    public PagedTask(Task realTask) {
        this(realTask, 1);
    }

    public PagedTask(Task realTask, long totalInvocations) {
        this(realTask, totalInvocations, null, 1);
    }

    public PagedTask(Task realTask, long totalInvocations, PageListener listener, long pageSize) {
        this(realTask, totalInvocations, listener, pageSize, 1, Executors.newSingleThreadExecutor());
    }

    public PagedTask(Task realTask, long totalInvocations, PageListener listener, long pageSize, int threads, ExecutorService executor) {
        this.realTask = realTask;
        this.listener = listener;
        this.totalInvocations = totalInvocations;
        this.pageSize = pageSize;
        this.threadCount = threads;
        this.executor = executor;
    }

    // Task implementation ---------------------------------------------------------------------------------------------

    public void run() {
    	if (totalInvocations == 0)
    		return;
        this.exception = null;
        int invocationCount = 0;
        if (logger.isDebugEnabled())
            logger.debug("Running PagedTask[" + getTaskName() + "]");
        int currentPageNo = 0;
        while (workPending(currentPageNo)) {
            pageStarting(currentPageNo, -1); // TODO 
            long currentPageSize = (totalInvocations < 0 ? pageSize : Math.min(pageSize, totalInvocations - invocationCount));
            int maxLoopsPerPage = (int)((currentPageSize + threadCount - 1) / threadCount);
            int shorterLoops = (int)(threadCount * maxLoopsPerPage - currentPageSize);
            if (realTask instanceof ThreadSafe)
                realTask.init(context);
            // create threads for a page
            CountDownLatch latch = new CountDownLatch(threadCount);
            for (int threadNo = 0; threadNo < threadCount; threadNo++) {
                int loopSize = maxLoopsPerPage;
                if (totalInvocations >= 0 && threadNo >= threadCount - shorterLoops)
               		loopSize--;
                if (loopSize > 0) {
                    Task task = realTask;
                    if (threadCount > 1) {
                        if (!(task instanceof ThreadSafe)) {
                            if (task instanceof Parallelizable)
                                task = cloneTask((Parallelizable) task);
                            else
                                throw new ConfigurationError("Since the task is not marked as thread-safe," +
                                        "it must either be used single-threaded " +
                                        "or implement the Parallelizable interface");
                        }
                    }
                    task = new LoopedTask(task, loopSize);
                    TaskRunnable thread = new TaskRunnable(task, (realTask instanceof ThreadSafe ? null : context), latch);
                    executor.execute(thread);
                    invocationCount += loopSize;
                } else
                	latch.countDown();
            }
            
            if (logger.isDebugEnabled())
                logger.debug("Waiting for end of page " + (currentPageNo + 1) + " of " + getTaskName() + "...");
            try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            if (realTask instanceof ThreadSafe)
                realTask.destroy();
            pageFinished(currentPageNo, -1); // TODO
            if (exception != null)
                throw new RuntimeException(exception);
            currentPageNo++;
        }
        if (logger.isDebugEnabled())
            logger.debug("PagedTask " + getTaskName() + " finished");
    }

    protected boolean workPending(int currentPageNo) {
        long pages = (totalInvocations >= 0 ? (totalInvocations + pageSize - 1) / pageSize : -1);
		return pages < 0 || currentPageNo < pages;
	}

	private Task cloneTask(Parallelizable task) {
        try {
            Method cloneMethod = task.getClass().getMethod("clone");
            return (Task) cloneMethod.invoke(task);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unexpected exception", e); // This is not supposed to happen
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unexpected exception", e); // This is not supposed to happen
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Execption occured in clone() method", e);
        }
    }

    protected void pageStarting(int currentPageNo, long totalPages) {
        if (logger.isDebugEnabled())
            logger.debug("Starting page " + (currentPageNo + 1) + '/' + totalPages + " of " + getTaskName());
        if (listener != null)
            listener.pageStarting(currentPageNo, totalPages);
    }

    protected void pageFinished(int currentPageNo, long totalPages) {
        if (logger.isDebugEnabled())
            logger.debug("Page " + (currentPageNo + 1) + '/' + totalPages + " of " + getTaskName() + " finished");
        if (listener != null)
            listener.pageFinished(currentPageNo, totalPages);
    }

    public void uncaughtException(Thread t, Throwable e) {
        this.exception = e;
    }
}
