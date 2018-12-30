package me.soyoungpark.nosql.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "accounts")
public class Account {

    @Id
    private String id;

    private String username;

    private String email;

}
