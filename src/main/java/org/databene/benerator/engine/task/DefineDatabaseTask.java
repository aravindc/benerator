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

package org.databene.benerator.engine.task;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManager;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.expression.StringExpression;
import org.databene.model.data.DataModel;
import org.databene.platform.db.DBSystem;
import org.databene.task.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes a &lt;database/&gt; from an XML descriptor.<br/>
 * <br/>
 * Created at 23.07.2009 07:13:02
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class DefineDatabaseTask extends AbstractTask { // TODO move to DB package?
	
	private static Logger logger = LoggerFactory.getLogger(DefineDatabaseTask.class);
	
	private StringExpression id;
	private StringExpression url;
	private StringExpression driver;
	private StringExpression user;
	private StringExpression password;
	private StringExpression schema;
	private Expression batch;
	private Expression fetchSize;
	private Expression readOnly;
	private ResourceManager resourceManager;
	
	public DefineDatabaseTask(StringExpression id, StringExpression url, StringExpression driver, 
			StringExpression user, StringExpression password, 
			StringExpression schema, Expression batch, 
			Expression fetchSize, Expression readOnly, ResourceManager resourceManager) {
		this.id = id;
	    this.url = url;
	    this.driver = driver;
	    this.user = user;
	    this.password = password;
	    this.schema = schema;
	    this.batch = batch;
	    this.fetchSize = fetchSize;
	    this.readOnly = readOnly;
	    this.resourceManager = resourceManager;
    }

    public void run(Context context) {
	    logger.debug("Instantiating database with id '" + id + "'");
	    String idValue = id.evaluate(context);
		DBSystem db = new DBSystem(
	    		idValue, 
	    		url.evaluate(context), 
	    		driver.evaluate(context), 
	    		user.evaluate(context), 
	    		password.evaluate(context));
	    db.setSchema(schema.evaluate(context));
	    db.setBatch((Boolean) batch.evaluate(context));
	    db.setFetchSize((Integer) fetchSize.evaluate(context));
	    db.setBatch((Boolean) readOnly.evaluate(context));
	    context.set(idValue, db);
	    BeneratorContext beneratorContext = (BeneratorContext) context;
	    DataModel.getDefaultInstance().addDescriptorProvider(db, beneratorContext.isValidate());
	    resourceManager.addResource(db);
    }

}
