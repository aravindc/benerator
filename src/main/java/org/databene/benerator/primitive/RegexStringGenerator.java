/*
 * (c) Copyright 2006-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.factory.VolumeGeneratorFactory;
import org.databene.benerator.wrapper.GeneratorProxy;

import java.util.Locale;

/**
 * Generates Strings that comply to a regular expression.<br/>
 * <br/>
 * Created: 18.07.2006 19:32:52
 * @since 0.1
 * @author Volker Bergmann
 */
public class RegexStringGenerator extends GeneratorProxy<String> {

    /** Optional String representation of a regular expression */
    private String pattern;

    /** The locale from which to choose letters */
    private Locale locale;

    private int minLength;

    private int maxLength;

    /** indicates if the generated values shall be unique */
    private boolean unique;

    // constructors ----------------------------------------------------------------------------------------------------

    /** Initializes the generator to an empty regular expression, a maxQuantity of 30 and the fallback locale */
    public RegexStringGenerator() {
        this(30);
    }

    /** Initializes the generator to an empty regular expression and the fallback locale */
    public RegexStringGenerator(int maxLength) {
        this((String) null, maxLength);
    }

    /** Initializes the generator to a maxQuantity of 30 and the fallback locale */
    public RegexStringGenerator(String pattern) {
        this(pattern, 30);
    }

    /** Initializes the generator to the fallback locale */
    public RegexStringGenerator(String pattern, int maxLength) {
        this(pattern, maxLength, false);
    }

    /** Initializes the generator with the String representation of a regular expression */
    public RegexStringGenerator(String pattern, Integer maxLength, boolean unique) {
        super();
        this.pattern = pattern;
        this.maxLength = maxLength;
        this.unique = unique;
    }

    // config properties -----------------------------------------------------------------------------------------------

    /** Sets the String representation of the regular expression */
    public String getPattern() {
        return pattern;
    }

    /** Returns the String representation of the regular expression */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
	public int getMinLength() {
		return minLength;
	}
	
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxQuantity(int maxLength) {
        this.maxLength = maxLength;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    /** ensures consistency of the generators state */
    @Override
    public void init(GeneratorContext context) {
    	Generator<String> tmp = getGeneratorFactory(context).createRegexStringGenerator(pattern, minLength, maxLength, unique);
        try {
        	System.out.println(pattern);
			setSource(tmp);
            super.init(context);
        } catch (Exception e) {
        	getGeneratorFactory(context).createRegexStringGenerator(pattern, minLength, maxLength, unique);
            throw new InvalidGeneratorSetupException("Illegal regular expression: ", e);
        }
    }

	protected GeneratorFactory getGeneratorFactory(GeneratorContext context) {
		return (context != null ? context.getGeneratorFactory() : new VolumeGeneratorFactory());
	}

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + (unique ? "unique '" : "'") + pattern + "']";
    }

}