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

package org.databene.benerator.engine.task;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.task.AbstractTask;

/**
 * TODO document class ImportTask.<br/>
 * <br/>
 * Created at 22.07.2009 08:19:54
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class ImportTask extends AbstractTask {
	
	private boolean defaultImports;
	private String[] classImports;
	private String[] packageImports;
	private String[] domainImports;
	private String[] platformImports;

    public ImportTask(boolean defaultImports, String[] classImports, String[] packageImports, String[] domainImports,
            String[] platformImports) {
	    this.defaultImports = defaultImports;
	    this.classImports = classImports;
	    this.packageImports = packageImports;
	    this.domainImports = domainImports;
	    this.platformImports = platformImports;
    }

	public void run(Context context) {
    	if (!(context instanceof BeneratorContext))
    		throw new ConfigurationError("ImportTask can only be used with Contexts that extend " 
    				+ BeneratorContext.class);
    	BeneratorContext beneratorContext = (BeneratorContext) context;

    	if (defaultImports)
    		beneratorContext.importDefaults();
    	
    	if (classImports != null)
    		for (String classImport : classImports)
    			beneratorContext.importClass(classImport);
		
    	if (packageImports != null)
    		for (String packageImport : packageImports)
    			beneratorContext.importPackage(packageImport);
		
    	if (domainImports != null)
    		for (String domainImport : domainImports)
    			importDomain(domainImport, beneratorContext);
		
    	if (platformImports != null)
    		for (String platformImport : platformImports)
    			importPlatform(platformImport, beneratorContext);
    }

	public void importDomain(String domain, BeneratorContext context) {
		if (domain.indexOf('.') < 0)
			context.importPackage("org.databene.domain." + domain);
		else
			context.importPackage(domain);
	}

	public void importPlatform(String platform, BeneratorContext context) {
		if (platform.indexOf('.') < 0)
			context.importPackage("org.databene.platform." + platform);
		else
			context.importPackage(platform);
	}

}
