/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.dbunit;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.commons.IOUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.ToStringConverter;
import org.databene.model.consumer.AbstractConsumer;
import org.databene.model.data.Entity;

/**
 * Exports entities in DbUnit XML file format.
 * @since 0.3.04
 * @author Volker Bergmann
 */
public class DbUnitEntityExporter extends AbstractConsumer<Entity> {
    
    // attributes ------------------------------------------------------------------------------------------------------

    private static final Log logger = LogFactory.getLog(DbUnitEntityExporter.class);

    private static final String DEFAULT_FILE_ENCODING = "UTF-8";
    private static final String DEFAULT_URI = "data.dbunit.xml";

    private String uri;
    private String encoding;

    private PrintWriter printer;

    // constructors ----------------------------------------------------------------------------------------------------

    public DbUnitEntityExporter() {
        this(DEFAULT_URI);
    }

    public DbUnitEntityExporter(String uri) {
        this(uri, DEFAULT_FILE_ENCODING);
    }

    public DbUnitEntityExporter(String uri, String encoding) {
        setUri(uri);
        setEncoding(encoding);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = (encoding != null ? encoding : SystemInfo.fileEncoding());
        if (this.encoding == null)
            this.encoding = DEFAULT_FILE_ENCODING;
    }

    // Consumer interface ----------------------------------------------------------------------------------------------

    public void startConsuming(Entity entity) {
        try {
            if (logger.isDebugEnabled())
                logger.debug("exporting " + entity);
            if (printer == null)
                initPrinter();
            printer.print("    <" + entity.getName());
            for (Map.Entry<String, Object> entry : entity.getComponents().entrySet()) {
                Object value = entry.getValue();
                if (value == null)
                    continue;
                String s = ToStringConverter.convert(value, null);
                if (s != null)
                	printer.print(' ' + entry.getKey() + "=\"" + s + '"');
            }
            printer.println("/>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void flush() {
        if (printer != null)
            printer.flush();
    }

    public void close() {
        if (printer != null) { 
            printer.print("</dataset>");
            printer.close();
        }
    }

// java.lang.String overrides --------------------------------------------------------------------------------------

    private void initPrinter() throws IOException {
        // create file and write header
        printer = IOUtil.getPrinterForURI(uri, encoding);
        printer.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
        printer.println("<dataset>");
    }

    public String toString() {
        return getClass().getSimpleName() + '[' + uri + ", " + encoding + "]";
    }
}
