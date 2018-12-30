package users.test;

import users.dao.UserDao;
import users.domain.Level;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import users.AppContext;
import users.vo.User;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test") /* 해당 프로파일을 활성화시켜야 함 */
@ContextConfiguration(classes = AppContext.class)
public class UserDaoTest {
    @Autowired private ApplicationContext context;
    @Autowired private UserDao dao;
    @Autowired DefaultListableBeanFactory df; /* 등록된 빈 조회 */

    private User user1;
    private User user2;
    private User user3;

    @Before
    public void init() {
        user1 = new User("user1", "email1@gmail.com", "name1", "password1", Level.BASIC, 49, 0);
        user2 = new User("user2", "email2@gmail.com", "name2", "password2", Level.BASIC, 50, 0);
        user3 = new User("user3", "email3@gmail.com", "name3", "password3", Level.SILVER, 60, 29);
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        System.out.println("addAndGet Test is started... ");

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userget1 = dao.get(user1.getId());
        checkSameUser(userget1, user1);

        User userget2 = dao.get(user2.getId());
        checkSameUser(userget2, user2);
    }

    @Test
    public void count() throws ClassNotFoundException, SQLException {
        System.out.println("count Test is started... ");
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));

        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        dao.add(user3);
        assertThat(dao.getCount(), is(3));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws ClassNotFoundException, SQLException {
        System.out.println("getUserFailure Test is started... ");
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.get("unknown_id");
    }

    @Test
    public void getAll() throws SQLException{
        System.out.println("getAll Test is started... ");
        dao.deleteAll();

        List<User> users0 = dao.getAll();
        assertThat(users0.size(), is(0));

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));
    }

    @Test
    public void update() {
        System.out.println("update Test is started... ");

        dao.deleteAll();
        dao.add(user1); // 수정할 사용자
        dao.add(user2); // 수정하지 않을 사용자

        user1.setName("newName");
        user1.setPassword("newPasswd");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);

        User user1update = dao.get(user1.getId());
        checkSameUser(user1, user1update);
        User user2same = dao.get(user2.getId());
        checkSameUser(user2, user2same);
    }

    @Test
    public void listingRegisteredBeans() {
        System.out.println("listingRegisteredBeans Test is started... ");

        for (String n : df.getBeanDefinitionNames()) {
            System.out.println(n + "\t" + df.getBean(n).getClass().getName());
        }
    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getEmail(), is(user2.getEmail()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
        assertThat(user1.getLevel(), is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
    }
}