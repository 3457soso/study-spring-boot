package me.soyoungpark.nosql.redis;


import org.springframework.data.repository.CrudRepository;

// vo의 타입과 key의 타입을 줘야 한다.
public interface RedisAccountRepository extends CrudRepository<Account, String> {}
