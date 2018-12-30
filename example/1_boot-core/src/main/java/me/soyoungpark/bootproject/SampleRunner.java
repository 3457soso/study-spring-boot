package me.soyoungpark.bootproject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component // 빈으로 등록해야 실행이 된다...
public class SampleRunner implements ApplicationRunner {

    private Logger logger = LoggerFactory.getLogger(SampleRunner.class);

    /*
    application.properties 사용

    @Value("${soyoung.name}")
    private String name;

    @Value("${soyoung.age}")
    private int age;

    @Value("${soyoung.fullName}")
    private String fullName;

    */

    @Autowired
    SoyoungProperties properties;

    @Autowired
    private String hello;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("====================");
//        System.out.println(name);
//        System.out.println(age);
//        System.out.println(fullName);
//        System.out.println(properties.getName());
//        System.out.println(properties.getAge());
//        System.out.println(properties.getFullName());
//        System.out.println(properties.getDuration());
//        System.out.println(hello);
//        System.out.println("====================");

        logger.info("====================");
        logger.info(hello);
        logger.info(properties.getName());
        logger.info(properties.getFullName());
        logger.info("====================");
    }
}
