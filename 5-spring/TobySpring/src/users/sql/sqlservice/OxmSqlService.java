package users.sql.sqlservice;

import users.dao.UserDao;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import users.sql.exception.SqlNotFoundException;
import users.sql.exception.SqlRetrievalFailureException;
import users.sql.jaxb.SqlType;
import users.sql.jaxb.Sqlmap;
import users.sql.sqlreader.SqlReader;
import users.sql.sqlregistry.HashMapSqlRegistry;
import users.sql.sqlregistry.SqlRegistry;

import javax.annotation.PostConstruct;
import org.springframework.oxm.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class OxmSqlService implements SqlService{
    private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
    private final BaseSqlService baseSqlService = new BaseSqlService();

    private SqlRegistry sqlRegistry = new HashMapSqlRegistry();

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.oxmSqlReader.setUnmarshaller(unmarshaller);
    }

    public void setSqlmap(Resource sqlmap) {
        this.oxmSqlReader.setSqlmap(sqlmap);
    }

    @PostConstruct
    public void loadSql() {
        this.baseSqlService.setSqlReader(this.oxmSqlReader);
        this.baseSqlService.setSqlRegistry(this.sqlRegistry);

        this.baseSqlService.loadSql();
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        return this.baseSqlService.getSql(key);
    }

    private class OxmSqlReader implements SqlReader {
        private Unmarshaller unmarshaller;
        private final static String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
        private Resource sqlmap = new ClassPathResource(DEFAULT_SQLMAP_FILE);

        public void setUnmarshaller(Unmarshaller unmarshaller) {
            this.unmarshaller = unmarshaller;
        }

        public void setSqlmap(Resource sqlmap) {
            this.sqlmap = sqlmap;
        }

        @Override
        public void read(SqlRegistry sqlRegistry) {
            try {
//                Source source = new StreamSource(
//                        UserDao.class.getResourceAsStream(this.sqlmapFile));
                Source source = new StreamSource(sqlmap.getInputStream());
                Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);

                for (SqlType sql : sqlmap.getSql()) {
                    sqlRegistry.registerSql(sql.getKey(), sql.getValue());
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(
                    "Can't find SQL with " + this.sqlmap.getFilename(), e);
            }
        }
    }
}
