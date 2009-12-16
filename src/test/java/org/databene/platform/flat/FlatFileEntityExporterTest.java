/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.flat;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;

import org.databene.commons.Encodings;
import org.databene.commons.FileUtil;
import org.databene.commons.ReaderLineIterator;
import org.databene.model.data.Entity;
import org.junit.Test;

/**
 * Tests the {@link FlatFileEntityExporter}.<br/><br/>
 * Created: 14.11.2009 10:04:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class FlatFileEntityExporterTest {

	private static final String ENCODING = Encodings.UTF_8;

	@Test
	public void testMultiThreaded() throws Exception {
		File file = File.createTempFile(getClass().getSimpleName(), ".flat", new File("target"));
		String uri = file.getAbsolutePath();
		FlatFileEntityExporter exporter = new FlatFileEntityExporter(uri, ENCODING, "name[10],age[3r0]");
		try {
			Entity entity = new Entity("Person", "name", "Alice", "age", 23);
			exporter.startConsuming(entity);
			exporter.finishConsuming(entity);
		} finally {
			exporter.close();
		}
		assertTrue(file.exists());
		ReaderLineIterator iterator = new ReaderLineIterator(new FileReader(file));
		try {
			assertTrue(iterator.hasNext());
			String line = iterator.next();
			assertEquals("Alice     023", line);
		} finally {
			iterator.close();
		}
		FileUtil.deleteIfExists(file);
	}
	
}
