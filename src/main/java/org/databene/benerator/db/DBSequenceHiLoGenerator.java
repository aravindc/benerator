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

package org.databene.benerator.db;

import org.databene.benerator.primitive.HiLoGenerator;
import org.databene.platform.db.adapter.DBSystem;

/**
 * Creates Unique keys efficiently by connecting a database, retrieving a (unique) sequence value 
 * and building sub keys of it.
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class DBSequenceHiLoGenerator extends HiLoGenerator {
    
    private DBSystem source;
    
    private String selector;
    
    // constructors -----------------------------------------------------------------------------------

    public DBSequenceHiLoGenerator() {
        this(null, null, DEFAULT_MAX_LO);
    }

    public DBSequenceHiLoGenerator(DBSystem source, String selector, int maxLo) {
        super(null);
        this.source = source;
        this.selector = selector;
        this.dirty = true;
    }
    
    // properties ----------------------------------------------------------------------------------
    
    /**
     * @param source the source to set
     */
    public void setSource(DBSystem source) {
        this.source = source;
    }

    /**
     * @return the selector
     */
    public String getSelector() {
        return selector;
    }

    /**
     * @param selector the selector to set
     */
    public void setSelector(String selector) {
        this.selector = selector;
    }
    
    // Generator interface ----------------------------------------------------------------------------------
    
    @Override
    public void validate() {
        if (dirty) {
            super.setHiGenerator(new SQLQueryGenerator(source, selector));
            super.validate();
            dirty = false;
        }
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + selector + ',' + maxLo +']';
    }

}
