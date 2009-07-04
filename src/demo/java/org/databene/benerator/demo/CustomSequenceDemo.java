package org.databene.benerator.demo;

import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.benerator.Generator;

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
        Sequence odd = new OddNumberSequence();
        Generator<Integer> generator = GeneratorFactory.getNumberGenerator(Integer.class, 3, Integer.MAX_VALUE, 2, odd, 0);
        for (int i = 0; i < 10; i++)
            System.out.println(generator.generate());
    }

    /** The custom Sequence implementation */
    public static class OddNumberSequence extends Sequence {

        protected OddNumberSequence() {
	        super("odd");
        }

        @Override
        public <T extends Number> Generator<T> createGenerator(Class<T> numberType, T min, T max, T precision) {
        	OddNumberGenerator doubleGenerator = new OddNumberGenerator(min.doubleValue(), max.doubleValue());
			return WrapperFactory.wrapNumberGenerator(numberType, doubleGenerator);
        }
    }

    public static class OddNumberGenerator extends LightweightGenerator<Double> {
    	
    	private double min;
    	private double max;
    	private double precision;
    	
    	private double next;
    	
		public OddNumberGenerator(double min, double max) {
	        this(min, max, null);
        }

		public OddNumberGenerator(double min, double max, Double precision) {
	        this.min = min;
	        this.max = max;
	        this.precision = (precision != null ? precision : 2);
	        this.next = min;
        }
		
		// Generator interface implementation --------------------------------------------------------------------------

        public Class<Double> getGeneratedType() {
	        return Double.class;
        }
    	
        @Override
        public boolean available() {
            return (next < max);
        }
        
        public Double generate() {
	        double result = next;
	        next += precision;
	        return result;
        }

        @Override
        public void reset() {
            next = min;
        }
    }

}
