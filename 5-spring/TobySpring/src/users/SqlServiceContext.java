package users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import users.sql.sqlmapping.SqlMapConfig;
import users.sql.sqlregistry.ConcurrentHashMapSqlRegistry;
import users.sql.sqlregistry.SqlRegistry;
import users.sql.sqlservice.OxmSqlService;
import users.sql.sqlservice.SqlService;

import javax.sql.DataSource;

@Configuration
public class SqlServiceContext {
    @Autowired SqlMapConfig sqlMapConfig;

    /**
     * SQL 서비스
     */

    @Bean
    public SqlService sqlService() {
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());
        sqlService.setSqlmap(this.sqlMapConfig.getSqlMapResource());

        return sqlService;
    }

    @Bean
    public SqlRegistry sqlRegistry() {
//        EmbeddedDBSqlRegistry sqlRegistry = new EmbeddedDBSqlRegistry();
//        sqlRegistry.setDataSource(embeddedDatabase());
        ConcurrentHashMapSqlRegistry sqlRegistry = new ConcurrentHashMapSqlRegistry();

        return sqlRegistry;
    }

    @Bean
    public Unmarshaller unmarshaller() {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setContextPath("users.vol1.learning.sql.jaxb");

        return unmarshaller;
    }

    @Bean
    public DataSource embeddedDatabase() {
        return new EmbeddedDatabaseBuilder()
                .setName("embeddedDatabase")
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:/users/raw/vol1.learning.sql/schema.vol1.learning.sql")
                .build();
    }
}
