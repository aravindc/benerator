package org.databene.benerator.demo;

import java.util.List;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.Generator;
import org.databene.commons.CollectionUtil;

/**
 * Generates salutations using different salutation words for greeting different persons.
 * @author Volker Bergmann
 */
public class HelloWorldDemo {

    public static void main(String[] args) {
    	// first create a context
    	BeneratorContext context = new BeneratorContext();
    	
        // create and initialize the salutation generator
    	GeneratorFactory generatorFactory = context.getGeneratorFactory();
    	List<String> salutations = CollectionUtil.toList("Hi", "Hello", "Howdy");
		Generator<String> salutationGenerator = generatorFactory.createSampleGenerator(salutations, String.class, false);
        salutationGenerator.init(context);
        
        // create and initialize the name generator
        List<String> names = CollectionUtil.toList("Alice", "Bob", "Charly");
		Generator<String> name = generatorFactory.createSampleGenerator(names, String.class, false);
        name.init(context);
        
        // use the generators
        for (int i = 0; i < 5; i++)
            System.out.println(salutationGenerator.generate() + " " + name.generate());
        
        // in the end, close the generators
        salutationGenerator.close();
        name.close();
    }

}
