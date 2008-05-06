/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

import org.databene.task.AbstractTask;
import org.databene.task.TaskException;
import org.databene.commons.ConfigurationError;
import org.databene.commons.db.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Runs a SQL script against the specified database.<br/>
 * <br/>
 * Created: 23.08.2007 06:38:43
 * @author Volker Bergmann
 */
public class RunSqlScriptTask extends AbstractTask {

    private String uri;
    private DBSystem db;
    private boolean haltOnError;
    private boolean ignoreComments;

    // constructors ----------------------------------------------------------------------------------------------------

    public RunSqlScriptTask() {
        this(null, null);
    }

    public RunSqlScriptTask(String uri, DBSystem db) {
        this.uri = uri;
        this.db = db;
        this.haltOnError = true;
        this.ignoreComments = false;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public DBSystem getDb() {
        return db;
    }

    public void setDb(DBSystem db) {
        this.db = db;
    }

    public boolean isHaltOnError() {
        return haltOnError;
    }

    public void setHaltOnError(boolean haltOnError) {
        this.haltOnError = haltOnError;
    }
    
    /**
     * @return the ignoringComments
     */
    public boolean isIgnoreComments() {
        return ignoreComments;
    }

    /**
     * @param ignoreComments the ignoringComments to set
     */
    public void setIgnoreComments(boolean ignoreComments) {
        this.ignoreComments = ignoreComments;
    }

    // Task implementation ---------------------------------------------------------------------------

    public void run() {
        Connection connection = null;
        try {
            connection = db.createConnection();
            DBUtil.runScript(uri, connection, haltOnError, ignoreComments);
            db.invalidate();
            connection.commit();
        } catch (IOException e) {
			throw new ConfigurationError(e);
		} catch (SQLException sqle) {
            if (connection != null) {
                try {
                    if (haltOnError)
                        connection.rollback();
                    else
                        connection.commit();
                } catch (SQLException e) {
                    // ignore this 2nd exception, we have other problems now (sqle)
                }
            }
            throw new TaskException(sqle);
		} finally {
            DBUtil.close(connection);
        }
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + '[' + uri + "->" + db.getId() + ']';
    }

}
