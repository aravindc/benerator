/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

package org.databene.benerator.engine.statement;

import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;

/**
 * Causes the thread to sleep for a certain number of milliseconds.<br/><br/>
 * Created: 21.02.2010 07:46:50
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class WaitStatement implements Statement {
	
	private NonNullGenerator<Long> durationGenerator;
	private boolean generatorInitialized = false;

	public WaitStatement(NonNullGenerator<Long> durationGenerator) {
	    this.durationGenerator = durationGenerator;
    }
	
	public boolean execute(BeneratorContext context) {
		try {
	        Thread.sleep(generateDuration(context));
	    	return true;
        } catch (InterruptedException e) {
	        throw new RuntimeException(e);
        }
	}

	public int generateDuration(BeneratorContext context) {
		if (!generatorInitialized) {
			durationGenerator.init(context);
			generatorInitialized = true;
		}
		return durationGenerator.generate().intValue();
	}
	
}
