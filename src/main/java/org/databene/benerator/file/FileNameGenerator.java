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

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.util.AbstractGenerator;
import org.databene.commons.BeanUtil;

/**
 * Generates file and/or directory names out of a directory.<br/><br/>
 * Created: 24.02.2010 06:30:22
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class FileNameGenerator extends AbstractGenerator<String> {

	FileGenerator fileGenerator;
	
	public FileNameGenerator() {
	    this(".", null, false, true, false);
    }
	
	public FileNameGenerator(String rootUri, String filter, boolean recursive, boolean files, boolean folders) {
		this.fileGenerator = new FileGenerator(rootUri, filter, recursive, folders, files);
	    setRootUri(rootUri);
	    setFilter(filter);
	    setRecursive(recursive);
	    setFolders(folders);
	    setFiles(files);
    }

	// properties ------------------------------------------------------------------------------------------------------

	public void setRootUri(String rootUri) {
	    fileGenerator.setRootUri(rootUri);
    }

	public void setFilter(String filter) {
	    fileGenerator.setFilter(filter);
    }

	public void setFiles(boolean files) {
	    fileGenerator.setFiles(files);
    }

	public void setFolders(boolean folders) {
	    fileGenerator.setFolders(folders);
    }

	public void setRecursive(boolean recursive) {
	    fileGenerator.setRecursive(recursive);
    }

	public void setUnique(boolean unique) {
	    fileGenerator.setUnique(unique);
    }

	
	// Generator implementation ----------------------------------------------------------------------------------------

	public Class<String> getGeneratedType() {
	    return String.class;
    }

	@Override
    public void init(GeneratorContext context) {
	    fileGenerator.init(context);
	    super.init(context);
    }
	
	public String generate() {
	    return fileGenerator.generate().getAbsolutePath();
    }

	public void reset() {
	    fileGenerator.reset();
    }

	public void close() {
	    fileGenerator.close();
    }

	// java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
    public boolean equals(Object obj) {
	    return fileGenerator.equals(obj);
    }

	@Override
    public int hashCode() {
	    return fileGenerator.hashCode();
    }

	@Override
	public String toString() {
	    return BeanUtil.toString(this);
	}

}
