package org.databene.benerator.demo;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.Generator;

/**
 * Generates salutations using different salutation words for greeting different persons.
 * @author Volker Bergmann
 */
public class HelloWorldDemo {

    public static void main(String[] args) {
    	// first create a context
    	BeneratorContext context = new BeneratorContext();
        // create and initialize the salutation generator
    	Generator<String> salutation = GeneratorFactory.getSampleGenerator("Hi", "Hello", "Howdy");
        salutation.init(context);
        // create and initialize the name generator
        Generator<String> name = GeneratorFactory.getSampleGenerator("Alice", "Bob", "Charly");
        name.init(context);
        // use the generators
        for (int i = 0; i < 5; i++)
            System.out.println(salutation.generate() + " " + name.generate());
        // in the end, close the generators
        salutation.close();
        name.close();
    }

}
