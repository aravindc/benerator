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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.IllegalGeneratorStateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Proxy that forwards a limited number of products of another Generator.<br/>
 * <br/>
 * Created: 01.09.2007 10:54:01
 */
public class NShotGeneratorProxy<E> extends GeneratorProxy<E> {

    private static final Log logger = LogFactory.getLog(NShotGeneratorProxy.class);

    private long shots;

    private long remainingShots;

    public NShotGeneratorProxy(Generator<E> source, long shots) {
        super(source);
        this.shots = shots;
        this.remainingShots = shots;
    }

    public boolean available() {
        if (remainingShots <= 0) {
            logger.debug("requested count reached for " + source);
            return false;
        }
        return super.available();
    }

    public E generate() {
        if (remainingShots <= 0)
            throw new IllegalGeneratorStateException("Generator not available.");
        this.remainingShots--;
        return super.generate();
    }

    public void reset() {
        super.reset();
        remainingShots = shots;
    }

    public void close() {
        super.close();
        remainingShots = 0;
    }
}
