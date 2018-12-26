package me.soyoungpark.bootproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringInitApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringInitApplication.class, args);
        // 이 부분까지 작성하는 것이 스프링 부트를 시작하는 기본이 된다.
    }
}
