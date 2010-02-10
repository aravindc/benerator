/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.db;

import java.sql.SQLException;

import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.commons.StringUtil;

/**
 * Reads the current value of a sequence on first invocation, 
 * increases the value locally on subsequent calls and 
 * finally (on close()) updates the DB sequence with the local value.
 * This saves database round trips but limits execution to a single 
 * client.<br/><br/>
 * Created: 11.11.2009 18:35:26
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class OfflineSequenceGenerator extends LightweightGenerator<Long> {

	private DBSystem target;
	private String sequenceName;
	private boolean initialized;
	private long next;
	
	public OfflineSequenceGenerator() {
		this(null, null);
    }

	public OfflineSequenceGenerator(DBSystem target, String sequenceName) {
		this.target = target;
	    this.sequenceName = sequenceName;
	    this.initialized = false;
    }
	
	public DBSystem getTarget() {
    	return target;
    }

	public void setTarget(DBSystem target) {
    	this.target = target;
    }

	public String getSequenceName() {
    	return sequenceName;
    }

	public void setSequenceName(String sequenceName) {
    	this.sequenceName = sequenceName;
    }
	
	@Override
	public void validate() {
	    super.validate();
	    if (target == null)
	    	throw new InvalidGeneratorSetupException("No 'target' database defined");
	    if (StringUtil.isEmpty(sequenceName))
	    	throw new InvalidGeneratorSetupException("No sequence name defined");
	}

	public Class<Long> getGeneratedType() {
	    return Long.class;
    }
	
	public Long generate() {
	    if (!initialized)
	    	next = target.nextSequenceValue(sequenceName);
	    return next++;
    }
	
	@Override
	public void close() {
	    try {
	        super.close();
	        target.setSequenceValue(sequenceName, next);
        } catch (SQLException e) {
	        throw new RuntimeException(e);
        }
	}
	
}
