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

package org.databene.domain.address;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link CityManager}.<br/><br/>
 * Created at 06.05.2008 06:26:40
 * @since 0.5.3
 * @author Volker Bergmann
 */
public class CityManagerTest {
	
	@Test
	public void testParseCityName() {
		check("Unterhaching", "Unterhaching", "");
		check("Bad Wiessee", "Bad Wiessee", "");
		check("Bad Neustadt an der Saale", "Bad Neustadt", "an der Saale");
		check("Bern Land", "Bern", "Land");
		check("Bern 8003", "Bern", "");
		check("Bern 8003 R�tlibank", "Bern", "");
		check("Neustadt (Aisch)", "Neustadt", "(Aisch)");
		check("Neustadt am Kulm", "Neustadt", "am Kulm");
		check("Munich BY", "Munich", "BY");
		
	}

	private void check(String name, String idName, String idExtension) {
		CityId id = CityManager.parseCityName(name, "BY", false);
		assertEquals("Name does not match", idName, id.getName());
		assertEquals("name extension does not match", idExtension, id.getNameExtension());
	}
	
}
