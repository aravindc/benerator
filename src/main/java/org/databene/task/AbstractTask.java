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

import java.io.IOException;

import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.Level;

/**
 * Simple abstract implementation of the Task interface.<br/>
 * <br/>
 * Created: 16.07.2007 18:55:16
 * @since 0.2
 * @author Volker Bergmann
 */
public abstract class AbstractTask implements Task {

    protected String taskName;
    private Expression<ErrorHandler> errorHandler;
    
    // constructor -----------------------------------------------------------------------------------------------------

    protected AbstractTask() {
        this(null, null);
    }

    protected AbstractTask(Expression<ErrorHandler> errorHandler) {
        this(null, errorHandler);
    }

    protected AbstractTask(String taskName) {
        this(taskName, null);
    }
    
    protected AbstractTask(String taskName, Expression<ErrorHandler> errorHandler) {
    	if (taskName == null)
    		taskName = getClass().getSimpleName();
        setTaskName(taskName);
        this.errorHandler = errorHandler;
    }
    
    // Task interface --------------------------------------------------------------------------------------------------

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public ErrorHandler getErrorHandler(Context context) {
    	if (errorHandler == null)
    		return new ErrorHandler(taskName, Level.fatal);
		return errorHandler.evaluate(context);
	}
    
    public boolean available() {
    	return true;
    }

    @SuppressWarnings("unused")
    public void close() throws IOException {
    	// empty
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + taskName + ']';
    }

}
