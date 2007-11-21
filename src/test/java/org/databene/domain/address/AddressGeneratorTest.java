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

package org.databene.domain.address;

import org.databene.region.Country;
import org.databene.benerator.GeneratorClassTest;

/**
 * Tests the AddressGenerator<br/><br/>
 * Created: 12.06.2007 06:45:41
 */
public class AddressGeneratorTest extends GeneratorClassTest {

    private static boolean quiet = true;

    public AddressGeneratorTest() {
        super(AddressGenerator.class);
    }

    public void testGermany() {
        check(Country.GERMANY);
    }
/* TODO v0.4 test austrian and swiss addresses
    public void testAustria() {
        check(Country.AUSTRIA);
    }

    public void testSwitzerland() {
        check(Country.SWITZERLAND);
    }
*/
    private void check(Country country) {
        AddressGenerator generator = new AddressGenerator(country);
        for (int i = 0; i < 100; i++) {
            Address address = generator.generate();
            if (!quiet)
                System.out.println(address);
        }
    }
}