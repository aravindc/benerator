/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.model.version;

import org.databene.commons.StringUtil;
import org.databene.model.version.VersionNumber;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the class {@link VersionNumber}.<br/>
 * <br/>
 * Created at 22.12.2008 16:51:22
 * @since 0.4.7
 * @author Volker Bergmann
 */

public class VersionNumberTest {

	@Test
	public void testNumberCreation() {
		checkCreation(null, 1);
		checkCreation("", 1);
		checkCreation("1", 1);
		checkCreation("1.0", 1, ".", 0);
		checkCreation("1.0.0", 1, ".", 0, ".", 0);
		checkCreation("10.2.3.4", 10, ".", 2, ".", 3, ".", 4);
		checkCreation("1.6.0_07", 1, ".", 6, ".", 0, "_", 7);
		checkCreation("1.6.0_11", 1, ".", 6, ".", 0, "_", 11);
	}
	
	@Test
	public void testMixedCreation() {
		checkCreation("1-alpha", 1, "-", "alpha");
		checkCreation("1.0-alpha", 1, ".", 0, "-", "alpha");
		checkCreation("1.0.0-alpha3", 1, ".", 0, ".", 0, "-", "alpha", "", 3);
		checkCreation("10.2-alpha-2.3", 10, ".", 2, "-", "alpha", "-", 2, ".", 3);
	}
	
	@Test
	public void testCompareNumber() {
		// test versions of equal length
		VersionNumber v10202 = new VersionNumber("10.2.0.2");
		VersionNumber v10204 = new VersionNumber("10.2.0.04");
		VersionNumber v9999 = new VersionNumber("9.9.9.9");
		assertTrue(v10202.compareTo(v10202) ==  0);
		assertTrue( v9999.compareTo(v10202) == -1);
		assertTrue(v10202.compareTo( v9999) ==  1);
		assertTrue(v10202.compareTo(v10204) == -1);
		assertTrue(v10204.compareTo(v10202) ==  1);
		
		// test versions of different length
		VersionNumber v10 = new VersionNumber("1.0");
		VersionNumber v2 = new VersionNumber("2");
		VersionNumber v100 = new VersionNumber("1.0.0");
		VersionNumber v101 = new VersionNumber("1.0.1");
		assertTrue(v10 .compareTo(v100) ==  0);
		assertTrue(v10 .compareTo(v101) == -1);
		assertTrue(v10 .compareTo(v2  ) == -1);
		assertTrue(v100.compareTo(v10 ) ==  0);
		assertTrue(v100.compareTo(v101) == -1);
		assertTrue(v100.compareTo(v2  ) == -1);
		assertTrue(v101.compareTo(v10 ) ==  1);
		assertTrue(v101.compareTo(v100) ==  1);
		assertTrue(v101.compareTo(v2  ) == -1);
		assertTrue(v2  .compareTo(v100) ==  1);
		assertTrue(v2  .compareTo(v101) ==  1);
		assertTrue(v2  .compareTo(v10 ) ==  1);
	}
	
	@Test
	public void testCompareMixed() {
		VersionNumber v10 = new VersionNumber("1.0");
		VersionNumber v2 = new VersionNumber("2");
		VersionNumber v2a = new VersionNumber("2-alpha");
		VersionNumber v2A = new VersionNumber("2-ALPHA");
		VersionNumber v20a = new VersionNumber("2.0-alpha");
		VersionNumber v20b = new VersionNumber("2.0-beta");
		VersionNumber v20 = new VersionNumber("2.0");
		VersionNumber v20f = new VersionNumber("2.0-FINAL");
		VersionNumber v20s = new VersionNumber("2.0-SP");
		VersionNumber v20s2 = new VersionNumber("2.0-SP2");
		VersionNumber v21a = new VersionNumber("2.1-alpha");
		VersionNumber v3 = new VersionNumber("3");
		
		assertEquals(-1, v10.compareTo(v2));
		assertEquals( 1, v2.compareTo(v10));
		assertEquals(-1, v2a.compareTo(v2));
		assertEquals( 1, v2.compareTo(v2a));
		assertEquals( 0, v2a.compareTo(v2A));
		assertEquals(-1, v2a.compareTo(v20a));
		assertEquals( 1, v20a.compareTo(v2a));
		assertEquals(-1, v20a.compareTo(v20b));
		assertEquals( 1, v20b.compareTo(v20a));
		assertEquals(-1, v20b.compareTo(v20));
		assertEquals( 1, v20.compareTo(v20b));
		assertEquals( 0, v20.compareTo(v20f));
		assertEquals( 0, v20f.compareTo(v20));
		assertEquals(-1, v20f.compareTo(v20s));
		assertEquals( 1, v20s.compareTo(v20f));
		assertEquals(-1, v20s.compareTo(v20s2));
		assertEquals( 1, v20s2.compareTo(v20s));
		assertEquals(-1, v20s2.compareTo(v21a));
		assertEquals( 1, v21a.compareTo(v20s2));
		assertEquals(-1, v21a.compareTo(v3));
		assertEquals( 1, v3.compareTo(v21a));
	}
	
	// private helpers -------------------------------------------------------------------------------------------------
	
	private void checkCreation(String versionString, Object... components) {
		VersionNumber result = new VersionNumber(versionString);
		VersionNumber expected = new VersionNumber(components);
		assertEquals(expected, result);
		checkToString(versionString);
	}
	
	private void checkToString(String version) {
		if (!StringUtil.isEmpty(version))
			assertEquals(version, new VersionNumber(version).toString());
		else
			assertEquals("1", new VersionNumber(version).toString());
	}
}
