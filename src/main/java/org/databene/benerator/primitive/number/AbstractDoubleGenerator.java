package org.databene.benerator.primitive.number;

import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.LightweightGenerator;

/**
 * Abstract Double genarator that serves as parent class for implementation of custom Sequences.<br/>
 * <br/>
 * Created: 07.06.2006 18:51:16
 */
public abstract class AbstractDoubleGenerator extends LightweightGenerator<Double> implements NumberGenerator<Double> {

    /** The minimum value to create */
    protected double min;

    /** The maximum value to create */
    protected double max;

    /** the precision used */
    protected double precision;

    /** first distribution parameter */
    protected double variation1;

    /** second distribution parameter */
    protected double variation2;

    /** consistency flag */
    protected boolean dirty;

    // constructors ----------------------------------------------------------------------------------------------------

    protected AbstractDoubleGenerator() {
        this(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    protected AbstractDoubleGenerator(double min, double max) {
        this(min, max, 1L);
    }

    protected AbstractDoubleGenerator(double min, double max, double precision) {
        this(min, max, precision, 1L, 1L);
    }

    protected AbstractDoubleGenerator(double min, double max, double precision, double variation1, double variation2) {
        if (min > max)
            throw new IllegalArgumentException("min. value (" + min + ") is greater than max. value (" + max + ')');
        this.min = min;
        this.max = max;
        if (precision < 0)
            throw new IllegalArgumentException("Unsupported precision: " + precision);
        this.precision = precision;
        this.variation1 = variation1;
        this.variation2 = variation2;
        this.dirty = true;
    }

    // config properties -----------------------------------------------------------------------------------------------

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
        this.dirty = true;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
        this.dirty = true;
    }

    public Double getPrecision() {
        return precision;
    }

    public void setPrecision(Double precision) {
        this.precision = precision;
        this.dirty = true;
    }

    public Double getVariation1() {
        return variation1;
    }

    public void setVariation1(Double variation1) {
        this.variation1 = variation1;
        this.dirty = true;
    }

    public Double getVariation2() {
        return variation2;
    }

    public void setVariation2(Double variation2) {
        this.variation2 = variation2;
        this.dirty = true;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public Class<Double> getGeneratedType() {
        return Double.class;
    }

    public void validate() {
        if (dirty) {
            if (min > max)
                throw new InvalidGeneratorSetupException("min", "greater than max (" + min + ")");
            super.validate();
            dirty = false;
        }
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + "[min=" + min + ", max=" + max + ", precision=" + precision + ", " +
                "variation1=" + variation1 + ", variation2=" + variation2 + ']';
    }
}
