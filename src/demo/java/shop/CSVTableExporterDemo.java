package shop;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.commons.TypedIterable;
import org.databene.model.data.Entity;
import org.databene.model.storage.StorageSystem;
import org.databene.platform.csv.CSVEntityExporter;
import org.databene.platform.db.DBSystem;

public class CSVTableExporterDemo {
    
    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "benerator";
    private static final String PASSWORD = "benerator";

    private static Log logger = LogFactory.getLog(CSVTableExporterDemo.class);
    
    public static void main(String[] args) {
        DBSystem db = new DBSystem(null, JDBC_URL, JDBC_DRIVER, USER, PASSWORD);
        db.setFetchSize(100);
        exportTableAsCSV(db);
        logger.info("...done!");
    }

    private static void exportTableAsCSV(StorageSystem db) {
        TypedIterable<Entity> entities = db.queryEntities("db_product", null, null);
        Iterator<Entity> iterator = entities.iterator();
        if (iterator.hasNext()) {
            Entity cursor = iterator.next();
            CSVEntityExporter exporter = new CSVEntityExporter(cursor.getDescriptor());
            logger.info("exporting data, please wait...");
            exporter.startConsuming(cursor);
            while (iterator.hasNext())
                exporter.startConsuming(iterator.next());
            exporter.close();
        }
    }
    
}
