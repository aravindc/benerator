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
import java.util.concurrent.Executors;

import org.databene.commons.Context;
import org.databene.commons.context.DefaultContext;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link PagedTask}<br/><br/>
 * Created: 16.07.2007 20:02:03
 * @since 0.2
 * @author Volker Bergmann
 */
public class PagedTaskTest {

	@Test
    public void testSingleInvocation() throws Exception {
        checkRun(1,  1,  1,   1, 1, 1);
        checkRun(1, 10,  1,   1, 1, 1);
        checkRun(1,  1, 10,   1, 1, 1);
        checkRun(1, 10, 10,   1, 1, 1);
    }

	@Test
    public void testSingleThreadedInvocation() throws Exception {
        checkRun(10,  1,  1,   10, 10, 10);
        checkRun(10, 10,  1,    1, 10,  1);
        checkRun( 4,  3,  1,    2,  4,  2);
    }

	@Test
    public void testMultiThreadedInvocation() throws Exception {
        checkRun(10,  1, 10,   10, 10, 10);
        checkRun(10, 10, 10,    1, 10,  1);
    }

	@Test
    public void testMultiPagedInvocation() throws Exception {
        checkRun(10,  5, 1,    2, 10,  2);
        checkRun(10,  5, 2,    2, 10,  2);
        checkRun(20,  5, 2,    4, 20,  4);
    }

	@Test
    public void testNonThreadSafeTask() throws Exception {
        checkNonThreadSafeTask(1,   1, 1, 1); // single threaded
        checkNonThreadSafeTask(10, 10, 1, 1); // single threaded, single-paged
        checkNonThreadSafeTask(10,  5, 1, 1); // single threaded, multi-paged

        checkNonThreadSafeTask(10, 10, 2, 3); // multi-threaded, single-paged
        checkNonThreadSafeTask(10,  5, 2, 5); // multi-threaded, multi-paged
    }

	@Test
    public void checkNonThreadSafeTask(int totalInvocations, int pageSize, int threads, int expectedInstanceCount) throws IOException {
        SingleThreadedTask.instanceCount = 0;
        SingleThreadedTask task = new SingleThreadedTask() {
			public void run(Context context) { }
        };
        PagedTask<SingleThreadedTask> pagedTask = new PagedTask<SingleThreadedTask>(
        		task, totalInvocations, null, pageSize, threads, Executors.newCachedThreadPool());
        pagedTask.run(new DefaultContext());
        pagedTask.close();
        assertEquals("Unexpected instanceCount,", expectedInstanceCount, SingleThreadedTask.instanceCount);
    }

	// helpers ---------------------------------------------------------------------------------------------------------
	
    private void checkRun(int totalInvocations, int pageSize, int threads,
                          int expectedInitCount, int expectedRunCount, int expectedCloseCount) {
        CountTask countTask = new CountTask();
        PagedTask<CountTask> pagedTask = new PagedTask<CountTask>(
        		countTask, totalInvocations, null, pageSize, threads, Executors.newCachedThreadPool());
        pagedTask.run(new DefaultContext());
        assertEquals("Unexpected runCount,", expectedRunCount, countTask.runCount);
        assertEquals("Unexpected closeCount,", expectedCloseCount, countTask.closeCount);
    }
    
}
