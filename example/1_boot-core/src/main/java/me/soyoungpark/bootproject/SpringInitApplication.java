package me.soyoungpark.bootproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
//@EnableConfigurationProperties(SoyoungProperties.class) // 사용할 프로퍼티 추가
public class SpringInitApplication {

    @ConfigurationProperties("server")
    @Bean
    public ServerProperties serverProperties() {
        return new ServerProperties();
    }

    public static void main(String[] args) {
        /* 기본
        SpringApplication.run(SpringInitApplication.class, args);
        // 이 부분까지 작성하는 것이 스프링 부트를 시작하는 기본이 된다.
        */

        SpringApplication app = new SpringApplication(SpringInitApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}
