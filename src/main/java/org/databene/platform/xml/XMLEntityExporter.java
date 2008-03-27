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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.databene.commons.ConfigurationError;
import org.databene.commons.SystemInfo;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.consumer.AbstractConsumer;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.TypeDescriptor;

/**
 * Writes Entities to an XML file.<br/><br/>
 * Created: 20.02.2008 15:39:23
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class XMLEntityExporter extends AbstractConsumer<Entity> {

    private static final Log logger = LogFactory.getLog(XMLEntityExporter.class);
    
    // defaults --------------------------------------------------------------------------------------------------------
    
    private static String DEFAULT_INDENT_STEP = "\t";
    private static final String DEFAULT_ENCODING  = SystemInfo.fileEncoding();
    private static final String DEFAULT_URI       = "export.xml";

    // attributes ------------------------------------------------------------------------------------------------------

    private String uri;
    private String encoding;

    private PrintWriter printer;
    private String indent;
    private Stack<Boolean> childFlag;
    private XMLPath path;

    // constructors ----------------------------------------------------------------------------------------------------

    public XMLEntityExporter() {
        this(DEFAULT_URI);
    }
    
    public XMLEntityExporter(String uri) {
        this(uri, DEFAULT_ENCODING);
    }

    public XMLEntityExporter(String uri, String encoding) {
        this.uri = uri;
        this.encoding = encoding;
        this.indent = "";
        this.childFlag = new Stack<Boolean>();
        this.path = null;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    // Consumer interface ----------------------------------------------------------------------------------------------

    public void startConsuming(Entity entity) {
        if (logger.isDebugEnabled())
            logger.debug("startConsuming(" + entity + ')');
        String entityName = entity.getName();
        if (path == null) {
            path = new XMLPath(entityName);
        }
        do {
            InstanceDescriptor[] allowedEntities = path.allowedChildren();
//            System.out.println("allowed: " + ArrayFormat.format(allowedEntities) + " -- " + "fd: " + entityName);
            for (InstanceDescriptor allowedEntity : allowedEntities) {
                String allowedName = allowedEntity.getName();
                if (allowedName.equalsIgnoreCase(entityName)) {
                    renderElementStart(entity);
                    return;
                } else if (path.isKept(allowedName))
                    renderSimpleElement(allowedName, path.getKept(allowedName));
                else
                    throw new ConfigurationError();
            }
        } while (true);
    }

    @Override
    public void finishConsuming(Entity entity) {
        if (logger.isDebugEnabled())
            logger.debug("finishConsuming(" + entity + ')');
        InstanceDescriptor[] allowedEntities = path.allowedChildren();
//      System.out.println("allowed: " + ArrayFormat.format(allowedEntities) + " -- " + "fd: " + entityName);
        for (InstanceDescriptor allowedEntity : allowedEntities) {
            String allowedName = allowedEntity.getName();
            if (path.isKept(allowedName))
                renderSimpleElement(allowedName, path.getKept(allowedName));
        }
        Boolean hadChildren = childFlag.pop();
        indent = indent.substring(0, indent.length() - DEFAULT_INDENT_STEP.length());
        if (hadChildren)
            printer.println(indent + "</" + entity.getName() + ">");
        else
            printer.println("/>");
        super.finishConsuming(entity);
        path.closeElement(entity);
    }

    public void flush() {
        if (printer != null)
            printer.flush();
    }

    public void close() {
        if (printer != null) {
            printer.close();
            printer = null;
        }
    }

    // private helpers -------------------------------------------------------------------------------------------------
    
    private void renderSimpleElement(String name, Object value) {
        markChildren();
        path.emptyElement(name);
        printer.println(indent + '<' + name + '>' + value + "</" + name + '>');
    }

    private void markChildren() {
        if (!childFlag.isEmpty()) {
            Boolean hasSiblings = childFlag.pop();
            if (!hasSiblings)
                printer.println(">");
            childFlag.push(Boolean.TRUE);
        }
    }

    private void renderElementStart(Entity entity) {
        path.openElement(entity);
        try {
            if (printer == null)
                initPrinter();
            markChildren();
            childFlag.push(Boolean.FALSE);
            printer.print(indent + '<' + entity.getName());
            renderAttributes(entity);
            indent += DEFAULT_INDENT_STEP;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void renderAttributes(Entity entity) {
        ComplexTypeDescriptor descriptor = entity.getDescriptor();
        for (Map.Entry<String, Object> entry : entity.getComponents().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null && hasSimpleType(value)) 
                if (isXmlElement(descriptor, key))
                    path.keep(key, value);
                else
                    printer.print(' ' + key + "=\"" + value + '"');
        }
    }

    private boolean hasSimpleType(Object value) {
        return !(value instanceof Entity) && !value.getClass().isArray();
    }

    private boolean isXmlElement(ComplexTypeDescriptor elementDescriptor, String componentName) {
        ComponentDescriptor componentDescriptor = elementDescriptor.getComponent(componentName);
        if (componentDescriptor == null)
            return false; // assume it's an attribute (applicable for e.g. xmlns)
        TypeDescriptor type = componentDescriptor.getType();
        if (type instanceof ComplexTypeDescriptor)
            return true;
        Object style = componentDescriptor.getPSInfo(XMLSchemaDescriptorProvider.XML_REPRESENTATION);
        return "element".equals(style);
    }
    
    private void initPrinter() throws IOException {
        logger.debug("Initializing " + uri);
        // create file
        printer = XMLUtil.createXMLFile(uri, encoding);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + '[' + uri + ']';
    }

}
