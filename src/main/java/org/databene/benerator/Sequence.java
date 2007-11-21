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

package org.databene.benerator;

import org.databene.commons.ConfigurationError;
import org.databene.commons.BeanUtil;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Properties;
import java.io.IOException;

/**
 * Provides access to specific Sequence number Generators.<br/>
 * <br/>
 * Created: 11.09.2006 21:12:57
 */
public class Sequence implements Distribution {

    private static Map<String, Sequence> map = new HashMap<String, Sequence>();

    private static final String CONFIG_FILE_NAME = "org/databene/benerator/sequence.properties";

    static {
        try {
            Properties properties = IOUtil.readProperties(CONFIG_FILE_NAME);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String name = (String) entry.getKey();
                String setup = (String) entry.getValue();
                String[] generatorClasses = StringUtil.tokenize(setup, ',');
                Class<AbstractDoubleGenerator> dgClass
                        = (Class<AbstractDoubleGenerator>) BeanUtil.forName(generatorClasses[0]);
                if (generatorClasses.length == 1) {
                    new Sequence(name, dgClass);
                } else {
                    Class<AbstractLongGenerator> ngClass
                            = (Class<AbstractLongGenerator>) BeanUtil.forName(generatorClasses[1]);
                    new Sequence(name, dgClass, ngClass);
                }
            }
        } catch (IOException e) {
            throw new ConfigurationError("Configuration file cannot be read", e);
        }
    }

    // predefined Sequences --------------------------------------------------------------------------------------------

    public static final Sequence RANDOM      = Sequence.getInstance("random");
    public static final Sequence SHUFFLE     = Sequence.getInstance("shuffle");
    public static final Sequence CUMULATED   = Sequence.getInstance("cumulated");
    public static final Sequence RANDOM_WALK = Sequence.getInstance("randomWalk");
    public static final Sequence STEP        = Sequence.getInstance("step");
    public static final Sequence WEDGE       = Sequence.getInstance("wedge");
    public static final Sequence BIT_REVERSE = Sequence.getInstance("bitreverse");

    // attributes ------------------------------------------------------------------------------------------------------

    private String name;
    private Class<? extends AbstractLongGenerator> longGeneratorClass;
    private Class<? extends AbstractDoubleGenerator> doubleGeneratorClass;

    // factory methods -------------------------------------------------------------------------------------------------

    public Sequence(String name, Class<? extends NumberGenerator> numberGeneratorClass) {
        this(
                name,
                (AbstractDoubleGenerator.class.isAssignableFrom(numberGeneratorClass) ? (Class<AbstractDoubleGenerator>)numberGeneratorClass : DoubleFromLongGenerator.class),
                (AbstractLongGenerator.class.isAssignableFrom(numberGeneratorClass) ? (Class<AbstractLongGenerator>)numberGeneratorClass : LongFromDoubleGenerator.class)
        );
    }

    public Sequence(String name, Class<? extends AbstractDoubleGenerator> doubleGeneratorClass, Class<? extends AbstractLongGenerator> longGeneratorClass) {
        this.name = name;
        this.longGeneratorClass = longGeneratorClass;
        this.doubleGeneratorClass = doubleGeneratorClass;
        if (map.get(name) != null)
            throw new ConfigurationError("Sequence defined twice: " + name);
        map.put(name, this);
    }

    public static Collection<Sequence> getInstances() {
        return map.values();
    }

    public static Sequence getInstance(String name) {
        Sequence sequence = map.get(name);
        if (sequence == null)
            throw new IllegalArgumentException("Sequence undefined: " + name);
        return sequence;
    }

    // interface -------------------------------------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public AbstractLongGenerator createLongGenerator() {
        if (LongFromDoubleGenerator.class.equals(longGeneratorClass))
            return new LongFromDoubleGenerator(createDoubleGenerator());
        else
            return BeanUtil.newInstance(longGeneratorClass);
    }

    public AbstractDoubleGenerator createDoubleGenerator() {
        if (DoubleFromLongGenerator.class.equals(doubleGeneratorClass))
            return new DoubleFromLongGenerator(createLongGenerator());
        else
            return BeanUtil.newInstance(doubleGeneratorClass);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return name;
    }
}
