/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.address;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.csv.WeightedDatasetCSVGenerator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.Encodings;
import org.databene.dataset.DatasetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a street name for a region.<br/>
 * <br/>
 * Created: 12.06.2006 00:08:28
 * @since 0.1
 * @author Volker Bergmann
 */
public class StreetNameGenerator extends GeneratorProxy<String> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StreetNameGenerator.class);

    private static final String REGION = "org/databene/dataset/region";
    private static final String FILENAME_PATTERN = "org/databene/domain/address/street_{0}.csv";
    
    private String datasetName;

    public StreetNameGenerator() {
    	this(null);
    }

    public StreetNameGenerator(String datasetName) {
        super(null);
        this.datasetName = datasetName;
    }

    @Override
    public synchronized void init(GeneratorContext context) {
    	if (datasetName != null) {
    		source = createSource(datasetName);
    	} else {
    		// none was explicitly configured, try default
			String defaultRegionName = DatasetUtil.defaultRegionName();
	    	try {
				source = createSource(defaultRegionName);
	    	} catch (Exception e) {
	    		// if the default fails, try the fallback
	    		String fallbackRegionName = DatasetUtil.fallbackRegionName();
	    		LOGGER.error("Error creating " + getClass().getSimpleName() + " for dataset '" + defaultRegionName + "'." +
	    				" Falling back to '" + fallbackRegionName + "'");
				source = createSource(fallbackRegionName);
	    	}
    	}
        super.init(context);
    }
    
	private static WeightedDatasetCSVGenerator<String> createSource(String datasetName) {
	    return new WeightedDatasetCSVGenerator<String>(FILENAME_PATTERN, datasetName, REGION, Encodings.UTF_8);
    }

}
