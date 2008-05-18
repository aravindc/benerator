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

package shop;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.ComplexTypeGeneratorFactory;
import org.databene.benerator.factory.SimpleGenerationSetup;
import org.databene.benerator.factory.SimpleTypeGeneratorFactory;
import org.databene.commons.Validator;
import org.databene.commons.validator.StringLengthValidator;
import org.databene.domain.product.EAN13Validator;
import org.databene.domain.product.EAN8Validator;
import org.databene.domain.product.EANValidator;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.platform.xml.XMLSchemaDescriptorProvider;

import java.io.IOException;

/**
 * Tests processing of the shop.xsd file.<br/>
 * <br/>
 * Created: 28.02.2008 15:17:13
 */
public class ShopXMLTest extends TestCase {
    
    private static final Log logger = LogFactory.getLog(ShopXMLTest.class);
    
    public void test() throws IOException {
        String schemaUri = "demo/shop/shop.xsd";
        System.setProperty("stage", "test");
        
        BeneratorContext context = new BeneratorContext();
        XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(schemaUri, context);
        DataModel.getDefaultInstance().addDescriptorProvider(provider);
        DataModel.getDefaultInstance().validate();

        logger.debug("Supported types:");
        logger.debug("----------------");
        for (TypeDescriptor descriptor : provider.getTypeDescriptors())
            logger.debug(descriptor);
        
        checkSimpleType("category-id", provider, new CategoryIdValidator());
        checkSimpleType("string30", provider, new StringLengthValidator(30));
        checkSimpleType("ean-type", provider, new EANValidator());
        checkSimpleType("ean8-type", provider, new EAN8Validator());
        checkSimpleType("ean13-type", provider, new EAN13Validator());
        checkSimpleType("price-type", provider, new PriceValidator());
        checkSimpleType("surrogate-id", provider, new LongValidator());
        checkSimpleType("string16", provider, new StringLengthValidator(16));
        
        checkComplexType("audited", provider, new AuditedValidator());
        checkComplexType("audited-updateable", provider, new AuditedUpdateableValidator());
        checkComplexType("category", provider, new CategoryValidator());
        checkComplexType("product",  provider, new ProductValidator());
        checkComplexType("admin",    provider, new UserValidator("admin"));
        checkComplexType("clerk",    provider, new UserValidator("clerk"));
        checkComplexType("customer", provider, new CustomerValidator());
    }

    private <T> void checkSimpleType(String name, XMLSchemaDescriptorProvider provider, Validator<T> validator) {
        SimpleTypeDescriptor descriptor = (SimpleTypeDescriptor) provider.getTypeDescriptor(name);
        logger.debug("");
        logger.debug("Testing simple type: " + descriptor.getName());
        logger.debug("-------------------------------------");
        Generator<T> generator = (Generator<T>) SimpleTypeGeneratorFactory.create(
            descriptor, false, provider.getContext(), new SimpleGenerationSetup());
        for (int i = 0; i < 10; i++) {
            T object = generator.generate();
            logger.debug(object);
            assertTrue("Invalid object: " + object, validator.valid(object));
        }
    }

    private void checkComplexType(String name, XMLSchemaDescriptorProvider provider, Validator<Entity> validator) {
        ComplexTypeDescriptor descriptor = (ComplexTypeDescriptor) provider.getTypeDescriptor(name);
        logger.debug("");
        logger.debug("Testing complex type: " + descriptor.getName());
        logger.debug("-------------------------------------");
        Generator<Entity> generator = ComplexTypeGeneratorFactory.createComplexTypeGenerator(
                descriptor, false, provider.getContext(), new SimpleGenerationSetup());
        for (int i = 0; i < 10; i++)
            if (generator.available()) {
                Entity entity = generator.generate();
                logger.debug(entity);
                assertTrue("Invalid entity: " + entity, validator.valid(entity));
            }
    }
}
