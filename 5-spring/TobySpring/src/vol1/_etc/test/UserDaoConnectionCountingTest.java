package _etc.test;

import _etc.factory.CountingConnectionMaker;
import _etc.factory.CountingDaoFactory;
import _etc.factory.SimpleConnectionMaker;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class UserDaoConnectionCountingTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(CountingDaoFactory.class);
        SimpleConnectionMaker.UserDao dao = context.getBean("userDao", SimpleConnectionMaker.UserDao.class);

        CountingConnectionMaker ccm =
                context.getBean("connectionMaker", CountingConnectionMaker.class);
        System.out.println("Connection counter: " + ccm.getCount());
    }
}
