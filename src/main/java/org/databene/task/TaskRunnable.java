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

import java.util.concurrent.CountDownLatch;

import org.databene.commons.Context;
import org.databene.commons.IOUtil;

/**
 * Thread implementation that executes a Task.<br/>
 * <br/>
 * Created: 16.07.2007 20:14:52
 * @since 0.2
 * @author Volker Bergmann
 */
public class TaskRunnable implements Runnable {

    private Task target;
    private Context context;
    private CountDownLatch latch;

    /**
     * Initializes a TaskThread. If the context is null,
     * the target task's init() and destroy() methods will not be called.
     * @param target
     * @param context
     */
    public TaskRunnable(Task target, Context context) {
        this(target, context, null);
    }

    public TaskRunnable(Task target, Context context, CountDownLatch latch) {
        this.target = target;
        this.context = context;
        this.latch = latch;
    }

    public void run() {
        try {
            target.run(context);
            if (context != null)
                IOUtil.close(target);
        } finally {
            if (latch != null)
            	latch.countDown();
        }
    }
}
