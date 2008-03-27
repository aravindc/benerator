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

package org.databene.benerator.primitive.regex;

import org.databene.benerator.Generator;
import org.databene.benerator.wrapper.GeneratorWrapper;
import org.databene.benerator.wrapper.UniqueCompositeGenerator;

/**
 * Uses n String generators and appends the output of each one in each generate() call.<br/>
 * <br/>
 * Created: 17.11.2007 17:33:21
 */
public class UniqueFixedCountCompositeStringGenerator extends GeneratorWrapper<String[], String> {

    // constructors ----------------------------------------------------------------------------------------------------

    public UniqueFixedCountCompositeStringGenerator() {
        super(null);
    }

    public UniqueFixedCountCompositeStringGenerator(Generator<String>... sources) {
        super(wrap(sources));
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public String generate() {
        StringBuilder builder = new StringBuilder();
        Object[] parts = source.generate();
        for (Object part : parts)
            builder.append(part);
        return builder.toString();
    }

    public Class<String> getGeneratedType() {
        return String.class;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        int count = 0;
        if (source != null)
            count = ((UniqueCompositeGenerator)source).getSources().length;
        return getClass().getSimpleName() + "[count=" + count + ", " +
                "source=" + source + ']';
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private static Generator<String[]> wrap(Generator<String> ... sources) {
        return new UniqueCompositeGenerator<String>(String.class, sources);
    }

}
