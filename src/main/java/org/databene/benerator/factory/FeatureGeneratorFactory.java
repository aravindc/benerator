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

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.ValidatingGeneratorProxy;
import org.databene.commons.Validator;
import org.databene.model.data.EntityDescriptor;
import org.databene.model.data.FeatureDescriptor;
import org.databene.model.data.FeatureDetail;
import org.databene.model.data.Iteration;

/**
 * Creates generators that generate entity components.<br/>
 * <br/>
 * @author Volker Bergmann
 */
abstract class FeatureGeneratorFactory {

    private static final Log logger = LogFactory.getLog(FeatureGeneratorFactory.class);

    // descriptor feature names ----------------------------------------------------------------------------------------
    
    protected static final String ENCODING    = "encoding";
    protected static final String GENERATOR   = "generator";
    protected static final String SOURCE      = "source";
    protected static final String VALIDATOR   = "validator";
    protected static final String NULL_QUOTA  = "nullQuota";
    protected static final String TYPE        = "type";
    protected static final String TRUE_QUOTA  = "trueQuota";
    protected static final String PATTERN     = "pattern";
    protected static final String LOCALE      = "locale";
    protected static final String MIN_LENGTH  = "minLength";
    protected static final String MAX_LENGTH  = "maxLength";
    protected static final String REGION      = "region";
    protected static final String UNIQUE      = "unique";

    // helper methods for child classes --------------------------------------------------------------------------------
    
    protected static void checkUsedDetails(FeatureDescriptor descriptor, Set<String> usedDetails) {
        for (FeatureDetail<? extends Object> detail : descriptor.getDetails()) {
            String name = detail.getName();
            if (!"name".equals(name) && detail.getValue() != null && !usedDetails.contains(name))
                logger.debug("Ignored detail: " + detail + " in descriptor " + descriptor); // TODO v0.4.1 improve tracking of unused features
        }
    }

    protected static <T> Generator<T> createProxy(FeatureDescriptor descriptor, Generator<T> generator, Set<String> usedDetails) {
        // check cyclic flag
        Boolean cyclic = descriptor.isCyclic();
        if (cyclic == null)
            cyclic = false;
        else
            usedDetails.add("cyclic");

        // check proxy
        Long proxyParam1 = null;
        Long proxyParam2 = null;
        Iteration iteration = descriptor.getProxy();
        if (iteration != null) {
            usedDetails.add("proxy");
            proxyParam1 = descriptor.getProxyParam1();
            if (proxyParam1 != null)
                usedDetails.add("proxy-param1");
            proxyParam2 = descriptor.getProxyParam2();
            if (proxyParam2 != null)
                usedDetails.add("proxy-param2");
        }
        return GeneratorFactory.createProxy(generator, cyclic, iteration, proxyParam1, proxyParam2);
    }

    protected static <T> Generator<T> createValidatingGenerator(
            EntityDescriptor descriptor, Generator<T> generator, Set<String> usedDetails) {
        Validator<T> validator = null;
        if (descriptor.getValidator() != null) {
            usedDetails.add(VALIDATOR);
            validator = (Validator<T>) descriptor.getValidator();
            generator = new ValidatingGeneratorProxy<T>(generator, validator);
        }
        return generator;
    }

}
