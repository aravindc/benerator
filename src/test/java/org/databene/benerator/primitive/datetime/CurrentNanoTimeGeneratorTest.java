/*
 * (c) Copyright 2007-2013 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive.datetime;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidatorContext;

import org.databene.benerator.test.GeneratorClassTest;
import org.databene.commons.validator.bean.AbstractConstraintValidator;
import org.junit.Test;

/**
 * Tests the CurrentNanoTimeGenerator.<br/>
 * <br/>
 * Created: 19.11.2007 20:43:45
 * @author Volker Bergmann
 * @since 0.3.03
 */
public class CurrentNanoTimeGeneratorTest extends GeneratorClassTest {

    public CurrentNanoTimeGeneratorTest() {
        super(CurrentNanoTimeGenerator.class);
    }

    @Test
    public void testProducts() {
        expectGenerations(new CurrentNanoTimeGenerator(), 10, new AbstractConstraintValidator<Annotation, Long>() {
			@Override
			public boolean isValid(Long generatedNanos, ConstraintValidatorContext context) {
                long nanoTime = System.nanoTime();
                return Math.abs(nanoTime - generatedNanos) < 500000000L;
            }
        });
    }
    
}
