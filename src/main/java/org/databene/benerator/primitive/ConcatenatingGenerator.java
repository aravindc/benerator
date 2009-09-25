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

import org.databene.benerator.Generator;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.wrapper.GeneratorWrapper;

/**
 * {@link Generator} implementation that wraps several String generators 
 * and concatenates their results to a composite {@link String}.<br/>
 * <br/>
 * Created at 21.09.2009 18:54:32
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class ConcatenatingGenerator extends GeneratorWrapper<String[], String> { // TODO test

    public ConcatenatingGenerator(Generator<String[]> source) {
	    super(source);
    }
    
	public Class<String> getGeneratedType() {
	    return String.class;
    }

    public String generate() throws IllegalGeneratorStateException {
        String[] parts = source.generate();
        StringBuilder builder = new StringBuilder();
        for (String part : parts)
            builder.append(part);
        return builder.toString();
    }

}
