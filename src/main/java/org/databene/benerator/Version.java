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

package org.databene.benerator;

import java.io.IOException;
import java.util.Map;

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
	
	private static final String VERSION_FILE_URI = "org/databene/benerator/version.properties";

	private static final Version INSTANCE = readVersion();
	public static final String VERSION = INSTANCE.beneratorVersion;
	
	public static final String XML_PUBLIC_ID = "http://databene.org/benerator/" + VERSION;
	public static final String XML_HTTP_SYSTEM_ID = "http://databene.org/benerator-" + VERSION + ".xsd";
	public static final String XML_SCHEMA_PATH = "org/databene/benerator/benerator-" + VERSION + ".xsd";

	public static void main(String[] args) {
		System.out.println("Benerator " + VERSION + " uses");
		System.out.println("- DB Sanity " + INSTANCE.getDbsanityVersion());
		System.out.println("- ContiPerf " + INSTANCE.getContiperfVersion());
		System.out.println("- jdbacl " + INSTANCE.getJdbaclVersion());
		System.out.println("- gui " + INSTANCE.getGuiVersion());
		System.out.println("- webdecs " + INSTANCE.getWebdecsVersion());
		System.out.println("- commons " + INSTANCE.getCommonsVersion());
	}
	
	public static Version instance() {
		return INSTANCE;
	}

	private String commonsVersion;
	private String webdecsVersion;
	private String guiVersion;
	private String contiperfVersion;
	private String jdbaclVersion;
	private String dbsanityVersion;
	private String beneratorVersion;

	public String getCommonsVersion() {
		return commonsVersion;
	}

	public String getWebdecsVersion() {
		return webdecsVersion;
	}

	public String getGuiVersion() {
		return guiVersion;
	}

	public String getContiperfVersion() {
		return contiperfVersion;
	}

	public String getJdbaclVersion() {
		return jdbaclVersion;
	}

	public String getDbsanityVersion() {
		return dbsanityVersion;
	}

	public String getBeneratorVersion() {
		return beneratorVersion;
	}

	private static Version readVersion() {
		Version version = new Version();
		version.beneratorVersion = "<unknown version>";
	    try {
	        if (IOUtil.isURIAvailable(VERSION_FILE_URI)) {			// This works in Maven, but...
	    		Map<String, String> properties = IOUtil.readProperties(VERSION_FILE_URI);
	        	version.beneratorVersion = properties.get("benerator_version");								
	        	version.dbsanityVersion  = properties.get("dbsanity_version");								
	        	version.jdbaclVersion    = properties.get("jdbacl_version");								
	        	version.contiperfVersion = properties.get("contiperf_version");								
	        	version.guiVersion       = properties.get("gui_version");								
	        	version.webdecsVersion   = properties.get("webdecs_version");								
	        	version.commonsVersion   = properties.get("commons_version");								
	        }
	        if (version.beneratorVersion.startsWith("${") || version.beneratorVersion.startsWith("<unknown")) { // ...in Eclipse no filtering is applied,...
	        	LOGGER.warn("Version number file could not be found, falling back to POM"); // ...so we fetch it directly from the POM!
	    		Document doc = XMLUtil.parse("pom.xml");
	    		Element versionElement = XMLUtil.getChildElement(doc.getDocumentElement(), false, true, "version");
	    		Element propsElement = XMLUtil.getChildElement(doc.getDocumentElement(), false, true, "properties");
	    		version.beneratorVersion = versionElement.getTextContent();
	    		version.dbsanityVersion  = property(propsElement, "dbsanity_version");
	    		version.jdbaclVersion    = property(propsElement, "jdbacl_version");
	    		version.contiperfVersion = property(propsElement, "contiperf_version");
	    		version.guiVersion       = property(propsElement, "gui_version");
	    		version.webdecsVersion   = property(propsElement, "webdecs_version");
	    		version.commonsVersion   = property(propsElement, "commons_version");
	        }
        } catch (IOException e) {
	        LOGGER.error("Error reading version info file", e);
        }
		return version;
    }
	
	private static String property(Element parent, String name) {
		return XMLUtil.getChildElement(parent, false, true, name).getTextContent();
	}
	
	@Override
	public String toString() {
		return beneratorVersion;
	}
	
}
