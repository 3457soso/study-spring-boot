package me.soyoungpark.core.account;

import org.hibernate.Session;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

//@Component
public class AccountRunner implements ApplicationRunner {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Account account = new Account();
        account.setUsername("soyoung");
        account.setPassword("qwer1234");

        Study study = new Study();
        study.setName("String Data JPA");
        study.setOwner(account);

        Session session = entityManager.unwrap(Session.class);
        session.save(study);
        account.getStudies().add(study);

        session.save(account);
    }
}
