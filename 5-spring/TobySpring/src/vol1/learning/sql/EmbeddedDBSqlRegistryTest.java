//package vol1.learning.sql;
//
//import org.junit.After;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
//import vol1.learning.sql.sqlregistry.EmbeddedDBSqlRegistry;
//import vol1.learning.sql.sqlregistry.UpdatableSqlRegistry;
//
//import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;
//
//public class EmbeddedDBSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
//    EmbeddedDatabase vol2.db;
//
//    @Override
//    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
//        vol2.db = new EmbeddedDatabaseBuilder()
//                .setType(HSQL)
//                .addScript("classpath:/vol1.learning.sql/sqlbuilder/schema.vol1.learning.sql")
//                .build();
//
//        EmbeddedDBSqlRegistry embeddedDBSqlRegistry = new EmbeddedDBSqlRegistry();
//        embeddedDBSqlRegistry.setDataSource(vol2.db);
//
//        return embeddedDBSqlRegistry;
//    }
//
//    @After
//    public void tearDown() {
//        vol2.db.shutdown();
//    }
//}
