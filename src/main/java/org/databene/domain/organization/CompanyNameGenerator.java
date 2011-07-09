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

package org.databene.domain.organization;

import java.util.HashMap;
import java.util.Map;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.csv.WeightedDatasetCSVGenerator;
import org.databene.benerator.dataset.AbstractDatasetGenerator;
import org.databene.benerator.dataset.Dataset;
import org.databene.benerator.dataset.DatasetUtil;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.nullable.NullableGeneratorFactory;
import org.databene.benerator.primitive.RegexStringGenerator;
import org.databene.benerator.primitive.TokenCombiner;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.sample.SequencedCSVSampleGenerator;
import org.databene.benerator.util.ThreadSafeGenerator;
import org.databene.benerator.wrapper.AlternativeGenerator;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.benerator.wrapper.MessageGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.benerator.wrapper.ThreadLocalProductWrapper;
import org.databene.commons.Encodings;
import org.databene.commons.bean.PropertyAccessConverter;
import org.databene.domain.address.City;
import org.databene.domain.address.CityGenerator;
import org.databene.domain.address.Country;
import org.databene.text.NameNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates company names.<br/><br/>
 * Created: 14.03.2008 08:26:44
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class CompanyNameGenerator extends AbstractDatasetGenerator<CompanyName> {
	
	protected static final Logger logger = LoggerFactory.getLogger(CompanyNameGenerator.class);

    private static final String ORG = "/org/databene/domain/organization/";
    private static final String PERS = "/org/databene/domain/person/";

    protected static Map<String, NullableGenerator<String>> locationGenerators = new HashMap<String, NullableGenerator<String>>();
    
    protected String datasetName;
    protected boolean sector;
    protected boolean location;
    protected boolean legalForm;
    
    public CompanyNameGenerator() {
    	this(true, true, true);
    }
    
    public CompanyNameGenerator(boolean sector, boolean location, boolean legalForm) {
    	this(sector, location, legalForm, Country.getDefault().getIsoCode());
    }

    public CompanyNameGenerator(String dataset) {
    	this(true, true, true, dataset);
    }

    public CompanyNameGenerator(boolean sector, boolean location, boolean legalForm, String datasetName) {
    	super(DatasetUtil.REGION_NESTING, datasetName);
    	this.sector = sector;
    	this.location = location;
    	this.legalForm = legalForm;
        this.datasetName = datasetName;
        setDataset(datasetName);
    }
    
	@Override
	protected Generator<CompanyName> createGeneratorForAtomicDataset(Dataset dataset) {
		return new AtomicCompanyNameGenerator();
	}

	class AtomicCompanyNameGenerator extends ThreadSafeGenerator<CompanyName> {
		
	    private AlternativeGenerator<String> shortNameGenerator;
	    private NullableGenerator<String> sectorGenerator;
	    private Generator<String> legalFormGenerator;
	    private NullableGenerator<String> locationGenerator;

	    private transient ThreadLocalProductWrapper<String> productWrapper;
	    
	    public AtomicCompanyNameGenerator() {
	        this.productWrapper = new ThreadLocalProductWrapper<String>();
	    }
	    
		public Class<CompanyName> getGeneratedType() {
		    return CompanyName.class;
	    }

	    @Override
	    public synchronized void init(GeneratorContext context) {
	        try {
				initWithDataset(datasetName, context);
			} catch (Exception e) {
				String fallbackDataset = DatasetUtil.fallbackRegionName();
				logger.warn("Error initializing location generator for dataset " + datasetName + ", falling back to " + fallbackDataset);
				initWithDataset(fallbackDataset, context);
			}
	    }

		public void initWithDataset(String datasetToUse, GeneratorContext context) {
			createAndInitLocationGenerator(datasetToUse);
			initLegalFormGenerator(datasetToUse);
			initSectorGenerator(datasetToUse);
			shortNameGenerator = new AlternativeGenerator<String>(String.class);
			createInitialsNameGenerator();
			createPersonNameGenerator(datasetToUse);
			createArtificialNameGenerator();
			createTechNameGenerator();
			shortNameGenerator.init(context);
			super.init(context);
		}

		public CompanyName generate() {
			CompanyName name = new CompanyName();
	        name.setShortName(shortNameGenerator.generate());
	        ProductWrapper<String> wrapper = productWrapper.get();
	        if (sectorGenerator != null) {
				String sector = sectorGenerator.generate(wrapper).product;
		        if (sector != null)
		            name.setSector(sector);
	        }
	        if (locationGenerator != null) {
	        	wrapper = locationGenerator.generate(wrapper);
	        	if (wrapper != null)
		            name.setLocation(wrapper.product);
	        }
	        if (legalFormGenerator != null)
	        	name.setLegalForm(legalFormGenerator.generate());
	        name.setDatasetName(datasetName);
	        return name;
	    }

	    @Override
	    public String toString() {
	        return getClass().getSimpleName() + '[' + datasetName + ']';
	    }

	    // private helpers -------------------------------------------------------------------------------------------------
	    
		private void createInitialsNameGenerator() {
			shortNameGenerator.addSource(new RegexStringGenerator("[A-Z]{3}"));
		}

		private void createTechNameGenerator() {
		    try {
	            Generator<String> tech = new MessageGenerator("{0}{1}", 
	                    new SequencedCSVSampleGenerator<String>(ORG + "tech1.csv"),
	                    new SequencedCSVSampleGenerator<String>(ORG + "tech2.csv")
	                );
		        shortNameGenerator.addSource(tech);
	        } catch (Exception e) {
	        	logger.info("Cannot create technical company name generator: " + e.getMessage());
	        }
	    }

		private void createArtificialNameGenerator() {
		    try {
		    	TokenCombiner artificial = new TokenCombiner(ORG + "artificialName.csv", false, '-', Encodings.UTF_8, false);
		    	shortNameGenerator.addSource(artificial);
	        } catch (Exception e) {
	        	logger.info("Cannot create artificial company name generator: " + e.getMessage());
	        }
	    }

		private void createPersonNameGenerator(String datasetToUse) {
		    try {
		        Generator<String> person = new MessageGenerator("{0} {1}", 
		                new WeightedDatasetCSVGenerator<String>(PERS + "givenName_male_{0}.csv", datasetToUse, DatasetUtil.REGION_NESTING),
		                new WeightedDatasetCSVGenerator<String>(PERS + "familyName_{0}.csv", datasetToUse, DatasetUtil.REGION_NESTING)
		            );
		        shortNameGenerator.addSource(person);
	        } catch (Exception e) {
	        	logger.info("Cannot create person-based company name generator: " + e.getMessage());
	        }
	    }

		private void initSectorGenerator(String datasetName) {
		    if (sector) {
	        	try {
	        		WeightedDatasetCSVGenerator<String> source = new WeightedDatasetCSVGenerator<String>(
	        				ORG + "sector_{0}.csv", datasetName, DatasetUtil.REGION_NESTING, Encodings.UTF_8);
					sectorGenerator = NullableGeneratorFactory.injectNulls(source, 0.7);
	        		sectorGenerator.init(context);
	        	} catch (Exception e) {
	        		logger.info("Cannot create sector generator: " + e.getMessage() + ". Falling back to US");
	        		initSectorGenerator("US");
	        	}
	        }
	    }

		private void initLegalFormGenerator(String datasetName) {
		    if (legalForm) {
		    	
	        	try {
	        		legalFormGenerator = new WeightedDatasetCSVGenerator<String>(ORG + "legalForm_{0}.csv", 
	        				datasetName, DatasetUtil.REGION_NESTING, Encodings.UTF_8);
	        		legalFormGenerator.init(context);
	        	} catch (Exception e) {
	        		logger.error("Cannot create legal form generator: " + e.getMessage() + ". Falling back to US. ");
	        		initLegalFormGenerator("US");
	        	}
	        }
	    }

		@SuppressWarnings("unchecked")
	    private void createAndInitLocationGenerator(String datasetName) {
			locationGenerator = locationGenerators.get(datasetName);
			if (locationGenerator == null) {
				double nullQuota = 0.8;
			    Country country = Country.getInstance(datasetName);
			    Generator<String> locationBaseGen;
		        if (location && country != null) {
		        	try {
			            Generator<String> city = new ConvertingGenerator<City, String>(
			            		new CityGenerator(country.getIsoCode()), new PropertyAccessConverter("name"), new NameNormalizer());
			            locationBaseGen = new AlternativeGenerator<String>(String.class, 
			                    			new ConstantGenerator<String>(country.getLocalName()), 
			                    			city);
		        	} catch (Exception e) {
		        		logger.info("Cannot create location generator: " + e.getMessage());
		                locationBaseGen = new ConstantGenerator<String>(null);
		        	}
		        } else
		        	locationBaseGen = new ConstantGenerator<String>(null);
		        locationGenerator = NullableGeneratorFactory.injectNulls(locationBaseGen, nullQuota);
		        locationGenerator.init(context);
		        locationGenerators.put(datasetName, locationGenerator);
			}
	    }
	}

}
