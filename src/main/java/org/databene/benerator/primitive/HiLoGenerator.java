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

package org.databene.benerator.primitive;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.InvalidGeneratorSetupException;

/**
 * Combines the output of a 'slow' generator (e.g. a remote hiGenerator) 
 * with quickly generated numbers in a range: value = hi * maxLo + local.
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class HiLoGenerator implements Generator<Long> {

    private static final Log logger = LogFactory.getLog(HiLoGenerator.class);

    protected static final int DEFAULT_MAX_LO = 100;

    protected int maxLo;
    
    private int lo;
    private long hi;

    protected Generator<Long> hiGenerator;
    protected boolean dirty;

    public HiLoGenerator() {
        this(new IncrementGenerator(), DEFAULT_MAX_LO);
    }
    
    public HiLoGenerator(Generator<Long> hiGenerator) {
        this(hiGenerator, DEFAULT_MAX_LO);
    }
    
    public HiLoGenerator(Generator<Long> hiGenerator, int maxLo) {
        this.hiGenerator = hiGenerator;
        setMaxLo(maxLo);
        this.lo = -1;
        this.hi = -1;
        this.dirty = true;
    }
    
    // properties ------------------------------------------------------------------------------------

    public void setHiGenerator(Generator<Long> hiGenerator) {
        this.hiGenerator = hiGenerator;
    }

    /**
     * @return the maxLo
     */
    public int getMaxLo() {
        return maxLo;
    }

    /**
     * @param maxLo the maxLo to set
     */
    public void setMaxLo(int maxLo) {
        if (maxLo <= 0)
            throw new IllegalArgumentException("maxLo must be greater than 0, was: " + maxLo);
        this.maxLo = maxLo;
        this.dirty = true;
    }

    // Generator interface -------------------------------------------------------------------
    
    public Class<Long> getGeneratedType() {
        return Long.class;
    }

    public void validate() {
        if (dirty) {
            if (hiGenerator == null)
                throw new InvalidGeneratorSetupException("hiGenerator", "is null");
            hiGenerator.validate();
            dirty = false;
        }
    }

    public boolean available() {
        if (dirty)
            validate();
        return hiGenerator.available();
    }

    public Long generate() {
        if (dirty)
            validate();
        if (hi == -1 || lo >= maxLo) {
            hi = hiGenerator.generate();
            if (logger.isDebugEnabled())
                logger.debug("fetched new hi value: " + hi);
            lo = 0;
        } else
            lo++;
        return hi * (maxLo + 1) + lo;
    }
    
    public void reset() {
        if (dirty)
            validate();
        hiGenerator.reset();
        hi = -1;
        dirty = true;
    }

    public void close() {
        if (dirty)
            validate();
        hiGenerator.close();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + maxLo + ',' + hiGenerator + ']';
    }
}
