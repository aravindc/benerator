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

package org.databene.platform.file;

import org.junit.Test;
import static junit.framework.Assert.*;

import java.io.File;
import java.io.IOException;

import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.FileUtil;
import org.databene.commons.IOUtil;

/**
 * Tests the {@link FileJoiner}.<br/>
 * <br/>
 * Created at 16.09.2009 16:32:45
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class FileJoinerTest extends FileTest {

	@Test
	public void testDefault() throws Exception {
		check(1, false, false, "ABC123");
	}

	@Test
	public void testAppendTrue() throws Exception {
		check(2, true, false, "ABC123ABC123");
	}

	@Test
	public void testAppendFalse() throws Exception {
		check(2, false, false, "ABC123");
	}

	@Test
	public void testDeleteSources() throws Exception {
		check(1, false, true, "ABC123");
	}

	// helpers ---------------------------------------------------------------------------------------------------------
	
    private void check(int executionCount, boolean append, boolean deleteSources, String result) throws IOException {
	    File sourceFile1 = null;
		File sourceFile2 = null;
		File destFile = null;
		Context context = new DefaultBeneratorContext();
		try {
			sourceFile1 = createSource1();
			sourceFile2 = createSource2();
			destFile = File.createTempFile(prefix(), ".txt", new File("target"));
			FileJoiner joiner = new FileJoiner();
			joiner.setAppend(append);
			joiner.setSources(new String[] { "target" + File.separator + sourceFile1.getName(), "target" + File.separator + sourceFile2.getName() });
			joiner.setDestination("target" + File.separator + destFile.getName());
			joiner.setDeleteSources(deleteSources);
			for (int i = 0; i < executionCount; i++)
				joiner.execute(context, ErrorHandler.getDefault());
			assertEquals(deleteSources, !sourceFile1.exists());
			assertEquals(deleteSources, !sourceFile2.exists());
			assertEquals(result, IOUtil.getContentOfURI(destFile.getAbsolutePath()));
		} finally {
			FileUtil.deleteIfExists(sourceFile1);
			FileUtil.deleteIfExists(sourceFile2);
			FileUtil.deleteIfExists(destFile);
		}
    }

}
