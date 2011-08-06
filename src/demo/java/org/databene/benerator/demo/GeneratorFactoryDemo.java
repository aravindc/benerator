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

package org.databene.benerator.demo;

import java.util.Arrays;

import static org.databene.benerator.util.GeneratorUtil.*;

import org.databene.benerator.Generator;
import org.databene.benerator.factory.EquivalenceGeneratorFactory;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.factory.SourceFactory;
import org.databene.commons.Encodings;
import org.databene.model.data.Uniqueness;

/**
 * Demonstrates usages of the {@link GeneratorFactory}.<br/><br/>
 * Created: 08.03.2011 17:39:20
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class GeneratorFactoryDemo {

	public static void main(String[] args) {
		GeneratorFactory factory = new EquivalenceGeneratorFactory();
		generateByRegex(factory);
		generateByWeightedLiteralList(factory);
		iterateCsv(factory);
	}

	private static void iterateCsv(GeneratorFactory generatorFactory) {
		Generator<String[]> generator = SourceFactory.createCSVLineGenerator(
				"org/databene/benerator/products.csv", ';', Encodings.UTF_8, true);
		init(generator);
		String[] row;
		while ((row = generateNonNull(generator)) != null) // null signals that the generator is used up
			System.out.println(Arrays.toString(row));
		close(generator);
	}

	private static void generateByWeightedLiteralList(GeneratorFactory generatorFactory) {
		String valueSpec = "'Alpha'^4,'Bravo'^1";
		Generator<String> generator = generatorFactory.createFromWeightedLiteralList(
				valueSpec, String.class, null, false);
		init(generator);
		for (int i = 0; i < 10; i++)
			System.out.println(generateNonNull(generator));
		close(generator);
	}

	private static void generateByRegex(GeneratorFactory generatorFactory) {
		// generating German phone numbers
		String pattern = "\\+49\\-[1-9]{2,5}\\-[1-9][0-9]{3,9}";
		Generator<String> generator = generatorFactory.createRegexStringGenerator(pattern, 8, 20, Uniqueness.NONE);
		init(generator);
		for (int i = 0; i < 10; i++)
			System.out.println(generateNonNull(generator));
		close(generator);
	}
	
}
