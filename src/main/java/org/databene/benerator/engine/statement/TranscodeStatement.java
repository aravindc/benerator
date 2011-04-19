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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.databene.benerator.composite.ComponentAndVariableSupport;
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.factory.ComplexTypeGeneratorFactory;
import org.databene.benerator.factory.DescriptorUtil;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.HeavyweightTypedIterable;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.jdbacl.identity.IdentityModel;
import org.databene.jdbacl.identity.IdentityProvider;
import org.databene.jdbacl.identity.KeyMapper;
import org.databene.jdbacl.identity.NoIdentity;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.platform.db.DBSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Statement} that transcodes a database table.<br/><br/>
 * Created: 08.09.2010 16:23:56
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class TranscodeStatement extends SequentialStatement implements CascadeParent {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TranscodeStatement.class);
	
	Expression<ComplexTypeDescriptor> typeExpression;
	Expression<DBSystem> sourceEx;
	Expression<String> selectorEx;
	Expression<DBSystem> targetEx;
	Expression<Long> pageSizeEx;
	Expression<ErrorHandler> errorHandlerEx;
	TranscodingTaskStatement parent;

	DBSystem source;
	private DBSystem target;

	private Entity currentEntity;

	public TranscodeStatement(MutatingTypeExpression typeExpression, TranscodingTaskStatement parent,
            Expression<DBSystem> sourceEx, Expression<String> selectorEx, Expression<DBSystem> targetEx, 
            Expression<Long> pageSizeEx, Expression<ErrorHandler> errorHandlerEx) {
	    this.typeExpression = cache(typeExpression);
	    this.parent = parent;
	    this.sourceEx = sourceEx;
	    this.selectorEx = selectorEx;
	    this.targetEx = targetEx;
	    this.pageSizeEx = pageSizeEx;
	    this.errorHandlerEx = errorHandlerEx;
	    this.currentEntity = null;
    }

	@Override
	public void execute(BeneratorContext context) {
		DBSystem target = targetEx.evaluate(context);
		Long pageSize = ExpressionUtil.evaluate(pageSizeEx, context);
		if (pageSize == null)
			pageSize = 1L;
		transcodeTable(getSource(context), target, pageSize, context);
    }
    
	public KeyMapper getKeyMapper() {
		return parent.getKeyMapper();
	}

	public IdentityProvider getIdentityProvider() {
		return parent.getIdentityProvider();
	}

	public Entity currentEntity() {
		return currentEntity;
	}

    public ComplexTypeDescriptor getType(DBSystem db, BeneratorContext context) {
    	return typeExpression.evaluate(context);
    }
    
	public DBSystem getSource(BeneratorContext context) {
		if (source == null)
			source = sourceEx.evaluate(context);
		return source;
	}
    
	public DBSystem getTarget(BeneratorContext context) {
		if (target == null)
			target = targetEx.evaluate(context);
		return target;
	}

	public boolean needsNkMapping(String tableName) {
		return parent.needsNkMapping(tableName);
	}

    // helper methods --------------------------------------------------------------------------------------------------

    private void transcodeTable(DBSystem source, DBSystem target, long pageSize, BeneratorContext context) {
		KeyMapper mapper = getKeyMapper();
		ComplexTypeDescriptor type = typeExpression.evaluate(context);
		IdentityModel identity = getIdentityProvider().getIdentity(type.getName(), false);
		String tableName = type.getName();
		LOGGER.info("Starting transcoding of " + tableName + " from " + source.getId() + " to " + target.getId());
		
		// iterate rows
		String selector = ExpressionUtil.evaluate(selectorEx, context);
		HeavyweightTypedIterable<Entity> iterable = source.queryEntities(tableName, selector, context);
    	List<ComponentBuilder<Entity>> componentBuilders = 
    		ComplexTypeGeneratorFactory.createMutatingComponentBuilders(type, Uniqueness.NONE, context);
        Map<String, NullableGenerator<?>> variables = DescriptorUtil.parseVariables(type, context);
        ComponentAndVariableSupport<Entity> cavs = new ComponentAndVariableSupport<Entity>(variables, componentBuilders, context);
        cavs.init(context);
        Iterator<Entity> iterator = iterable.iterator();
		mapper.registerSource(source.getId(), source.getConnection());
		long rowCount = 0;
	    while (iterator.hasNext()) {
			Entity sourceEntity = iterator.next();
	    	Object sourcePK = sourceEntity.idComponentValues();
	    	boolean mapNk = parent.needsNkMapping(tableName);
	    	String nk = null;
	    	if (mapNk)
	    		nk = mapper.getNaturalKey(source.getId(), identity, sourcePK);
	    	Entity targetEntity = new Entity(sourceEntity);
			cavs.apply(targetEntity);
	    	Object targetPK = targetEntity.idComponentValues();
			transcodeForeignKeys(targetEntity, source, context);
			mapper.store(source.getId(), identity, nk, sourcePK, targetPK);
		    target.store(targetEntity);
	        LOGGER.debug("transcoded {} to {}", sourceEntity, targetEntity);
	        cascade(sourceEntity, context);
	        rowCount++;
	        if (rowCount % pageSize == 0)
	        	target.flush();
	    }
    	target.flush();
		LOGGER.info("Finished transcoding " + source.countEntities(tableName) + " rows of table " + tableName);
    }
    
	private void cascade(Entity sourceEntity, BeneratorContext context) {
		this.currentEntity = sourceEntity;
		executeSubStatements(context);
		this.currentEntity = null;
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
					IdentityModel sourceIdentity = identityProvider.getIdentity(refereeTable, false);
					if (sourceIdentity == null) {
						sourceIdentity = new NoIdentity(source.getDbMetaData().getTable(refereeTable));
						identityProvider.registerIdentity(sourceIdentity, refereeTable);
					}
						
					boolean needsNkMapping = parent.needsNkMapping(refereeTable);
					if (sourceIdentity instanceof NoIdentity && needsNkMapping)
						throw new ConfigurationError("No identity defined for table " + refereeTable);
					KeyMapper mapper = parent.getKeyMapper();
					Object targetRef;
					if (needsNkMapping) {
						String sourceRefNK = mapper.getNaturalKey(source.getId(), sourceIdentity, sourceRef);
						targetRef = mapper.getTargetPK(sourceIdentity, sourceRefNK);
					} else {
						targetRef = mapper.getTargetPK(source.getId(), sourceIdentity, sourceRef);
					}
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
