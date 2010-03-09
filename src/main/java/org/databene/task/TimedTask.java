/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.BeanUtil;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps a Task and logs its execution time.<br/>
 * <br/>
 * Created: 06.07.2007 06:49:20
 * @since 0.2
 * @author Volker Bergmann
 */
public class TimedTask<E extends Task> extends TaskProxy<E> {

    private static final Logger logger = LoggerFactory.getLogger(TimedTask.class);

    public TimedTask(E realTask) {
        super(realTask);
    }

    @Override
    public TaskResult execute(Context context, ErrorHandler errorHandler) {
        long startTime = System.currentTimeMillis();
        TaskResult result = super.execute(context, errorHandler);
        logger.info("Executing " + realTask + " took " + 
        		String.valueOf(System.currentTimeMillis() - startTime) + " ms");
        return result;
    }

	@Override
    public Object clone() {
	    return new TimedTask<E>(BeanUtil.clone(realTask));
    }
    
}
