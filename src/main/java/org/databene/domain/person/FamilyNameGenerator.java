/*
 * (c) Copyright 2006-2013 by Volker Bergmann. All rights reserved.
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.csv.WeightedDatasetCSVGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.benerator.util.SharedGenerator;
import org.databene.commons.Encodings;
import org.databene.domain.address.Country;

/**
 * Generates family names.<br/>
 * <br/>
 * Created: 09.06.2006 22:03:56
 * @since 0.1
 * @author Volker Bergmann
 */
public class FamilyNameGenerator extends WeightedDatasetCSVGenerator<String> implements NonNullGenerator<String> {
	
	// default instance management -------------------------------------------------------------------------------------
	
	private static Map<String, Generator<String>> defaultInstances = new HashMap<String, Generator<String>>();
	
	public static Generator<String> sharedInstance(String datasetName) {
		String key = datasetName;
		Generator<String> instance = defaultInstances.get(key);
		if (instance == null) {
			instance = new SharedGenerator<String>(new FamilyNameGenerator(datasetName));
			defaultInstances.put(key, instance);
		}
		return instance;
	}
	
	// Constructors ----------------------------------------------------------------------------------------------------
	
	public FamilyNameGenerator() {
        this(Locale.getDefault().getCountry());
    }

    public FamilyNameGenerator(String datasetName) {
        this(datasetName, 
                "/org/databene/dataset/region", 
                "/org/databene/domain/person/familyName_{0}.csv");
    }

    public FamilyNameGenerator(String datasetName, String nesting, String fileNamePattern) {
        super(String.class, fileNamePattern, datasetName, nesting, true, Encodings.UTF_8);
        logger.debug("Instantiated FamilyNameGenerator for dataset '{}'", datasetName);
    }
    
    @Override
    public double getWeight() {
    	Country country = Country.getInstance(datasetName);
    	return (country != null ? country.getPopulation() : super.getWeight());
    }
    
    // NonNullGenerator interface implementation -----------------------------------------------------------------------

	@Override
	public String generate() {
		return GeneratorUtil.generateNonNull(this);
	}
    
}
