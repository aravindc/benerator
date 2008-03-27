/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive.number.adapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.databene.benerator.primitive.number.AbstractDoubleGenerator;
import org.databene.benerator.primitive.number.AbstractLongGenerator;
import org.databene.benerator.primitive.number.DoubleFromLongGenerator;
import org.databene.benerator.primitive.number.LongFromDoubleGenerator;
import org.databene.benerator.primitive.number.NumberGenerator;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;
import org.databene.model.function.Sequence;

/**
 * TODO documentation<br/><br/>
 * Created: 27.03.2008 13:00:35
 * @author Volker Bergmann
 */
public class SequenceFactory {

    private static final String CONFIG_FILE_NAME = "org/databene/benerator/sequence.properties";

    private static final Map<String, SequenceDef> defs = new HashMap<String, SequenceDef>();
    
    static {
        try {
            Map<String, String> properties = IOUtil.readProperties(CONFIG_FILE_NAME);
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String name = entry.getKey();
                String setup = entry.getValue();
                String[] generatorClasses = StringUtil.tokenize(setup, ',');
                Class<AbstractDoubleGenerator> dgClass = BeanUtil.forName(generatorClasses[0]);
                SequenceDef def;
                if (generatorClasses.length == 1) {
                    defineSequence(name, dgClass);
                } else {
                    Class<AbstractLongGenerator> ngClass = BeanUtil.forName(generatorClasses[1]);
                    defineSequence(name, dgClass, ngClass);
                }
            }
        } catch (IOException e) {
            throw new ConfigurationError("Configuration file cannot be read", e);
        }
    }

    public static Sequence defineSequence(String name,
            Class<AbstractDoubleGenerator> dgClass,
            Class<AbstractLongGenerator> ngClass) {
        new SequenceDef(name, dgClass, ngClass);
        return new Sequence(name);
    }

    public static Sequence defineSequence(String name,
            Class<? extends NumberGenerator> dgClass) {
        new SequenceDef(name, dgClass);
        return new Sequence(name);
    }

    static AbstractLongGenerator createLongGenerator(Sequence sequence) {
        SequenceDef def = defs.get(sequence.getName());
        if (LongFromDoubleGenerator.class.equals(def.longGeneratorClass))
            return new LongFromDoubleGenerator(createDoubleGenerator(sequence));
        else
            return BeanUtil.newInstance(def.longGeneratorClass);
    }

    static AbstractDoubleGenerator createDoubleGenerator(Sequence sequence) {
        SequenceDef def = defs.get(sequence.getName());
        if (DoubleFromLongGenerator.class.equals(def.doubleGeneratorClass))
            return new DoubleFromLongGenerator(createLongGenerator(sequence));
        else
            return BeanUtil.newInstance(def.doubleGeneratorClass);
    }

    private static class SequenceDef {
        String name;
        Class<? extends AbstractLongGenerator> longGeneratorClass;
        Class<? extends AbstractDoubleGenerator> doubleGeneratorClass;
        
        SequenceDef(String name, Class<? extends NumberGenerator> numberGeneratorClass) {
            this(   name,
                    (AbstractDoubleGenerator.class.isAssignableFrom(numberGeneratorClass) ? (Class<AbstractDoubleGenerator>)numberGeneratorClass : DoubleFromLongGenerator.class),
                    (AbstractLongGenerator.class.isAssignableFrom(numberGeneratorClass) ? (Class<AbstractLongGenerator>)numberGeneratorClass : LongFromDoubleGenerator.class)
            );
        }

        SequenceDef(String name, Class<? extends AbstractDoubleGenerator> doubleGeneratorClass, Class<? extends AbstractLongGenerator> longGeneratorClass) {
            this.name = name;
            this.longGeneratorClass = longGeneratorClass;
            this.doubleGeneratorClass = doubleGeneratorClass;
            if (defs.get(name) != null)
                throw new ConfigurationError("Sequence defined twice: " + name);
            defs.put(name, this);
        }

    }
}
