package users.test;

import users.dao.UserDao;
import users.domain.Level;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import users.AppContext;
import users.service.UserService;
import users.service.UserServiceImpl;
import users.vo.User;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


//@ContextConfiguration(locations = "/applicationContext.xml") > 자바 클래스로 DI
//@ContextConfiguration(classes = {TestAppContext.class, AppContext.class}) > 프로파일 설정
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test") /* 해당 프로파일을 활성화시켜야 함 */
@ContextConfiguration(classes = AppContext.class)
public class UserServiceTest {
    @Autowired ApplicationContext context;
    @Autowired UserService userService;
    @Autowired UserService testUserService;
    @Autowired UserDao userDao;
    @Autowired DataSource dataSource;
    @Autowired MailSender mailSender;
    @Autowired PlatformTransactionManager transactionManager;
    List<User> users;

    @Before
    public void init() {
        users = Arrays.asList(
                new User("user1", "email1@gmail.com", "name1", "password1", Level.BASIC, 49, 0),
                new User("user2", "email2@gmail.com", "name2", "password2", Level.BASIC, 50, 0),
                new User("user3", "email3@gmail.com", "name3", "password3", Level.SILVER, 60, 29),
                new User("user4", "email4@gmail.com", "name4", "password4", Level.SILVER, 60, 30),
                new User("user5", "email5@gmail.com", "name5", "password5", Level.GOLD, 100, 100)
        );
    }

    public static class TestUserService extends UserServiceImpl {
        private String id = "user4";

        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }

        public List<User> getAll() {
            for (User user: super.getAll()) {
                super.update(user);
            }
            return null;
        }
    }

    static class TestUserServiceException extends RuntimeException {}

    static class MockMailSedner implements MailSender {
        private List<String> requests = new ArrayList<String>();

        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage simpleMailMessage) throws MailException {
            requests.add(simpleMailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage... simpleMailMessages) throws MailException {}
    }

    static class MockUserDao implements UserDao {
        private List<User> users;
        private List<User> updated = new ArrayList<>();

        private MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return this.updated;
        }

        @Override
        public void update(User user) {
            updated.add(user);
        }

        @Override
        public List<User> getAll() {
            return this.users;
        }

        @Override
        public void add(User user) { throw new UnsupportedOperationException(); }

        @Override
        public User get(String id) { throw new UnsupportedOperationException(); }

        @Override
        public void deleteAll() { throw new UnsupportedOperationException(); }

        @Override
        public int getCount() { throw new UnsupportedOperationException(); }
    }


    @Test
    @DirtiesContext
    public void upgradeLevels() throws Exception {
        System.out.println("upgradeLevels Test is started... ");

        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSedner mockMailSedner = new MockMailSedner();
        userServiceImpl.setMailSender(mockMailSedner);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size(), is(2));
        checkUserAndLevel(updated.get(0), "user2", Level.SILVER);
        checkUserAndLevel(updated.get(1), "user4", Level.GOLD);

        List<String> request = mockMailSedner.getRequests();
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));
    }

    @Test
    public void mockUpgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);
        // 다이내믹한 목 오브젝트 생성과 메소드의 리턴 값 설정하기, DI까지

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);
        // 리턴 값이 없는 메소드의 목 오브젝트 생성

        userServiceImpl.upgradeLevels();

        // 목 오브젝트가 제공하는 검증 기능
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao, times(2)).update(any(User.class));
        // 해당 메소드가 몇 번 호출되었는지 확인

        verify(mockUserDao).update(users.get(1));
        // 해당 값을 파라미터로 메소드가 호출 된 적이 있는지 확인

        assertThat(users.get(1).getLevel(), is(Level.SILVER));
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel(), is(Level.GOLD));

        // 파라미터를 정밀하게 검사하기 위해 캡처할 수도 있음
        ArgumentCaptor<SimpleMailMessage> mailMessageArg =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
    }

    @Test
    public void add() {
        System.out.println("add Test is started... ");

        userDao.deleteAll();

        User userWithLevel = users.get(4); // GOLD
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWIthoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWIthoutLevelRead.getLevel(), is(Level.BASIC));
    }

    @Test
    public void upgradeAllOrNothing() throws Exception {
        System.out.println("upgradeAllOrNothing Test is started... ");

        userDao.deleteAll();
        for (User user : users) userDao.add(user);

        try {
            this.testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {}

        checkLevelUpgraded(users.get(1), false);
    }

    @Test(expected = TransientDataAccessResourceException.class)
    public void readOnlyTransactionAttribute() {
        System.out.println("readOnlyTransactionAttribute Test is started... ");
        testUserService.getAll();
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());

        if (upgraded) {
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        } else {
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId(), is(expectedId));
        assertThat(updated.getLevel(), is(expectedLevel));
    }
}

/*
    @Test
    public void transactionSync() {
        System.out.println("transactionSync Test is started... ");
        userDao.deleteAll();
        assertThat(userDao.getCount(), is(0));
        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
//        txDefinition.setReadOnly(true);
        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
//        userService.deleteAll();
        userService.add(users.get(0));
        userService.add(users.get(1));
        assertThat(userDao.getCount(), is(2));
        transactionManager.rollback(txStatus);
        assertThat(userDao.getCount(), is(0));
//        transactionManager.commit(txStatus);
    }
    @Test
    public void rollbackTest() {
        System.out.println("rollbackTest Test is started... ");
        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
        try {
            userService.deleteAll();
            userService.add(users.get(0));
            userService.add(users.get(1));
        } finally {
            transactionManager.rollback(txStatus);
        }
    }
    @Test
    @Transactional(readOnly = true)
    public void transactionSyncAnnotation() {
        System.out.println("transactionSyncAnnotation Test is started... ");
        userService.deleteAll();
        userService.add(users.get(0));
        userService.add(users.get(1));
    }
*/
