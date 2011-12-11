/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.statement;

import java.beans.PropertyDescriptor;
import java.io.Closeable;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManager;
import org.databene.commons.BeanUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.context.ContextAware;
import org.databene.model.data.DescriptorProvider;
import org.databene.script.Expression;
import org.databene.script.expression.BeanConstruction;
import org.databene.task.Task;

/**
 * {@link Task} implementation that instantiates a JavaBean.<br/>
 * <br/>
 * Created at 24.07.2009 07:00:52
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class BeanStatement extends SequentialStatement {
	
	private String id;
    private Expression<?> constructionExpression;
    private ResourceManager resourceManager;

    public BeanStatement(String id, Expression<?> constructionExpression, ResourceManager resourceManager) {
    	this.id = id;
        this.constructionExpression = constructionExpression;
        this.resourceManager = resourceManager;
    }

	@SuppressWarnings({ "rawtypes" })
    @Override
    public boolean execute(BeneratorContext context) {
		// invoke constructor
        Object bean = constructionExpression.evaluate(context);
        // post construction steps
        super.execute(context);
        if (!StringUtil.isEmpty(id)) {
        	PropertyDescriptor descriptor = BeanUtil.getPropertyDescriptor(bean.getClass(), "id");
        	if (descriptor != null && descriptor.getWriteMethod() != null)
        		BeanUtil.setPropertyValue(bean, "id", id, false);
        }
        if (bean instanceof ContextAware)
			((ContextAware) bean).setContext(context);
		if (bean instanceof DescriptorProvider)
			context.getDataModel().addDescriptorProvider((DescriptorProvider) bean);
		if (bean instanceof Closeable && resourceManager != null)
			resourceManager.addResource((Closeable) bean);
		if (bean instanceof Generator && constructionExpression instanceof BeanConstruction)
			((Generator) bean).init(context);
		context.set(id, bean);
    	return true;
    }

}
