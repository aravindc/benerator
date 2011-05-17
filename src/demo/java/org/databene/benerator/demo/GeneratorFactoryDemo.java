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

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.GeneratorFactory;

/**
 * Demonstrates usages of the {@link GeneratorFactory}.<br/><br/>
 * Created: 08.03.2011 17:39:20
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class GeneratorFactoryDemo {

	public static void main(String[] args) {
		generateByRegex();
		generateByWeightedLiteralList();
		iterateCsv();
	}

	private static void iterateCsv() {
		Generator<String[]> generator = GeneratorFactory.getCSVLineGenerator("org/databene/benerator/products.csv", ';', true, false);
		generator.init(new BeneratorContext());
		String[] row;
		while ((row = generator.generate()) != null) // null signals that the generator is used up
			System.out.println(Arrays.toString(row));
		generator.close();
	}

	private static void generateByWeightedLiteralList() {
		Generator<String> generator = GeneratorFactory.createFromWeightedLiteralList("'Alpha'^4,'Bravo'^1", String.class, null, false);
		generator.init(new BeneratorContext());
		for (int i = 0; i < 10; i++)
			System.out.println(generator.generate());
		generator.close();
	}

	private static void generateByRegex() {
		// generating german phone numbers
		Generator<String> generator = GeneratorFactory.getRegexStringGenerator("\\+49\\-[1-9]{2,5}\\-[1-9][0-9]{3,9}", 8, 20, false);
		generator.init(new BeneratorContext());
		for (int i = 0; i < 10; i++)
			System.out.println(generator.generate());
		generator.close();
	}
	
}
