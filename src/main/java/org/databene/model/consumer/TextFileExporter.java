/*
 * (c) Copyright 2008, 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.model.consumer;

import java.io.IOException;
import java.io.PrintWriter;

import org.databene.commons.ConfigurationError;
import org.databene.commons.IOUtil;
import org.databene.commons.SystemInfo;

/**
 * Parent class for Exporters that export data to a text file.<br/>
 * <br/>
 * Created: 11.07.2008 09:50:46
 * @since 0.5.4
 * @author Volker Bergmann
 */
public abstract class TextFileExporter<E> extends FormattingConsumer<E> {

    private static final String DEFAULT_ENCODING = SystemInfo.fileEncoding();
    private static final String DEFAULT_LINE_SEPARATOR = SystemInfo.lineSeparator();

    // attributes ------------------------------------------------------------------------------------------------------

    protected String uri;
    protected String encoding;
    protected String lineSeparator;
    protected boolean append;

    protected PrintWriter printer;

    // constructors ----------------------------------------------------------------------------------------------------

    public TextFileExporter(String uri, String encoding, String lineSeparator) {
    	this.uri = uri;
        this.encoding = (encoding != null ? encoding : DEFAULT_ENCODING);
        this.lineSeparator = (lineSeparator != null ? lineSeparator : DEFAULT_LINE_SEPARATOR);
        this.append = false;
    }
    
    // callback interface for child classes ----------------------------------------------------------------------------

    protected abstract void postInitPrinter();

    protected abstract void startConsumingImpl(E data);

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
        this.encoding = encoding;
    }

    public String getLineSeparator() {
		return lineSeparator;
	}

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}
	
	public boolean isAppend() {
		return append;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}

    // Consumer interface ----------------------------------------------------------------------------------------------

	public void startConsuming(E data) {
        try {
            if (printer == null)
                initPrinter();
            startConsumingImpl(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	public void flush() {
        if (printer != null)
            printer.flush();
    }

    @Override
	public void close() {
        if (printer != null)
            printer.close();
    }

    // private helpers -------------------------------------------------------------------------------------------------
    
    protected void initPrinter() throws IOException {
        if (uri == null)
            throw new ConfigurationError("Property 'uri' not set on bean " + getClass().getName());
        printer = IOUtil.getPrinterForURI(uri, encoding, append, lineSeparator);
        postInitPrinter();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public String toString() {
        return getClass().getSimpleName() + "[" + uri + "]";
    }

}
