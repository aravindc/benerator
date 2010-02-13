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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.primitive.number.AbstractNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Long Generator that implements a 'wedge' Long Sequence.<br/>
 * <br/>
 * Created: 13.11.2007 12:54:29
 * @author Volker Bergmann
 */
public class WedgeLongGenerator extends AbstractNumberGenerator<Long> {

    private static final Logger logger = LoggerFactory.getLogger(WedgeLongGenerator.class);

    private Long cursor;
    private long end;

    public WedgeLongGenerator() {
        this(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public WedgeLongGenerator(long min, long max) {
        this(min, max, 1);
    }

    public WedgeLongGenerator(long min, long max, long precision) {
        super(Long.class, min, max, precision);
        this.cursor = min;
    }

    // generator interface ---------------------------------------------------------------------------------------------

    @Override
	public void validate() {
        if (dirty) {
            cursor = min;
            max = min + (max - min) / precision * precision;
            super.validate();
            long steps = (max - min) / precision + 1;
            end = min + steps / 2 * precision;
            this.dirty = false;
            if (logger.isDebugEnabled())
                logger.debug("validated state: " + this);
        }
    }

    public Long generate() throws IllegalGeneratorStateException {
        if (dirty)
            validate();
        if (cursor == null)
            return null;
        long result = cursor;
        if (cursor == end)
            cursor = null;
        else {
            cursor = max - cursor + min;
            if (cursor < end)
                cursor += precision;
        }
        return result;
    }

    @Override
	public void reset() {
        super.reset();
        this.cursor = min;
    }

    @Override
	public void close() {
        super.close();
        this.cursor = null;
    }
    
}
