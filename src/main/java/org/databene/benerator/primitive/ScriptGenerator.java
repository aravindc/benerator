/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.commons.Context;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;

/**
 * Creates Strings based on a Script.<br/><br/>
 * Created: 29.01.2008 17:19:24
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class ScriptGenerator extends LightweightGenerator<Object>{
    
    private Script script;
    private Context context;
    
    public ScriptGenerator(Script script, Context context) {
    	super(Object.class);
        this.script = script;
        this.context = context;
    }

    public Object generate() {
        Object result = ScriptUtil.execute(script, context);
        if (logger.isDebugEnabled())
            logger.debug("Generated: " + result);
        return result;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + script + ']';
    }

    private static final Log logger = LogFactory.getLog(ScriptGenerator.class);
}
