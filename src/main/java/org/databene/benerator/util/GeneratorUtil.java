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

package org.databene.benerator.util;

import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.commons.ConfigurationError;
import org.databene.model.data.Entity;

/**
 * Provides general utility methods for generation.<br/>
 * <br/>
 * Created: 19.11.2007 15:27:50
 */
public class GeneratorUtil {
	
	private static final int MAX_SIZE = 100000;

    public static <T> IllegalGeneratorStateException stateException(Generator<T> generator) {
        return new IllegalGeneratorStateException("Generator is not available: " + generator);        
    }

	public static List<Entity> allProducts(Generator<Entity> generator) {
		List<Entity> list = new ArrayList<Entity>();
		int count = 0;
		while (generator.available()) {
			list.add(generator.generate());
			if (count++ > MAX_SIZE)
				throw new ConfigurationError("Dataset is to large");
		}
		return list;
	}
	
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

}
