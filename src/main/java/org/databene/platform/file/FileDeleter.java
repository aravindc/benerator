/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.task.AbstractTask;
import org.databene.task.TaskResult;

/**
 * Deletes one or more files.<br/>
 * <br/>
 * Created at 16.09.2009 15:50:25
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class FileDeleter extends AbstractTask {
	
	private String[] files = new String[0];

    public String[] getFiles() {
    	return files;
    }

    public void setFiles(String[] files) {
    	this.files = files;
    }

	public TaskResult execute(Context ctx, ErrorHandler errorHandler) {
		BeneratorContext context = (BeneratorContext) ctx;
		for (String filename : files) {
			File file = new File(context.resolveRelativeUri(filename));
			if (file.exists()) {
				try {
					if (!file.delete())
						errorHandler.handleError("File could not be deleted: " + filename + ". " +
								"Probably it is locked.");
				} catch (Exception e) {
					errorHandler.handleError("Error deleting file " + file);
				}
			} else
				errorHandler.handleError("File not found: " + file);
		}
		return TaskResult.FINISHED;
    }

}
