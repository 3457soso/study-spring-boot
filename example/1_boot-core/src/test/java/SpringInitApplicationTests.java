import me.soyoungpark.bootproject.SpringInitApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
// 프로퍼티를 주는 방법들...
// @TestPropertySource(properties = "soyoung.name=kong")
// @SpringBootTest(classes = SpringInitApplication.class, properties = "soyoung.name=kong")
@TestPropertySource(locations = "classpath:/test.properties") // application.properties 재정의 X
@SpringBootTest(classes = SpringInitApplication.class)
public class SpringInitApplicationTests {
    @Autowired
    Environment environment;

    @Test
    public void contextLoads() {
        assertEquals(environment.getProperty("soyoung.name"), "Kong");
    }
}
