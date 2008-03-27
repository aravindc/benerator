/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

import java.util.Random;

/**
 * Forwards the products from a source generator and injects a quota of null values.<br/>
 * <br/>
 * Created: 23.06.2006 20:01:07
 */
public class NullableGenerator<E> extends GeneratorProxy<E> {

    /** the quota of nulls to create, ranging from 0 to 1 */
    private double nullQuota;

    /** Helper for random number creation */
    private Random random;

    // constructors ----------------------------------------------------------------------------------------------------

    public NullableGenerator() {
        this(null, 0);
    }

    public NullableGenerator(Generator<E> realGenerator, double nullQuota) {
        super(realGenerator);
        this.nullQuota = nullQuota;
        this.random = new Random();
    }

    // config properties -----------------------------------------------------------------------------------------------

    /**
     * Returns the nullQuota.
     * @see #nullQuota
     */
    public double getNullQuota() {
        return nullQuota;
    }

    /**
     * Sets the null quota
     * @param nullQuota sets the nullQuota
     * @see #nullQuota
     */
    public void setNullQuota(double nullQuota) {
        this.nullQuota = nullQuota;
    }

    // generator interface ---------------------------------------------------------------------------------------------

    /**
     * Forwards the generated values of the source generator,
     * including a defined quota of null values.
     */
    public E generate() {
        if (random.nextFloat() < nullQuota)
            return null;
        else
            return source.generate();
    }

}
