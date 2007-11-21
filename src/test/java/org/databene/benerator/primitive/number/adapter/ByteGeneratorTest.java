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
import org.databene.commons.ArrayUtil;

/**
 * Created: 11.10.2006 23:04:43
 */
public class ByteGeneratorTest extends GeneratorClassTest {

    public ByteGeneratorTest() {
        super(ByteGenerator.class);
    }

    public void testDefaultConstructor() {
        new ByteGenerator();
    }

    public void test() {
        checkEqualDistribution(ByteGenerator.class, (byte)-2, (byte) 2, (byte)1,
                10000, 0.1, ArrayUtil.toSet((byte)-2, (byte)-1, (byte)0, (byte)1, (byte)2));
        checkEqualDistribution(ByteGenerator.class, (byte)-2, (byte) 2, (byte)2,
                10000, 0.1, ArrayUtil.toSet((byte)-2, (byte)0, (byte)2));
        checkEqualDistribution(ByteGenerator.class, (byte) 1, (byte) 5, (byte)2,
                10000, 0.1, ArrayUtil.toSet((byte)1, (byte)3, (byte)5));
        checkEqualDistribution(ByteGenerator.class, (byte)-5, (byte)-1, (byte)2,
                10000, 0.1, ArrayUtil.toSet((byte)-5, (byte)-3, (byte)-1));
    }
}
