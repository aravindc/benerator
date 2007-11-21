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

package org.databene.platform.flat;

import org.databene.model.Processor;
import org.databene.model.Converter;
import org.databene.model.data.Entity;
import org.databene.model.format.PadFormat;
import org.databene.model.format.Alignment;
import org.databene.model.converter.ConverterChain;
import org.databene.model.converter.FormatFormatConverter;
import org.databene.model.converter.AccessingConverter;
import org.databene.model.data.ComponentAccessor;
import org.databene.document.flat.FlatFileColumnDescriptor;
import org.databene.commons.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.ParseException;

/**
 * Exports Entities to flat files.<br/>
 * <br/>
 * Created: 26.08.2007 06:17:41
 */
public class FlatFileEntityExporter implements Processor<Entity> {

    private static final Log logger = LogFactory.getLog(FlatFileEntityExporter.class);

    private String uri;
    private Converter<Entity, String> converters[];

    private PrintWriter printer;

    public FlatFileEntityExporter() {
        this(null, null);
    }

    public FlatFileEntityExporter(String uri, String propertyFormatList) {
        super();
        this.uri = uri;
        setProperties(propertyFormatList);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setProperties(String propertyFormatList) { // TODO v0.4 simplify by FlatFileUtil
        if (propertyFormatList == null) {
            converters = null;
            return;
        }
        try {
            String[] propertyFormats = StringUtil.tokenize(propertyFormatList, ',');
            this.converters = new Converter[propertyFormats.length];
            for (int i = 0; i < propertyFormats.length; i++) {
                String propertyFormat = propertyFormats[i];
                int lbIndex = propertyFormat.indexOf('[');
                if (lbIndex < 0)
                    throw new ConfigurationError("'[' expected in property format descriptor '" + propertyFormat + "'");
                int rbIndex = propertyFormat.indexOf(']');
                if (rbIndex < 0)
                    throw new ConfigurationError("']' expected in property format descriptor '" + propertyFormat + "'");
                String propertyName = propertyFormat.substring(0, lbIndex);
                // parse width
                ParsePosition pos = new ParsePosition(lbIndex + 1);
                int width = (int) ParseUtil.parseNonNegativeInteger(propertyFormat, pos);
                // parse fractionDigits
                int minFractionDigits = 0;
                int maxFractionDigits = 2;
                if (pos.getIndex() < rbIndex && propertyFormat.charAt(pos.getIndex()) == '.') {
                    pos.setIndex(pos.getIndex() + 1);
                    minFractionDigits = (int) ParseUtil.parseNonNegativeInteger(propertyFormat, pos);
                    maxFractionDigits = minFractionDigits;
                }
                // parse alignment
                Alignment alignment = Alignment.LEFT;
                if (pos.getIndex() < rbIndex) {
                    char alignmentCode = propertyFormat.charAt(pos.getIndex());
                    switch (alignmentCode) {
                        case 'l' : alignment = Alignment.LEFT; break;
                        case 'r' : alignment = Alignment.RIGHT; break;
                        case 'c' : alignment = Alignment.CENTER; break;
                        default: throw new ConfigurationError("Illegal alignment code '" + alignmentCode + "' in property format descriptor '" + propertyFormat + "'");
                    }
                    pos.setIndex(pos.getIndex() + 1);
                }
                // parse pad char
                char padChar = ' ';
                if (pos.getIndex() < rbIndex)
                    padChar = propertyFormat.charAt(pos.getIndex());
                assert pos.getIndex() == rbIndex;
                FlatFileColumnDescriptor descriptor = new FlatFileColumnDescriptor(propertyName, width, alignment, padChar);
                this.converters[i] = new ConverterChain<Entity, String>(
                    new AccessingConverter<Entity, Object>(Object.class, new ComponentAccessor(descriptor.getName())),
                    new FormatFormatConverter(
                        new PadFormat(descriptor.getWidth(), minFractionDigits, maxFractionDigits, descriptor.getAlignment(), padChar)
                    )
                );
            }
        } catch (ParseException e) {
            throw new ConfigurationError("Invalid format: " + propertyFormatList, e);
        }
    }

    // Processor interface ---------------------------------------------------------------------------------------------

    public void process(Entity entity) {
        try {
            if (printer == null) {
                // it's the first call, we need to create the PrintWriter
                if (this.uri == null)
                    throw new ConfigurationError("Property 'uri' not set on bean " + getClass().getName());
                if (this.converters == null)
                    throw new ConfigurationError("Property 'properties' not set on bean " + getClass().getName());
                printer = new PrintWriter(new FileWriter(uri));
            }
            if (logger.isDebugEnabled())
                logger.debug("exporting " + entity);
            for (Converter<Entity, String> converter : converters) {
                printer.print(converter.convert(entity));
            }
            printer.println();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void flush() {
        if (printer != null)
            printer.flush();
    }

    public void close() {
        IOUtil.close(printer);
    }

    // java.lang.Object overrrides -------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + '[' + ArrayFormat.format() + ']';
    }
}
