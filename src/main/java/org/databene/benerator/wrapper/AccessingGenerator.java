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

import org.databene.benerator.util.LightweightGenerator;
import org.databene.commons.Accessor;

/**
 * Returns the results of an accessor that is applied on a constant provider object.<br/>
 * <br/>
 * Created: 22.08.2007 19:05:40
 */
public class AccessingGenerator<S, P> extends LightweightGenerator<P> {

    private Accessor<S, P> accessor;
    private S provider;

    private boolean fetched;
    private P next;

    public AccessingGenerator(Class<P> targetType, Accessor<S, P> accessor, S provider) {
        super(targetType);
        this.accessor = accessor;
        this.provider = provider;
        this.fetched = false;
        this.next = null;
    }

    @Override
    public boolean available() {
        if (!fetched)
            fetchNext();
        return (next != null);
    }

    public P generate() {
        if (!fetched)
            fetchNext();
        fetched = false;
        return next;
    }

    private void fetchNext() {
        next = accessor.getValue(provider);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[accessor=" + accessor + ']';
    }
}
