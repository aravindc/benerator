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

package org.databene.benerator.engine.statement;

import java.io.IOException;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DescriptorRunner;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.parser.DefaultEntryConverter;
import org.databene.commons.ConfigurationError;
import org.databene.commons.IOUtil;
import org.databene.platform.xml.XMLSchemaDescriptorProvider;
import org.databene.script.Expression;
import org.databene.script.ScriptConverterForStrings;
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

	private Expression<String> uriEx;
	
    public IncludeStatement(Expression<String> uri) {
    	this.uriEx = uri;
    }
    
	public Expression<String> getUri() {
    	return uriEx;
    }

	public void setUri(Expression<String> uri) {
    	this.uriEx = uri;
    }

	public boolean execute(BeneratorContext context) {
		String uri = context.resolveRelativeUri(uriEx.evaluate(context));
		String lcUri = uri.toLowerCase();
        try {
			if (lcUri.endsWith(".properties"))
	            includeProperties(uri, context);
			else if (lcUri.endsWith(".ben.xml") || lcUri.endsWith("benerator.xml"))
				includeDescriptor(uri, context);
			else if (lcUri.endsWith(".xsd"))
				includeXmlSchema(uri, context);
			else
				throw new ConfigurationError("Not a supported import file type: " + uri);
	    	return true;
        } catch (IOException e) {
            throw new ConfigurationError("Error processing " + uri, e);
        }
	}

	public static void includeProperties(String uri, BeneratorContext context) throws IOException {
        logger.debug("Including properties file: " + uri);
        ScriptConverterForStrings preprocessor = new ScriptConverterForStrings(context);
        DefaultEntryConverter converter = new DefaultEntryConverter(preprocessor, context, true);
        IOUtil.readProperties(uri, converter);
    }

	public static void includeXmlSchema(String uri, BeneratorContext context) {
        logger.debug("Including XML Schema: " + uri);
        new XMLSchemaDescriptorProvider(uri, context);
    }

    private static void includeDescriptor(String uri, BeneratorContext context) throws IOException {
        logger.debug("Including Benerator descriptor file: " + uri);
		new DescriptorRunner(uri, context).runWithoutShutdownHook();
    }

}
