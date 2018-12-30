package users.sql.sqlregistry;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import users.sql.exception.SqlNotFoundException;
import users.sql.exception.SqlUpdateFailureException;

import javax.sql.DataSource;
import java.util.Map;

public class EmbeddedDBSqlRegistry implements UpdatableSqlRegistry {
    JdbcTemplate jdbc;

    public void setDataSource(DataSource dataSource) {
        jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public void registerSql(String key, String sql) {
        jdbc.update("INSERT INTO SQLMAP(KEY_, SQL_) VALUES(?,?)", key, sql);
    }

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        try {
            return jdbc.queryForObject(
                    "SELECT SQL_ FROM SQLMAP WHERE KEY_=?", String.class, key);
        } catch (EmptyResultDataAccessException e) {
            throw new SqlNotFoundException("Can't find SQL with " + key, e);
        }
    }

    @Override
    public void updateSql(String key, String sql) throws SqlUpdateFailureException {
        int affected = jdbc.update("UPDATE SQLMAP SET SQL_=? WHERE KEY_=?", sql, key);

        if (affected == 0) {
            throw new SqlNotFoundException("Can't find SQL with " + key);
        }
    }

    @Override
    public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
        for (Map.Entry<String, String> entry : sqlmap.entrySet()) {
            updateSql(entry.getKey(), entry.getValue());
        }
    }

}
