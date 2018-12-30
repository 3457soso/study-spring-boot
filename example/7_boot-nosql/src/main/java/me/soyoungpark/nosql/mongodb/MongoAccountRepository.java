package me.soyoungpark.nosql.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoAccountRepository extends MongoRepository<Account, String> {
    Optional<Account> findByEmail(String email);
}
