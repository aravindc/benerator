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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Task implementation that provides for repeated, paged, multithreaded Task execution.<br/>
 * <br/>
 * Created: 16.07.2007 19:25:30
 * @author Volker Bergmann
 */
public class PagedTask extends AbstractTask implements Thread.UncaughtExceptionHandler {

    private static final Log logger = LogFactory.getLog(PagedTask.class);

    protected Task realTask;
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

    /**
     * @return the totalInvocations
     */
    public long getTotalInvocations() {
        return totalInvocations;
    }

    /**
     * @return the pageSize
     */
    public long getPageSize() {
        return pageSize;
    }

    /**
     * @return the threadCount
     */
    public int getThreadCount() {
        return threadCount;
    }

    public void run() {
    	if (totalInvocations == 0)
    		return;
        this.exception = null;
        int invocationCount = 0;
        if (logger.isDebugEnabled())
            logger.debug("Running PagedTask[" + getTaskName() + "]");
        int currentPageNo = 0;
        while (workPending(currentPageNo)) {
        	try {
	            pageStarting(currentPageNo);
	            long currentPageSize = (totalInvocations < 0 ? pageSize : Math.min(pageSize, totalInvocations - invocationCount));
	            if (threadCount > 1)
	                invocationCount += runMultiThreaded(currentPageNo, currentPageSize);
	            else
	                invocationCount += runSingleThreaded(currentPageSize);
	            pageFinished(currentPageNo);
	            if (exception != null)
	                throw new RuntimeException(exception);
	            currentPageNo++;
        	} catch (Exception e) {
        		errorHandler.handleError("Error in execution of task " + getTaskName(), e);
        	}
        }
        if (logger.isDebugEnabled())
            logger.debug("PagedTask " + getTaskName() + " finished");
    }
    
    @Override
    public String getTaskName() {
    	return realTask.getTaskName();
    }

    private long runMultiThreaded(int currentPageNo, long currentPageSize) {
        long localInvocationCount = 0;
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
                if (threadCount > 1 && !(task instanceof ThreadSafe)) {
                    if (task instanceof Parallelizable)
                        task = cloneTask((Parallelizable) task);
                    else
                        throw new ConfigurationError("Since the task is not marked as thread-safe," +
                                "it must either be used single-threaded " +
                                "or implement the Parallelizable interface");
                }
                task = new LoopedTask(task, loopSize); // TODO v0.6.0 leave the loop if generator has become unavailable 
                TaskRunnable thread = new TaskRunnable(task, (realTask instanceof ThreadSafe ? null : context), latch);
                executor.execute(thread);
                localInvocationCount += loopSize;
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
        return localInvocationCount;
    }

    private long runSingleThreaded(long currentPageSize) {
        Task task = new LoopedTask(realTask, currentPageSize);
        task.init(context);
        task.run();
        task.destroy();
        return currentPageSize;
    }

    protected boolean workPending(int currentPageNo) {
        if (!realTask.wantsToRun())
            return false;
        if (totalInvocations < 0)
        	return true;
        long pages = (totalInvocations + pageSize - 1) / pageSize;
		return (currentPageNo < pages);
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

    protected void pageStarting(int currentPageNo) {
        if (logger.isDebugEnabled())
            logger.debug("Starting page " + (currentPageNo + 1) + " of " + getTaskName() + " with pagesize=" + pageSize);
        if (listener != null)
            listener.pageStarting(currentPageNo, -1);
    }

    protected void pageFinished(int currentPageNo) {
        if (logger.isDebugEnabled())
            logger.debug("Page " + (currentPageNo + 1) + " of " + getTaskName() + " finished");
        if (listener != null)
            listener.pageFinished(currentPageNo, -1);
    }

    public void uncaughtException(Thread t, Throwable e) {
        this.exception = e;
    }
    
}
