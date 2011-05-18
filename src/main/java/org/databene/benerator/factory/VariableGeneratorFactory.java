/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.factory;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.nullable.NullableGeneratorFactory;
import org.databene.benerator.nullable.NullableScriptGenerator;
import org.databene.commons.Context;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;

/**
 * TODO Document class.<br/><br/>
 * Created: 18.05.2011 12:01:24
 * @since TODO version
 * @author Volker Bergmann
 */
public class VariableGeneratorFactory {

	public static NullableGenerator<?> createGenerator(
			InstanceDescriptor descriptor, BeneratorContext context) {
		NullableGenerator<?> generator = null;
		
		// check if nullQuota == 1
		generator = createNullQuotaOneGenerator(descriptor);
		if (generator != null)
			return null;
		
		// check for script
		TypeDescriptor type = descriptor.getTypeDescriptor();
		generator = createScriptGenerator(type, context);

		if (generator == null) {
			Generator<?> source = InstanceGeneratorFactory.createSingleInstanceGenerator(descriptor, Uniqueness.NONE, context);
			generator = NullableGeneratorFactory.wrap(source);
		}
		
		return NullableGeneratorFactory.injectNulls(generator, descriptor.getNullQuota());
	}

	private static NullableGenerator<?> createNullQuotaOneGenerator(InstanceDescriptor descriptor) {
		// check if nullQuota is 1
        Double nullQuota = descriptor.getNullQuota();
        if (nullQuota != null && nullQuota.doubleValue() == 1.)
            return NullableGeneratorFactory.createConstantGenerator(null);
        else
        	return null;
	}
	
    protected static NullableGenerator<?> createScriptGenerator(TypeDescriptor descriptor, Context context) {
        String scriptText = descriptor.getScript();
        if (scriptText != null) {
            Script script = ScriptUtil.parseScriptText(scriptText);
            return new NullableScriptGenerator(script, context);
        } else
        	return null;
    }

}
