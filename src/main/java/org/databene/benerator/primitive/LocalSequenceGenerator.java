/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.databene.benerator.Generator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.ConfigurationError;
import org.databene.commons.IOUtil;

/**
 * Local implementation of an increment {@link Generator} that behaves like a database sequence.<br/>
 * <br/>
 * Created at 29.05.2009 19:35:27
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class LocalSequenceGenerator extends GeneratorProxy<Long> {
	
    static final String FILENAME = LocalSequenceGenerator.class.getSimpleName() + ".properties";
    
	private static final Map<String, IncrementalIdGenerator> MAP 
		= new HashMap<String, IncrementalIdGenerator>();
	
	// Initialization --------------------------------------------------------------------------------------------------

	static {
		init();
	}

	public LocalSequenceGenerator() {
		this("default");
	}
	
	public LocalSequenceGenerator(String name) {
		super(getOrCreateSource(name));
	}
	
	// Generator interface ---------------------------------------------------------------------------------------------

	@Override
	public void reset() {
		// ignore reset - we need to generate unique values!
	}
	
	@Override
	public void close() {
		persist();
		super.close();
	}
	
	// private helpers -------------------------------------------------------------------------------------------------

	private static void init() {
		if (IOUtil.isURIAvailable(FILENAME)) {
	        try {
	    	    Map<String, String> values = IOUtil.readProperties(FILENAME);
	            for (Map.Entry<String, String> entry : values.entrySet())
	            	MAP.put(entry.getKey(), new IncrementalIdGenerator(Long.parseLong(entry.getValue())));
            } catch (Exception e) {
            	throw new ConfigurationError(e);
            }
		}
    }
	
    private static void persist() {
    	Map<String, String> values = new HashMap<String, String>();
    	for (Map.Entry<String, IncrementalIdGenerator> entry : MAP.entrySet())
    		values.put(entry.getKey(), String.valueOf(entry.getValue().getCursor()));
		try {
	        IOUtil.writeProperties(values, FILENAME);
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
    }

	private static Generator<Long> getOrCreateSource(String name) {
		IncrementalIdGenerator generator = MAP.get(name);
		if (generator == null) {
			generator = new IncrementalIdGenerator();
			MAP.put(name, generator);
		}
		return generator;
    }
    
}
