package org.databene.benerator.primitive.number;

import org.databene.benerator.Generator;

/**
 * Common parent interface for all number generators.<br/>
 * <br/>
 * Created: 28.12.2006 07:33:42
 */
public interface NumberGenerator<E> extends Generator<E> {

    E getMin();
    void setMin(E min);

    E getMax();
    void setMax(E max);

    E getPrecision();
    void setPrecision(E precision);

    E getVariation1();
    void setVariation1(E variation1);

    E getVariation2();
    void setVariation2(E variation2);

//    it's the base class for specific generators, too
//    Distribution getDistribution();
//    void setDistribution(Distribution distribution);
}
