/*
 * (c) Copyright 2012-2013 by Volker Bergmann. All rights reserved.
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

package org.databene.task;

import java.io.PrintWriter;
import java.util.List;

import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.contiperf.PerformanceTracker;
import org.databene.platform.contiperf.PerfTrackingTaskProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single-threaded non-locking {@link Task} executor.<br/><br/>
 * Created: 19.12.2012 09:54:56
 * @since 0.8.0
 * @author Volker Bergmann
 */
public class TaskExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutor.class);

    private Task target;
    private Context context;
    private ErrorHandler errorHandler;
    private List<PageListener> pageListeners;
    private long pageSize;
    private boolean infoLog;
    private PerformanceTracker tracker;

    private TaskExecutor(Task target, List<PageListener> pageListeners, long pageSize, 
    		boolean stats, Context context, ErrorHandler errorHandler, boolean infoLog) {
    	this.context = context;
    	this.errorHandler = errorHandler;
    	if (stats) {
       		target = new PerfTrackingTaskProxy(target);
        	this.tracker = ((PerfTrackingTaskProxy) target).getOrCreateTracker();
    	}
    	this.target = new StateTrackingTaskProxy<Task>(target);
        this.pageListeners = pageListeners;
        this.pageSize = pageSize;
        this.infoLog = infoLog;
    }

    public static void execute(Task task, Context context, Long requestedInvocations, Long minInvocations,
            List<PageListener> pageListeners, long pageSize, boolean stats,
            ErrorHandler errorHandler, boolean infoLog) {
    	TaskExecutor runner = new TaskExecutor(task, pageListeners, 
				pageSize, stats, context, errorHandler, infoLog);
		runner.run(requestedInvocations, minInvocations);
	}

    private long run(Long requestedInvocations, Long minInvocations) {
		logExecutionInfo(target, requestedInvocations, minInvocations, pageSize, infoLog);
		// first run without verification
		long countValue = run(requestedInvocations);
		// afterwards verify execution count
		if (minInvocations != null && countValue < minInvocations)
			throw new TaskUnavailableException(target, minInvocations, countValue);
		if (tracker != null)
			tracker.getCounters()[0].printSummary(new PrintWriter(System.out), 90, 95);
		return countValue;
    }

    private long run(Long requestedInvocations) {
    	if (requestedInvocations != null && requestedInvocations == 0)
    		return 0;
        long queuedInvocations = 0;
        long actualCount = 0;
    	if (requestedInvocations != null)
	    	queuedInvocations = requestedInvocations;
        LOGGER.debug("Starting task {}", getTaskName());
        int currentPageNo = 0;
        do {
        	try {
        		if (pageSize > 0)
        			pageStarting(currentPageNo);
	            long currentPageSize = currentPageSize(requestedInvocations, queuedInvocations);
	            queuedInvocations -= currentPageSize;
	            actualCount += runPage(currentPageSize, (pageSize > 0));
        		if (pageSize > 0)
        			pageFinished(currentPageNo, context);
	            currentPageNo++;
        	} catch (Exception e) {
        		errorHandler.handleError("Error in execution of task " + getTaskName(), e);
        	}
        } while (workPending(requestedInvocations, queuedInvocations));
        LOGGER.debug("Finished task {}", getTaskName());
        return actualCount;
	}

	protected long currentPageSize(Long requestedInvocations, long queuedInvocations) {
		if (pageSize > 0)
			return (requestedInvocations == null ? pageSize : Math.min(pageSize, queuedInvocations));
		else
			return (requestedInvocations == null ? 1 : Math.min(requestedInvocations, queuedInvocations));
	}
	
    private String getTaskName() {
    	return target.getTaskName();
    }
    
	private long runPage(Long invocationCount, boolean finishPage) {
		try {
			return runWithoutPage(target, invocationCount, context, errorHandler);
		} finally {
			if (finishPage)
				target.pageFinished();
		}
    }

	private static long runWithoutPage(Task target, Long invocationCount, Context context, ErrorHandler errorHandler) {
		long actualCount = 0;
        for (int i = 0; invocationCount == null || i < invocationCount; i++) {
            TaskResult stepResult = target.execute(context, errorHandler);
			if (stepResult != TaskResult.UNAVAILABLE)
            	actualCount++;
			if (stepResult != TaskResult.EXECUTING)
				break;
        }
        return actualCount;
	}
	
    @SuppressWarnings("unchecked")
    private boolean workPending(Long maxInvocationCount, long queuedInvocations) {
        if (!((StateTrackingTaskProxy<? extends Task>) target).isAvailable())
            return false;
        if (maxInvocationCount == null)
        	return true;
        return (queuedInvocations > 0);
	}

    private void pageStarting(int currentPageNo) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting page " + (currentPageNo + 1) + " of " + getTaskName() + " with pageSize=" + pageSize);
        if (pageListeners != null)
        	for (PageListener listener : pageListeners)
        		listener.pageStarting();
    }

    private void pageFinished(int currentPageNo, Context context) {
        LOGGER.debug("Page {} of {} finished", currentPageNo + 1, getTaskName());
        if (pageListeners != null)
        	for (PageListener listener : pageListeners)
        		listener.pageFinished();
    }

	private static void logExecutionInfo(Task task, Long minInvocations, Long maxInvocations, long pageSize, boolean infoLog) {
	    if (infoLog) {
			if (LOGGER.isInfoEnabled())
				LOGGER.info(executionInfo(task, minInvocations, maxInvocations, pageSize));
		} else if (LOGGER.isDebugEnabled())
			LOGGER.debug(executionInfo(task, minInvocations, maxInvocations, pageSize));
    }

	private static String executionInfo(Task task, Long minInvocations, Long maxInvocations, long pageSize) {
	    String invocationInfo = (maxInvocations == null ? "as long as available" :
	         (maxInvocations > 1 ? maxInvocations + " times" : ""));
	    if (minInvocations != null && minInvocations > 0 && (maxInvocations == null || maxInvocations > minInvocations))
	    	invocationInfo += " requiring at least " + minInvocations + " generations";
	    if (invocationInfo.length() > 0)
	    	invocationInfo += " with page size " + pageSize + " in a single thread";
	    return "Running task " + task + " " + invocationInfo;
    }
	
    
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public String toString() {
	    return getClass().getSimpleName();
	}

}
