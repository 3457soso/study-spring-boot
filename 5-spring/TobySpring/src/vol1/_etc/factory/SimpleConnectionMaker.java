package _etc.factory;

import org.springframework.dao.EmptyResultDataAccessException;
import users.vo.User;

import javax.sql.DataSource;
import java.sql.*;

public class SimpleConnectionMaker {
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");

        Connection c = DriverManager.getConnection(
            "Jdbc:mysql://localhost/toby?autoReconnect=true&useSSL=false", "study", "1111");

        return c;
    }

    public static class UserDao {
    //	private SimpleConnectionMaker simpleConnectionMaker;
        private ConnectionMaker connectionMaker;

        private DataSource dataSource;

        UserDao() {}

        UserDao(ConnectionMaker connectionMaker) {
    //		simpleConnectionMaker = new SimpleConnectionMaker();
    //		connectionMaker = new DConnectionMaker();
            this.connectionMaker = connectionMaker;

            try {
                init();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void setConnectionMaker(ConnectionMaker connectionMaker) {
            this.connectionMaker = connectionMaker;
        }

        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        private void init() throws ClassNotFoundException, SQLException {
            Connection c = connectionMaker.makeConnection();

            PreparedStatement ps = c.prepareStatement("DELETE FROM users");
            ps.executeUpdate();
            ps.close();
            c.close();
        }

        public void add(User user) throws ClassNotFoundException, SQLException {
    //		Connection c = connectionMaker.makeConnection();
            Connection c = dataSource.getConnection();

            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO users(id, name, password) values(?, ?, ?)");
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());

            ps.executeUpdate();

            ps.close();
            c.close();
        }

        public User get(String id) throws ClassNotFoundException, SQLException {
    //		Connection c = connectionMaker.makeConnection();
            Connection c = dataSource.getConnection();

            PreparedStatement ps = c.prepareStatement(
                    "SELECT * FROM users WHERE id = ?");
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();

            User user = null;

            if (rs.next()) {
                user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
            }

            rs.close();
            ps.close();
            c.close();

            if (user == null) {
                throw new EmptyResultDataAccessException(1);
            }

            return user;
        }

        public void deleteAll() throws SQLException {
            Connection c = null;
            PreparedStatement ps = null;

            try {
                c = dataSource.getConnection();
                ps = c.prepareStatement("DELETE from users");
                ps.executeUpdate();
            } catch (SQLException e) {
                throw e;
            } finally { // try 블록에서 예외가 발생하든 바생하지 않든 실행된다.
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {}
                }

                if (c != null) {
                    try {
                        c.close();
                    } catch (SQLException e) {}
                }
            }
        }

        public int getCount() throws SQLException {
            Connection c = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                c = dataSource.getConnection();

                ps = c.prepareStatement("SELECT count(*) from users");
                rs = ps.executeQuery();
                rs.next();
                return rs.getInt(1);
            } catch (SQLException e) {
                throw e;
            } finally {
                // close()는 만들어진 순서의 반대로 하는 것이 원칙이다.
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {}
                }
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {}
                }
                if (c != null) {
                    try {
                        c.close();
                    } catch (SQLException e) {}
                }
            }
        }

    //	public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
    }
}
