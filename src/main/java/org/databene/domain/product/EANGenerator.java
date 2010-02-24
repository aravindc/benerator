/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.product;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.benerator.wrapper.UniqueAlternativeGenerator;
import org.databene.benerator.wrapper.AlternativeGenerator;

/**
 * Generates EAN8 and EAN13 codes at the configured ratio.<br/>
 * <br/>
 * Created: 30.07.2007 21:23:44
 */
public class EANGenerator extends GeneratorProxy<String> {

    private boolean unique;

    public EANGenerator() {
        this(false);
    }

    public EANGenerator(boolean unique) {
        super(null);
        this.unique = unique;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
    public Class<String> getGeneratedType() {
        return String.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(BeneratorContext context) {
    	assertNotInitialized();
        if (unique)
            setSource(new UniqueAlternativeGenerator<String>(String.class,
                    new EAN8Generator(true),
                    new EAN13Generator(true)));
        else
            setSource(new AlternativeGenerator<String>(String.class,
                    new EAN8Generator(false),
                    new EAN13Generator(false)));
        super.init(context);
    }

    @Override
    public String generate() {
        assertInitialized();
        return super.generate();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + (unique ? "[unique]" : "");
    }
}
