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

package org.databene.benerator;

import java.io.IOException;

import org.databene.commons.ConfigurationError;
import org.databene.commons.IOUtil;

/**
 * Provides information about the Benerator version.<br/>
 * <br/>
 * Created at 22.06.2009 14:27:08
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class Version {

	public static final String VERSION = readVersion();
	public static final String XML_PUBLIC_ID = "http://databene.org/benerator/" + VERSION;
	public static final String XML_HTTP_SYSTEM_ID = "http://databene.org/benerator-" + VERSION + ".xsd";
	public static final String XML_SCHEMA_PATH = "org/databene/benerator/benerator-" + VERSION + ".xsd";

	public static void main(String[] args) {
		System.out.println(VERSION);
	}

	private static String readVersion() {
	    try {
	        String version = IOUtil.getContentOfURI("org/databene/benerator/version.txt");
	        if (version.startsWith("${")) // in Eclipse, the version is not resolved
	        	version = "0.6.3"; // TODO v0.6.4 resolve version automatically in Eclipse
			return version;
        } catch (IOException e) {
	        throw new ConfigurationError("Error reading version info file", e);
        }
    }
	
}
