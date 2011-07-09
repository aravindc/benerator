/*
 * (c) Copyright 2008-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.primitive.RegexStringGenerator;
import org.databene.benerator.util.AbstractGenerator;

/**
 * Creates an internet domain name from random characters.<br/><br/>
 * Created at 23.04.2008 22:44:29
 * @since 0.5.2
 * @author Volker Bergmann
 */
public class RandomDomainGenerator extends AbstractGenerator<String> {

	private Generator<String> nameGenerator = new RegexStringGenerator("[a-z]{4,12}");
	private Generator<String> tldGenerator = new TopLevelDomainGenerator();

	@Override
	public synchronized void init(GeneratorContext context) {
	    nameGenerator.init(context);
	    tldGenerator.init(context);
	    super.init(context);
	}
	
	public Class<String> getGeneratedType() {
	    return String.class;
	}
	
	public String generate() {
		return nameGenerator.generate() + '.' + tldGenerator.generate();
	}

	public boolean isThreadSafe() {
	    return nameGenerator.isThreadSafe() && tldGenerator.isThreadSafe();
	}

	public boolean isParallelizable() {
	    return nameGenerator.isParallelizable() && tldGenerator.isParallelizable();
	}

}
