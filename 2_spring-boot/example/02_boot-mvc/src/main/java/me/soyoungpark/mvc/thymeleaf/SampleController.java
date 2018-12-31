package me.soyoungpark.mvc.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SampleController {

    @GetMapping("/thymeleaf")
    public String hello(Model model) {
        model.addAttribute("name", "soyoung");

        return "thymeleaf";
    }

}
