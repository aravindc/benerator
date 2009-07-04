/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.ValidatingGenerator;
import org.databene.commons.validator.StringLengthValidator;

import java.text.MessageFormat;

/**
 * Assembles the output of several source generators by a java.text.MessageFormat.<br/>
 * <br/>
 * Created: 08.06.2006 21:48:08
 */
public class MessageGenerator extends ValidatingGenerator<String> {

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
    private CompositeArrayGenerator<?> helper;

    private boolean dirty;

    // constructors ----------------------------------------------------------------------------------------------------

    /** Sets minLength to 0, maxLength to 30 and all other values empty. */
    public MessageGenerator() {
        this(null);
    }

    public MessageGenerator(String pattern, Generator<?> ... sources) {
        this(pattern, 0, 30, sources);
    }

    /** Initializes Generator */
    @SuppressWarnings("unchecked")
    public MessageGenerator(String pattern, int minLength, int maxLength, Generator ... sources) {
        super(new StringLengthValidator());
        this.pattern = pattern;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.helper = new CompositeArrayGenerator<Object>(Object.class, sources);
        this.dirty = true;
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
    @SuppressWarnings("unchecked")
    public void setSources(Generator[] sources) {
        this.helper.setSources(sources);
    }

    // generator interface ---------------------------------------------------------------------------------------------

    /** ensures consistency of the generator's state */
    public void validate() {
        if (dirty) {
            if (pattern == null)
                throw new InvalidGeneratorSetupException("pattern", "is null");
            StringLengthValidator v = (StringLengthValidator) validator;
            v.setMinLength(minLength);
            v.setMaxLength(maxLength);
            helper.validate();
            dirty = false;
        }
    }

    public Class<String> getGeneratedType() {
        return String.class;
    }

    /** Implementation of ValidatingGenerator's generation callback method */
    public String generateImpl() {
        Object[] values = helper.generate();
        return MessageFormat.format(pattern, values);
    }

    /** @see org.databene.benerator.Generator#reset() */
    public void reset() {
        helper.reset();
    }

    /** @see org.databene.benerator.Generator#close() */
    public void close() {
        helper.close();
    }

    public boolean available() {
        return helper.available();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    /** Returns a String representation of the generator */
    public String toString() {
        return getClass().getSimpleName() + "[pattern='" + pattern + "', " + minLength + "<=length<=" + maxLength + "]";
    }
}
