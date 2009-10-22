/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Task implementation that provides for repeated, paged, multithreaded Task execution.<br/>
 * <br/>
 * Created: 16.07.2007 19:25:30
 * @author Volker Bergmann
 */
public class PagedTask<E extends Task> extends TaskProxy<E> implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(PagedTask.class);

    private PageListener listener;

    private long maxCount;
    private long minCount;
    private long pageSize;
    private int threadCount;
    
    private volatile AtomicLong queuedInvocations;
    private volatile AtomicLong queuedPages;
    private volatile AtomicLong actualCount;
    
    private ExecutorService executor;

    private Throwable exception;

    // constructors ----------------------------------------------------------------------------------------------------

    public PagedTask() {
        this(null);
    }

    public PagedTask(E realTask) {
        this(realTask, 1);
    }

    public PagedTask(E realTask, long totalInvocations) {
        this(realTask, totalInvocations, null, 1);
    }

    public PagedTask(E realTask, long totalInvocations, PageListener listener, long pageSize) {
        this(realTask, totalInvocations, listener, pageSize, 1, Executors.newSingleThreadExecutor());
    }

    public PagedTask(E realTask, long maxCount, PageListener listener, long pageSize, int threads, ExecutorService executor) {
    	super(realTask);
        this.listener = listener;
        this.maxCount = maxCount;
        this.minCount = maxCount;
        this.pageSize = pageSize;
        this.threadCount = threads;
        this.actualCount = new AtomicLong();
        this.queuedInvocations = new AtomicLong();
		this.queuedPages = new AtomicLong();
        this.executor = executor;
    }

    // Task implementation ---------------------------------------------------------------------------------------------

    public long getMinCount() {
    	return minCount;
    }

	public void setMinCount(long minCount) {
    	this.minCount = minCount;
    }

    public long getMaxCount() {
        return maxCount;
    }
    
	public void setMaxCount(long maxCount) {
    	this.maxCount = maxCount;
    }

    public long getActualCount() {
    	return actualCount.get();
    }

	public long getPageSize() {
        return pageSize;
    }

    public int getThreadCount() {
        return threadCount;
    }

    @Override
    public void run(Context context) {
    	if (maxCount == 0)
    		return;
    	this.actualCount.set(0);
    	queuedPages.set((maxCount + pageSize - 1) / pageSize);
    	queuedInvocations.set(maxCount);
        this.exception = null;
        if (logger.isDebugEnabled())
            logger.debug("Running PagedTask[" + getTaskName() + "]");
        int currentPageNo = 0;
        while (workPending(currentPageNo)) {
        	try {
	            pageStarting(currentPageNo);
	            long currentPageSize = (maxCount < 0 ? pageSize : Math.min(pageSize, queuedInvocations.get()));
	            queuedInvocations.addAndGet(- currentPageSize);
	            long localCount;
	            if (threadCount > 1)
	                localCount = runMultiThreaded(context, currentPageNo, currentPageSize);
	            else
	                localCount = runSingleThreaded(context, currentPageSize);
	            actualCount.addAndGet(localCount);
	            queuedPages.decrementAndGet();
	            pageFinished(currentPageNo, context);
	            if (exception != null)
	                throw new RuntimeException(exception);
	            currentPageNo++;
        	} catch (Exception e) {
        		getErrorHandler(context).handleError("Error in execution of task " + getTaskName(), e);
        	}
        }
        if (logger.isDebugEnabled())
            logger.debug("PagedTask " + getTaskName() + " finished");
        long countValue = actualCount.get();
		if (countValue < minCount)
        	throw new TaskUnavailableException(this, minCount, countValue);
    }
    
    @Override
    public String getTaskName() {
    	return realTask.getTaskName();
    }
    
    // non-public helpers ----------------------------------------------------------------------------------------------

    private long runMultiThreaded(Context context, int currentPageNo, long currentPageSize) {
        long localInvocationCount = 0;
        int maxLoopsPerPage = (int)((currentPageSize + threadCount - 1) / threadCount);
        int shorterLoops = (int)(threadCount * maxLoopsPerPage - currentPageSize);
        // create threads for a page
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int threadNo = 0; threadNo < threadCount; threadNo++) {
            int loopSize = maxLoopsPerPage;
            if (maxCount >= 0 && threadNo >= threadCount - shorterLoops)
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
            IOUtil.close(realTask);
        return localInvocationCount;
    }

    private long runSingleThreaded(Context context, long currentPageSize) {
        Task task = new LoopedTask(realTask, currentPageSize);
        task.run(context);
        IOUtil.close(task);
        return currentPageSize;
    }

    protected boolean workPending(int currentPageNo) {
        if (!realTask.available())
            return false;
        if (maxCount < 0)
        	return true;
        return (queuedPages.get() > 0);
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

    protected void pageFinished(int currentPageNo, Context context) {
        if (logger.isDebugEnabled())
            logger.debug("Page " + (currentPageNo + 1) + " of " + getTaskName() + " finished");
        if (listener != null)
            listener.pageFinished(currentPageNo, -1);
    }

    public void uncaughtException(Thread t, Throwable e) {
        this.exception = e;
    }

}
