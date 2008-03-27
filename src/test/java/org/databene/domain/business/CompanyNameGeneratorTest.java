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


package org.databene.domain.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.domain.organization.CompanyNameGenerator;

import junit.framework.TestCase;

/**
 * Tests the CompanyNameGenerator.<br/><br/>
 * Created: 14.03.2008 08:31:26
 * @author Volker Bergmann
 */
public class CompanyNameGeneratorTest extends TestCase {

    private static Log logger = LogFactory.getLog(CompanyNameGeneratorTest.class);
    
    public void test() {
        check("artificial");
        check("tech");
    }

    public void check(String style) {
        CompanyNameGenerator generator = new CompanyNameGenerator(style);
        for (int i = 0; i < 10; i++) {
            String name = generator.generate();
            logger.debug(name);
            assertNotNull(name);
            assertTrue(name.length() > 1);
        }
    }
}
