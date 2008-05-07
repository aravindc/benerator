/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.net;

import java.io.IOException;
import java.util.Locale;

import org.databene.benerator.Generator;
import org.databene.benerator.sample.SequencedSampleGenerator;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.LocaleUtil;
import org.databene.commons.converter.CaseConverter;
import org.databene.commons.converter.ConverterChain;
import org.databene.domain.address.Country;
import org.databene.domain.person.Person;
import org.databene.domain.person.PersonGenerator;
import org.databene.text.DelocalizingConverter;

/**
 * Generates EMail Addresses.<br/><br/>
 * Created at 09.04.2008 01:34:17
 * @since 0.5.1
 * @author Volker Bergmann
 */
public class EMailAddressGenerator extends LightweightGenerator<String> { 

	// TODO v0.5.4 improve email generation algorithm
	private PersonGenerator personGenerator;
	private DomainGenerator domainGenerator;
	private CaseConverter caseConverter;  
	private Converter<String, String> nameConverter;
	private Generator<Character> joinGenerator;
	
	public EMailAddressGenerator() {
		this(Country.getDefault().getIsoCode());
	}
	
	public EMailAddressGenerator(String dataset) {
		super(String.class);
		this.personGenerator = new PersonGenerator(dataset, LocaleUtil.getFallbackLocale());
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
	
	// properties ------------------------------------------------------------------------------------------------------
	
	public String getDataset() {
		return personGenerator.getDataset();
	}
	
	public void setDataset(String datasetName) {
		personGenerator.setDataset(datasetName);
		domainGenerator.setDataset(datasetName);
	}
	
	public void setLocale(Locale locale) {
		personGenerator.setLocale(locale);
		caseConverter.setLocale(locale);
	}
	
	public String generate() {
		Person person = personGenerator.generate();
		String given = nameConverter.convert(person.getGivenName());
		String family = nameConverter.convert(person.getFamilyName());
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
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
