/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.main;

import org.databene.benerator.Version;
import org.databene.benerator.engine.DescriptorRunner;
import org.databene.commons.LogCategories;
import org.databene.commons.SystemInfo;
import org.databene.commons.VMInfo;
import org.databene.commons.ui.ConsoleInfoPrinter;
import org.databene.model.version.VersionNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

/**
 * Parses and executes a benerator setup file.<br/>
 * <br/>
 * Created: 14.08.2007 19:14:28
 * @author Volker Bergmann
 */
public class Benerator {
	
	public static final String LOCALE_VM_PARAM = "benerator.locale";
	
	private static final Logger logger = LoggerFactory.getLogger(Benerator.class);
	private static final Logger configLogger = LoggerFactory.getLogger(LogCategories.CONFIG);

	// methods ---------------------------------------------------------------------------------------------------------

	public static void main(String[] args) throws IOException {
		String filename = (args.length > 0 ? args[0] : "benerator.xml");
		configLogger.debug("Running benerator " + Version.VERSION + " with file " + filename);
		checkSystem();
        listScriptEngines();
		new DescriptorRunner(filename).run();
	}

	private static void listScriptEngines() {
		// check installed JSR 223 script engines
        ScriptEngineManager mgr = new ScriptEngineManager();
        List<ScriptEngineFactory> engineFactories = mgr.getEngineFactories();
        if (engineFactories.size() > 0) {
        	configLogger.debug("Installed JSR 223 Script Engines:");
			for (ScriptEngineFactory engineFactory : engineFactories) {
				configLogger.debug("- " + engineFactory.getEngineName() + engineFactory.getNames());
	        }
        }
	}

	private static void checkSystem() {
		configLogger.debug("Max heap size: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB");
		configLogger.debug("Java " + VMInfo.getJavaVersion());
		configLogger.debug(VMInfo.getJavaVmName() + " " + VMInfo.getJavaVmVersion() + " (" + VMInfo.getJavaVmVendor() + ")");
		configLogger.debug(SystemInfo.getOsName() + " " + SystemInfo.getOsVersion() + " (" + SystemInfo.getOsArchitecture() + ")");
		try {
			Class.forName("javax.script.ScriptEngine");
		} catch (ClassNotFoundException e) {
			ConsoleInfoPrinter.printHelp("You need to run benerator with Java 6 or greater!");
			if (SystemInfo.isMacOsx())
				ConsoleInfoPrinter.printHelp("Please check the reference manual for Java setup on Mac OS X.");
			System.exit(-1);
		}
		VersionNumber javaVersion = new VersionNumber(VMInfo.getJavaVersion());
		if (javaVersion.compareTo(new VersionNumber("1.6")) < 0)
			logger.warn("benerator is written for and tested under Java 6 - " +
					"you managed to set up JSR 226, but may face other problems.");
	}

}
