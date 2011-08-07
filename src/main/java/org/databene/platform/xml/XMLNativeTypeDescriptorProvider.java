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

package org.databene.platform.xml;

import org.databene.model.data.DefaultDescriptorProvider;
import org.databene.model.data.SimpleTypeDescriptor;

/**
 * Provides descriptors for the simple types predefined in the XML Schema definition.<br/><br/>
 * Created: 08.03.2008 11:04:04
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class XMLNativeTypeDescriptorProvider extends DefaultDescriptorProvider {

    private static final String REGEX_YEAR = "\\-?\\d{4,}";
    private static final String REGEX_MONTH_NUMBER = "(0[1-9]|1[0-2])";
    private static final String REGEX_DAY_OF_MONTH = "(0[1-9]|[1-2][0-9]|3[01])";
    private static final String REGEX_TIMEZONE = "(Z|[+\\-]\\d{2}:\\d{2})";
    private static final String REGEX_OPTIONAL_TIMEZONE = REGEX_TIMEZONE + '?';

    public XMLNativeTypeDescriptorProvider(String id) {
        super(id);

        // schema types that resemble the benerator primitives
        addDescriptor(new SimpleTypeDescriptor("string", "string"));
        
        addDescriptor(new SimpleTypeDescriptor("boolean", "boolean"));
        
        addDescriptor(new SimpleTypeDescriptor("byte", "byte"));
        addDescriptor(new SimpleTypeDescriptor("short", "short"));
        addDescriptor(new SimpleTypeDescriptor("int", "int"));
        addDescriptor(new SimpleTypeDescriptor("long", "long"));
        
        addDescriptor(new SimpleTypeDescriptor("float", "float"));
        addDescriptor(new SimpleTypeDescriptor("double", "double"));
        
        addDescriptor(new SimpleTypeDescriptor("date", "date"));
        addDescriptor(new SimpleTypeDescriptor("time", "time"));
        
        // schema specific types
        addDescriptor(new SimpleTypeDescriptor("integer", "int"));
        addDescriptor(new SimpleTypeDescriptor("nonPositiveInteger", "int").withMin("-2147483648").withMax("0"));
        addDescriptor(new SimpleTypeDescriptor("negativeInteger", "int").withMin("-2147483648").withMax("-1"));
        addDescriptor(new SimpleTypeDescriptor("nonNegativeInteger", "int").withMin("0"));
        addDescriptor(new SimpleTypeDescriptor("positiveInteger", "int").withMin("1"));
                
        addDescriptor(new SimpleTypeDescriptor("unsignedLong", "big_decimal").withMin("0").withMax("9223372036854775807")); // this is only Long.MAX_VALUE
        addDescriptor(new SimpleTypeDescriptor("unsignedInt", "long").withMin("0").withMax("4294967295"));
        addDescriptor(new SimpleTypeDescriptor("unsignedShort", "int").withMin("0").withMax("32767"));
        addDescriptor(new SimpleTypeDescriptor("unsignedByte", "short").withMin("0").withMax("256"));

        addDescriptor(new SimpleTypeDescriptor("decimal", "big_decimal"));
        addDescriptor(new SimpleTypeDescriptor("precisionDecimal", "big_decimal"));

        addDescriptor(new SimpleTypeDescriptor("dateTime", "timestamp"));
        
        addDescriptor(new SimpleTypeDescriptor("duration", "string").withPattern("\\-?P(\\d+Y)?(\\d+M)?(\\d+D)?(T(\\d+H)?(\\d+M)?(\\d+S)?)?"));
        addDescriptor(new SimpleTypeDescriptor("gYearMonth", "string").withPattern(REGEX_YEAR + "\\-" + REGEX_MONTH_NUMBER + REGEX_OPTIONAL_TIMEZONE));
        addDescriptor(new SimpleTypeDescriptor("gYear", "string").withPattern(REGEX_YEAR + REGEX_OPTIONAL_TIMEZONE));
        addDescriptor(new SimpleTypeDescriptor("gMonthDay", "string").withPattern(REGEX_MONTH_NUMBER + "\\-" + REGEX_DAY_OF_MONTH + REGEX_OPTIONAL_TIMEZONE));
        addDescriptor(new SimpleTypeDescriptor("gDay", "string").withPattern(REGEX_DAY_OF_MONTH + REGEX_OPTIONAL_TIMEZONE));
        addDescriptor(new SimpleTypeDescriptor("gMonth", "int").withPattern(REGEX_MONTH_NUMBER + REGEX_OPTIONAL_TIMEZONE));
        
        addDescriptor(new SimpleTypeDescriptor("hexBinary", "string").withPattern("([0-9a-fA-F]{2})*"));
        addDescriptor(new SimpleTypeDescriptor("base64Binary", "string").withPattern("[a-zA-Z0-9+/= ]*"));

        addDescriptor(new SimpleTypeDescriptor("anyURI", "string"));
        addDescriptor(new SimpleTypeDescriptor("QName", "string"));
        addDescriptor(new SimpleTypeDescriptor("NOTATION", "string"));
        addDescriptor(new SimpleTypeDescriptor("normalizedString", "string"));
        addDescriptor(new SimpleTypeDescriptor("token", "string"));
        addDescriptor(new SimpleTypeDescriptor("language", "string"));
        addDescriptor(new SimpleTypeDescriptor("NMTOKEN", "string").withPattern("[A-Za-z:_\\-\\.0-9]*"));
        addDescriptor(new SimpleTypeDescriptor("NMTOKENS", "string"));
        addDescriptor(new SimpleTypeDescriptor("Name", "string"));
        addDescriptor(new SimpleTypeDescriptor("NCName", "string"));
        addDescriptor(new SimpleTypeDescriptor("ID", "string")); // TODO v0.8 support this in XML schema generation
        addDescriptor(new SimpleTypeDescriptor("IDREFS", "string"));
        addDescriptor(new SimpleTypeDescriptor("ENTITY", "string"));
        addDescriptor(new SimpleTypeDescriptor("ENTITIES", "string"));
    }

}
