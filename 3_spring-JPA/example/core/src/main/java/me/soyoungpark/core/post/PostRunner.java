package me.soyoungpark.core.post;

import org.hibernate.Session;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Transactional
@Component
public class PostRunner implements ApplicationRunner {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Post post = new Post();
        post.setTitle("TITLE");

        Comment comment1 = new Comment();
        comment1.setContents("COMMENT 1");
        post.addComment(comment1);

        Comment comment2 = new Comment();
        comment2.setContents("COMMENT 2");
        post.addComment(comment2);

        Session session = entityManager.unwrap(Session.class);
        session.save(post);
    }
}
