/*
 * (c) Copyright 2006-2013 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.benerator.util.ValidatingGenerator;
import org.databene.benerator.util.WrapperProvider;
import org.databene.commons.CollectionUtil;
import org.databene.commons.validator.StringLengthValidator;

import java.text.MessageFormat;
import java.util.List;

/**
 * Assembles the output of several source generators by a java.text.MessageFormat.<br/>
 * <br/>
 * Created: 08.06.2006 21:48:08
 * @since 0.1
 * @author Volker Bergmann
 */
public class MessageGenerator extends ValidatingGenerator<String> implements NonNullGenerator<String> {

    /**
     * Pattern of the MessageFormat to use.
     * @see MessageFormat
     */
    private String pattern;

    /** minimum length of the generated String */
    private int minLength;

    /** maximum length of the generated String */
    private int maxLength;

    /** provides the objects to format */
    private SimpleMultiSourceArrayGenerator<?> helper;

	private WrapperProvider<Object[]> sourceWrapperProvider;

    // constructors ----------------------------------------------------------------------------------------------------

    /** Sets minLength to 0, maxLength to 30 and all other values empty. */
    public MessageGenerator() {
        this(null);
    }

    public MessageGenerator(String pattern, Generator<?> ... sources) {
        this(pattern, 0, 30, sources);
    }

    /** Initializes Generator */
    public MessageGenerator(String pattern, int minLength, int maxLength, Generator<?> ... sources) {
        super(new StringLengthValidator());
        this.pattern = pattern;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.helper = new SimpleMultiSourceArrayGenerator<Object>(Object.class, sources);
        this.sourceWrapperProvider = new WrapperProvider<Object[]>();
    }

    // config properties -----------------------------------------------------------------------------------------------

    /** Returns the pattern property */
    public String getPattern() {
        return pattern;
    }

    /** Sets the pattern property */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /* Returns the minimum length of the generated String */
    public int getMinLength() {
        return minLength;
    }

    /* Sets the minimum length of the generated String */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    /* Returns the maximum length of the generated String */
    public int getMaxLength() {
        return maxLength;
    }

    /* Sets the maximum length of the generated String */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /** Sets the source generators */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setSources(Generator<?>[] sources) {
        this.helper.setSources((List) CollectionUtil.toList(sources));
    }

    // generator interface ---------------------------------------------------------------------------------------------

    /** ensures consistency of the generator's state */
    @Override
    public void init(GeneratorContext context) {
        if (pattern == null)
            throw new InvalidGeneratorSetupException("pattern", "is null");
        StringLengthValidator v = (StringLengthValidator) validator;
        v.setMinLength(minLength);
        v.setMaxLength(maxLength);
        helper.init(context);
        super.init(context);
    }

    @Override
	public Class<String> getGeneratedType() {
        return String.class;
    }

	@Override
	public String generate() {
		return GeneratorUtil.generateNonNull(this);
	}

    /** Implementation of ValidatingGenerator's generation callback method */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected ProductWrapper<String> doGenerate(ProductWrapper<String> wrapper) {
        Object[] values = helper.generate((ProductWrapper) getSourceWrapper()).unwrap();
        if (values == null)
        	return null;
        else
        	return wrapper.wrap(MessageFormat.format(pattern, values));
    }

	/** @see org.databene.benerator.Generator#reset() */
    @Override
    public void reset() {
        helper.reset();
        super.reset();
    }

	@Override
	public boolean isParallelizable() {
	    return helper.isParallelizable();
    }

	@Override
	public boolean isThreadSafe() {
	    return helper.isThreadSafe();
    }

	/** @see org.databene.benerator.Generator#close() */
    @Override
    public void close() {
        helper.close();
        super.close();
    }

    private ProductWrapper<Object[]> getSourceWrapper() {
		return sourceWrapperProvider.get();
	}

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    /** Returns a String representation of the generator */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[pattern='" + pattern + "', " + minLength + "<=length<=" + maxLength + "]";
    }

}
