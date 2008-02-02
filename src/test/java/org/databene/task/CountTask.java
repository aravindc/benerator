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

import org.databene.commons.Context;

/**
 * Helper class for testing Task handling.<br/><br/>
 * Created: 16.07.2007 19:58:41
 * @author Volker Bergmann
 */
public class CountTask implements Task, ThreadSafe {

    public int initCount = 0;
    public int runCount = 0;
    public int destroyCount = 0;
    
    // Task interface --------------------------------------------------------------------------------------------------

    public String getTaskName() {
        return getClass().getSimpleName();
    }

    public void init(Context context) {
        if (context == null)
            throw new IllegalArgumentException("context is null");
        initCount++;
    }

    public boolean wantsToRun() {
        return true;
    }

    public void run() {
        runCount++;
    }

    public void destroy() {
        destroyCount++;
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return "CountTask[initCount=" + initCount + ", runCount=" + runCount + ", destroyCount=" + destroyCount + ']';
    }

}
