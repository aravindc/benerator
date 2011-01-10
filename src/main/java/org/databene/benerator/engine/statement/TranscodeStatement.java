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

import java.io.Closeable;
import java.util.Iterator;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.IOUtil;
import org.databene.commons.TypedIterable;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.jdbacl.identity.IdentityModel;
import org.databene.jdbacl.identity.IdentityProvider;
import org.databene.jdbacl.identity.KeyMapper;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.platform.db.DBSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Statement} that transcodes a database table.<br/><br/>
 * Created: 08.09.2010 16:23:56
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class TranscodeStatement implements Statement {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TranscodeStatement.class);
	
	String tableName;
	Expression<DBSystem> sourceEx;
	Expression<DBSystem> targetEx;
	Expression<Integer> pageSizeEx;
	Expression<ErrorHandler> errorHandlerEx;
	TranscodingTaskStatement parent;

	public TranscodeStatement(String tableName, TranscodingTaskStatement parent,
            Expression<DBSystem> sourceEx, Expression<DBSystem> targetEx, 
            Expression<Integer> pageSizeEx, Expression<ErrorHandler> errorHandlerEx) {
	    this.tableName = tableName;
	    this.parent = parent;
	    this.sourceEx = sourceEx;
	    this.targetEx = targetEx;
	    this.pageSizeEx = pageSizeEx;
	    this.errorHandlerEx = errorHandlerEx;
    }

    public void execute(BeneratorContext context) {
		DBSystem source = sourceEx.evaluate(context);
		DBSystem target = targetEx.evaluate(context);
		Integer pageSize = ExpressionUtil.evaluate(pageSizeEx, context);
		if (pageSize == null)
			pageSize = 1;
		IdentityModel identity = parent.getIdentityProvider().getIdentity(tableName);
		transcode(identity, source, target, pageSize, context);
    }

    public void transcode(IdentityModel identity, DBSystem source, DBSystem target, int pageSize, Context context) {
		LOGGER.info("Starting transcoding of " + tableName + " from " + source.getId() + " to " + target.getId());
		long rowCount = 0;
		KeyMapper mapper = parent.getKeyMapper();
		mapper.registerSource(source.getId(), source.getConnection());
		
		// iterate rows
		String selector = null;
		TypedIterable<Entity> iterable = source.queryEntities(tableName, selector, context);
		Iterator<Entity> iterator = iterable.iterator();
	    while (iterator.hasNext()) {
	    	Entity entity = iterator.next();
	    	Object sourcePK = entity.idComponentValues();
			String nk = mapper.getNaturalKey(source.getId(), identity, sourcePK);
			// TODO create new PK
	    	Object targetPK = entity.idComponentValues();
			transcodeForeignKeys(entity, source, context);
			mapper.store(source.getId(), identity, nk, sourcePK, targetPK);
		    target.store(entity);
	        LOGGER.debug("transcoded {}", entity);
	        rowCount++;
	        if (rowCount % pageSize == 0)
	        	target.flush();
	    }
		IOUtil.close((Closeable) iterator);
    	target.flush();
		LOGGER.info("Finished transcoding " + source.countEntities(tableName) + " rows of table " + tableName);
    }
    
	private void transcodeForeignKeys(Entity entity, DBSystem source, Context context) {
		ComplexTypeDescriptor tableDescriptor = entity.descriptor();
		for (ComponentDescriptor component : tableDescriptor.getComponents()) {
			if (component instanceof ReferenceDescriptor) {
				ReferenceDescriptor fk = (ReferenceDescriptor) component;
				String refereeTable = fk.getTargetType();
				Object sourceRef = entity.get(fk.getName());
				if (sourceRef != null) {
					IdentityProvider identityProvider = parent.getIdentityProvider();
					IdentityModel sourceIdentity = identityProvider.getIdentity(refereeTable);
					if (sourceIdentity == null)
						throw new ConfigurationError("No identity defined for table " + tableName);
					KeyMapper mapper = parent.getKeyMapper();
					String sourceRefNK = mapper.getNaturalKey(source.getId(), sourceIdentity, sourceRef);
					Object targetRef = mapper.getTargetPK(sourceIdentity, sourceRefNK);
					if (targetRef == null) {
						String message = "No mapping found for " + source.getId() + '.' + refereeTable + "#" + sourceRef + 
								" referred in " + entity.type() + "(" + fk.getName() + "). " +
								"Probably has not been in the result set of the former '" + refereeTable + "' nk query.";
						getErrorHandler(context).handleError(message);
					}
					entity.set(fk.getName(), targetRef);
				}
			}
		}
	}

	private ErrorHandler getErrorHandler(Context context) {
		ErrorHandler result = ExpressionUtil.evaluate(errorHandlerEx, context);
		return (result != null ? result : ErrorHandler.getDefault());
	}
	
	/*
	@Override
    public void merge(DBSystem source, DBSystem target, int pageSize, KeyMapper mapper, Context context) {
		String activity = "Merging " + name + " from " + source.getId() + " to " + target.getId();
		startActivity(activity);
		HeavyweightIterator<Object[]> nkIterator = createNkPkIterator(source, mapper, context);
		Set<String> sourceNKs = new HashSet<String>();
		try {
			while (nkIterator.hasNext()) {
				Object[] row = nkIterator.next();
				String nk = String.valueOf(row[0]);
				sourceNKs.add(nk);
				Object sourceId = extractPK(row);
				Entity sourceEntity = source.queryEntityById(name, sourceId);
				Object targetId = mapper.getTargetPK(this, nk);
				if (targetId == null) {
					handleNKNotFound(nk, name, source, target);
					continue;
                } else {
					Entity targetEntity = target.queryEntityById(name, targetId);
					String message = checkEquivalence(sourceEntity, targetEntity, source, nk, mapper);
					if (message != null)
						handleNonEquivalence(message, source.getId(), sourceEntity);
                }
				// TODO v1.1 store in target if there is a rule one day
				mapper.store(source, this, nk, sourceId, targetId);
			}
		} finally {
			IOUtil.close(nkIterator);
		}
    	target.flush();
    	
		endActivity(activity, source.countEntities(name));
    }
*/

}
