package vol2.ioc;

import vol2.ioc.bean.AnnotatedHello;
import vol2.ioc.bean.Hello;
import vol2.ioc.bean.Printer;
import vol2.ioc.bean.StringPrinter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IoCTest {
    StaticApplicationContext ac;

    @Before
    public void init() {
        System.out.println("Initializing...");

        ac = new StaticApplicationContext();
    }

    @Test
    public void IoCRegisterTest() {
        System.out.println("IoCRegisterTest is started...");
        ac.registerSingleton("hello1", Hello.class);

        Hello hello1 = ac.getBean("hello1", Hello.class);
        assertThat(hello1, is(notNullValue()));

        BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        ac.registerBeanDefinition("hello2", helloDef);

        Hello hello2 = ac.getBean("hello2", Hello.class);
        assertThat(hello2.sayHello(), is("Hello Spring"));

        assertThat(hello1, is(not(hello2)));

        assertThat(ac.getBeanFactory().getBeanDefinitionCount(), is(2));
    }

    @Test
    public void registerBeanWithDependencyTest() {
        System.out.println("registerBeanWithDependencyTest is started...");
        ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));

        BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        helloDef.getPropertyValues().addPropertyValue(
                "printer", new RuntimeBeanReference("printer"));

        ac.registerBeanDefinition("hello", helloDef);

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
    }

    @Test
    public void genericApplicationContextTest() {
        System.out.println("genericApplicationContextTest is started...");

        /* GenericApplicationContext
            GenericApplicationContext ac = new GenericApplicationContext();
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
            reader.loadBeanDefinitions("vol2.ioc/raw/genericApplicationContext.xml");
            ac.refresh();
        */

        /* GenericXmlApplicationContext = GenericApplicationContext + XmlBeanDefinitionReader 내장 */
        GenericApplicationContext ac = new GenericXmlApplicationContext(
                "vol2/ioc/raw/genericApplicationContext.xml");

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
    }

    @Test
    public void hierarchyContextTest() {
        System.out.println("hierarchyContextTest is started...");

        ApplicationContext parent =
                new GenericXmlApplicationContext("/vol2/ioc/raw/parentContext.xml");

        GenericApplicationContext child = new GenericApplicationContext(parent); // 부모 컨텍스트 지정
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
        reader.loadBeanDefinitions("/vol2/ioc/raw/childrenContext.xml");
        child.refresh();

        Printer printer = child.getBean("printer", Printer.class);
        assertThat(printer, is(notNullValue()));

        Hello hello = child.getBean("hello", Hello.class);
        assertThat(hello, is(notNullValue()));

        hello.print();
        assertThat(printer.toString(), is("Hello Child"));
    }

    @Test
    public void simpleBeanScanningTest() {
        System.out.println("simpleBeanScanningTest is started...");

        ApplicationContext ctx = new AnnotationConfigApplicationContext("vol2/ioc/bean");

        AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);
        assertThat(hello, is(notNullValue()));
        assertThat(hello.toString(), is("I am AnnotatedHello"));
    }

    @Test
    public void annotationBeanScanningTest() {
        System.out.println("annotationBeanScanningTest is started...");

        ApplicationContext ctx = new AnnotationConfigApplicationContext(AnnotatedHelloConfig.class);

        AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);
        assertThat(hello, is(notNullValue()));
        assertThat(hello.toString(), is("I am AnnotatedHello"));
    }
}
