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

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.Assert;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ConversionException;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.IOUtil;
import org.databene.commons.LogCategories;
import org.databene.commons.ReaderLineIterator;
import org.databene.commons.ShellUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.Level;
import org.databene.commons.converter.LiteralParser;
import org.databene.commons.db.DBUtil;
import org.databene.platform.db.DBSystem;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;
import org.databene.script.jsr227.Jsr223ScriptFactory;
import org.databene.task.AbstractTask;
import org.databene.task.TaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO document class EvaluateTask.<br/>
 * <br/>
 * Created at 23.07.2009 17:59:36
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class EvaluateTask extends AbstractTask {
	
	private static final Logger logger = LoggerFactory.getLogger(EvaluateTask.class);
	
	Expression<String> id;
	Expression<String> text;
	Expression<String> uri;
	Expression<String> type;
	Expression<Object> targetObject;
    Expression<String> onError;
    Expression<String> encoding;
    Expression<Boolean> optimize;
    Expression<Object> assertion;

    public EvaluateTask(Expression<String> id, Expression<String> text, Expression<String> uri, Expression<String> type, Expression<Object> targetObject,
            Expression<String> onError, Expression<String> encoding, Expression<Boolean> optimize,
            Expression<Object> assertion) {
    	this.id = id;
    	this.text = text;
    	this.uri = uri;
    	this.type = type;
    	this.targetObject = targetObject;
    	this.onError = onError;
    	this.encoding = encoding;
    	this.optimize = optimize;
    	this.assertion = assertion;
    }

	public void run(Context context) {
		try {
			BeneratorContext beneratorContext = (BeneratorContext) context;
			// error handler
			String onErrorValue = onError.evaluate(context);
			if (StringUtil.isEmpty(onErrorValue))
				onErrorValue = "fatal";
			ErrorHandler errorHandler = new ErrorHandler(getClass().getName(), Level.valueOf(onErrorValue));
			
			String typeValue = type.evaluate(context);
			// if type is not defined, derive it from the file extension
			String uriValue = uri.evaluate(context);
			if (type == null && uri != null) {
				// check for SQL file URI
				String lcUri = uri.evaluate(context).toLowerCase();
				// TODO v0.6 map generically and extendible (Using/Including
				// Java Scripting?)
				if (lcUri.endsWith(".sql"))
					typeValue = "sql";
				// check for shell file URI
				if ((lcUri.endsWith(".bat") || lcUri.endsWith(".sh")))
					typeValue = "shell";
				// check for jar file URI
				if (lcUri.endsWith(".jar"))
					typeValue = "jar";
				// check for JavaScript file URI
				if (lcUri.endsWith(".js"))
					typeValue = "js";
				uriValue = IOUtil.resolveLocalUri(uriValue, beneratorContext.getContextUri());
			}
			if (typeValue == null && targetObject instanceof DBSystem)
				typeValue = "sql";
			
            String textValue = text.evaluate(context);

			// run
			Object result = null;
			if ("sql".equals(typeValue))
	            result = runSql(uriValue, targetObject, onErrorValue, encoding.evaluate(context), 
						textValue, optimize.evaluate(context));
            else if ("shell".equals(typeValue)) {
				if (!StringUtil.isEmpty(uriValue))
					textValue = IOUtil.getContentOfURI(uriValue);
				textValue = String.valueOf(ScriptUtil.render(textValue, context));
				result = runShell(null, textValue, onErrorValue); // TODO v0.6 remove null uri parameter
			} else {
				if (StringUtil.isEmpty(typeValue))
					throw new ConfigurationError("script type is not defined");
				if (!StringUtil.isEmpty(uriValue))
					textValue = IOUtil.getContentOfURI(uriValue);
				result = runScript(textValue, typeValue, onErrorValue, context);
			}
			context.set("result", result);
			Object assertionValue = assertion.evaluate(beneratorContext);
			if (assertionValue instanceof String)
				assertionValue = LiteralParser.parse((String) assertionValue);
			if (assertionValue != null && !(assertionValue instanceof String && ((String) assertionValue).length() == 0)) {
				if (assertionValue instanceof Boolean) {
					if (!(Boolean) assertionValue)
						errorHandler.handleError("Assertion failed: '" + assertion + "'");
				} else {
					if (!BeanUtil.equalsIgnoreType(assertionValue, result))
						errorHandler.handleError("Assertion failed. Expected: '" + assertionValue + "', found: '" + result + "'");
				}
			}
			String idValue = id.evaluate(context);
			if (idValue != null)
				context.set(idValue, result);
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		} catch (IOException e) {
			throw new ConfigurationError(e);
		}
    }

	private Object runScript(String text, String type, String onError, Context context) {
		ErrorHandler errorHandler = new ErrorHandler(getClass().getName(),
				Level.valueOf(onError));
		try {
			Script script = Jsr223ScriptFactory.parseText(text, type);
			return script.evaluate(context);
		} catch (Exception e) {
			errorHandler.handleError("Error in script evaluation", e);
			return null;
		}
	}

	private int runShell(String uri, String text, String onError) {
		ErrorHandler errorHandler = new ErrorHandler(getClass().getName(),
				Level.valueOf(onError));
		if (text != null)
			return ShellUtil.runShellCommands(new ReaderLineIterator(
					new StringReader(text)), errorHandler);
		else if (uri != null) {
			try {
				return ShellUtil.runShellCommands(new ReaderLineIterator(IOUtil
						.getReaderForURI(uri)), errorHandler);
			} catch (IOException e) {
				errorHandler.handleError("Error in shell invocation", e);
				return 1;
			}
		} else
			throw new ConfigurationError(
					"At least uri or text must be provided in <execute>");
	}

	private Object runSql(String uri, Object targetObject, String onError,
			String encoding, String text, boolean optimize) {
		if (targetObject == null)
			throw new ConfigurationError("Please specify the 'target' database to execute the SQL script");
		Assert.instanceOf(targetObject, DBSystem.class, "target");
		DBSystem db = (DBSystem) targetObject;
		if (uri != null)
			logger.info("Executing script " + uri);
		else if (text != null)
			logger.info("Executing inline script");
		else
			throw new TaskException("No uri or content");
        Connection connection = null;
        Object result = null;
		ErrorHandler errorHandler = new ErrorHandler(LogCategories.SQL, Level.valueOf(onError));
        try {
            connection = db.createConnection();
            if (text != null)
            	result = DBUtil.runScript(text, connection, optimize, errorHandler);
            else
            	result = DBUtil.runScript(uri, encoding, connection, optimize, errorHandler);
            db.invalidate(); // possibly we changed the database structure
            connection.commit();
		} catch (Exception sqle) { 
            if (connection != null) {
            	try {
                    connection.rollback();
                } catch (SQLException e) {
                    // ignore this 2nd exception, we have other problems now (sqle)
                }
            }
            errorHandler.handleError("Error in SQL script execution", sqle);
		} finally {
            DBUtil.close(connection);
        }
		return result;
	}

}
