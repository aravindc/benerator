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

import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.IOUtil;
import org.databene.commons.Level;
import org.databene.commons.expression.ConstantExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
public class PagedTaskRunner implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(PagedTaskRunner.class);

    protected Task target;
    private List<PageListener> pageListeners;

    private Long maxCount;
    private long minCount;
    private long pageSize;
    private int  threadCount;
    
    private volatile AtomicLong queuedInvocations;
    private volatile AtomicLong queuedPages;
    private volatile AtomicLong actualCount;
    
    private Expression<ExecutorService> executor;

    private Throwable exception;

    // constructors ----------------------------------------------------------------------------------------------------

    public PagedTaskRunner() {
        this(null);
    }

    public PagedTaskRunner(Task realTask) {
        this(realTask, 1);
    }

    public PagedTaskRunner(Task realTask, long totalInvocations) {
        this(realTask, totalInvocations, null, 1);
    }

    public PagedTaskRunner(Task realTask, long totalInvocations, List<PageListener> listeners, long pageSize) {
        this(realTask, totalInvocations, listeners, pageSize, 1, Executors.newSingleThreadExecutor());
    }

    public PagedTaskRunner(Task realTask, Long maxCount, List<PageListener> listeners, long pageSize, int threads, 
    		ExecutorService executor) {
    	this(realTask, maxCount, listeners, pageSize, threads, 
    			new ConstantExpression<ExecutorService>(executor));
    }

    public PagedTaskRunner(Task target, Long maxCount, List<PageListener> pageListeners, long pageSize, int threads, 
    		Expression<ExecutorService> executor) {
    	this.target = target;
        this.pageListeners = pageListeners;
        this.maxCount = maxCount;
        this.minCount = 0;
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

    public void execute(BeneratorContext context) {
    	if (maxCount != null && maxCount == 0)
    		return;
    	this.actualCount.set(0);
    	if (maxCount != null) {
	    	queuedPages.set((maxCount + pageSize - 1) / pageSize);
	    	queuedInvocations.set(maxCount);
    	}
        this.exception = null;
        if (logger.isDebugEnabled())
            logger.debug("Running PagedTask[" + getTaskName() + "]");
        int currentPageNo = 0;
        while (workPending(currentPageNo)) {
        	try {
	            pageStarting(currentPageNo);
	            long currentPageSize = (maxCount == null ? pageSize : Math.min(pageSize, queuedInvocations.get()));
	            queuedInvocations.addAndGet(- currentPageSize);
	            long localCount;
	            if (threadCount > 1)
	                localCount = runMultiThreaded(context, currentPageNo, currentPageSize);
	            else
	                localCount = runSingleThreaded(context, currentPageSize);
	            actualCount.addAndGet(localCount);
	            if (maxCount != null)
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
        	throw new TaskUnavailableException(target, minCount, countValue);
    }
    
	public String getTaskName() {
    	return target.getTaskName();
    }
    
    public static void execute(Task task, BeneratorContext context, Long invocations,
            List<PageListener> pageListeners, long pageSize, int threadCount) {
		if (logger.isInfoEnabled()) {
			String invocationInfo = (invocations == null ? "as long as available" :
			     (invocations > 1 ? invocations + " times " : ""));
			if (invocationInfo.length() > 0)
				invocationInfo += " with page size " + pageSize + " in " 
					+ (threadCount > 1 ? threadCount + " threads" : "a single thread");
			logger.info("Running task " + task + " " + invocationInfo);
		}
		PagedTaskRunner pagedTask = new PagedTaskRunner(task, invocations, pageListeners, pageSize, threadCount, 
				context.getExecutorService());
		pagedTask.execute(context);
	}
	
    // non-public helpers ----------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
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
                Task task = target;
                if (threadCount > 1 && !(task instanceof ThreadSafe)) {
                    if (task instanceof Parallelizable)
                        task = cloneTask((Parallelizable) task);
                    else
                        throw new ConfigurationError("Since the task is not marked as thread-safe," +
                                "it must either be used single-threaded " +
                                "or implement the Parallelizable interface");
                }
                task = new LoopedTask(task, loopSize); 
                TaskRunnable thread = new TaskRunnable(task, (target instanceof ThreadSafe ? null : context), latch);
                executor.evaluate(context).execute(thread);
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
        if (target instanceof ThreadSafe)
            IOUtil.close(target);
        return localInvocationCount;
    }

    @SuppressWarnings("unchecked")
    private long runSingleThreaded(Context context, long currentPageSize) {
        Task task = new LoopedTask(target, currentPageSize);
        task.run(context);
        IOUtil.close(task);
        return currentPageSize;
    }

    protected boolean workPending(int currentPageNo) {
        if (!target.available())
            return false;
        if (maxCount == null)
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
        if (pageListeners != null)
        	for (PageListener listener : pageListeners)
        		listener.pageStarting(currentPageNo, -1);
    }

    protected void pageFinished(int currentPageNo, Context context) {
        if (logger.isDebugEnabled())
            logger.debug("Page " + (currentPageNo + 1) + " of " + getTaskName() + " finished");
        if (pageListeners != null)
        	for (PageListener listener : pageListeners)
        		listener.pageFinished(currentPageNo, -1);
    }

    private ErrorHandler getErrorHandler(Context context) {
	    return new ErrorHandler(getClass().getName(), Level.fatal); // TODO read config
    }

    public void uncaughtException(Thread t, Throwable e) {
        this.exception = e;
    }

	@Override
	public String toString() {
	    return getClass().getSimpleName() + '[' + target + ']';
	}
	
}
