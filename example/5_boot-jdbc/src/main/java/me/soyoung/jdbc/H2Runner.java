package me.soyoung.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class H2Runner implements ApplicationRunner{

    Logger logger = LoggerFactory.getLogger(H2Runner.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        try (Connection connection = dataSource.getConnection()) {
            logger.info(dataSource.getClass().getCanonicalName());

            // 접속한 DB에 대한 정보를 얻을 수 있다.
            logger.info(connection.getMetaData().getDriverName());
            logger.info(connection.getMetaData().getURL());
            logger.info(connection.getMetaData().getUserName());

            jdbcTemplate.execute("DROP TABLE IF EXISTS users");

            Statement statement = connection.createStatement();
            String sql = "CREATE TABLE users (ID INTEGER NOT NULL, name VARCHAR(255), PRIMARY KEY (id))";
            statement.executeUpdate(sql);

            jdbcTemplate.execute("INSERT INTO users VALUES (1, 'soyoung')");
        }
    }
}
