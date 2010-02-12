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

import java.io.IOException;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.parser.DefaultEntryConverter;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Expression;
import org.databene.commons.IOUtil;
import org.databene.script.ScriptConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes an &lt;include/&gt; from an XML descriptor file.<br/>
 * <br/>
 * Created at 23.07.2009 07:18:54
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class IncludeStatement implements Statement {
	
	private static Logger logger = LoggerFactory.getLogger(IncludeStatement.class);

	private Expression<String> uri;
	
    public IncludeStatement(Expression<String> uri) {
    	this.uri = uri;
    }
    
	public Expression<String> getUri() {
    	return uri;
    }

	public void setUri(Expression<String> uri) {
    	this.uri = uri;
    }

	public void execute(BeneratorContext context) {
		String uriValue = context.resolveRelativeUri(uri.evaluate(context));
        try {
            importProperties(uriValue, context);
        } catch (IOException e) {
            throw new ConfigurationError("Properties file not found for uri: " + uri);
        }
	}

    public static void importProperties(String uri, BeneratorContext context) throws IOException {
        logger.debug("reading properties from uri: " + uri);
        ScriptConverter preprocessor = new ScriptConverter(context);
        DefaultEntryConverter converter = new DefaultEntryConverter(preprocessor, context, true);
        IOUtil.readProperties(uri, converter);
    }

}
