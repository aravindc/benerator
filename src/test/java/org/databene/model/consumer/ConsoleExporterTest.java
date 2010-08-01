/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.model.consumer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.databene.commons.Patterns;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.TimestampFormatter;
import org.databene.model.data.Entity;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link ConsoleExporter}.<br/><br/>
 * Created at 11.04.2008 06:58:53
 * @since 0.5.2
 * @author Volker Bergmann
 */
public class ConsoleExporterTest {
	
	@Test
	public void testSimpleTypes() {
		check("Test", "Test");
		check(1, "1");
		check(1., "1.0");
		check(true, "true");
	}

	@Test
	public void testDate() {
		Date date = new Date(((60 + 2) * 60 + 3) * 1000);
		check(date, new SimpleDateFormat("yyyy-MM-dd").format(date));
	}

	@Test
	public void testTimestamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		TimestampFormatter formatter = new TimestampFormatter(Patterns.DEFAULT_DATETIME_SECONDS_PATTERN + '.');
		check(timestamp, formatter.format(timestamp));
	}
	
	@Test
	public void testEntity() {
		Entity entity = new Entity("e", "i", 3, "d", 5., "s", "sss");
		check(entity, "e[i=3, d=5.0, s=sss]");
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------

	private void check(Object in, String expectedOut) {
		ConsoleExporter exporter = new ConsoleExporter();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		exporter.setOut(new PrintStream(stream));
		try {
			exporter.startConsuming(in);
			exporter.finishConsuming(in);
			exporter.flush();
			assertEquals(expectedOut + SystemInfo.getLineSeparator(), stream.toString());
		} finally {
			exporter.close();
		}
	}
	
}
