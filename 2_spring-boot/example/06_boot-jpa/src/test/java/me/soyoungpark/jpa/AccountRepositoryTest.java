package me.soyoungpark.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void di() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println(metaData.getURL());
            System.out.println(metaData.getDriverName());
            System.out.println(metaData.getUserName());

            Account account = new Account();
            account.setUsername("soyoungpark");
            account.setPassword("qwer1234");

            Account newAccount = accountRepository.save(account);

            assertThat(newAccount).isNotNull();

            Account existingAccount = accountRepository.findByUsername(newAccount.getUsername());
            assertThat(existingAccount).isNotNull();

            Account notExistingAccount = accountRepository.findByUsername("troll");
            assertThat(notExistingAccount).isNotNull();

        }
    }
}