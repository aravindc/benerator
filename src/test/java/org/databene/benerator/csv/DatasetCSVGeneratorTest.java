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

package org.databene.benerator.csv;

import junit.framework.TestCase;

/**
 * Tests the DataSetCSVGenerator.<br/><br/>
 * Created: 21.03.2008 16:58:20
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class DatasetCSVGeneratorTest extends TestCase {

    private static final String FAMILY_NAME = "org/databene/domain/person/familyName";
    private static final String REGION = "org/databene/dataset/region";

    public void testDE() {
        DatasetCSVGenerator<String> generator = new DatasetCSVGenerator<String>(FAMILY_NAME + "_{0}.csv", "DE", REGION, "UTF-8");
        boolean mueller = false;
        for (int i = 0; i < 1000; i++) {
            if ("M�ller".equals(generator.generate()))
                mueller = true;
        }
        assertTrue(mueller);
    }

    public void testEurope() {
        DatasetCSVGenerator<String> generator = new DatasetCSVGenerator<String>(FAMILY_NAME + "_{0}.csv", "europe", REGION, "UTF-8");
        boolean mueller = false; // German name
        boolean garcia = false;  // Spanish name
        for (int i = 0; i < 100000 && (!mueller || !garcia); i++) {
            String name = generator.generate();
            if ("M�ller".equals(name))
                mueller = true;
            if ("Garc�a".equals(name))
                garcia = true;
        }
        assertTrue(mueller);
        assertTrue(garcia);
    }
}
