package vol2.db.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;

public class MemberDao {
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert memberInsert;
    private SimpleJdbcCall memberFindCall;

    @Autowired
    public void init(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.memberInsert = new SimpleJdbcInsert(dataSource).withTableName("member");
        this.memberFindCall = new SimpleJdbcCall(dataSource).withFunctionName("find_member");
    }
}
