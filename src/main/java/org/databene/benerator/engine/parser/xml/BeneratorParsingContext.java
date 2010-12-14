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

package org.databene.benerator.engine.parser.xml;

import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.Statement;
import org.databene.webdecs.xml.ParsingContext;

/**
 * TODO Document class.<br/><br/>
 * Created: 14.12.2010 16:29:38
 * @since TODO version
 * @author Volker Bergmann
 */
public class BeneratorParsingContext extends ParsingContext<Statement> {

	ResourceManager resourceManager;

	public BeneratorParsingContext(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		factory.addParser(new BeanParser());
		factory.addParser(new BeepParser());
		factory.addParser(new CommentParser());
		factory.addParser(new DatabaseParser());
		factory.addParser(new DbSanity4BeneratorParser());
		factory.addParser(new DefaultComponentParser());
		factory.addParser(new EchoParser());
		factory.addParser(new EvaluateParser());
		factory.addParser(new GenerateOrIterateParser());
		factory.addParser(new IfParser());
		factory.addParser(new ImportParser());
		factory.addParser(new IncludeParser());
		factory.addParser(new PropertyParser());
		factory.addParser(new RunTaskParser());
		factory.addParser(new SetupParser());
		factory.addParser(new WaitParser());
		factory.addParser(new WhileParser());
	}
	
	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public BeneratorParsingContext createSubContext(ResourceManager resourceManager) {
		return new BeneratorParsingContext(resourceManager);
	}
	
}
