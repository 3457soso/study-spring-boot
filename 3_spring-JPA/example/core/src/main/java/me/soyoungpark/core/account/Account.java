package me.soyoungpark.core.account;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Account {

    @Id @GeneratedValue
    private Long Id;

    @Column
    private String username;

    @Column
    private String password;

    @OneToMany(mappedBy = "owner")
    private Set<Study> studies = new HashSet<>();

}
