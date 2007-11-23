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
