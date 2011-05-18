/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.nullable;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Context;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Evaluates a script which may resolve to <code>null</code> as a valid value.<br/><br/>
 * Created: 18.02.2010 10:42:33
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class NullableScriptGenerator extends AbstractNullableGenerator<Object>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NullableScriptGenerator.class);

    private Script script;
    private Context context;
    
    public NullableScriptGenerator(Script script, Context context) {
        this.script = script;
        this.context = context;
    }

    public Class<Object> getGeneratedType() {
	    return Object.class;
    }

	public ProductWrapper<Object> generate(ProductWrapper<Object> wrapper) {
        Object result = ScriptUtil.execute(script, context);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Generated: " + result);
        return wrapper.setProduct(result);
    }
    
	public void reset() throws IllegalGeneratorStateException {
    }

	public void close() {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + script + ']';
    }

	public boolean isParallelizable() {
	    return true;
    }

	public boolean isThreadSafe() {
	    return true;
    }

}
