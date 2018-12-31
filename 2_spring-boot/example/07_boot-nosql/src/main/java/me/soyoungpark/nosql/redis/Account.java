package me.soyoungpark.nosql.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash("accounts")
public class Account {

    @Id
    private String id;

    private String username;

    private String email;

}
