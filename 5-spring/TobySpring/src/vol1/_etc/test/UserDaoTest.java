package _etc.test;

import _etc.factory.SimpleConnectionMaker;
import org.springframework.context.support.GenericXmlApplicationContext;
import users.vo.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        ConnectionMaker connectionMaker = new DConnectionMaker();
//        UserDao dao = new UserDao(connectionMaker);

//        UserDao dao = new DaoFactory().userDao();

//        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        GenericXmlApplicationContext context =
                new GenericXmlApplicationContext("applicationContext.xml");
        SimpleConnectionMaker.UserDao dao = context.getBean("userDao", SimpleConnectionMaker.UserDao.class);

        User user = new User();
        user.setId("3457soso");
        user.setName("soyoungpark");
        user.setPassword("qwer1234");

        dao.add(user);

        System.out.println(user.getId() + "등록 성공!");

        User user2 = dao.get(user.getId());

        if (!user.getName().equals(user2.getName())) {
            System.out.println("Test Failed (name)");
        } else if (!user.getPassword().equals(user2.getPassword())) {
            System.out.println("Test Failed (password");
        } else {
            System.out.println("Select Test Success");
        }
    }
}
