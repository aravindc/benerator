/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.engine.parser.xml.BeanParser;
import org.databene.benerator.engine.parser.xml.CommentParser;
import org.databene.benerator.engine.parser.xml.GenerateOrIterateParser;
import org.databene.benerator.engine.parser.xml.DatabaseParser;
import org.databene.benerator.engine.parser.xml.DefaultComponentParser;
import org.databene.benerator.engine.parser.xml.EchoParser;
import org.databene.benerator.engine.parser.xml.EvaluateParser;
import org.databene.benerator.engine.parser.xml.IfParser;
import org.databene.benerator.engine.parser.xml.ImportParser;
import org.databene.benerator.engine.parser.xml.IncludeParser;
import org.databene.benerator.engine.parser.xml.PropertyParser;
import org.databene.benerator.engine.parser.xml.RunTaskParser;
import org.databene.benerator.engine.parser.xml.WaitParser;
import org.databene.benerator.engine.parser.xml.WhileParser;
import org.databene.commons.ConfigurationError;

/**
 * Provides the parsers for a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:20:33
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ParserFactory {
	
	private static List<DescriptorParser> parsers = new ArrayList<DescriptorParser>();
	
	static { // TODO v0.7 define extension mechanism (e.g. by PlatformDescriptor?)
		parsers.add(new DefaultComponentParser());
		parsers.add(new CommentParser());
		parsers.add(new BeanParser());
		parsers.add(new GenerateOrIterateParser());
		parsers.add(new DatabaseParser());
		parsers.add(new EchoParser());
		parsers.add(new EvaluateParser());
		parsers.add(new ImportParser());
		parsers.add(new IncludeParser());
		parsers.add(new PropertyParser());
		parsers.add(new RunTaskParser());
		parsers.add(new IfParser());
		parsers.add(new WhileParser());
		parsers.add(new WaitParser());
	}

	public static DescriptorParser getParser(String elementName, String parentName) {
		for (DescriptorParser parser : parsers)
			if (parser.supports(elementName, parentName))
				return parser;
		throw new ConfigurationError("Unknown element: " + elementName);
    }

}
