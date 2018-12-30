package users.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import _etc.template.UserDao;
import users.vo.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

// import에 주의한다

/*
 JUnit이 테스트 클래스를 가져와 테스트를 수행하는 과정
    1. 테스트 클래스에서 @Test가 붙은 public이고 void형이며 파라미터가 없는 테스트 메소드를 모두 찾는다.
    2. 테스트 클래스의 오브젝트를 하나 만든다.
    3. @Before가 붙은 메소드가 있으면 먼저 실행한다.
    4. @Test가 붙은 메소드를 하나 호출하고 테스트 결과를 저장해둔다.
    5. @After가 붙은 메소드가 있으면 실행한다.
    6. 나머지 테스트 메소드에 의해 2~5번을 반복한다.
    7. 모든 테스트의 결과를 종합해서 보여준다.
 */

/*
    @RunWith : 스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
    @ContextConfiguration : 테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트의 위치 지정
    @DirtiesContext :  테스트 메소드에서 AC의 구성이나 상태를 변경한다는 것을 테스트 컨텍스트 프레임워크에 알려준다.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/users/applicationContext.xml")
@DirtiesContext
public class UserDaoTestForTemplate {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserDao dao;
    private User user1;
    private User user2;
    private User user3;

    @Before
    public void init() {
//        dao = context.getBean("userDao", UserDao.class);
        DataSource dataSource = new SingleConnectionDataSource(
                "Jdbc:mysql://localhost/toby?autoReconnect=true&amp;useSSL=false",
                "study", "1111", true
        );

        user1 = new User("id1", "name1", "password1");
        user2 = new User("id2", "name2", "password2");
        user3 = new User("aid3", "name3", "password3");
    }

    // @Test는 해당하는 함수에 붙여야 한다!
//    @Test
//    public void addAndGet() throws SQLException, ClassNotFoundException {
//        System.out.println("addAndGet Test is started... ");
//        dao.deleteAll();
//        assertThat(dao.getCount(), is(0));
//
//        dao.create(user1);
//        dao.create(user2);
//        assertThat(dao.getCount(), is(2));
//
//        User userget1 = dao.get(user1.getId());
//        // assertThat은 첫 번째 match 조건에 따라, 일치하면 넘어가고 불일치하면 테스트 실패를 반환한다.
//        assertThat(userget1.getName(), is(user1.getName()));
//        assertThat(userget1.getPassword(), is(user1.getPassword()));
//
//        User userget2 = dao.get(user2.getId());
//        assertThat(userget2.getName(), is(user2.getName()));
//        assertThat(userget2.getPassword(), is(user2.getPassword()));
//    }
//
//    @Test
//    public void count() throws ClassNotFoundException, SQLException {
//        System.out.println("count Test is started... ");
//        dao.deleteAll();
//        assertThat(dao.getCount(), is(0));
//
//        dao.create(user1);
//        assertThat(dao.getCount(), is(1));
//
//        dao.create(user2);
//        assertThat(dao.getCount(), is(2));
//
//        dao.create(user3);
//        assertThat(dao.getCount(), is(3));
//    }
//
//    @Test(expected = EmptyResultDataAccessException.class)
//    public void getUserFailure() throws ClassNotFoundException, SQLException {
//        System.out.println("getUserFailure Test is started... ");
//        dao.deleteAll();
//        assertThat(dao.getCount(), is(0));
//
//        dao.get("unknown_id");
//    }
//
//    @Test
//    public void getAll() throws SQLException{
//        System.out.println("getAll Test is started... ");
//        dao.deleteAll();
//
//        List<User> users0 = dao.getAll();
//        assertThat(users0.size(), is(0));
//
//        dao.create(user1);
//        List<User> users1 = dao.getAll();
//        assertThat(users1.size(), is(1));
//        checkSameUser(user1, users1.get(0));
//
//        dao.create(user2);
//        List<User> users2 = dao.getAll();
//        assertThat(users2.size(), is(2));
//        checkSameUser(user1, users2.get(0));
//        checkSameUser(user2, users2.get(1));
//
//        dao.create(user3);
//        List<User> users3 = dao.getAll();
//        assertThat(users3.size(), is(3));
//        checkSameUser(user3, users3.get(0));
//        checkSameUser(user1, users3.get(1));
//        checkSameUser(user2, users3.get(2));
//    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
    }
}
