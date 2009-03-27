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

package org.databene.benerator.primitive.number.adapter;

import org.databene.benerator.GeneratorClassTest;
import org.databene.benerator.primitive.number.adapter.ShortGenerator;

import java.util.Set;
import java.util.HashSet;

/**
 * Tests the {@link ShortGenerator}.<br/><br/>
 * Created: 11.10.2006 23:04:43
 * @author Volker Bergmann
 */
public class ShortGeneratorTest extends GeneratorClassTest {

    public ShortGeneratorTest() {
        super(ShortGenerator.class);
    }

    public void testDistribution() {
        checkEqualDistribution(ShortGenerator.class, (short)-2, (short)2, (short)1,
                10000, 0.1, createShortSet(-2, -1, 0, 1, 2));
        checkEqualDistribution(ShortGenerator.class, (short)-2, (short)2, (short)2,
                10000, 0.1, createShortSet(-2, 0, 2));
        checkEqualDistribution(ShortGenerator.class, (short)1, (short)5, (short)2,
                10000, 0.1, createShortSet(1, 3, 5));
        checkEqualDistribution(ShortGenerator.class, (short)-5, (short)-1, (short)2,
                10000, 0.1, createShortSet(-5, -3, -1));
    }

    private Set<Short> createShortSet(int ... values) {
        Set<Short> set = new HashSet<Short>(values.length);
        for (int value : values)
            set.add((short)value);
        return set;
    }
}
