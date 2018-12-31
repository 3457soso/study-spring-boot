package me.soyoungpark.bootproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("prod") // 이 설정 파일 자체가 prod 상태일 때 사용된다.
@Configuration
public class BaseConfiguration {

    @Bean
    public String hello() {
        return "hello";
    }

}
