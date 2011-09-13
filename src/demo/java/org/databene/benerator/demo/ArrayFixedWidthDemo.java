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

package org.databene.benerator.demo;

import org.databene.benerator.Generator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.factory.StochasticGeneratorFactory;
import org.databene.benerator.file.FileBuilder;
import org.databene.benerator.wrapper.MultiSourceArrayGenerator;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.*;
import org.databene.commons.converter.FormatFormatConverter;
import org.databene.commons.format.Alignment;
import org.databene.document.fixedwidth.ArrayFixedWidthWriter;
import org.databene.document.fixedwidth.FixedWidthColumnDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.script.AbstractScript;

import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;

/**
 * Demonstrates the creation of fixed column width files from an array generator.<br/>
 * <br/>
 * Created: 07.06.2007 12:04:39
 * @author Volker Bergmann
 */
public class ArrayFixedWidthDemo {

    private static final String FILE_NAME = "persons.fcw";
    private static final int LENGTH = 5;

    public static void main(String[] args) throws IOException {
        Writer out = null;
        try {
            FixedWidthColumnDescriptor[] descriptors = new FixedWidthColumnDescriptor[] {
                    new FixedWidthColumnDescriptor("rowType", 1, Alignment.RIGHT),
                    new FixedWidthColumnDescriptor("recordNumber", 8, Alignment.RIGHT),
                    new FixedWidthColumnDescriptor("type", 4, Alignment.LEFT),
                    new FixedWidthColumnDescriptor("date", 8, Alignment.LEFT),
                    new FixedWidthColumnDescriptor("partner", 6, Alignment.LEFT),
                    new FixedWidthColumnDescriptor("articleNumber", 6, Alignment.RIGHT),
                    new FixedWidthColumnDescriptor("itemCount", 3, Alignment.LEFT),
                    new FixedWidthColumnDescriptor("itemPrice", 6, Alignment.LEFT)
            };
            //out = new BufferedWriter(new FileWriter(FILE_NAME));
            out = new OutputStreamWriter(System.out);
            HeaderScript headerScript = new HeaderScript(LENGTH);
            DocumentWriter<Object[]> writer = new ArrayFixedWidthWriter<Object>(out, headerScript, null, descriptors);
            System.out.println("Running...");
            long startMillis = System.currentTimeMillis();
            TransactionGenerator generator = new TransactionGenerator();
            FileBuilder.build(generator, LENGTH, writer);
            long elapsedTime = System.currentTimeMillis() - startMillis;
            System.out.println("Created file " + FILE_NAME + " with " + LENGTH + " entries " +
                    "within " + (elapsedTime / 1000) + "s (" + (LENGTH * 1000L / elapsedTime) + " entries per second)");
        } finally {
            IOUtil.close(out);
        }
    }

    public static class TransactionGenerator extends MultiSourceArrayGenerator<Object> {

        public TransactionGenerator() {
            super(Object.class, false, createSources());
        }

        @SuppressWarnings({ "unchecked", "cast" })
        private static Generator<Object>[] createSources() {
            StochasticGeneratorFactory generatorFactory = new StochasticGeneratorFactory();
			Generator<Date> dateGenerator = generatorFactory.createDateGenerator( // transaction date
                    TimeUtil.date(2004, 0, 1), TimeUtil.date(2006, 11, 31), Period.DAY.getMillis(),
                    SequenceManager.RANDOM_SEQUENCE);
            FormatFormatConverter<Date> dateRenderer = new FormatFormatConverter<Date>(Date.class, new SimpleDateFormat("yyyyMMdd"), false);
			Generator<Object>[] sources = (Generator<Object>[]) new Generator[] {
                    new ConstantGenerator<String>("R"),
                    generatorFactory.createNumberGenerator(Integer.class, 1, true, LENGTH, true, 1, SequenceManager.RANDOM_WALK_SEQUENCE, Uniqueness.NONE),
                    generatorFactory.createSampleGenerator(CollectionUtil.toList("BUY", "SALE"), String.class, false), // transaction type
                    WrapperFactory.applyConverter(dateGenerator, dateRenderer), // transaction date
                    generatorFactory.createSampleGenerator(CollectionUtil.toList("Alice", "Bob", "Charly"), String.class, false), // partner
                    generatorFactory.createRegexStringGenerator("[A-Z0-9]{6}", 6, 6, Uniqueness.NONE), // article number
                    generatorFactory.createNumberGenerator(Integer.class, 1, true, 20, true, 1, SequenceManager.RANDOM_SEQUENCE, Uniqueness.NONE), // item count
                    generatorFactory.createNumberGenerator(BigDecimal.class, // item price
                            new BigDecimal("0.50"), true, new BigDecimal("99.99"), true, new BigDecimal("0.01"),
                            SequenceManager.CUMULATED_SEQUENCE, Uniqueness.NONE)
            };
			GeneratorUtil.initAll(sources, new DefaultBeneratorContext());
			return sources;
        }
    }

    private static class HeaderScript extends AbstractScript {

        int length;

        public HeaderScript(int length) {
            this.length = length;
        }

        @Override
        public void execute(Context context, Writer writer) throws IOException {
            writer.write("H");
            writer.write(StringUtil.padRight("Tx", 12, ' '));
            writer.write(StringUtil.padLeft(String.valueOf(length), 8, ' '));
            writer.write(new SimpleDateFormat("yyyyMMdd").format(new Date()));
            writer.write(SystemInfo.getLineSeparator());
        }
    }
}
