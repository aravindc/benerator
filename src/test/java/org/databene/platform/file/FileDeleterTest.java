/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

import java.io.File;

import org.databene.commons.FileUtil;

/**
 * TODO document class FileDeleterTest.<br/>
 * <br/>
 * Created at 17.09.2009 10:26:38
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class FileDeleterTest extends FileTest {

	public void test() throws Exception {
	    File sourceFile1 = null;
		File sourceFile2 = null;
		try {
			sourceFile1 = createSource1();
			sourceFile2 = createSource2();
			assertTrue(sourceFile1.exists());
			assertTrue(sourceFile2.exists());
			FileDeleter joiner = new FileDeleter();
			joiner.setFiles(new String[] { sourceFile1.getAbsolutePath(), sourceFile2.getAbsolutePath() });
			joiner.run(null);
			assertFalse(sourceFile1.exists());
			assertFalse(sourceFile2.exists());
		} finally {
			FileUtil.deleteIfExists(sourceFile1);
			FileUtil.deleteIfExists(sourceFile2);
		}
    }

}
