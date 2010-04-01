package shop;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.commons.IOUtil;
import org.databene.commons.ReaderLineIterator;
import org.databene.commons.TypedIterable;
import org.databene.model.data.Entity;
import org.databene.model.storage.StorageSystem;
import org.databene.platform.csv.CSVEntityExporter;
import org.databene.platform.db.DBSystem;

public class CSVTableExporterDemo {
    
    private static final String JDBC_DRIVER = "org.hsqldb.jdbcDriver";
    private static final String JDBC_URL = "jdbc:hsqldb:mem:benerator";
    private static final String USER = "sa";
    private static final String PASSWORD = null;

    private static Log logger = LogFactory.getLog(CSVTableExporterDemo.class);
    
    public static void main(String[] args) throws SQLException, IOException {
    	// first we create a table with some data to export
        DBSystem db = new DBSystem(null, JDBC_URL, JDBC_DRIVER, USER, PASSWORD);
        try {
	        db.execute("create table db_data (" + 
	        			"    id   int," +
	        			"    name varchar(30) NOT NULL," +
	        			"    PRIMARY KEY  (id)" +
	        			")");
	        db.execute("insert into db_data values (1, 'alpha')");
	        db.execute("insert into db_data values (2, 'beta')");
	        db.execute("insert into db_data values (3, 'gamma')");
	        db.setFetchSize(100);
	        // ...and then we export it
	        exportTableAsCSV(db, "db_data.csv");
	        logger.info("...done!");
	        printFile("db_data.csv");
        } finally {
            db.execute("drop table db_data");
        }
    }

	private static void exportTableAsCSV(StorageSystem db, String filename) {
        TypedIterable<Entity> entities = db.queryEntities("db_data", null, null);
        Iterator<Entity> iterator = entities.iterator();
        if (iterator.hasNext()) {
            Entity cursor = iterator.next();
            CSVEntityExporter exporter = new CSVEntityExporter(filename, cursor.descriptor());
        	try {
	            logger.info("exporting data, please wait...");
	            exporter.startConsuming(cursor);
	            while (iterator.hasNext())
	                exporter.startConsuming(iterator.next());
        	} finally {
        		exporter.close();
        	}
        }
    }
    
    private static void printFile(String filename) throws IOException {
    	System.out.println("Content of file " + filename + ":");
	    ReaderLineIterator iterator = new ReaderLineIterator(IOUtil.getReaderForURI(filename));
	    while (iterator.hasNext())
	    	System.out.println(iterator.next());
	    iterator.close();
    }

}
