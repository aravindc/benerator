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

package org.databene.platform.script;

import org.databene.model.consumer.AbstractConsumer;
import org.databene.model.data.Entity;
import org.databene.script.ScriptedDocumentWriter;
import org.databene.commons.ConfigurationError;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Script based entity exporter.
 * Three scripts may be combined for formatting header, generated document part(s) and footer<br/>
 * <br/>
 * Created: 01.09.2007 18:05:04
 * @author Volker Bergmann
 */
public class ScriptedEntityExporter extends AbstractConsumer<Entity> {

    private static final Log logger = LogFactory.getLog(ScriptedEntityExporter.class);

    private String uri;
    private String headerScript;
    private String partScript;
    private String footerScript;

    private ScriptedDocumentWriter<Entity> writer;

    // constructors ----------------------------------------------------------------------------------------------------

    public ScriptedEntityExporter() {
        this(null, null);
    }

    public ScriptedEntityExporter(String uri, String partScript) {
        this(uri, null, partScript, null);
    }

    public ScriptedEntityExporter(String uri, String headerScript, String partScript, String footerScript) {
        this.uri = uri;
        this.headerScript = headerScript;
        this.partScript = partScript;
        this.footerScript = footerScript;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getHeaderScript() {
        return headerScript;
    }

    public void setHeaderScript(String headerScript) {
        this.headerScript = headerScript;
    }

    public String getPartScript() {
        return partScript;
    }

    public void setPartScript(String partScript) {
        this.partScript = partScript;
    }

    public String getFooterScript() {
        return footerScript;
    }

    public void setFooterScript(String footerScript) {
        this.footerScript = footerScript;
    }

    // Consumer interface ----------------------------------------------------------------------------------------------

    public void startConsuming(Entity entity) {
        try {
            if (writer == null) {
                writer = new ScriptedDocumentWriter<Entity>(
                        new FileWriter(uri), headerScript, partScript, footerScript);
            }
            if (logger.isDebugEnabled())
                logger.debug("Exporting " + entity);
            writer.writeElement(entity);
        } catch (IOException e) {
            throw new ConfigurationError(e);
        }
    }

    public void flush() {
        // ScriptedDocumentWriter does not support flushing
    }

    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                logger.error(e, e);
            }
        }
    }

}
