/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive.regex;

import org.databene.benerator.Generator;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.benerator.wrapper.MultiGeneratorWrapper;
import org.databene.benerator.wrapper.UniqueCompositeGenerator;
import org.databene.benerator.wrapper.CompositeArrayGenerator;
import org.databene.regex.*;
import org.databene.commons.LocaleUtil;
import org.databene.commons.NullSafeComparator;

import java.util.Locale;
import java.text.ParseException;

/**
 * Generates Strings that comply to a regular expression.<br/>
 * <br/>
 * Created: 18.07.2006 19:32:52
 */
public class RegexStringGenerator extends LightweightGenerator<String> {

    /** Optional String representation of a regular expression */
    private String pattern;

    /** Object representation of the regular expression to match */
    private Regex regex;

    /** The locale from which to choose letters */
    private Locale locale;

    /** maximum value for unlimited quantities */
    private int maxQuantity;

    private boolean unique;

    /** Validity flag for consistency check */
    private boolean dirty;

    private MultiGeneratorWrapper<String, String[]> partsGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    /** Initializes the generator to an empty regular expression, a maxQuantity of 30 and the fallback locale */
    public RegexStringGenerator() {
        this(30);
    }

    /** Initializes the generator to an empty regular expression and the fallback locale */
    public RegexStringGenerator(int maxQuantity) {
        this((String)null, maxQuantity);
    }

    /** Initializes the generator to a maxQuantity of 30 and the fallback locale */
    public RegexStringGenerator(String pattern) {
        this(pattern, 30);
    }

    /** Initializes the generator to the fallback locale */
    public RegexStringGenerator(String pattern, int maxQuantity) {
        this(pattern, LocaleUtil.getFallbackLocale(), maxQuantity);
    }

    public RegexStringGenerator(String pattern, Locale locale, Integer maxQuantity) {
        this(pattern, locale, maxQuantity, false);
    }

    /** Initializes the generator with the String representation of a regular expression */
    public RegexStringGenerator(String pattern, Locale locale, Integer maxQuantity, boolean unique) {
        this(parse(pattern, locale), maxQuantity, unique);
        partsGenerator = (unique ? new UniqueCompositeGenerator<String>(String.class) : new CompositeArrayGenerator<String>(String.class));
        this.locale = locale;
        this.pattern = pattern;
        this.maxQuantity = (maxQuantity != null ? maxQuantity : 30);
        this.unique = unique;
        this.dirty = true;
    }

    public RegexStringGenerator(Regex regex, Integer maxQuantity) {
        this(regex, maxQuantity, false);
    }

    /** Initializes the generator with the object representation of a regular expression */
    public RegexStringGenerator(Regex regex, Integer maxQuantity, boolean unique) {
    	super(String.class);
        partsGenerator = (unique ? new UniqueCompositeGenerator<String>(String.class) : new CompositeArrayGenerator<String>(String.class));
        this.regex = regex;
        this.maxQuantity = (maxQuantity != null ? maxQuantity : 30);
        this.unique = unique;
        this.dirty = true;
    }

    // config properties -----------------------------------------------------------------------------------------------

    /** Sets the String representation of the regular expression */
    public String getPattern() {
        return pattern;
    }

    /** Returns the String representation of the regular expression */
    public void setPattern(String pattern) {
    	if (!NullSafeComparator.equals(this.pattern, pattern)) {
	        this.dirty = true;
	        this.pattern = pattern;
	        this.regex = null;
    	}
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.dirty = true;
        this.locale = locale;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.dirty = true;
        this.maxQuantity = maxQuantity;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    /** ensures consistency of the generators state */
    public void validate() {
        if (dirty) {
            try {
                if (regex == null)
                    regex = parse(pattern, locale);
                Generator<String>[] sources = null;
                if (regex != null) {
                    RegexPart[] parts = regex.getParts();
                    sources = new Generator[parts.length];
                    for (int i = 0; i < parts.length; i++)
                        sources[i] = RegexPartGeneratorFactory.createRegexPartGenerator(parts[i], maxQuantity, unique);
                    partsGenerator.setSources(sources);
                } else
                    partsGenerator.setSources(new ConstantGenerator<String>(null, String.class));
            } catch (Exception e) {
                throw new InvalidGeneratorSetupException(e);
            }
            dirty = false;
        }
    }

    public boolean available() {
        if (dirty)
            validate();
        return partsGenerator.available();
    }

    /** Calls all sub generators and assembles their products to a string */
    public String generate() {
        if (dirty)
            validate();
        if (regex == null)
            return null;
        StringBuilder builder = new StringBuilder();
        Object[] parts = partsGenerator.generate();
        for (Object part : parts)
            builder.append(part);
        return builder.toString();
    }

    public Class<String> getGeneratedType() {
        return String.class;
    }

    public void reset() {
        partsGenerator.reset();
    }

    public void close() {
        partsGenerator.close();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + "[" + (unique ? "unique '" : "'") + regex + "']";
    }

    // private helpers -------------------------------------------------------------------------------------------------

    /** parses the string representation of a regular expression into an object representation */
    private static Regex parse(String pattern, Locale locale) {
        if (pattern == null)
            return null;
        try {
            return new RegexParser(locale).parse(pattern);
        } catch (ParseException e) {
            throw new InvalidGeneratorSetupException("Invalid pattern: " + pattern, e);
        }
    }

}
