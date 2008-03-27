package org.databene.benerator.demo;

import org.databene.benerator.Generator;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.model.function.Sequence;

import javax.swing.*;
import java.awt.*;

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
        contentPane.add(new DistributionPane("randomWalk[0,2]", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, Sequence.RANDOM_WALK, 0, 2, 0)));
        contentPane.add(new DistributionPane("randomWalk[-1,1]", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, Sequence.RANDOM_WALK, -1, 1, 0)));
        contentPane.add(new DistributionPane("step[1]", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, Sequence.STEP, 1, 0, 0)));
        contentPane.add(new DistributionPane("wedge", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, Sequence.WEDGE, 0, 0, 0)));
        contentPane.add(new DistributionPane("shuffle", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, Sequence.SHUFFLE, 8, 0, 0)));
        contentPane.add(new DistributionPane("bitreverse", GeneratorFactory.getNumberGenerator(Integer.class, 0, N - 1, 1, Sequence.BIT_REVERSE, 0, 0, 0)));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /** Pane that displays a title and a visualization of the Sequence's products */
    private static class DistributionPane extends Component {

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
        public void paint(Graphics g) {
            super.paint(g);
            g.drawString(title, 0, 10);
            for (int i = 0; i < N; i++) {
                int y = generator.generate();
                g.fillRect(i, 16 + N - y, 2, 2);
            }
        }

        /** Returns the invocation count multiplied by the magnification factor (2) in each dimension */
        public Dimension getPreferredSize() {
            return new Dimension(N * 2, N * 2);
        }
    }

}
