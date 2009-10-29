/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

import java.io.Closeable;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.Statement;
import org.databene.commons.BeanUtil;
import org.databene.commons.Expression;
import org.databene.commons.StringUtil;
import org.databene.commons.context.ContextAware;
import org.databene.model.data.DataModel;
import org.databene.model.data.DescriptorProvider;
import org.databene.task.Task;

/**
 * {@link Task} implementation that instantiates a JavaBean.<br/>
 * <br/>
 * Created at 24.07.2009 07:00:52
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class CreateBeanStatement implements Statement {
	
	private String id;
    private Expression<?> beanExpression;
    private ResourceManager resourceManager;

    public CreateBeanStatement(String id, Expression<?> beanExpression, ResourceManager resourceManager) {
    	this.id = id;
        this.beanExpression = beanExpression;
        this.resourceManager = resourceManager;
    }

	public void execute(BeneratorContext context) {
        Object bean = beanExpression.evaluate(context);
        if (!StringUtil.isEmpty(id))
            BeanUtil.setPropertyValue(bean, "id", id, false);
		context.set(id, bean);
		if (bean instanceof ContextAware)
			((ContextAware) bean).setContext(context);
		if (bean instanceof DescriptorProvider)
			DataModel.getDefaultInstance().addDescriptorProvider((DescriptorProvider) bean);
		if (bean instanceof Closeable)
			resourceManager.addResource((Closeable) bean);
    }

}
