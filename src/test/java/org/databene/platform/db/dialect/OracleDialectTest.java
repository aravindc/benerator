/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.db.dialect;

import static org.junit.Assert.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.databene.commons.TimeUtil;
import org.junit.Test;

/**
 * Tests the {@link OracleDialect}.<br/><br/>
 * Created: 10.11.2009 17:22:59
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class OracleDialectTest extends DatabaseDialectTest {

	public OracleDialectTest() {
	    super(new OracleDialect());
    }

	@Test
	public void testnextSequenceValue() {
		assertEquals("select SEQ.nextval from dual", dialect.renderFetchSequenceValue("SEQ"));
	}
	
	@Test
	public void testDropSequence() {
		assertEquals("drop sequence SEQ", dialect.renderDropSequence("SEQ"));
	}
	
	@Test
	public void testFormatDate() {
		Date date = TimeUtil.date(1971, 1, 3, 13, 14, 15, 0);
		assertEquals("to_date('1971-02-03 13:14:15', 'yyyy-mm-dd HH24:mi:ss')", 
				dialect.formatValue(date));
	}
	
	@Test
	public void testFormatTime() {
		Time time = TimeUtil.time(13, 14, 15, 123);
		assertEquals("to_date('13:14:15', 'HH24:mi:ss')", dialect.formatValue(time));
	}
	
	@Test
	public void testFormatTimestamp() {
		Timestamp timestamp = TimeUtil.timestamp(1971, 1, 3, 13, 14, 15, 123456789);
		assertEquals("to_timestamp('1971-02-03 13:14:15.123456789', 'yyyy-mm-dd HH24:mi:ss.FF')", 
				dialect.formatValue(timestamp));
	}
	
}
