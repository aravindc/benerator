/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.us;

import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.distribution.sequence.RandomIntegerGenerator;
import org.databene.benerator.wrapper.CompositeGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.StringUtil;

/**
 * Generates US Social Security Numbers.<br/>
 * <br/>
 * Created at 17.11.2008 06:54:42
 * @since 0.5.6
 * @author Volker Bergmann
 * @see "http://en.wikipedia.org/wiki/Social_security_number"
 * @see "http://www.socialsecurity.gov/history/ssn/geocard.html"
 * @see "http://www.socialsecurity.gov/employer/stateweb.htm"
 * @see "http://www.socialsecurity.gov/employer/ssnvhighgroup.htm"
 */

public class SSNGenerator extends CompositeGenerator<String> implements NonNullGenerator<String> {

	private RandomIntegerGenerator areaNumberGenerator;
	private RandomIntegerGenerator groupNumberGenerator;
	private RandomIntegerGenerator serialNumberGenerator;

	public SSNGenerator() {
		this(772);
	}

	public SSNGenerator(int maxAreaCode) {
		super(String.class);
		areaNumberGenerator = registerComponent(new RandomIntegerGenerator(1, maxAreaCode));
		groupNumberGenerator = registerComponent(new RandomIntegerGenerator(1, 99));
		serialNumberGenerator = registerComponent(new RandomIntegerGenerator(1, 9999));
	}

	public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
		return wrapper.wrap(generate());
	}

	public String generate() {
		Integer area;
		do {
			area = areaNumberGenerator.generate();
		} while (area == 666 || (area >= 734 && area <= 749));
		String ssn = StringUtil.padLeft(String.valueOf(area), 3, '0') + '-' + 
			StringUtil.padLeft(String.valueOf(groupNumberGenerator.generate()), 2, '0') + '-' +
			StringUtil.padLeft(String.valueOf(serialNumberGenerator.generate()), 4, '0');
		return ssn;
	}

	public void setMaxAreaCode(int maxAreaCode) {
		areaNumberGenerator.setMax(maxAreaCode);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
