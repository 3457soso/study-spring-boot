package learning.sql;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.util.List;
import java.util.Map;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmbeddedDBTest {
    EmbeddedDatabase db;
    JdbcTemplate template;

    @Before
    public void setUp() {
        db = new EmbeddedDatabaseBuilder()
                .setType(HSQL)
                .addScript("classpath:/vol1.learning.sql/sqlbuilder/schema.vol1.learning.sql")
                .addScript("classpath:/vol1.learning.sql/sqlbuilder/data.vol1.learning.sql")
                .build();

//        스크립트 순서에 주의한다!
        template = new JdbcTemplate(db);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void initData() {
        assertThat(template.queryForObject("SELECT count(*) FROM sqlmap", Integer.class),
                is(2));

        List<Map<String, Object>> list =
                template.queryForList("SELECT * FROM sqlmap ORDER BY key_");

        assertThat((String) list.get(0).get("key_"), is("KEY1"));
        assertThat((String) list.get(0).get("sql_"), is("SQL1"));
        assertThat((String) list.get(1).get("key_"), is("KEY2"));
        assertThat((String) list.get(1).get("sql_"), is("SQL2"));
    }

    @Test
    public void insert() {
        template.update("INSERT INTO sqlmap(key_, sql_) VALUES(?,?)", "KEY3", "SQL3");

        assertThat(template.queryForObject("SELECT count(*) FROM sqlmap", Integer.class),
                is(3));
    }
}
