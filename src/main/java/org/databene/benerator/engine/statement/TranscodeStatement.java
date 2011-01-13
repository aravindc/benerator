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

import java.util.List;
import java.util.Map;

import org.databene.benerator.Generator;
import org.databene.benerator.composite.ComponentAndVariableSupport;
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.parser.xml.TranscodeParser.TypeExpression;
import org.databene.benerator.factory.ComplexTypeGeneratorFactory;
import org.databene.benerator.factory.DescriptorUtil;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.wrapper.IteratingGenerator;
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
public class TranscodeStatement implements Statement {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TranscodeStatement.class);
	
	TypeExpression typeExpression;
	Expression<DBSystem> sourceEx;
	Expression<String> selectorEx;
	Expression<DBSystem> targetEx;
	Expression<Long> pageSizeEx;
	Expression<ErrorHandler> errorHandlerEx;
	TranscodingTaskStatement parent;

	public TranscodeStatement(TypeExpression typeExpression, TranscodingTaskStatement parent,
            Expression<DBSystem> sourceEx, Expression<String> selectorEx, Expression<DBSystem> targetEx, 
            Expression<Long> pageSizeEx, Expression<ErrorHandler> errorHandlerEx) {
	    this.typeExpression = typeExpression;
	    this.parent = parent;
	    this.sourceEx = sourceEx;
	    this.selectorEx = selectorEx;
	    this.targetEx = targetEx;
	    this.pageSizeEx = pageSizeEx;
	    this.errorHandlerEx = errorHandlerEx;
    }

    public void execute(BeneratorContext context) {
		DBSystem source = sourceEx.evaluate(context);
		DBSystem target = targetEx.evaluate(context);
		Long pageSize = ExpressionUtil.evaluate(pageSizeEx, context);
		if (pageSize == null)
			pageSize = 1L;
		transcode(source, target, pageSize, context);
    }

    public void transcode(DBSystem source, DBSystem target, long pageSize, Context ctx) {
    	BeneratorContext context = (BeneratorContext) ctx;
    	ComplexTypeDescriptor type = typeExpression.evaluate(context);
		IdentityModel identity = parent.getIdentityProvider().getIdentity(type.getName());
		LOGGER.info("Starting transcoding of " + type.getName() + " from " + source.getId() + " to " + target.getId());
		long rowCount = 0;
		KeyMapper mapper = parent.getKeyMapper();
		mapper.registerSource(source.getId(), source.getConnection());
		String tableName = type.getName();
		
		// iterate rows
		String selector = ExpressionUtil.evaluate(selectorEx, context);
		TypedIterable<Entity> iterable = source.queryEntities(tableName, selector, context);
		Generator<Entity> generator = new IteratingGenerator<Entity>(iterable);
		//Generator<Entity> mutGen = ComplexTypeGeneratorFactory.createMutatingEntityGenerator(tableName, type, Uniqueness.NONE, context, generator);
		generator.init(context);
    	List<ComponentBuilder<Entity>> componentBuilders = 
    		ComplexTypeGeneratorFactory.createMutatingComponentBuilders(type, Uniqueness.NONE, context);
        Map<String, NullableGenerator<?>> variables = DescriptorUtil.parseVariables(type, context);
        ComponentAndVariableSupport<Entity> cavs = new ComponentAndVariableSupport<Entity>(variables, componentBuilders, context);
		Entity entity;
	    while ((entity = generator.generate()) != null) {
	    	Object sourcePK = entity.idComponentValues();
	    	boolean mapNk = parent.needsNkMapping(tableName);
	    	String nk = null;
	    	if (mapNk)
	    		nk = mapper.getNaturalKey(source.getId(), identity, sourcePK);
			cavs.apply(entity);
	    	Object targetPK = entity.idComponentValues();
			transcodeForeignKeys(entity, source, context);
			mapper.store(source.getId(), identity, nk, sourcePK, targetPK);
		    target.store(entity);
	        LOGGER.debug("transcoded {}", entity);
	        rowCount++;
	        if (rowCount % pageSize == 0)
	        	target.flush();
	    }
		IOUtil.close(generator);
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
						throw new ConfigurationError("No identity defined for table " + refereeTable);
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
