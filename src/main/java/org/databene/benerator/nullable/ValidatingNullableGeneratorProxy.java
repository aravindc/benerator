/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

package org.databene.benerator.nullable;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proxy for {@link NullableGenerator} that only generates products which pass a validator.<br/><br/>
 * Created: 18.02.2010 11:19:59
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ValidatingNullableGeneratorProxy<E> extends NullableGeneratorWrapper<E, E>{

    /** The Logger */
    private static Logger logger = LoggerFactory.getLogger(ValidatingNullableGeneratorProxy.class);

    /** The number of invalid consecutive generations that causes a warning */
    public static final int WARNING_THRESHOLD = 100;

    /** The number of invalid consecutive generations that causes an exception */
    public static final int ERROR_THRESHOLD   = 1000;

    /** The validator used for validation */
    protected Validator<? super E> validator;

    /** Constructor that takes the validator */
	public ValidatingNullableGeneratorProxy(NullableGenerator<E> realValidator, Validator<? super E> validator) {
	    super(realValidator);
	    this.validator = validator;
    }

	public Class<E> getGeneratedType() {
	    return source.getGeneratedType();
    }
    
    /**
     * Generator implementation that calls generateImpl() to generate values
     * and validator.validate() in order to validate them.
     * Consecutive invalid values are counted. If this count reaches the
     * WARNING_THRESHOLD value, a warning is logged, if the count reaches the
     * ERROR_THRESHOLD, an exception is raised.
     */
	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
        boolean valid;
        int count = 0;
        do {
        	wrapper = source.generate(wrapper);
        	if (wrapper == null)
        		return null;
			valid = validator.valid(wrapper.product);
            count++;
            if (count >= ERROR_THRESHOLD)
                throw new IllegalGeneratorStateException("Aborting generation, because of " + ERROR_THRESHOLD
                        + " consecutive invalid generations. Validator is: " + validator +
                    ". Last attempt was: " + wrapper.product);
        } while (!valid);
        if (count >= WARNING_THRESHOLD)
            logger.warn("Inefficient generation: needed " + count + " tries to generate a valid value. ");
        return wrapper;
    }

}
