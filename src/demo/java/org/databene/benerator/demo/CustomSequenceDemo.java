package org.databene.benerator.demo;

import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.primitive.number.AbstractDoubleGenerator;
import org.databene.benerator.primitive.number.adapter.SequenceFactory;
import org.databene.benerator.Generator;
import org.databene.model.function.Sequence;

/**
 * Demonstrates definition and use of the custom Sequence 'odd'
 * by an example that generates a sequence of odd numbers:
 * 3, 5, 7, ...<br/>
 * <br/>
 * Created: 13.09.2006 20:27:54
 */
public class CustomSequenceDemo {

    /** Defines the Sequence 'odd', creates an Integer generator that acceses it and invokes the generator 10 times */
    public static void main(String[] args) {
        Sequence odd = SequenceFactory.defineSequence("odd", OddDoubleGenerator.class);
        Generator<Integer> generator = GeneratorFactory.getNumberGenerator(Integer.class, 3, Integer.MAX_VALUE, 2, odd, 0);
        for (int i = 0; i < 10; i++)
            System.out.println(generator.generate());
    }

    /** The custom Sequence implementation */
    public static class OddDoubleGenerator extends AbstractDoubleGenerator {

        /** Pointer to the next value to return */
        private double cursor;

        /** sets the miniume value and initializes the cursor to this value */
        public void setMin(Double min) { // Take care to use the number wrapper classes
            super.setMin(min);
            cursor = min;
        }

        /** Generates a value from the cursor and increases the cursor by two */
        public Double generate() {
            double value = cursor;
            cursor += precision;
            return value;
        }
    }
}
