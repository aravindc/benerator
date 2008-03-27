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
import org.databene.commons.DBUtil;
import org.databene.commons.IOUtil;
import org.databene.commons.ReaderLineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Runs a SQL script against the specified database.<br/>
 * <br/>
 * Created: 23.08.2007 06:38:43
 * @author Volker Bergmann
 */
public class RunSqlScriptTask extends AbstractTask {

    private static final Log logger = LogFactory.getLog(RunSqlScriptTask.class);
    private static final Log sqlLogger = LogFactory.getLog("org.databene.benerator.SQL"); 

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
        Exception exception = null;
        try {
            connection = db.createConnection();
            BufferedReader reader = IOUtil.getReaderForURI(uri);
            ReaderLineIterator iterator = new ReaderLineIterator(reader);
            StringBuilder cmd = new StringBuilder();
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.startsWith("--"))
                    continue;
                if (cmd.length() > 0)
                    cmd.append('\r');
                cmd.append(line.trim());
                if (line.endsWith(";"))
                    execute(connection, cmd);
            }
            iterator.close();
        } catch (IOException e) {
            exception = e;
            if (haltOnError)
                throw new TaskException(e);
            else
                logger.error(e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    if (exception != null && haltOnError)
                        connection.rollback();
                    else
                        connection.commit();
                } catch (SQLException e) {
                    exception = e;
                }
                DBUtil.close(connection);
            }
        }
        if (exception != null)
            throw new RuntimeException(exception);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + '[' + uri + "->" + db.getId() + ']';
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void execute(Connection connection, StringBuilder cmd) {
        // delete the trailing ';'
        cmd.delete(cmd.length() - 1, cmd.length());
        try {
            executeUpdate(cmd.toString(), connection);
        } catch (SQLException e) {
            if (haltOnError)
                throw new TaskException(e);
            else
                logger.error(e.getMessage() + ":\n" + cmd);
        } finally {
            cmd.delete(0, cmd.length());
        }
    }

    private int executeUpdate(String sql, Connection connection) throws SQLException {
        if (ignoreComments && sql.trim().toUpperCase().startsWith("COMMENT"))
            return 0;
        if (sqlLogger.isDebugEnabled())
            sqlLogger.debug(sql);
        int result = 0;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            result = statement.executeUpdate(sql);
        } finally {
            if (statement != null)
                statement.close();
        }
        return result;
    }

}
