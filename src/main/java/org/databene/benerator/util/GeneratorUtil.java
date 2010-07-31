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

package org.databene.benerator.util;

import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.engine.BeneratorOpts;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.wrapper.GeneratorWrapper;
import org.databene.commons.Resettable;
import org.databene.commons.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides utility methods for data generation.<br/><br/>
 * Created: 19.11.2007 15:27:50
 * @author Volker Bergmann
 */
public class GeneratorUtil {
	
	private static Logger LOGGER = LoggerFactory.getLogger(GeneratorUtil.class);
	
	public static boolean isBeneratorFile(String localFilename) {
		if (StringUtil.isEmpty(localFilename))
			return false;
		String lcFilename = localFilename.toLowerCase();
		return "benerator.xml".equals(lcFilename) || lcFilename.endsWith(".ben.xml");
	}

	public static <T> List<T> allProducts(Generator<T> generator) {
		List<T> list = new ArrayList<T>();
		int count = 0;
		T product;
		int cacheSize = BeneratorOpts.getCacheSize();
		while ((product = generator.generate()) != null) {
			count++;
			if (count > cacheSize) {
				LOGGER.error("Data set of generator has reached the cache limit and will be reduced to its size " +
						"of " + cacheSize + " elements). " +
						"If that is not acceptable then choose a distribution that does not cache data sets " +
						"or increase the cache size.");
				break;
			}
			list.add(product);
		}
		return list;
	}
	
    @SuppressWarnings("unchecked")
    public static <T> Class<T> commonTargetTypeOf(Generator<T>... sources) {
    	if (sources.length == 0)
    		return (Class<T>) Object.class;
    	Class<T> type = sources[0].getGeneratedType();
    	for (int i = 1; i < sources.length; i++) {
    		Class<T> tmp = sources[i].getGeneratedType();
    		if (tmp.isAssignableFrom(type))
    			type = tmp;
    	}
		return type;
	}

	public static void initAll(Generator<?>[] generators, GeneratorContext context) {
	    for (Generator<?> generator : generators)
	    	generator.init(context);
    }

	public static void initAll(NullableGenerator<?>[] generators, GeneratorContext context) {
	    for (NullableGenerator<?> generator : generators)
	    	generator.init(context);
    }

	public static void resetAll(Resettable[] resettables) {
	    for (Resettable resettable : resettables)
	    	resettable.reset();
    }

	@SuppressWarnings("unchecked")
    public static Generator<?> unwrap(Generator<?> generator) {
		Generator<?> result = generator;
		while (result instanceof GeneratorWrapper)
			result = ((GeneratorWrapper) result).getSource();
		return result;
	}
	
}
