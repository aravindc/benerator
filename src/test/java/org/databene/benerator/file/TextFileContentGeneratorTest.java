/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

package org.databene.benerator.file;

import java.io.File;

import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.CollectionUtil;
import org.junit.Test;

/**
 * Tests the {@link TextFileContentGenerator}.<br/><br/>
 * Created: 24.02.2010 10:22:20
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class TextFileContentGeneratorTest extends FileContentGeneratorTest {

	@Test
	public void testListFiles() throws Exception {
		// prepare tests
		createTestFolders();
		try {
			// execute tests
			check(null, false, ROOT_DIR_FILE_CONTENT); // non-recursive, only files, w/o pattern
			check("fr.*", false, ROOT_DIR_FILE_CONTENT); // non-recursive, only files,  w/ pattern
			check(null, true, ROOT_DIR_FILE_CONTENT, SUB_DIR_FILE_CONTENT); // recursive, w/o pattern
			check("fr.*", true, ROOT_DIR_FILE_CONTENT); // recursive, w/ pattern
        } finally {
        	// remove the used files
        	removeTestFolders();
        }
	}

	private void check(String regex, boolean recursive, String... values) {
	    TextFileContentGenerator generator = new TextFileContentGenerator();
	    generator.setUri(ROOT_DIR.getParent() + File.separator + ROOT_DIR.getName());
	    generator.setFilter(regex);
	    generator.setRecursive(recursive);
	    generator.init(context);
	    GeneratorTest.checkProductSet(generator, 20, CollectionUtil.toSet(values));
    }
	
}
