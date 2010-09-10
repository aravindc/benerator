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

package org.databene.benerator.engine;

import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.BeneratorFactory;
import org.databene.benerator.engine.parser.xml.BeanParser;
import org.databene.benerator.engine.parser.xml.CommentParser;
import org.databene.benerator.engine.parser.xml.DatabaseParser;
import org.databene.benerator.engine.parser.xml.DefaultComponentParser;
import org.databene.benerator.engine.parser.xml.EchoParser;
import org.databene.benerator.engine.parser.xml.EvaluateParser;
import org.databene.benerator.engine.parser.xml.GenerateOrIterateParser;
import org.databene.benerator.engine.parser.xml.IfParser;
import org.databene.benerator.engine.parser.xml.ImportParser;
import org.databene.benerator.engine.parser.xml.IncludeParser;
import org.databene.benerator.engine.parser.xml.PropertyParser;
import org.databene.benerator.engine.parser.xml.RunTaskParser;
import org.databene.benerator.engine.parser.xml.WaitParser;
import org.databene.benerator.engine.parser.xml.WhileParser;
import org.databene.commons.ConfigurationError;

/**
 * TODO Document class.<br/><br/>
 * Created: 08.09.2010 15:45:25
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class DefaultBeneratorFactory extends BeneratorFactory {

	protected List<DescriptorParser> parsers;
	private boolean defaultParsersInitialized;

	public DefaultBeneratorFactory() {
		this.parsers = new ArrayList<DescriptorParser>();
		this.defaultParsersInitialized = false;
    }

	public boolean addParser(DescriptorParser parser) {
	    return parsers.add(parser);
    }

	@Override
    public DescriptorParser getParser(String elementName, String parentName) {
		if (!defaultParsersInitialized)
			initDefaultParsers();
		for (DescriptorParser parser : parsers)
			if (parser.supports(elementName, parentName))
				return parser;
		throw new ConfigurationError("Unknown element: <" + elementName + ">");
    }

	protected void initDefaultParsers() {
	    addParser(new DefaultComponentParser());
		addParser(new CommentParser());
		addParser(new BeanParser());
		addParser(new GenerateOrIterateParser());
		addParser(new DatabaseParser());
		addParser(new EchoParser());
		addParser(new EvaluateParser());
		addParser(new ImportParser());
		addParser(new IncludeParser());
		addParser(new PropertyParser());
		addParser(new RunTaskParser());
		addParser(new IfParser());
		addParser(new WhileParser());
		addParser(new WaitParser());
    }

}
