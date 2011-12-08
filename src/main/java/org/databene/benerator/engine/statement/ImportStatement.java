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

package org.databene.benerator.engine.statement;

import java.util.List;

import org.databene.benerator.PlatformDescriptor;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;

/**
 * Imports classes by package, class, domain and platform definition(s).<br/>
 * <br/>
 * Created at 22.07.2009 08:19:54
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class ImportStatement implements Statement {
	
	private boolean defaultImports;
	private String[] classImports;
	private String[] domainImports;
	private List<PlatformDescriptor> platformImports;

    public ImportStatement(boolean defaultImports, String[] classImports, String[] domainImports,
    		List<PlatformDescriptor> platformImports) {
	    this.defaultImports = defaultImports;
	    this.classImports = classImports;
	    this.domainImports = domainImports;
	    this.platformImports = platformImports;
    }

	public boolean execute(BeneratorContext context) {
    	if (defaultImports)
    		context.importDefaults();
    	
    	if (classImports != null)
    		for (String classImport : classImports)
    			context.importClass(classImport);
		
    	if (domainImports != null)
    		for (String domainImport : domainImports)
    			importDomain(domainImport, context);
		
    	if (platformImports != null)
    		for (PlatformDescriptor platformImport : platformImports)
    			importPlatform(platformImport, context);
    	return true;
    }

	public void importDomain(String domain, BeneratorContext context) {
		if (domain.indexOf('.') < 0)
			context.importPackage("org.databene.domain." + domain);
		else
			context.importPackage(domain);
	}

	public void importPlatform(PlatformDescriptor platformDescriptor, BeneratorContext context) {
		platformDescriptor.init(context);
	}

}
