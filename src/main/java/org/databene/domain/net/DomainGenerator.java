/*
 * (c) Copyright 2008-2013 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.benerator.wrapper.AlternativeGenerator;
import org.databene.domain.address.Country;

/**
 * Creates Internet domains of companies, web mailers or random characters.<br/><br/>
 * Created at 20.04.2008 08:14:35
 * @since 0.5.2
 * @author Volker Bergmann
 *
 */
public class DomainGenerator extends AlternativeGenerator<String> implements NonNullGenerator<String> {
	
	public DomainGenerator() {
		this(Country.getDefault().getIsoCode());
	}

	@SuppressWarnings("unchecked")
    public DomainGenerator(String datasetName) {
		super(String.class, 
				new RandomDomainGenerator(), 
				new WebmailDomainGenerator(),
				new CompanyDomainGenerator(datasetName));
	}

	public void setDataset(String datasetName) {
		((CompanyDomainGenerator) sources.get(2)).setDataset(datasetName);
	}

	@Override
	public String generate() {
		return GeneratorUtil.generateNonNull(this);
	}
	
}
