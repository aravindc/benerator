package org.databene.benerator.distribution.sequence;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.distribution.sequence.StepDoubleGenerator;
import org.databene.benerator.test.GeneratorClassTest;

/**
 * Tests the {@link StepDoubleGenerator}.<br/>
 * <br/>
 * Created: 26.07.2007 18:41:19
 * @author Volker Bergmann
 */
public class StepDoubleGeneratorTest extends GeneratorClassTest {

    public StepDoubleGeneratorTest() {
        super(StepDoubleGenerator.class);
    }

    public void testIncrement() throws IllegalGeneratorStateException {
    	// test increment 1
        StepDoubleGenerator simpleGenerator = new StepDoubleGenerator(1, 5, 1);
        expectGeneratedSequence(simpleGenerator, 1., 2., 3., 4., 5.).withCeasedAvailability();
        // test increment 2
        StepDoubleGenerator oddGenerator = new StepDoubleGenerator(1, 5, 2);
        expectGeneratedSequence(oddGenerator, 1., 3., 5.).withCeasedAvailability();
    }

    public void testInitial() {
        StepDoubleGenerator incGenerator = new StepDoubleGenerator(1., 5., 2., 2.);
        expectGeneratedSequence(incGenerator, 2., 4.).withCeasedAvailability();
        StepDoubleGenerator decGenerator = new StepDoubleGenerator(1., 5., -2.);
        expectGeneratedSequence(decGenerator, 5., 3., 1.).withCeasedAvailability();
    }

    public void testDecrement() throws IllegalGeneratorStateException {
        StepDoubleGenerator simpleGenerator = new StepDoubleGenerator(1, 5, -1);
        expectGeneratedSequence(simpleGenerator, 5., 4., 3., 2., 1.).withCeasedAvailability();
        StepDoubleGenerator oddGenerator = new StepDoubleGenerator(1, 5, -2);
        expectGeneratedSequence(oddGenerator, 5., 3., 1.).withCeasedAvailability();
    }

}
