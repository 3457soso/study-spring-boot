package me.soyoungpark.mvc.error;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {

    @GetMapping("/exception")
    public String hello() {
        throw new SampleException();
    }

    // 이 핸들러는 이 컨트롤러 안에서만 사용할 수 있다.
    // 전역으로 사용하고 싶다면 클래스를 따로 만들어줘야 한다.
    @ExceptionHandler(SampleException.class) // 이 예외가 발생하면 이 핸들러를 쓰겠다.
    public @ResponseBody AppError sampleError(SampleException e) {
        AppError appError = new AppError();
        appError.setMessage("error.app.key");
        appError.setDescription("Error 발생");

        return appError;
    }
}
