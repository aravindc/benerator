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

package org.databene.platform.fixedwidth;

import java.io.PrintWriter;
import java.text.ParseException;
import java.text.ParsePosition;

import org.databene.commons.Assert;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.ParseUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.converter.AccessingConverter;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.FormatFormatConverter;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.format.Alignment;
import org.databene.commons.format.PadFormat;
import org.databene.document.fixedwidth.FixedWidthColumnDescriptor;
import org.databene.model.data.ComponentAccessor;
import org.databene.model.data.Entity;

/**
 * Formats Entities' attributes as a fixed-width table.<br/><br/>
 * Created: 20.02.2014 14:03:25
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class FWRecordFormatter {

    private Converter<Entity, String> converters[];
    private ToStringConverter plainConverter;

    public FWRecordFormatter(String columnFormatList, ToStringConverter plainConverter) {
        Assert.notNull(columnFormatList, "columnFormatList");
        Assert.notNull(plainConverter, "plainConverter");
        this.plainConverter = plainConverter;
        try {
            String[] columnFormats = StringUtil.tokenize(columnFormatList, ',');
            this.converters = new Converter[columnFormats.length];
            for (int i = 0; i < columnFormats.length; i++) {
                String columnFormat = columnFormats[i];
                int lbIndex = columnFormat.indexOf('[');
                if (lbIndex < 0)
                    throw new ConfigurationError("'[' expected in column format descriptor '" + columnFormat + "'");
                int rbIndex = columnFormat.indexOf(']');
                if (rbIndex < 0)
                    throw new ConfigurationError("']' expected in column format descriptor '" + columnFormat + "'");
                String columnName = columnFormat.substring(0, lbIndex);
                // parse width
                ParsePosition pos = new ParsePosition(lbIndex + 1);
                int width = (int) ParseUtil.parseNonNegativeInteger(columnFormat, pos);
                // parse fractionDigits
                int minFractionDigits = 0;
                int maxFractionDigits = 2;
                if (pos.getIndex() < rbIndex && columnFormat.charAt(pos.getIndex()) == '.') {
                    pos.setIndex(pos.getIndex() + 1);
                    minFractionDigits = (int) ParseUtil.parseNonNegativeInteger(columnFormat, pos);
                    maxFractionDigits = minFractionDigits;
                }
                // parse alignment
                Alignment alignment = Alignment.LEFT;
                if (pos.getIndex() < rbIndex) {
                    char alignmentCode = columnFormat.charAt(pos.getIndex());
                    switch (alignmentCode) {
                        case 'l' : alignment = Alignment.LEFT; break;
                        case 'r' : alignment = Alignment.RIGHT; break;
                        case 'c' : alignment = Alignment.CENTER; break;
                        default: throw new ConfigurationError("Illegal alignment code '" + alignmentCode + "'" +
                        		" in colun format descriptor '" + columnFormat + "'");
                    }
                    pos.setIndex(pos.getIndex() + 1);
                }
                // parse pad char
                char padChar = ' ';
                if (pos.getIndex() < rbIndex) {
                    padChar = columnFormat.charAt(pos.getIndex());
                    pos.setIndex(pos.getIndex() + 1);
                }
                assert pos.getIndex() == rbIndex;
                FixedWidthColumnDescriptor descriptor = new FixedWidthColumnDescriptor(columnName, width, alignment, padChar);
                PadFormat format = new PadFormat(descriptor.getWidth(), minFractionDigits, maxFractionDigits, descriptor.getAlignment(), padChar);
                ConverterChain<Entity, String> chain = new ConverterChain<Entity, String>();
                chain.addComponent(new AccessingConverter<Entity, Object>(Entity.class, Object.class, new ComponentAccessor(descriptor.getName())));
                if (format.getMinimumFractionDigits() == 0)
                	chain.addComponent(plainConverter);
				chain.addComponent(new FormatFormatConverter(String.class, format, true));
                this.converters[i] = chain;
            }
        } catch (ParseException e) {
            throw new ConfigurationError("Invalid column definition: " + columnFormatList, e);
        }
    }
    
    public void format(Entity entity, PrintWriter printer) {
        for (Converter<Entity, String> converter : converters)
            printer.print(converter.convert(entity));
    }
    
}
