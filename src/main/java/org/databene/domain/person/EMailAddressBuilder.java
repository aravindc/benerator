/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.person;

import java.io.IOException;
import java.util.Locale;

import org.databene.benerator.Generator;
import org.databene.benerator.sample.SequencedSampleGenerator;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.converter.CaseConverter;
import org.databene.commons.converter.ConverterChain;
import org.databene.domain.net.DomainGenerator;
import org.databene.text.DelocalizingConverter;

/**
 * Generates email addresses of random domain for a given person name.<br/><br/>
 * Created: 22.02.2010 12:16:11
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class EMailAddressBuilder {

	private DomainGenerator domainGenerator;
	private CaseConverter caseConverter;  
	private Converter<String, String> nameConverter;
	private Generator<Character> joinGenerator;

	public EMailAddressBuilder(String dataset) {
		this.domainGenerator = new DomainGenerator(dataset);
		this.caseConverter = new CaseConverter(false);
		try {
			this.nameConverter = new ConverterChain<String, String>(
					new DelocalizingConverter(),
					caseConverter);
		} catch (IOException e) {
			throw new ConfigurationError("Error in Converter setup", e);
		}
		this.joinGenerator = new SequencedSampleGenerator<Character>(Character.class, '_', '.', '0', '1');
    }

	public String generate(String givenName, String familyName) {
		String given = nameConverter.convert(givenName);
		String family = nameConverter.convert(familyName);
		String domain = domainGenerator.generate();
		Character join = joinGenerator.generate();
		switch (join) {
			case '.' : return given + '.' + family + '@' + domain;
			case '_' : return given + '_' + family + '@' + domain;
			case '0' : return given + family + '@' + domain;
			case '1' : return given.charAt(0) + family + '@' + domain;
			default  : throw new ConfigurationError("Invalid join strategy: " + join);
		}
    } 
	
	public void setDataset(String datasetName) {
		domainGenerator.setDataset(datasetName);
	}
	
	public void setLocale(Locale locale) {
		caseConverter.setLocale(locale);
	}
	
	@Override
	public String toString() {
	    return BeanUtil.toString(this);
	}
	
}
