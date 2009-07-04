package org.databene.benerator.demo;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.AbstractWeightFunction;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.factory.GeneratorFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Demonstrates the use of Sequences and weight functions for generating numbers.
 * For the horizontal distribution, a weight function of sin^2(cx) is used, vertically
 * a Sequence of type 'cumulated'.
 * <br/>
 * Created: 07.09.2006 19:06:16
 * @see XFunction <br/>
 */
public class ScatterplotDemo extends Component {

	private static final long serialVersionUID = 5264230937667632984L;

	@Override
    public void paint(Graphics g) {
        Generator<Integer> xGen = GeneratorFactory.getNumberGenerator(Integer.class, 0, getWidth(), 1, new XFunction(), 0);
        Generator<Integer> yGen = GeneratorFactory.getNumberGenerator(Integer.class, 0, getHeight(), 1, Sequence.CUMULATED, 0.);
        int n = getWidth() * getHeight() / 16;
        for (int i = 0; i < n; i++) {
            int x = xGen.generate();
            int y = yGen.generate();
            g.drawLine(x, y, x, y);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ScatterplotDemo");
        frame.getContentPane().add(new ScatterplotDemo(), BorderLayout.CENTER);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setBounds(0, 0, (int)(Math.PI * 150), 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    static class XFunction extends AbstractWeightFunction {
        public double value(double param) {
            double s = Math.sin(param / 30);
            return s * s;
        }
    }
}
