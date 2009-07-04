package org.databene.benerator.demo;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.distribution.sequence.RandomWalkSequence;
import org.databene.benerator.distribution.sequence.ShuffleSequence;
import org.databene.benerator.distribution.sequence.StepSequence;
import org.databene.benerator.factory.GeneratorFactory;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Demonstrates the built-in Sequences of 'databene generator'.<br/>
 * <br/>
 * Created: 07.09.2006 21:13:33
 */
public class DistributionDemo {

    /** The number of invocations */
    private static final int N = 128;

    /**
     * Instantiates a frame with a DistributionPane for reach built-in Sequence and usage mode.
     * @see DistributionPane
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("DistributionDemo");
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridLayout(2, 4));
        contentPane.setBackground(Color.WHITE);
        contentPane.add(new DistributionPane("random", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, Sequence.RANDOM, 0)));
        contentPane.add(new DistributionPane("cumulated", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, Sequence.CUMULATED, 0)));
        contentPane.add(new DistributionPane("randomWalk[0,2]", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, new RandomWalkSequence(BigDecimal.valueOf(0), BigDecimal.valueOf(2)), 0)));
        contentPane.add(new DistributionPane("randomWalk[-1,1]", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, new RandomWalkSequence(BigDecimal.valueOf(-1), BigDecimal.valueOf(1)), 0)));
        contentPane.add(new DistributionPane("step[1]", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, new StepSequence(BigDecimal.ONE), 0)));
        contentPane.add(new DistributionPane("wedge", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, Sequence.WEDGE, 0)));
        contentPane.add(new DistributionPane("shuffle", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, new ShuffleSequence(BigDecimal.valueOf(8)), 0)));
        contentPane.add(new DistributionPane("bitreverse", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, Sequence.BIT_REVERSE, 0)));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /** Pane that displays a title and a visualization of the Sequence's products */
    private static class DistributionPane extends Component {

		private static final long serialVersionUID = -437124282866811738L;

		/** The title to display on top of the pane */
        private String title;

        /** The number generator to use */
        private Generator<Integer> generator;

        /** Initializes the pane's attributes */
        public DistributionPane(String title, Generator<Integer> generator) {
            this.title = title;
            this.generator = generator;
        }

        /** @see Component#paint(java.awt.Graphics) */
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawString(title, 0, 10);
            for (int i = 0; i < N; i++) {
                int y = generator.generate();
                g.fillRect(i, 16 + N - y, 2, 2);
            }
        }

        /** Returns the invocation count multiplied by the magnification factor (2) in each dimension */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(N * 2, N * 2);
        }
    }

}
