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
import org.databene.benerator.wrapper.IdGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.id.GlobalIdProviderFactory;
import org.databene.id.IdProvider;
import org.databene.id.IdProviderFactory;
import org.databene.id.IdStrategy;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.TypeDescriptor;

/**
 * Creates generators that generate identifiers.<br/><br/>
 * Created: 03.03.2008 15:52:27
 * @author Volker Bergmann
 */
public class IdGeneratorFactory extends ComponentGeneratorFactory {

    public static Generator<? extends Object> createIdGenerator(IdDescriptor descriptor, Context context) {
        TypeDescriptor typeDescriptor = descriptor.getType();
        IdProviderFactory source = null;
        // check strategy
        String strategyName = descriptor.getStrategy();
        if (strategyName == null)
            throw new ConfigurationError("No strategy defined for key: " + descriptor.getName());

        // check scope
        String scope = descriptor.getScope();
        // check source
        String sourceId = typeDescriptor.getSource();
        if (sourceId != null) {
            source = (IdProviderFactory) context.get(sourceId);
        }
        // check param
        String param = descriptor.getParam();

        //checkUsedDetails(descriptor, usedDetails);
        IdStrategy idStrategy = IdStrategy.getInstance(strategyName);
        IdProvider idProvider;
        if (source != null)
            idProvider = source.idProvider(idStrategy, param, scope);
        else
            idProvider = GLOBAL_ID_PROVIDER_FACTORY.idProvider(idStrategy, param, scope);
        Generator<Object> generator = new IdGenerator(idProvider);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return generator;
    }

    private static final Log logger = LogFactory.getLog(ComponentGeneratorFactory.class);

    private static final GlobalIdProviderFactory GLOBAL_ID_PROVIDER_FACTORY = new GlobalIdProviderFactory();

}
