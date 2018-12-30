package _etc.template;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import users.vo.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDao {
    private JdbcTemplate jdbcTemplate;
//    private JdbcContext jdbcContext;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate();
        this.jdbcTemplate.setDataSource(dataSource);
    }

    private RowMapper<User> userMapper =
        new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(resultSet.getString("id"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));

                return user;
            }
    };

    /*
    // 로컬 클래스의 코드에서 외부의 로컬 변수에 접근하기 위해선 final로 선언해 줘야 한다.
    public void create(final User user) throws SQLException {
        StatementStrategy st = new StatementStrategy() {
            public PreparedStatement makePreParedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO users (id, name, password) VALUES(?, ?, ?)"
                );

                ps.setString(1, user.getId());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPassword());

                return ps;
            }
        };

        this.jdbcContext.workWithStatementStrategy(st);
    }


    // 굳이 변수에 담아 두지 말고 파라미터에서 그냥 바로 생성한다.
    public void deleteAll() throws SQLException {
        this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
            public PreparedStatement makePreParedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement("DELETE FROM users");

                return ps;
            }
        });
    }
    */

    public void create(final User user) {
        String query = "INSERT INTO users (id, name, password) VALUES(?, ?, ?)";
//        this.jdbcContext.executeSql(query, user.getId(), user.getName(), user.getPassword());

        this.jdbcTemplate.update(query, user.getId(), user.getName(), user.getPassword());
    }

    public void deleteAll() {
        String query = "DELETE FROM users";
//        this.jdbcContext.executeSql(query);

        /*
            this.jdbcContext.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        return connection.prepareStatement(query);
                    }
                }
            );
         */
        this.jdbcTemplate.update(query);
    }

    public User get(String id) {
        String query = "SELECT * FROM users WHERE id = ?";

        return this.jdbcTemplate.queryForObject(
            query,                      // SQL
            new Object[]{id},           // 바인딩해줄 실제 값
            userMapper
        );
    }

    public List<User> getAll() {
        String query = "SELECT * FROM users ORDER BY id";

        return this.jdbcTemplate.query(
            query,
            userMapper
        );

    }

    public int getCount() {
        String query = "SELECT count(*) FROM users";

        /*
        return this.jdbcTemplate.query(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        return connection.prepareStatement(query);
                    }
                }, new ResultSetExtractor<Integer>() {
                    @Override
                    public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                        resultSet.next();

                        return resultSet.getInt(1);
                    }
                }
        );
        */

        return this.jdbcTemplate.queryForObject(query, Integer.class);
    }
}
