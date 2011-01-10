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

import org.databene.commons.IOUtil;
import org.databene.commons.xml.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provides information about the Benerator version.<br/>
 * <br/>
 * Created at 22.06.2009 14:27:08
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class Version {

	private static final Logger LOGGER = LoggerFactory.getLogger(Version.class);
	
	private static final String VERSION_FILE_URI = "org/databene/benerator/version.txt";

	public static final String VERSION = readVersion();
	
	public static final String XML_PUBLIC_ID = "http://databene.org/benerator/" + VERSION;
	public static final String XML_HTTP_SYSTEM_ID = "http://databene.org/benerator-" + VERSION + ".xsd";
	public static final String XML_SCHEMA_PATH = "org/databene/benerator/benerator-" + VERSION + ".xsd";

	public static void main(String[] args) {
		System.out.println(VERSION);
	}

	private static String readVersion() {
        String version = "<unknown version>";
	    try {
	        if (IOUtil.isURIAvailable(VERSION_FILE_URI))
	        	version = IOUtil.getContentOfURI(VERSION_FILE_URI);                         // This works in Maven, but...
	        if (version.startsWith("${") || version.startsWith("<unknown")) {               // ...in Eclipse no filtering is applied,...
	        	LOGGER.warn("Version number file could not be found, falling back to POM"); // ...so we fetch it directly from the POM!
	    		Document doc = XMLUtil.parse("pom.xml");
	    		Element versionElement = XMLUtil.getChildElement(doc.getDocumentElement(), false, true, "version");
	    		version = versionElement.getTextContent();
	        }
        } catch (IOException e) {
	        LOGGER.error("Error reading version info file", e);
        }
		return version;
    }
	
}
