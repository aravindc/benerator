/*
 * (c) Copyright 2006, 2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.file.FileBuilder;
import org.databene.benerator.wrapper.CompositeArrayGenerator;
import org.databene.benerator.wrapper.FormatFormatGenerator;
import org.databene.commons.*;
import org.databene.commons.format.Alignment;
import org.databene.document.flat.FlatFileColumnDescriptor;
import org.databene.document.flat.ArrayFlatFileWriter;
import org.databene.model.function.Sequence;
import org.databene.script.AbstractScript;
import org.databene.script.Script;

import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;

/**
 * Demonstrates the creation of flat files from an array generator.<br/>
 * <br/>
 * Created: 07.06.2007 12:04:39
 */
public class ArrayFlatFileDemo {

    private static final String FILE_NAME = "persons.flat";
    private static final int LENGTH = 5;

    public static void main(String[] args) throws IOException {
        Writer out = null;
        try {
            FlatFileColumnDescriptor[] descriptors = new FlatFileColumnDescriptor[] {
                    new FlatFileColumnDescriptor("rowType", 1, Alignment.RIGHT),
                    new FlatFileColumnDescriptor("recordNumber", 8, Alignment.RIGHT),
                    new FlatFileColumnDescriptor("type", 4, Alignment.LEFT),
                    new FlatFileColumnDescriptor("date", 8, Alignment.LEFT),
                    new FlatFileColumnDescriptor("partner", 6, Alignment.LEFT),
                    new FlatFileColumnDescriptor("articleNumber", 6, Alignment.RIGHT),
                    new FlatFileColumnDescriptor("itemCount", 3, Alignment.LEFT),
                    new FlatFileColumnDescriptor("itemPrice", 6, Alignment.LEFT)
            };
            //out = new BufferedWriter(new FileWriter(FILE_NAME));
            out = new OutputStreamWriter(System.out);
            HeaderScript headerScript = new HeaderScript(LENGTH);
            DocumentWriter<Object[]> writer = new ArrayFlatFileWriter<Object>(out, headerScript, null, descriptors);
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

    public static class TransactionGenerator extends CompositeArrayGenerator<Object> {

        public TransactionGenerator() {
            super(Object.class, createSources());
        }

        private static Generator<Object>[] createSources() {
            Generator<Date> dateGenerator = GeneratorFactory.getDateGenerator( // transaction date
                    TimeUtil.date(2004, 0, 1), TimeUtil.date(2006, 11, 31), Period.DAY.getMillis(),
                    Sequence.RANDOM, 0);
            return (Generator<Object>[]) new Generator[] {
                    new ConstantGenerator("R"),
                    GeneratorFactory.getNumberGenerator(Integer.class, 1, LENGTH, 1, Sequence.RANDOM_WALK, 1, 1, 0),
                    GeneratorFactory.getSampleGenerator("BUY", "SALE"), // transaction type
                    new FormatFormatGenerator(dateGenerator, new SimpleDateFormat("yyyyMMdd")),
                    GeneratorFactory.getSampleGenerator("Alice", "Bob", "Charly"), // partner
                    GeneratorFactory.getRegexStringGenerator("[A-Z0-9]{6}", 6, 6, null, 0), // article number
                    GeneratorFactory.getNumberGenerator(Integer.class, 1, 20, 1, Sequence.RANDOM, 1, 1, 0), // item count
                    GeneratorFactory.getNumberGenerator(BigDecimal.class, // item price
                            new BigDecimal("0.50"), new BigDecimal("99.99"), new BigDecimal("0.01"),
                            Sequence.CUMULATED, 0)
            };
        }
    }

    private static class HeaderScript extends AbstractScript {

        int length;

        public HeaderScript(int length) {
            this.length = length;
        }

        public void execute(Context context, Writer writer) throws IOException {
            writer.write("H");
            writer.write(StringUtil.padRight("Tx", 12, ' '));
            writer.write(StringUtil.padLeft(String.valueOf(length), 8, ' '));
            writer.write(new SimpleDateFormat("yyyyMMdd").format(new Date()));
            writer.write(SystemInfo.getLineSeparator());
        }
    }
}
