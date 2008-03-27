/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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


package org.databene.benerator.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.commons.Context;
import org.databene.model.data.PartDescriptor;

/**
 * Creates part Generators.<br/><br/>
 * Created: 05.03.2008 17:02:13
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class PartGeneratorFactory extends ComponentGeneratorFactory {
    
    private static final Log logger = LogFactory.getLog(InstanceGeneratorFactory.class);

    public static Generator<? extends Object> createPartGenerator(
            PartDescriptor descriptor, Context context, GenerationSetup setup) {
        Generator<? extends Object> generator = createSingleInstanceGenerator(
                descriptor, context, setup);
        generator = createComponentGeneratorWrapper(descriptor, generator, context);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return generator;
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private PartGeneratorFactory() {}

}
