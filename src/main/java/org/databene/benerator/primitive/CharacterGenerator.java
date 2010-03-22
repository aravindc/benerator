/*
 * (c) Copyright 2006-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.*;
import org.databene.regex.RegexParser;
import org.databene.benerator.sample.SampleGenerator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.LocaleUtil;

import java.util.*;
import java.text.ParseException;

/**
 * Generates Character values from a character set or a regular expression.<br/>
 * <br/>
 * Created: 09.06.2006 20:34:55
 * @since 0.1
 * @author Volker Bergmann
 */
public class CharacterGenerator extends GeneratorProxy<Character> {

    /** The regular exception */
    private String pattern;

    /** The locale */
    private Locale locale;

    /** The set of characters to generate from */
    private Set<Character> values;

    // constructors ----------------------------------------------------------------------------------------------------

    /**
     * initializes the generator to use letters of the fallback locale.
     * @see org.databene.commons.LocaleUtil#getFallbackLocale()
     */
    public CharacterGenerator() {
        this("\\w");
    }

    /**
     * initializes the generator to create character that match a regular expressions and the fallback locale.
     * @see org.databene.commons.LocaleUtil#getFallbackLocale()
     */
    public CharacterGenerator(String pattern) {
        this(pattern, LocaleUtil.getFallbackLocale());
    }

    /**
     * initializes the generator to create character that match a regular expressions and a locale.
     * @see org.databene.commons.LocaleUtil#getFallbackLocale()
     */
    public CharacterGenerator(String pattern, Locale locale) {
        this.pattern = pattern;
        this.locale = locale;
        this.values = new HashSet<Character>();
    }

    /**
     * initializes the generator to create characters from a character collection.
     * @see org.databene.commons.LocaleUtil#getFallbackLocale()
     */
    public CharacterGenerator(Collection<Character> values) {
        this.pattern = null;
        this.locale = LocaleUtil.getFallbackLocale();
        this.values = new HashSet<Character>(values);
    }

    // config properties -----------------------------------------------------------------------------------------------

    /** Returns the regular expression to match */
    public String getPattern() {
        return pattern;
    }

    /** Sets the regular expression to match */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /** Returns the {@link Locale} of which letters are taken */
    public Locale getLocale() {
        return locale;
    }

    /** Sets the {@link Locale} of which letters are taken */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /** Returns the available values */
    public Set<Character> getValues() {
        return values;
    }

    // source interface ------------------------------------------------------------------------------------------------

    @Override
    public Class<Character> getGeneratedType() {
        return Character.class;
    }

    /**
     * Initializes the generator's state.
     */
    @Override
    public void init(GeneratorContext context) {
    	assertNotInitialized();
        try {
            if (pattern != null) {
                Object regex = new RegexParser(locale).parseSingleChar(pattern);
                values = RegexParser.toSet(regex);
            }
            this.source = new SampleGenerator<Character>(Character.class, values);
            super.init(context);
        } catch (ParseException e) {
            throw new IllegalGeneratorStateException(e);
        }
    }

    /** @see org.databene.benerator.Generator#generate() */
    @Override
    public Character generate() {
    	assertInitialized();
        return source.generate();
    }

    @Override
	public String toString() {
        return getClass().getSimpleName() + values;
    }
    
}
