package me.soyoungpark.cors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @CrossOrigin(origins = "http://locahost:8080")
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

}
