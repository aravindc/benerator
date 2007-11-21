package org.databene.benerator.demo;

import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.Generator;

/**
 * Generates salutations using different salutation words for greeting different persons.<br/>
 * <br/>
 * Created: 06.09.2006 00:00:03
 */
public class HelloWorldDemo {

    public static void main(String[] args) {
        Generator<String> salutation = GeneratorFactory.getSampleGenerator("Hi", "Hello", "Howdy");
        Generator<String> name = GeneratorFactory.getSampleGenerator("Alice", "Bob", "Charly");
        for (int i = 0; i < 5; i++)
            System.out.println(salutation.generate() + " " + name.generate());
    }

}
