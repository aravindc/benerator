/*
 * (c) Copyright 2007-2008 by Volker Bergmann. All rights reserved.
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

import org.databene.LogCategories;
import org.databene.task.AbstractTask;
import org.databene.task.TaskException;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Escalator;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.SystemInfo;
import org.databene.commons.ErrorHandler.Level;
import org.databene.commons.db.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Runs a SQL script against the specified database.<br/>
 * <br/>
 * Created: 23.08.2007 06:38:43
 * @author Volker Bergmann
 */
public class RunSqlScriptTask extends AbstractTask {

	private static final String DEFAULT_ENCODING = SystemInfo.fileEncoding();
	
	private static Escalator escalator = new LoggerEscalator();
	
    private String uri;
    private String encoding;
    private String text;
    private DBSystem db;
    private boolean ignoreComments;
    private ErrorHandler errorHandler;
    private Object result;

    // constructors ----------------------------------------------------------------------------------------------------

    public RunSqlScriptTask() {
        this(null, DEFAULT_ENCODING, null);
    }

    public RunSqlScriptTask(String uri, String encoding, DBSystem db) {
        this.uri = uri;
        this.encoding = (encoding != null ? encoding : DEFAULT_ENCODING);
        this.db = db;
        initDefaults();
    }

    public RunSqlScriptTask(String text, DBSystem db) {
        this.text = text;
        this.db = db;
        initDefaults();
    }

	private void initDefaults() {
		String message = getClass().getName() + " is deprecated. When running benerator, use <execute> instead.";
		escalator.escalate(message, getClass(), null);
		this.ignoreComments = false;
        this.errorHandler = new ErrorHandler(LogCategories.SQL);
        this.errorHandler.setLoggingStackTrace(false);
	}

    // Task implementation ---------------------------------------------------------------------------

	public void run() {
        Connection connection = null;
        try {
            connection = db.createConnection();
            if (text != null)
            	result = DBUtil.runScript(text, connection, ignoreComments, errorHandler);
            else
            	result = DBUtil.runScript(uri, encoding, connection, ignoreComments, errorHandler);
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
            throw new TaskException(sqle);
		} finally {
            DBUtil.close(connection);
        }
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public DBSystem getDb() {
        return db;
    }

    public void setDb(DBSystem db) {
        this.db = db;
    }

    /**
     * @deprecated Use the errorHandler property
     */
    @Deprecated
    public boolean isHaltOnError() {
        return (errorHandler.getLevel() == Level.fatal);
    }

    /**
     * @deprecated Use the errorHandler property
     */
    @Deprecated
    public void setHaltOnError(boolean haltOnError) {
        this.errorHandler = new ErrorHandler(LogCategories.SQL, haltOnError ? Level.fatal : Level.error);
        this.errorHandler.setLoggingStackTrace(false);
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

	public Object getResult() {
		return result;
	}

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + '[' + uri + "->" + db.getId() + ']';
    }

}
