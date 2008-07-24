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

package org.databene.benerator.factory;

import junit.framework.TestCase;

import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.ComponentBuilderFactory;
import org.databene.commons.context.DefaultContext;
import org.databene.model.data.AlternativeGroupDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;

/**
 * Tests the ComponentGeneratorFactory class for all useful setups.<br/>
 * <br/>
 * Created: 10.08.2007 12:40:41
 */
public class ComponentBuilderFactoryTest extends TestCase {
	
	// TODO v0.5.5 define tests for all syntax paths
/*
    private static Log logger = LogFactory.getLog(ComponentBuilderFactory.class);
    
    private static Set<String> componentFeatures = CollectionUtil.toSet(
            "type", "unique", "nullable", "minCount", "maxCount", "count", "nullQuota");
    
    private static int testCount = 0;
*/
	public void testNullQuotaOneReference() {
		ReferenceDescriptor reference = (ReferenceDescriptor) new ReferenceDescriptor("ref").withNullQuota(1);
		ComponentBuilder builder = createComponentBuilder(reference);
		expectNulls(builder, "ref", 10);
	}

	public void testNullQuotaOneAttribute() {
		ReferenceDescriptor attribute = (ReferenceDescriptor) new PartDescriptor("part").withNullQuota(1);
		ComponentBuilder builder = createComponentBuilder(attribute);
		expectNulls(builder, "part", 10);
	}

    // TODO v0.5.5 add tests

/*
    public void testGenerator() {
        createGenerator("test", "generator", BooleanGenerator.class.getName());
        createGenerator("test",
                "generator", BooleanGenerator.class.getName(),
                "nullQuota", "0.5");
        createGenerator("test",
                "generator", BooleanGenerator.class.getName(),
                "type", "string");
        createGenerator("test",
                "generator", BooleanGenerator.class.getName(),
                "type", "string",
                "nullQuota", "0.5");
    }

    public void testSamples() {
        createGenerator("test",
                "values", "1,2,3");
        createGenerator("test",
                "values", "1,2,3",
                "type", "char");
        createGenerator("test",
                "values", "1,2,3",
                "nullQuota", "0.5");
        createGenerator("test",
                "values", "1,2,3",
                "type", "int");
        createGenerator("test",
                "values", "1,2,3",
                "type", "int",
                "nullQuota", "0.5");
        createGenerator("test",
                "values", "2000-01-01,2000-01-02,2000-01-03",
                "type", "date",
                "pattern", "yyyy-MM-dd",
                "nullQuota", "0.5");
        // sequence
        createGenerator("test",
                "values", "1,2,3",
                "distribution", "cumulated");
        createGenerator("test",
                "values", "1,2,3",
                "distribution", "cumulated",
                "nullQuota", "0.5");
        createGenerator("test",
                "values", "1,2,3",
                "distribution", "cumulated",
                "type", "int");
        createGenerator("test",
                "values", "1,2,3",
                "distribution", "cumulated",
                "type", "int",
                "nullQuota", "0.5");
        // weight function
        createGenerator("test",
                "values", "1,2,3",
                "distribution", ConstantFunction.class.getName());
        createGenerator("test",
                "values", "1,2,3",
                "distribution", ConstantFunction.class.getName(),
                "nullQuota", "0.5");
        createGenerator("test",
                "values", "1,2,3",
                "distribution", ConstantFunction.class.getName(),
                "type", "int");
        createGenerator("test",
                "values", "1,2,3",
                "distribution", ConstantFunction.class.getName(),
                "type", "int",
                "nullQuota", "0.5");
    }

    public void testNumbers() {
        checkNumberType("int");
        checkNumberType("byte");
        checkNumberType("short");
        checkNumberType("long");
        checkNumberType("double");
        checkNumberType("float");
        checkNumberType("big_integer");
        checkNumberType("big_decimal");
    }

    public void testStrings() {
        createGenerator("test",
                "type", "string");
        createGenerator("test",
                "type", "string",
                "maxLength", "10");
        createGenerator("test",
                "type", "string",
                "minLength", "5",
                "maxLength", "10");
        createGenerator("test",
                "type", "string",
                "minLength", "5",
                "maxLength", "5");
        createGenerator("test",
                "type", "string",
                "pattern", "[0-9]{5}",
                "minLength", "5",
                "maxLength", "5");
        createGenerator("test",
                "type", "string",
                "pattern", "[0-9]{5}",
                "minLength", "4",
                "maxLength", "6");
    }

    public void testBoolean() {
        createGenerator("test",
                "type", "boolean");
        createGenerator("test",
                "type", "boolean",
                "trueQuota", "0.5");
        createGenerator("test",
                "type", "boolean",
                "nullQuota", "0");
        createGenerator("test",
                "type", "boolean",
                "trueQuota", "0.5",
                "nullQuota", "0");
    }

    public void testDate() {
        createGenerator("test", "type", "date");
        createGenerator("test",
                "type", "date",
                "min", "2000-01-01",
                "max", "2000-12-31",
                "pattern", "yyyy-MM-dd"
        );
        createGenerator("test",
                "type", "date",
                "min", "01/01/2000",
                "max", "01/03/2000"
        );
        createGenerator("test",
                "type", "date",
                "min", "01.01.2000",
                "max", "03.01.2000",
                "locale", "de"
        );
        createGenerator("test",
                "type", "date",
                "min", "2000-01-01",
                "max", "2000-12-31",
                "precision", "0000-00-01",
                "distribution", "cumulated",
                "pattern", "yyyy-MM-dd",
                "nullQuota", "0.1"
        );
    }

    public void testCharacter() {
        createGenerator("test", "type", "char");
        createGenerator("test",
                "type", "char",
                "pattern", "\\w",
                "locale", "de",
                "nullQuota", "0.5");
    }

    public void testImportToType() {
        createGenerator("test",
                "source", "org/databene/benerator/composite/dates.txt",
                "type", "string");
        createGenerator("test",
                "source", "org/databene/benerator/composite/booleans.txt",
                "type", "boolean");
        createGenerator("test",
                "source", "org/databene/benerator/composite/chars.txt",
                "type", "char");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "byte");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "short");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "int");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "long");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "float");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "double");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "big_integer");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "big_decimal");
    }

    public void testDateImport() {
        createGenerator("test",
                "source", "org/databene/benerator/composite/dates.txt",
                "pattern", "yyyy-MM-dd",
                "type", "date");
    }

    public void testConvertingImport() {
        createGenerator("test",
                "source", "org/databene/benerator/composite/dates.txt",
                "converter", "org.databene.commons.converter.NoOpConverter");
        createGenerator("test",
                "source", "org/databene/benerator/composite/dates.txt",
                "type", "date",
                "pattern", "yyyy-MM-dd");
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void checkNumberType(String type) {
        createGenerator("test",
                "type", type);
        createGenerator("test",
                "type", type,
                "min", "1");
        createGenerator("test",
                "type", type,
                "max", "2");
        createGenerator("test",
                "type", type,
                "distribution", "cumulated");
        createGenerator("test",
                "type", type,
                "min", "1",
                "max", "2");
        createGenerator("test",
                "type", type,
                "min", "-2",
                "max", "1");
        createGenerator("test",
                "type", type,
                "min", "1",
                "max", "2",
                "distribution", "cumulated");
        createGenerator("test",
                "type", type,
                "min", "-2",
                "max", "1",
                "distribution", "cumulated");
    }
*/
    // private helpers -------------------------------------------------------------------------------------------------
/*
    private ComponentBuilder createGenerator(String name, String ... featureDetails) {
        GenerationSetup setup = new SimpleGenerationSetup();
        logger.debug("Test #" + (++testCount));
        if (featureDetails.length % 2 != 0)
            throw new ConfigurationError("Illegal setup: need an even number of parameters (name/value pairs)");
        SimpleTypeDescriptor type = new SimpleTypeDescriptor(name, (String) null);
        PartDescriptor part = new PartDescriptor(name, type);
        for (int i = 0; i < featureDetails.length; i += 2)
            if (componentFeatures.contains(featureDetails[i]))
                if ("type".equals(featureDetails[i]))
                    part.setTypeName(featureDetails[i + 1]);
                else
                    part.setDetailValue(featureDetails[i], featureDetails[i + 1]);
            else
                type.setDetailValue(featureDetails[i], featureDetails[i + 1]);
        ComponentBuilder builder = ComponentBuilderFactory.createComponentBuilder(part, new DefaultContext(), setup);
        Entity entity = new Entity("Entity");
        for (int i = 0; i < 10; i++) {
            builder.buildComponentFor(entity);
            logger.debug(entity.getComponent(name));
            System.out.println(entity.getComponent(name));
        }
        return builder;
    }
*/
	// private helpers -------------------------------------------------------------------------------------------------
	
	private ComponentBuilder createComponentBuilder(ReferenceDescriptor reference) {
		return ComponentBuilderFactory.createComponentBuilder(reference, new BeneratorContext(), new SimpleGenerationSetup());
	}
	
    public void testAlternative() {
    	AlternativeGroupDescriptor alternativeType = new AlternativeGroupDescriptor(null);
    	SimpleTypeDescriptor typeA = (SimpleTypeDescriptor) new SimpleTypeDescriptor("A", "string").withValues("1");
		alternativeType.addComponent(new PartDescriptor("a", typeA));
    	SimpleTypeDescriptor typeB = (SimpleTypeDescriptor) new SimpleTypeDescriptor("B", "string").withValues("2");
		alternativeType.addComponent(new PartDescriptor("b", typeB));
		DefaultContext context = new DefaultContext();
		SimpleGenerationSetup setup = new SimpleGenerationSetup();
		PartDescriptor part = new PartDescriptor(null, alternativeType);
		ComponentBuilder builder = ComponentBuilderFactory.createComponentBuilder(part, context, setup);
		Entity entity = new Entity("Entity");
		builder.buildComponentFor(entity);
		System.out.println(entity);
    }
    
	private void expectNulls(ComponentBuilder builder, String componentName, int invocations) {
		Entity entity = new Entity("Test");
		for (int i = 0; i < invocations; i++) {
			builder.buildComponentFor(entity);
			assertNull(entity.get(componentName));
		}
	}

}
