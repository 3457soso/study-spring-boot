package me.soyoungpark.nosql.neo4j;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@Getter
@Setter
@NodeEntity
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
