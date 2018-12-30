package _etc.template;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {
    private DataSource dataSource;

    public void setDataSource (DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();
            ps = stmt.makePreParedStatement(c);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) { try {ps.close(); } catch (SQLException e) {} }
            if (c != null) { try {c.close(); } catch (SQLException e) {} }
        }
    }

    public void executeSql(final String query, final String... parameter) throws SQLException {
        workWithStatementStrategy(new StatementStrategy() {
            public PreparedStatement makePreParedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement(query);

                if (parameter != null) {
                    for (int i=0; i<parameter.length; i++) {
                        ps.setString(i+1, parameter[i]);
                    }
                }

                return ps;
            }
        });
    }
}
