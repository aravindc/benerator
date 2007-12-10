package org.databene.benerator.factory;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.ValidatingGeneratorProxy;
import org.databene.model.Validator;
import org.databene.model.data.EntityDescriptor;
import org.databene.model.data.FeatureDescriptor;
import org.databene.model.data.FeatureDetail;
import org.databene.model.data.Iteration;

class FeatureGeneratorFactory {

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

    private static final Log logger = LogFactory.getLog(FeatureGeneratorFactory.class);
    
    protected static void checkUsedDetails(FeatureDescriptor descriptor, Set<String> usedDetails) {
        for (FeatureDetail<? extends Object> detail : descriptor.getDetails()) {
            String name = detail.getName();
            if (!"name".equals(name) && detail.getValue() != null && !usedDetails.contains(name))
                logger.warn("Ignored detail: " + detail + " in descriptor " + descriptor);
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
