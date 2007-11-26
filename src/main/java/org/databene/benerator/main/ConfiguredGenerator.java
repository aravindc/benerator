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

package org.databene.benerator.main;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.model.data.Entity;
import org.databene.task.TaskContext;

public class ConfiguredGenerator implements Generator<Entity> {
	
	private static Log logger = LogFactory.getLog(ConfiguredGenerator.class);

    private Generator<Entity> entityGenerator;
	private Map<String, Generator<? extends Object>> variables;
	private TaskContext context;
	private boolean variablesInitialized;
	
	public ConfiguredGenerator(Generator<Entity> entityGenerator, Map<String, Generator<? extends Object>> variables, TaskContext context) {
		this.entityGenerator = entityGenerator;
		this.variables = variables;
		this.context = context;
		this.variablesInitialized = false;
	}
	
	public Class<Entity> getGeneratedType() {
		return Entity.class;
	}

	public void validate() {
        for (Generator<? extends Object> varGen : variables.values())
        	varGen.validate();
        entityGenerator.validate();
	}

	public boolean available() {
		if (!variablesInitialized) {
	        for (Generator<? extends Object> varGen : variables.values()) {
	            if (!varGen.available()) {
	                logger.debug("No more available: " + varGen);
	                return false;
	            }
	        }
	        for (Map.Entry<String, Generator<? extends Object>> entry : variables.entrySet())
	            context.set(entry.getKey(), entry.getValue().generate());
	        variablesInitialized = true;
		}
        return entityGenerator.available();
	}

	public Entity generate() {
		if (!available())
			throw new IllegalGeneratorStateException("Generator is not available");
        Entity entity = entityGenerator.generate();
        for (String variableName : variables.keySet())
            context.remove(variableName);
        variablesInitialized = false;
        return entity;
	}

	public void reset() {
        for (String variableName : variables.keySet())
            context.set(variableName, null);
		for (Generator<? extends Object> variable : variables.values())
			variable.reset();
	}

	public void close() {
        for (String variableName : variables.keySet())
            context.set(variableName, null);
		for (Generator<? extends Object> variable : variables.values())
			variable.close();
	}

}
