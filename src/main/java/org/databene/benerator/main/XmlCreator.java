/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.file.XMLFileGenerator;
import org.databene.commons.ArrayUtil;

/**
 * Main class for generating XML files from the command line.<br/><br/>
 * Created: 28.03.2008 16:52:49
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class XmlCreator {
    
    private static final Log logger = LogFactory.getLog(XmlCreator.class);

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            printHelp();
            System.exit(-1);
        }
        String schemaUri = args[0];
        String root = args[1];
        String pattern = args[2];
        long fileCount = Long.parseLong(args[3]);
        String[] propertiesFiles = (args.length > 4 ? ArrayUtil.copyOfRange(args, 4, args.length - 4) : new String[0]);
        if (logger.isDebugEnabled()) {
        	if (fileCount > 1)
        		logger.debug("Creating " + fileCount + " XML files for schema " + schemaUri + " with root " + root + " and pattern " + pattern);
        	else
        		logger.debug("Creating XML file " + MessageFormat.format(pattern, fileCount) + " for schema " + schemaUri + " with root " + root);
        }
        XMLFileGenerator fileGenerator = new XMLFileGenerator(schemaUri, root, pattern, propertiesFiles);
        for (long i = 0; i < fileCount && fileGenerator.available(); i++) {
            File file = fileGenerator.generate();
            logger.info("created file: " + file);
        }
    }

    private static void printHelp() {
        System.out.println("Invalid parameters");
        System.out.println("parameters: schemaUri root fileNamePattern count [propertiesFilenames]");
    }

}
