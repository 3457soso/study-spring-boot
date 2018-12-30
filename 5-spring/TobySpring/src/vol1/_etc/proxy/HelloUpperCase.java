package _etc.proxy;

public class HelloUpperCase implements Hello {
    Hello hello;

    public HelloUpperCase(Hello hello) {
        this.hello = hello;
    }

    @Override
    public String sayHello(String name) {
        return hello.sayHello(name).toUpperCase();
    }

    @Override
    public String sayHi(String name) {
        return hello.sayHi(name).toUpperCase();
    }

    @Override
    public String sayThankyou(String name) {
        return hello.sayThankyou(name).toUpperCase();
    }
}
