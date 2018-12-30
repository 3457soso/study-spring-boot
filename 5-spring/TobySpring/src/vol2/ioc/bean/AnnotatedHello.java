package vol2.ioc.bean;

import org.springframework.stereotype.Component;

@Component
public class AnnotatedHello {
    public String toString() {
        return "I am AnnotatedHello";
    }
}
