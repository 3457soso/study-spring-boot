package me.soyoungpark.nosql.neo4j;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class Neo4jRunner implements ApplicationRunner {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    Neo4jAccountRepository neo4jAccountRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Account account = new Account();
        account.setEmail("soyoungpark.me@gmail.com");
        account.setUsername("soyoungpark");

        Role role = new Role();
        role.setName("admin");
        account.getRoles().add(role);

        neo4jAccountRepository.save(account);

        Session session = sessionFactory.openSession();
        session.save(account);
        sessionFactory.close(); // 닫아줘야 앱이 뜬다!
    }
}
