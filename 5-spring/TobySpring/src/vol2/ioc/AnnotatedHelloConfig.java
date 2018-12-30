package vol2.ioc;

import vol2.ioc.bean.AnnotatedHello;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnnotatedHelloConfig {
    @Bean
    public AnnotatedHello annotatedHello() {
        return new AnnotatedHello();
    }
}
