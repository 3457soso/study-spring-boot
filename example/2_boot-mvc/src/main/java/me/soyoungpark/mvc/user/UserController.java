package me.soyoungpark.mvc.user;

import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    // 요청의 본문에 데이터가 들어온다. 이를 객체로 받고 싶다.
    // 그리고 해당 객체를 응답 본문으로 보내고 싶다.
    @PostMapping("/users")
    public User create(@RequestBody User user) {
        return user;
    }


}
