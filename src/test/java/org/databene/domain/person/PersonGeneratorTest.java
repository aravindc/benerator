/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.person;

import org.databene.benerator.GeneratorClassTest;
import org.databene.domain.address.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Tests the {@link PersonGenerator}.<br/>
 * <br/>
 * Created: 09.06.2006 22:14:08
 * @since 0.1
 * @author Volker Bergmann
 */
public class PersonGeneratorTest extends GeneratorClassTest {

    private static final Logger logger = LoggerFactory.getLogger(PersonGeneratorTest.class);

    public PersonGeneratorTest() {
        super(PersonGenerator.class);
    }

    public void testGermany() {
        PersonGenerator generator = new PersonGenerator(Country.GERMANY, Locale.GERMANY);
        for (int i = 0; i < 10; i++) {
            Person person = generator.generate();
            logger.debug(person.toString());
        }
    }

    public void testRussia() {
        PersonGenerator generator = new PersonGenerator(Country.RUSSIA, new Locale("ru"));
        for (int i = 0; i < 10; i++) {
            Person person = generator.generate();
            logger.debug(person.toString());
        }
    }

    public void testPoland() {
        PersonGenerator generator = new PersonGenerator(Country.POLAND, new Locale("pl"));
        for (int i = 0; i < 10; i++) {
            Person person = generator.generate();
            logger.debug(person.toString());
        }
    }

    public void testChina() {
        PersonGenerator generator = new PersonGenerator(Country.CHINA, Locale.CHINESE);
        for (int i = 0; i < 10; i++) {
            Person person = generator.generate();
            logger.debug(person.toString());
        }
    }
}
