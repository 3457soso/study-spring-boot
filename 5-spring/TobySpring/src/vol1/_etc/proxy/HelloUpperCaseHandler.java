package _etc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class HelloUpperCaseHandler implements InvocationHandler {
    Hello target;

    public HelloUpperCaseHandler(Hello target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = (String)method.invoke(target, args);
        if (ret instanceof String) {
            return ((String)ret).toUpperCase();
        } else {
            return ret;
        }
    }
}
