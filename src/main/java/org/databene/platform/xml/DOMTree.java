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

package org.databene.platform.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.storage.AbstractStorageSystem;
import org.databene.commons.CollectionUtil;
import org.databene.commons.Context;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.collection.OrderedNameMap;
import org.databene.commons.context.ContextAware;
import org.databene.commons.xml.XMLUtil;
import org.databene.commons.xml.XPathUtil;
import org.databene.formats.DataSource;
import org.databene.formats.util.DataSourceFromIterable;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.TypeDescriptor;
import org.databene.script.PrimitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Loads an XML document into a DOM structure, supports queries for data and updates. 
 * When an instance is {@link #close()}d, the tree content is written to the {@link #outputUri}.<br/><br/>
 * Created: 08.01.2014 15:27:38
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class DOMTree extends AbstractStorageSystem implements ContextAware {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DOMTree.class);
	
	private String id;
	private String inputUri;
	private String outputUri;
	private boolean namespaceAware;
	
	private Context context;
	private Document document;
	private OrderedNameMap<ComplexTypeDescriptor> types;

	
	public DOMTree() {
		this(null, null);
	}

	public DOMTree(String inOutUri, BeneratorContext context) {
		this.id = inOutUri;
		this.inputUri = inOutUri;
		this.outputUri = inOutUri;
		this.namespaceAware = true;
		this.document = null;
		this.types = OrderedNameMap.createCaseInsensitiveMap();
		setContext(context);
	}

	@Override
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getInputUri() {
		return inputUri;
	}
	
	public void setInputUri(String inputUri) {
		this.inputUri = inputUri;
	}
	
	public String getOutputUri() {
		return outputUri;
	}
	
	public void setOutputUri(String outputUri) {
		this.outputUri = outputUri;
	}
	
	public boolean isNamespaceAware() {
		return namespaceAware;
	}
	
	public void setNamespaceAware(boolean namespaceAware) {
		this.namespaceAware = namespaceAware;
	}
	
	@Override
	public void setContext(Context context) {
		this.context = context;
		if (context instanceof BeneratorContext)
			setDataModel(((BeneratorContext) context).getDataModel());
	}
	
	@Override
	public DataSource<Entity> queryEntities(String type, String selector, Context context) {
		beInitialized();
		LOGGER.debug("queryEntities({}, {}, context)", type, selector);
		try {
			NodeList nodes = XPathUtil.queryNodes(document, selector);
			LOGGER.debug("queryEntities() found {} results", nodes.getLength());
			List<Entity> list = new ArrayList<Entity>(nodes.getLength());
			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
				Entity entity = XMLPlatformUtil.convertElement2Entity(element, this);
				list.add(entity);
			}
			return new DataSourceFromIterable<Entity>(list, Entity.class);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("Error querying " + (type != null ? type : "") + " elements with xpath: " + selector, e);
		}
	}
	
	@Override
	public DataSource<?> queryEntityIds(String type, String selector, Context context) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support queries for entity ids");
	}

	@Override
	public DataSource<?> query(String selector, boolean simplify, Context context) {
		beInitialized();
		LOGGER.debug("query({}, {}, context)", selector, simplify);
		try {
			NodeList nodes = XPathUtil.queryNodes(document, selector);
			LOGGER.debug("query() found {} results", nodes.getLength());
			List<Object> list = new ArrayList<Object>(nodes.getLength());
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				list.add(node.getTextContent());
			}
			return new DataSourceFromIterable<Object>(list, Object.class);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("Error querying items with xpath: " + selector, e);
		}
	}

	@Override
	public void store(Entity entity) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support storing entities");
	}

	@Override
	public void update(Entity entity) {
		beInitialized();
		if (entity instanceof XmlEntity) {
			XMLPlatformUtil.mapEntityToElement(entity, ((XmlEntity) entity).getSourceElement());
		} else {
			throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot update entities from other sources");
		}
	}

	@Override
	public void flush() {
		// nothing to do
	}

	@Override
	public void close() {
		if (document != null) {
			save();
		} else {
			// if document is null, loading has failed and there already has been an error message
		}
	}

	@Override
	public TypeDescriptor[] getTypeDescriptors() {
		return CollectionUtil.toArray(types.values(), TypeDescriptor.class);
	}

	@Override
	public TypeDescriptor getTypeDescriptor(String typeName) {
		if (PrimitiveType.getInstance(typeName) != null)
			return null;
		ComplexTypeDescriptor type = types.get(typeName);
		if (type == null) {
			type = new ComplexTypeDescriptor(typeName, this);
			types.put(typeName, type);
		}
		return type;
	}
	
	public void save() {
		try {
			String encoding = normalizeEncoding(document.getInputEncoding());
			File uri = new File(resolveUri(outputUri));
			XMLUtil.saveDocument(document, uri, encoding);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error writing DOMTree to " + outputUri, e);
		}
	}
	
	
	
	// private helpers --------------------------------------------------------------------------------------------------------------

	private void beInitialized() {
		if (this.document == null)
			init();
	}

	private void init() {
		try {
			this.document = XMLUtil.parse(resolveUri(inputUri), namespaceAware, null, null, null);
			System.out.println(this.document.getDocumentElement().getNamespaceURI());
		} catch (IOException e) {
			throw new RuntimeException("Error parsing " + inputUri, e);
		}
	}

	private String resolveUri(String uri) {
		return (context instanceof BeneratorContext ? ((BeneratorContext) context).resolveRelativeUri(uri) : uri);
	}
	
	private static String normalizeEncoding(String encoding) {
		if (encoding.startsWith("UTF-16"))
			encoding = "UTF-16";
		return encoding;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + inputUri + (NullSafeComparator.equals(inputUri, outputUri) ? "" : " -> " + outputUri) + "]";
	}
	
}
