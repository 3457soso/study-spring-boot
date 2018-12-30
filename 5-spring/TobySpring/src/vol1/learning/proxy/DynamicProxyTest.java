package learning.proxy;

import _etc.proxy.Hello;
import _etc.proxy.HelloTarget;
import _etc.proxy.HelloUpperCaseHandler;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DynamicProxyTest {
    @Test
    public void simpleProxy() {
        System.out.println("simpleProxy Test is started... ");

        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] { Hello.class },
                new HelloUpperCaseHandler(new HelloTarget())
        );

        assertThat(proxiedHello.sayHello("Soyoung"), is("HELLO SOYOUNG"));
        assertThat(proxiedHello.sayHi("Soyoung"), is("HI SOYOUNG"));
        assertThat(proxiedHello.sayThankyou("Soyoung"), is("THANK YOU SOYOUNG"));
    }

    @Test
    public void proxyFactoryBean() {
        System.out.println("proxyFactoryBean Test is started... ");

        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UpperCaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();
        assertThat(proxiedHello.sayHello("Soyoung"), is("HELLO SOYOUNG"));
        assertThat(proxiedHello.sayHi("Soyoung"), is("HI SOYOUNG"));
        assertThat(proxiedHello.sayThankyou("Soyoung"), is("THANK YOU SOYOUNG"));
    }

    @Test
    public void pointcutAdvisor() {
        System.out.println("pointcutAdvisor Test is started... ");

        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UpperCaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();
        assertThat(proxiedHello.sayHello("Soyoung"), is("HELLO SOYOUNG"));
        assertThat(proxiedHello.sayHi("Soyoung"), is("HI SOYOUNG"));
        assertThat(proxiedHello.sayThankyou("Soyoung"), is("Thank you Soyoung"));
    }

    @Test
    public void classNamePointcutAdvisor() {
        // 포인트컷 준비
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
            @Override
            public ClassFilter getClassFilter() {
                return new ClassFilter() {
                    @Override
                    public boolean matches(Class<?> clazz) {
                        return clazz.getSimpleName().startsWith("HelloT");
                    }
                };
            }
        };
        classMethodPointcut.setMappedName("sayH*");

        // 테스트
        checkAdvice(new HelloTarget(), classMethodPointcut, true);

        class HelloWorld extends HelloTarget {};
        checkAdvice(new HelloWorld(), classMethodPointcut, false);

        class HelloToby extends HelloTarget {};
        checkAdvice(new HelloToby(), classMethodPointcut, true);
    }

    private void checkAdvice(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UpperCaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        if (adviced) {
            assertThat(proxiedHello.sayHello("Soyoung"), is("HELLO SOYOUNG"));
            assertThat(proxiedHello.sayHi("Soyoung"), is("HI SOYOUNG"));
            assertThat(proxiedHello.sayThankyou("Soyoung"), is("Thank you Soyoung"));
        } else {
            assertThat(proxiedHello.sayHello("Soyoung"), is("Hello Soyoung"));
            assertThat(proxiedHello.sayHi("Soyoung"), is("Hi Soyoung"));
            assertThat(proxiedHello.sayThankyou("Soyoung"), is("Thank you Soyoung"));
        }
    }

    static class UpperCaseAdvice implements MethodInterceptor{

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            String ret = (String) methodInvocation.proceed();
            return ret.toUpperCase();
        }
    }
}
