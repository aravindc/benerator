/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.consumer.AbstractConsumer;
import org.databene.benerator.consumer.FileExporter;
import org.databene.benerator.test.ModelTest;
import org.databene.benerator.wrapper.ProductWrapper;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link DescriptorRunner}.<br/>
 * <br/>
 * Created at 13.03.2009 07:16:55
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class DescriptorRunnerTest extends ModelTest {

    private static final String EXPORT_FILE_URI = "test-uri.txt";

    @Test
	public void testProgrammaticInvocation() throws IOException {
		DescriptorRunner runner = new DescriptorRunner(
				"string://<setup>" +
				"	<generate type='Person' count='1' consumer='myConsumer'>" +
				"		<attribute name='name' constant='Alice'/>" +
				"	</generate>" +
				"</setup>", new DefaultBeneratorContext());
		BeneratorContext context = runner.getContext();
		context.importDefaults();
		context.setValidate(false);
		MyConsumer myConsumer = new MyConsumer();
		context.set("myConsumer", myConsumer);
		runner.run();
		assertEquals(1, myConsumer.products.size());
		assertEquals(createEntity("Person", "name", "Alice"), myConsumer.products.get(0));
	}
	
    @Test
	public void testGetGeneratedFiles() {
		DescriptorRunner runner = new DescriptorRunner("string://<setup/>", new DefaultBeneratorContext());
		runner.addResource(new TestExporter());
		List<String> generatedFiles = runner.getGeneratedFiles();
		assertEquals(1, generatedFiles.size());
		assertEquals(EXPORT_FILE_URI, generatedFiles.get(0));
	}
	
	
	
	static class TestExporter implements FileExporter {

        public String getUri() {
	        return EXPORT_FILE_URI;
        }

        public void startConsuming(ProductWrapper<?> object) { }
        public void finishConsuming(ProductWrapper<?> object) { }
        public void flush() { }
        public void close() { }
	}
	
	static class MyConsumer extends AbstractConsumer {
		
		List<Object> products = new ArrayList<Object>();

        @Override
		public void startProductConsumption(Object object) {
	        products.add(object);
        }
	}

}
