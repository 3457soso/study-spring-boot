package users.sql.sqlservice;

import users.sql.sqlreader.JaxbXmlSqlReader;
import users.sql.sqlregistry.HashMapSqlRegistry;

public class DefaultSqlService extends BaseSqlService {
    public DefaultSqlService() {
        setSqlReader(new JaxbXmlSqlReader());
        setSqlRegistry(new HashMapSqlRegistry());
    }
}
