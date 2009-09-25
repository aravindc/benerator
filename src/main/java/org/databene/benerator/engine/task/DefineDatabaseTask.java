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
import org.databene.benerator.engine.DescriptorRunner;
import org.databene.commons.Context;
import org.databene.commons.Expression;
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

public class DefineDatabaseTask extends AbstractTask {
	
	private static Logger logger = LoggerFactory.getLogger(DefineDatabaseTask.class);
	
	private Expression<String> id;
	private Expression<String> url;
	private Expression<String> driver;
	private Expression<String> user;
	private Expression<String> password;
	private Expression<String> schema;
	private Expression<Boolean> batch;
	private Expression<Integer> fetchSize;
	private Expression<Boolean> readOnly;
	private DescriptorRunner runner;
	
	public DefineDatabaseTask(Expression<String> id, Expression<String> url, Expression<String> driver, 
			Expression<String> user, Expression<String> password, 
			Expression<String> schema, Expression<Boolean> batch, 
			Expression<Integer> fetchSize, Expression<Boolean> readOnly, DescriptorRunner runner) {
		this.id = id;
	    this.url = url;
	    this.driver = driver;
	    this.user = user;
	    this.password = password;
	    this.schema = schema;
	    this.batch = batch;
	    this.fetchSize = fetchSize;
	    this.readOnly = readOnly;
	    this.runner = runner;
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
	    db.setBatch(batch.evaluate(context));
	    db.setFetchSize(fetchSize.evaluate(context));
	    db.setBatch(readOnly.evaluate(context));
	    context.set(idValue, db);
	    BeneratorContext beneratorContext = (BeneratorContext) context;
	    DataModel.getDefaultInstance().addDescriptorProvider(db, beneratorContext.isValidate());
	    runner.addResource(db);
    }

}
