package me.soyoungpark.core.post;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Comment {

    @Id @GeneratedValue
    private Long id;

    private String contents;

    @ManyToOne
    private Post post;


}
