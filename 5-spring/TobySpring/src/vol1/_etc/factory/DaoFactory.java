package _etc.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {
    @Bean
    public SimpleConnectionMaker.UserDao userDao() {
        ConnectionMaker connectionMaker = new DConnectionMaker();
//        UserDao userDao = new UserDao(connectionMaker);
        SimpleConnectionMaker.UserDao userDao = new SimpleConnectionMaker.UserDao();
//        userDao.setConnectionMaker(connectionMaker);
        userDao.setDataSource(dataSource());
        return userDao;
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("Jdbc:mysql://localhost/toby?autoReconnect=true&useSSL=false");
        dataSource.setUsername("study");
        dataSource.setPassword("1111");

        return dataSource;
    }
}
