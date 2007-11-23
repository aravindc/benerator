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

import org.databene.model.Context;

import java.util.Map;
import java.util.HashMap;

/**
 * A thread-safe space to exchange data among difierent tasks.<br/>
 * <br/>
 * Created: 06.07.2007 06:30:43
 */
public class TaskContext implements Context {

    private Context parent;

    private Map<String, Object> map;

    public TaskContext() {
        this(null);
    }

    public TaskContext(Context parent) {
        this.map = new HashMap<String, Object>();
        this.parent = parent;
    }

    public synchronized Object get(String key) {
        Object value = map.get(key);
        if (value == null && parent != null)
            value = parent.get(key);
        return value;
    }

    public synchronized void set(String key, Object value) {
        map.put(key, value);
    }

	public void remove(String key) {
		map.remove(key);
	}
	
    public synchronized String toString() {
        return map.toString();
    }

}
