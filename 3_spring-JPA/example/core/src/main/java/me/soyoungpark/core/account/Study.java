package me.soyoungpark.core.account;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Study {

    @Id @GeneratedValue
    private Long id;

    @Column
    private String name;

    @ManyToOne
    private Account owner;

}
