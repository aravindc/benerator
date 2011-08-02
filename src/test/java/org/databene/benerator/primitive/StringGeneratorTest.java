/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive;

import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.validator.PrefixValidator;
import org.databene.commons.validator.RegexValidator;
import org.databene.commons.validator.StringLengthValidator;
import org.databene.commons.validator.SuffixValidator;
import org.junit.Test;

/**
 * Tests the {@link StringGenerator}.<br/><br/>
 * Created: 01.08.2011 20:15:04
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class StringGeneratorTest extends GeneratorTest {

	public static final int N = 500;
	
	@Test
	public void testDefault() {
		StringGenerator generator = initialize(new StringGenerator());
		expectGenerations(generator, N, 
				new StringLengthValidator(1, 8),
				new RegexValidator("\\w{1,8}"));
	}
	
	@Test
	public void testPattern() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[AB]");
		generator.setMinLength(2);
		generator.setMaxLength(3);
		initialize(generator);
		expectGeneratedSet(generator, N, 
				"AA", "AB", "BA", "BB", 
				"AAA", "AAB", "ABA", "ABB", "BAA", "BAB", "BBA", "BBB");
	}
	
	@Test
	public void testPrefix() {
		StringGenerator generator = new StringGenerator();
		generator.setPrefix("pp");
		generator.setMinLength(4);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(4, 8),
				new PrefixValidator("pp"));
	}
	
	@Test
	public void testSuffix() {
		StringGenerator generator = new StringGenerator();
		generator.setSuffix("ss");
		generator.setMinLength(4);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(4, 8),
				new SuffixValidator("ss"));
	}
	
	@Test
	public void testPrefixAndSuffix() {
		StringGenerator generator = new StringGenerator();
		generator.setPrefix("pp");
		generator.setSuffix("ss");
		generator.setMinLength(4);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(4, 8),
				new PrefixValidator("pp"),
				new SuffixValidator("ss"));
	}
	
	@Test
	public void testMinInitial() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[0-9]");
		generator.setMinInitial('9');
		generator.setMinLength(4);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(4, 8),
				new PrefixValidator("9"));
	}
	
	@Test
	public void testPrefixAndMinInitial() {
		StringGenerator generator = new StringGenerator();
		generator.setPrefix("pp");
		generator.setCharSet("[0-9]");
		generator.setMinInitial('9');
		generator.setMinLength(4);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(4, 8),
				new PrefixValidator("pp9"));
	}
	
	@Test
	public void testMinInitialAndSuffix() {
		StringGenerator generator = new StringGenerator();
		generator.setSuffix("ss");
		generator.setCharSet("[0-9]");
		generator.setMinInitial('9');
		generator.setMinLength(4);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(4, 8),
				new PrefixValidator("9"),
				new SuffixValidator("ss"));
	}
	
	@Test
	public void testPrefixMinInitialAndSuffix() {
		StringGenerator generator = new StringGenerator();
		generator.setPrefix("pp");
		generator.setSuffix("ss");
		generator.setCharSet("[0-9]");
		generator.setMinInitial('9');
		generator.setMinLength(5);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(5, 8),
				new PrefixValidator("pp9"),
				new SuffixValidator("ss"));
	}
	
	@Test
	public void testGermanLocale() {
		// TODO implement
	}
	
	@Test
	public void testLengthLimit() {
		// TODO implement
	}
	
	@Test
	public void testLengthGranularity() {
		// TODO implement
	}
	
	@Test
	public void testLengthDistribution() {
		// TODO implement
	}
	
	@Test
	public void testOrdered() {
		// TODO implement
	}
	
	@Test
	public void testUnordered() {
		// TODO implement
	}
	
	@Test
	public void testUniqueUnordered() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[AB]");
		generator.setMinLength(2);
		generator.setMaxLength(3);
		generator.setUnique(true);
		generator.setOrdered(false);
		initialize(generator);
		expectUniquelyGeneratedSet(generator, 
				"AA", "AB", "BA", "BB", 
				"AAA", "AAB", "ABA", "ABB", "BAA", "BAB", "BBA", "BBB");
	}
	
	@Test
	public void testUniqueOrdered() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[AB]");
		generator.setMinLength(2);
		generator.setMaxLength(3);
		generator.setUnique(true);
		generator.setOrdered(true);
		initialize(generator);
		expectGeneratedSequence(generator, 
				"AA", "AB", "BA", "BB", 
				"AAA", "AAB", "ABA", "ABB", "BAA", "BAB", "BBA", "BBB");
	}
	
}
