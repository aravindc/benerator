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

package org.databene.domain.organization;

import org.databene.benerator.Generator;
import org.databene.benerator.csv.DatasetCSVGenerator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.sample.SequencedCSVSampleGenerator;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.benerator.wrapper.AlternativeGenerator;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.benerator.wrapper.MessageGenerator;
import org.databene.benerator.wrapper.NullableGenerator;
import org.databene.commons.bean.PropertyAccessConverter;
import org.databene.domain.address.City;
import org.databene.domain.address.CityGenerator;
import org.databene.domain.address.Country;

/**
 * Generates company names.<br/><br/>
 * Created: 14.03.2008 08:26:44
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class CompanyNameGenerator extends LightweightGenerator<String> {

    private static final String ORG = "org/databene/domain/organization/";
    private static final String PERS = "org/databene/domain/person/";
    private static final String REGION  = "org/databene/dataset/region";
    
    private Generator<String> core;
    private Generator<String> sector;
    private Generator<String> legalForm;
    private String dataset;
    Generator<String> location;
    
    // TODO v0.5.2 french & italian company names
    public CompanyNameGenerator() {
    }
    
    public CompanyNameGenerator(String dataset) {
        this.dataset = dataset;
        Generator<String> person = new MessageGenerator("{0} {1}", 
                new DatasetCSVGenerator<String>(PERS + "givenName_male_{0}.csv", dataset, REGION),
                new DatasetCSVGenerator<String>(PERS + "familyName_{0}.csv", dataset, REGION)
            );
        Generator<String> artificial = new MessageGenerator("{0}{1}", 
                new SequencedCSVSampleGenerator<String>(ORG + "artificial1.csv"),
                new SequencedCSVSampleGenerator<String>(ORG + "artificial2.csv")
            );
        Generator<String> tech = new MessageGenerator("{0}{1}", 
                new SequencedCSVSampleGenerator<String>(ORG + "tech1.csv"),
                new SequencedCSVSampleGenerator<String>(ORG + "tech2.csv")
            );
        Country country = Country.getInstance(dataset);
        if (country != null) {
            Generator<String> city = new ConvertingGenerator<City, String>(new CityGenerator(country), new PropertyAccessConverter("name"));
            location = new NullableGenerator<String>(
                    	new AlternativeGenerator<String>(String.class, 
                    			new ConstantGenerator<String>(country.getLocalName()), 
                    			city), 
                    	0.8
                );
            
        } else
            location = new ConstantGenerator<String>(null);
        core = new AlternativeGenerator<String>(String.class, artificial, tech, person);
        legalForm = new DatasetCSVGenerator<String>(ORG + "legalForm_{0}.csv", dataset, REGION, "UTF-8");
        sector = new NullableGenerator<String>(new DatasetCSVGenerator<String>(ORG + "sector_{0}.csv", dataset, REGION, "UTF-8"), 0.7);
    }
    
    public String generate() {
        StringBuilder builder = new StringBuilder(core.generate());
        String sec = sector.generate();
        if (sec != null)
            builder.append(' ').append(sec);
        String loc = location.generate();
        if (loc != null)
            builder.append(' ').append(loc);
        builder.append(' ').append(legalForm.generate());
        return builder.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + dataset + ']';
    }
}
