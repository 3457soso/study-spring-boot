package learning.proxy;

import _etc.proxy.Hello;
import _etc.proxy.HelloTarget;
import _etc.proxy.HelloUpperCase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HelloTargetTest {
    @Test
    public void simpleProxy() {
        System.out.println("simpleProxy Test is started... ");

        Hello hello = new HelloTarget();
        assertThat(hello.sayHello("Soyoung"), is("Hello Soyoung"));
        assertThat(hello.sayHi("Soyoung"), is("Hi Soyoung"));
        assertThat(hello.sayThankyou("Soyoung"), is("Thank you Soyoung"));
    }

    @Test
    public void helloUpperCase() {
        System.out.println("helloUpperCase Test is started... ");

        Hello proxiedHello = new HelloUpperCase(new HelloTarget());
        assertThat(proxiedHello.sayHello("Soyoung"), is("HELLO SOYOUNG"));
        assertThat(proxiedHello.sayHi("Soyoung"), is("HI SOYOUNG"));
        assertThat(proxiedHello.sayThankyou("Soyoung"), is("THANK YOU SOYOUNG"));
    }

}
