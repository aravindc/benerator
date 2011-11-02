/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.statement;

import java.io.File;
import java.util.Locale;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Level;
import org.databene.commons.version.VersionNumber;
import org.databene.dbsanity.DbSanity;
import org.databene.dbsanity.ExecutionMode;
import org.databene.dbsanity.model.SanityCheckSuite;
import org.databene.jdbacl.JDBCConnectData;
import org.databene.jdbacl.version.ConstantVersionProvider;
import org.databene.platform.db.DBSystem;
import org.databene.script.Expression;
import org.databene.script.expression.ExpressionUtil;

/**
 * {@link Statement} implementation that performs DB Sanity checks 
 * and raises an error in case of check violations.<br/><br/>
 * Created: 29.11.2010 11:15:48
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class DBSanity4BeneratorStatement implements Statement {
	
	Expression<String> envEx;
	Expression<DBSystem> databaseEx;
	Expression<String> appVersionEx;
	Expression<String> inEx;
	Expression<String> outEx;
	Expression<String[]> tablesEx;
	Expression<String[]> tagsEx;
	Expression<String> skinEx;
	Expression<Locale> localeEx;
	Expression<ExecutionMode> modeEx;
	Expression<ErrorHandler> errHandlerEx;

	public DBSanity4BeneratorStatement(Expression<String> envEx, Expression<DBSystem> databaseEx, Expression<String> inEx, Expression<String> outEx, 
			Expression<String> appVersionEx, Expression<String[]> tablesEx, Expression<String[]> tagsEx, 
			Expression<String> skinEx, Expression<Locale> localeEx,
			Expression<ExecutionMode> modeEx, Expression<ErrorHandler> errHandlerEx) {
		this.envEx = envEx;
		this.databaseEx = databaseEx;
		this.appVersionEx = appVersionEx;
		this.inEx = inEx;
		this.outEx = outEx;
		this.tablesEx = tablesEx;
		this.tagsEx = tagsEx;
		this.skinEx = skinEx;
		this.localeEx = localeEx;
		this.modeEx = modeEx;
		this.errHandlerEx = errHandlerEx;
	}

	public boolean execute(BeneratorContext context) {
		try {
			// initialize DB Sanity
			DbSanity dbSanity = new DbSanity();

			if (envEx != null) {
				String environment = envEx.evaluate(context);
				dbSanity.setEnvironment(context.resolveRelativeUri(environment));
			} else {
				DBSystem db = databaseEx.evaluate(context);
				dbSanity.setConnection(db.getConnection());
				dbSanity.setConnectData(new JDBCConnectData(db.getDriver(), db.getUrl(), db.getUser(), db.getPassword()));
			}
			String in = ExpressionUtil.evaluate(inEx, context);
			String inFolderName = (in != null ? in : "dbsanity");
			File inFolder = new File(context.resolveRelativeUri(inFolderName));
			dbSanity.setCheckDefinitionFile(inFolder);

			String out = ExpressionUtil.evaluate(outEx, context);
			String outFolderName = (out != null ? out : "dbsanity-report");
			File outFolder = new File(context.resolveRelativeUri(outFolderName));
			dbSanity.setReportFolder(outFolder);

			dbSanity.setClearBefore(true);
			
			String skin = ExpressionUtil.evaluate(skinEx, context);
			if (skin != null)
				dbSanity.setSkin(skin);
			
			String appVersion = ExpressionUtil.evaluate(appVersionEx, context);
			dbSanity.setVersionProvider(new ConstantVersionProvider(VersionNumber.valueOf(appVersion)));
			
			String[] tables = ExpressionUtil.evaluate(tablesEx, context);
			dbSanity.setTables(tables);
			
			String[] tags = ExpressionUtil.evaluate(tagsEx, context);
			dbSanity.setTags(CollectionUtil.toSet(tags));
			
			Locale locale = ExpressionUtil.evaluate(localeEx, context);
			if (locale != null)
				dbSanity.setLocale(locale);
			
			ExecutionMode mode = ExpressionUtil.evaluate(modeEx, context);
			if (mode != null)
				dbSanity.setMode(mode);
			
			// perform check(s)
			SanityCheckSuite dbsSuite = dbSanity.execute();
			boolean success = (dbsSuite.countErredChecks() == 0 && dbsSuite.countFailedChecks() == 0);
			
			if (!success)
				getErrorHandler(context).handleError("DB Sanity check failed.");
		} catch (Exception e) {
			getErrorHandler(context).handleError("DB Sanity check failed with an exception", e);
		}
    	return true;
	}

	public ErrorHandler getErrorHandler(BeneratorContext context) {
		ErrorHandler handler = errHandlerEx.evaluate(context);
		return (handler != null ? handler : new ErrorHandler("dbsanity", Level.fatal));
	}

}
