package vol2.ioc.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleConfig {
    @Autowired Hello hello;

    @Bean Hello hello() {
        return new Hello();
    }
}
