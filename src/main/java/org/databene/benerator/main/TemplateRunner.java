/*
 * (c) Copyright 2014 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.databene.benerator.template.TemplateInputReader;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.ui.ConsoleInfoPrinter;
import org.databene.script.Script;
import org.databene.script.freemarker.FreeMarkerScriptFactory;

/**
 * Anonymizes XML files homogeneously based on generator and XPath definitions in an Excel sheet.<br/><br/>
 * Created: 27.02.2014 10:04:58
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class TemplateRunner {
	
	public static void main(String[] args) throws Exception {
		// check preconditions
		if (args.length < 3 || args.length > 4)
			printHelpAndExit();
		
		// parse parameters
		String configFile = args[0];
		String configParserClass = args[1];
		String templateFile = args[2];
		String generatedFile = (args.length == 4 ? args[3] : "benerator.xml");
		
		// generate descriptor file
		TemplateInputReader reader = (TemplateInputReader) BeanUtil.newInstance(configParserClass);
		Context context = new DefaultContext();
		reader.parse(configFile, context);
		createDescriptor(context, templateFile, generatedFile);
		
		// run descriptor file
		runDescriptor(generatedFile);
	}
	
	private static void printHelpAndExit() {
		ConsoleInfoPrinter.printHelp(
				"The class " + TemplateRunner.class.getSimpleName(),
				"creates Benerator descriptor files from custom templates.",
				"It has the following parameters:",
				"<config_file> <config_parser_class> <template_file> [<generated_file>]",
				"	config_file:         Path of an individual data file to provide configuration",
				"	config_parser_class: Fully qualified name of a Java class which is able to parse the config_file",
				"	template file:       File path of a FreeMarker template to generate a Benerator descriptor file",
				"	generated_file:      File path of the generated Benerator descriptor file"
			);
		System.exit(-1);
	}

	private static void createDescriptor(Context context, String templateUri, String targetUri) {
		try {
			Script script = new FreeMarkerScriptFactory().readFile(templateUri);
			Writer out = new FileWriter(targetUri);
			script.execute(context, out);
		} catch (Exception e) {
			throw new ConfigurationError("Error generating descriptor file " + targetUri, e);
		}
	}
	
	private static void runDescriptor(String descriptorUri) throws IOException {
		Benerator.main(new String[] { descriptorUri });
	}
	
}
