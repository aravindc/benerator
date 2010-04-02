/*
 * (c) Copyright 2006-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator;


/**
 * Helper class for testing.<br/><br/>
 * Created: 16.12.2006 07:51:30
 * @since 0.1
 * @author Volker Bergmann
 */
public class ConstantTestGenerator<E> implements Generator<E> {

    private final E value;
    private String lastMethodCall;
    private boolean initialized;

    public ConstantTestGenerator(E value) {
        this.value = value;
        this.lastMethodCall = "constructor";
        this.initialized = false;
    }

    public void init(GeneratorContext context) {
    	this.initialized = true;
    }

    @SuppressWarnings("unchecked")
    public Class<E> getGeneratedType() {
        return (value != null ? (Class<E>) value.getClass() : null);
    }

    public E generate() {
        this.lastMethodCall = "generate";
        return value;
    }

    public boolean wasInitialized() {
        return initialized;
    }

    public void reset() {
        this.lastMethodCall = "reset";
    }

    public void close() {
        this.lastMethodCall = "close";
    }

    public String getLastMethodCall() {
        return lastMethodCall;
    }

	public boolean isParallelizable() {
	    return true;
    }

	public boolean isThreadSafe() {
	    return true;
    }
    
}
