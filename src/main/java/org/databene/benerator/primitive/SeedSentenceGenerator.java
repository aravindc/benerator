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

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.sample.SeedGenerator;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.commons.IOUtil;
import org.databene.commons.ReaderLineIterator;
import org.databene.commons.StringUtil;

/**
 * Generates sentences based on a seed sentence set.<br/>
 * <br/>
 * Created at 16.07.2009 20:02:32
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class SeedSentenceGenerator extends LightweightGenerator<String> {

    private static final int DEFAULT_DEPTH = 4;
	private SeedGenerator<String> source;

	public SeedSentenceGenerator(String seedUri) throws IOException {
		this(seedUri, DEFAULT_DEPTH);
	}

	public SeedSentenceGenerator(String seedUri, int depth) throws IOException {
		source = new SeedGenerator<String>(String.class, depth);
		ReaderLineIterator iterator = new ReaderLineIterator(IOUtil.getReaderForURI(seedUri));
		while (iterator.hasNext()) {
			String line = iterator.next();
			if (StringUtil.isEmpty(line))
				continue;
	    	source.addSample(line.split("\\s"));
		}
    }

    public Class<String> getGeneratedType() {
	    return String.class;
    }

	public String generate() throws IllegalGeneratorStateException {
	    return toString(source.generate());
    }
	
    private static String toString(String[] tokens) {
	    StringBuilder builder = new StringBuilder();
	    for (String token : tokens)
	    	builder.append(token).append(' ');
	    return builder.toString();
    }

	// helpers ---------------------------------------------------------------------------------------------------------

    public void printState() {
	    System.out.println(getClass().getSimpleName());
	    source.printState("  ");
    }

}
