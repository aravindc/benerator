/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.Context;

/**
 * The Task interface.
 * General usage is to call init() for initialization,
 * then once or several times the run() method for executing the task's work.
 * After usage, destroy() is called. 
 * If the goal of a Task has been achieved by external influences or Task 
 * completion, the Task may signal that it does not require further execution 
 * by returning false in {@link #wantsToRun()}.
 * When implementing the Task interface, you should preferably inherit from 
 * {@link AbstractTask}, this may compensate for future interface changes.<br/>
 * <br/>
 * Created: 06.07.2007 06:30:22
 * @author Volker Bergmann
 */
public interface Task extends Runnable {

    /** @return the name of the task. */
    String getTaskName();

    /** initializes the task and, if necessary, stores a reference to the context. */
    void init(Context context);

    /**
     * tells if the task still wants to be executed.
     * @since 0.4.0
     */
    boolean wantsToRun();

    /** executes the main functionality of the task. */
    void run();
    
    /** closes the task and releases all resources. */
    void destroy();
}
