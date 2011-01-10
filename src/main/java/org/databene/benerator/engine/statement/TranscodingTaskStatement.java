/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.dbsanity.parser.TestSuiteParser;
import org.databene.jdbacl.identity.IdentityProvider;
import org.databene.jdbacl.identity.KeyMapper;
import org.databene.jdbacl.identity.mem.MemKeyMapper;
import org.databene.jdbacl.model.Database;
import org.databene.platform.db.DBSystem;

/**
 * TODO Document class.<br/><br/>
 * Created: 10.09.2010 18:25:18
 * @since TODO version
 * @author Volker Bergmann
 */
public class TranscodingTaskStatement extends SequentialStatement {
	
    Expression<DBSystem> sourceEx; 
    Expression<DBSystem> targetEx;
    Expression<String> identityEx;
	Expression<Integer> pageSizeEx;
    Expression<ErrorHandler> errorHandlerExpression;
    IdentityProvider identityProvider;
    KeyMapper mapper;
    
	public TranscodingTaskStatement(Expression<DBSystem> sourceEx, Expression<DBSystem> targetEx, Expression<String> identityEx, 
    		Expression<Integer> pageSizeEx, Expression<ErrorHandler> errorHandlerExpression) {
	    this.sourceEx = sourceEx;
	    this.targetEx = targetEx;
	    this.identityEx = identityEx;
	    this.pageSizeEx = pageSizeEx;
	    this.errorHandlerExpression = errorHandlerExpression;
		this.identityProvider = new IdentityProvider();
    }

	public Expression<DBSystem> getSourceEx() {
	    return sourceEx;
    }

	public Expression<DBSystem> getTargetEx() {
	    return targetEx;
    }

    public Expression<Integer> getPageSizeEx() {
	    return pageSizeEx;
    }

	public Expression<ErrorHandler> getErrorHandlerEx() {
    	return errorHandlerExpression;
    }

	public IdentityProvider getIdentityProvider() {
		return identityProvider;
	}
	
	KeyMapper getKeyMapper() {
		return mapper;
	}
	
	@Override
	public void execute(BeneratorContext context) {
		try {
			// check identity definition
			String identityUri = ExpressionUtil.evaluate(identityEx, context);
			if (identityUri == null)
				throw new ConfigurationError("No 'identity' definition file defined");
			String idFile = context.resolveRelativeUri(identityUri);
			DBSystem target = getTarget(context);
			Database database = target.getDbMetaData();
			mapper = new MemKeyMapper(null, null, target.getConnection(), target.getId(), identityProvider);
			File reportFolder = new File("dbsanity-report");
			new TestSuiteParser().parseHierarchy(new File(idFile), reportFolder, reportFolder, new File("temp"), database, mapper, identityProvider);
			
		} catch (Exception e) {
			throw new ConfigurationError("Error setting up transcoding task", e);
		}
		super.execute(context);
	}

	private DBSystem getTarget(BeneratorContext context) {
		DBSystem target = ExpressionUtil.evaluate(targetEx, context);
		if (target == null)
			throw new ConfigurationError("No 'target' database defined in <transcodingTask>");
		return target;
	}
	
}
