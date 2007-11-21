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

package org.databene.benerator.demo;

import org.databene.domain.person.PersonGenerator;
import org.databene.domain.person.Person;
import org.databene.benerator.FileBuilder;
import org.databene.commons.IOUtil;
import org.databene.document.flat.BeanFlatFileWriter;
import org.databene.document.flat.FlatFileColumnDescriptor;
import org.databene.model.format.Alignment;
import org.databene.model.DocumentWriter;

import java.io.*;

/**
 * Demonstrates how to format JavaBeans in a flat file.<br/>
 * <br/>
 * Created: 07.06.2007 12:04:39
 */
public class PersonFlatFileDemo {

    private static final String FILE_NAME = "persons.flat";
    private static final int LENGTH = 5;

    public static void main(String[] args) throws IOException {
        Writer out = null;
        try {
            FlatFileColumnDescriptor[] descriptors = new FlatFileColumnDescriptor[] {
                    new FlatFileColumnDescriptor("salutation", 8, Alignment.LEFT),
                    new FlatFileColumnDescriptor("title", 10, Alignment.LEFT),
                    new FlatFileColumnDescriptor("givenName", 20, Alignment.LEFT),
                    new FlatFileColumnDescriptor("familyName", 20, Alignment.LEFT)
            };
            //out = new BufferedWriter(new FileWriter(FILE_NAME));
            out = new OutputStreamWriter(System.out);
            DocumentWriter<Person> writer = new BeanFlatFileWriter<Person>(out, descriptors);
            System.out.println("Running...");
            long startMillis = System.currentTimeMillis();
            FileBuilder.build(new PersonGenerator(), LENGTH, writer);
            long elapsedTime = System.currentTimeMillis() - startMillis;
            System.out.println("Created file " + FILE_NAME + " with " + LENGTH + " entries " +
                    "within " + (elapsedTime / 1000) + "s (" + (LENGTH * 1000L / elapsedTime) + " entries per second)");
        } finally {
            IOUtil.close(out);
        }
    }
}
