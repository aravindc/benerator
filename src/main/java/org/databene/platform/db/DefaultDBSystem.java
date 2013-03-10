/*
 * (c) Copyright 2007-2013 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.db;

import org.databene.commons.IOUtil;
import org.databene.jdbacl.ColumnInfo;
import org.databene.model.data.*;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Default implementation of the {@link DBSystem} class.<br/><br/>
 * Created: 27.06.2007 23:04:19
 * @since 0.3
 * @author Volker Bergmann
 */
public class DefaultDBSystem extends DBSystem {

    private ConnectionHolder connectionHolder;
    
	public DefaultDBSystem(String id, String environment, DataModel dataModel) {
		super(id, environment, dataModel);
		this.connectionHolder = new ConnectionHolder(this);
	}

	public DefaultDBSystem(String id, String url, String driver, String user,
			String password, DataModel dataModel) {
		super(id, url, driver, user, password, dataModel);
		this.connectionHolder = new ConnectionHolder(this);
	}

	@Override
	public void flush() {
        logger.debug("flush()");
    	connectionHolder.commit();
	}

	@Override
	public void close() {
        logger.debug("close()");
        flush();
        IOUtil.close(connectionHolder);
        super.close();
	}

	@Override
	public Connection getConnection() {
		return connectionHolder.getConnection();
	}

	@Override
	protected PreparedStatement getSelectByPKStatement(ComplexTypeDescriptor descriptor) {
		return connectionHolder.getSelectByPKStatement(descriptor);
	}

	@Override
	protected PreparedStatement getStatement(ComplexTypeDescriptor descriptor,
			boolean insert, List<ColumnInfo> columnInfos) {
		return connectionHolder.getStatement(descriptor, insert, columnInfos);
	}
	
}
