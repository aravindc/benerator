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

package org.databene.platform.template;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.databene.benerator.Consumer;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.IOUtil;
import org.databene.commons.ProgrammerError;
import org.databene.commons.accessor.FeatureAccessor;
import org.databene.commons.context.ContextAware;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.mutator.AnyMutator;
import org.databene.formats.script.Script;
import org.databene.formats.script.ScriptException;
import org.databene.formats.script.ScriptUtil;
import org.databene.model.data.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exports generated data using template files, 
 * for example based on the FreeMarker Template Language.<br/><br/>
 * Created: 27.06.2014 16:50:44
 * @since 0.9.7
 * @author Volker Bergmann
 */

public class TemplateFileEntityExporter implements Consumer, ContextAware {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateFileEntityExporter.class);
	
	
	// attributes ------------------------------------------------------------------------------------------------------
	
	private String templateUri;
	private String uri;
	private String encoding;
	private Class<? extends TemplateRecord> recordType;
	
	private TemplateRecord root;
	private Stack<TemplateRecord> stack;

	private Context context;
	
	
	// constructors ----------------------------------------------------------------------------------------------------
	
	public TemplateFileEntityExporter() {
		this.recordType = DefaultTemplateRecord.class;
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
		this.encoding = encoding;
	}
	
	public String getTemplateUri() {
		return templateUri;
	}
	
	public void setTemplateUri(String templateUri) {
		this.templateUri = templateUri;
	}
	
	
	// ContextAware interface implementation ---------------------------------------------------------------------------
	
	public Class<? extends TemplateRecord> getRecordType() {
		return recordType;
	}


	public void setRecordType(Class<? extends TemplateRecord> recordType) {
		this.recordType = recordType;
	}


	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	
	// Consumer interface implementation -------------------------------------------------------------------------------

	@Override
	public void startConsuming(ProductWrapper<?> wrapper) {
		if (root == null)
			init();
		Object object = wrapper.unwrap();
		if (!(object instanceof Entity))
			throw new ConfigurationError(getClass() + " can only consume Entities, but was provided with a " + object.getClass());
		Entity product = (Entity) object;
		TemplateRecord productRecord = entityToRecord(product);
		String featureName = product.type();
		TemplateRecord parentRecord = stack.peek();
		updateFeature(featureName, parentRecord, productRecord);
		stack.push(productRecord);
	}

	@Override
	public void finishConsuming(ProductWrapper<?> wrapper) {
		Entity product = (Entity) wrapper.unwrap();
		if (stack.isEmpty())
			throw new ConfigurationError("Trying to pop product from empty stack: '" + product + "'");
		stack.pop();
	}
	
	@Override
	public void close() {
		if (root != null) {
			LOGGER.debug("Writing file {}", uri);
			try {
				Script template = ScriptUtil.readFile(templateUri);
				mapRootToContext();
				Context subContext = new DefaultContext(context);
				String text = ToStringConverter.convert(template.evaluate(subContext), "");
				String path = uri.replace('/', File.separatorChar);
				File folder = new File(path).getParentFile();
				if (folder != null)
					folder.mkdirs();
				IOUtil.writeTextFile(uri, text, encoding);
			} catch (ScriptException e) {
				throw new ConfigurationError("Error evaluating template " + templateUri, e);
			} catch (IOException e) {
				throw new RuntimeException("Error creating template-based output", e);
			}
		} else {
			LOGGER.error("Unable to write file {}", uri);
		}
	}
	
	@Override
	public void flush() {
		// nothing to do for this class
	}
	
	
	// private helper methods ------------------------------------------------------------------------------------------
	
	private void init() {
		this.root = BeanUtil.newInstance(recordType);
		this.stack = new Stack<TemplateRecord>();
		this.stack.push(root);
	}
	
	private TemplateRecord entityToRecord(Entity entity) {
		TemplateRecord record = BeanUtil.newInstance(recordType);
        for (Map.Entry<String, Object> entry : entity.getComponents().entrySet())
            AnyMutator.setValue(record, entry.getKey(), entry.getValue());
		return record;
	}

	@SuppressWarnings("unchecked")
	private static void updateFeature(String featureName, TemplateRecord parent, TemplateRecord product) {
		Object previousObject = FeatureAccessor.getValue(parent, featureName, false);
		if (previousObject == null) {
			List<TemplateRecord> list = new ArrayList<TemplateRecord>();
			list.add(product);
			AnyMutator.setValue(parent, featureName, list);
			//parentMap.put(product.type(), entityMap);
		} else if (previousObject instanceof List) {
			((List<TemplateRecord>) previousObject).add(product);
		} else {
			throw new ProgrammerError("Invalid assumption");
		}
	}

	private void mapRootToContext() {
		for (Map.Entry<String, ?> entry : root.entrySet())
			context.set(entry.getKey(), entry.getValue());
	}

}
