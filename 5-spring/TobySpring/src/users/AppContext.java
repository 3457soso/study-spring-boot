package users;

import users.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import users.service.DummyMailSender;
import users.service.UserService;
import users.sql.sqlmapping.SqlMapConfig;
import users.sql.sqlmapping.UserSqlMapConfig;
import users.test.UserServiceTest;

import javax.sql.DataSource;
import java.sql.Driver;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "users.dao") /* 전체를 다 뒤지지 말고 패키지를 지정한다 */
@PropertySource("classpath:/users/raw/properties/database.properties") /* 프로퍼티 소스 등록 */
@EnableSqlService
/*
    @Import : 자바 클래스로 된 설정 정보를 가져온다
        AppContext.TestAppContext.class, AppContext.ProductionAppContext.class
            얘들은 중첩 멤버 클래스로 정의되어 따로 Import 해주지 않아도 된다.
        SqlServiceContext.class
            custom 애노테이션 (EnableSqlService)를 생성해서 따로 Import 해주지 않아도 된다.
*/

public class AppContext implements SqlMapConfig {
    @Autowired Environment env; /* 컨테이너가 관리하는 환경 오브젝트, 여기서 프로퍼티를 끌어온다. */
    @Autowired UserDao userDao;

    /* @Value와 치환자를 이용해 프로퍼티 설정. @Value는 해당 값을 주입해준다. */
    @Value("${db.driverClass}") private Class<? extends Driver> driverClass;
    @Value("${db.url}")         private String url;
    @Value("${db.username}")    private String username;
    @Value("${db.password}")    private String password;

    @Bean /* @Value와 치환자를 이용해 프로퍼티 값을 필드에 주입할 때 필요 ## 반드시 스태틱으로 선언! ## */
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * DB 연결과 트랜잭션
     */

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        /*
        1. 프로퍼티로 설정해주는 것으로 변경
            dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
            dataSource.setUrl("Jdbc:mysql://localhost/toby?autoReconnect=true&amp;useSSL=false");
            dataSource.setUsername("study");
            dataSource.setPassword("1111");
        */

        try {
            dataSource.setDriverClass((Class<? extends java.sql.Driver>)
                    Class.forName(env.getProperty("db.driverClass")));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));

        /*
        2. @Value와 치환자를 사용하는 것으로 변경 >> 하려고 했으나 필드가 null이 됨..
            dataSource.setDriverClass(this.driverClass);
            dataSource.setUrl(this.url);
            dataSource.setUsername(this.username);
            dataSource.setPassword(this.password);
        */

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource());

        return tm;
    }

    @Override
    public Resource getSqlMapResource() {
        return new ClassPathResource("sqlmap.xml", UserDao.class);
    }

    @Profile("production") /* production 프로파일의 빈 설정정보를 담은 클래스 */
    public static class ProductionAppContext {
        @Bean
        public MailSender mailSender() {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("localhost");
            return mailSender;
        }
    }

    @Configuration
    @Profile("test") /* test 프로파일의 빈 설정정보를 담은 클래스 */
    @ComponentScan(basePackages = "users.dao")
    public static class TestAppContext {
        @Autowired
        UserDao userDao;

        /**
         * 어플리케이션 로직 & 테스트
         */

        @Bean
        public UserService testUserService() {
            /*
             * TestUserService 클래스는 UserServiceImpl을 상속받기 때문에
             * 굳이 UserDao와 Mailsender를 세팅해주지 않아도 된다.
             * UserServiceImpl이 알아서 가져오기 때문!
             */
            return new UserServiceTest.TestUserService();
        }

        @Bean
        public MailSender mailSender() {
            return new DummyMailSender();
        }
    }
}

/* Autowired, Component를 사용해서 필요 없다!

/**
 * 어플리케이션 로직 & 테스트

@Bean
public UserDao userDao() {
    UserDaoJdbc userDao = new UserDaoJdbc();
    userDao.setSqlService(sqlService());
    userDao.setDataSource(dataSource());

    return new UserDaoJdbc();
}
@Bean
public UserService userService() {
    UserServiceImpl userService = new UserServiceImpl();
    userService.setUserDao(this.userDao);
    userService.setMailSender(mailSender());

    return userService;
}

직접 구현해서 AppContext 자체를 빈으로 SqlServiceContext에 넘길 것!
    @Bean
    public SqlMapConfig sqlMapConfig() {
        return new UserSqlMapConfig();
    }


*/
