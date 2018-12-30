package me.soyoungpark.nosql.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

//@Component
public class MongoRunner implements ApplicationRunner {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MongoRepository mongoRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Account account = new Account();
        account.setEmail("soyoungpark.me@gmail.com");
        account.setUsername("soyoungpark");

        mongoTemplate.insert(account);
        mongoRepository.save(account);
    }
}
