package users.dao;

import users.domain.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import users.sql.sqlservice.SqlService;
import users.vo.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository /* 자동으로 빈을 등록해준다 */
public class UserDaoJdbc implements UserDao {
    private JdbcTemplate jdbcTemplate;
    @Autowired private SqlService sqlService;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /*  Autowired를 사용해서 필요 없다!
    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
    }
    */

    private RowMapper<User> userMapper =
            new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet resultSet, int i) throws SQLException {
                    User user = new User();
                    user.setId(resultSet.getString("id"));
                    user.setEmail(resultSet.getString("email"));
                    user.setName(resultSet.getString("name"));
                    user.setPassword(resultSet.getString("password"));
                    user.setLevel(Level.valueOf(resultSet.getInt("level")));
                    user.setLogin(resultSet.getInt("login"));
                    user.setRecommend(resultSet.getInt("recommend"));

                    return user;
                }
            };

    public void add(final User user) {
        this.jdbcTemplate.update(this.sqlService.getSql("userAdd"), user.getId(), user.getEmail(),
                user.getName(), user.getPassword(),
                user.getLevel().intValue(), user.getLogin(), user.getRecommend());
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject(
                this.sqlService.getSql("userGet"), // SQL
                new Object[]{id},                      // 바인딩해줄 실제 값
                userMapper
        );
    }

    public List<User> getAll() {return this.jdbcTemplate.query(
            this.sqlService.getSql("userGetAll"),
            userMapper
    );

    }

    public void deleteAll() {
        this.jdbcTemplate.update(this.sqlService.getSql("userDeleteAll"));
    }

    public int getCount() {
        return this.jdbcTemplate.queryForObject(
                this.sqlService.getSql("userGetCount"), Integer.class);
    }

    @Override
    public void update(User user1) {
        this.jdbcTemplate.update(
                this.sqlService.getSql("userUpdate"),
                user1.getEmail(), user1.getName(), user1.getPassword(),
                user1.getLevel().intValue(), user1.getLogin(), user1.getRecommend(),
                user1.getId()
        );
    }
}