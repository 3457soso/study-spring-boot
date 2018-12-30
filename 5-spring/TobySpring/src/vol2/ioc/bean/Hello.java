package vol2.ioc.bean;

import javax.annotation.PostConstruct;

public class Hello {
    String name;
    Printer printer;

    @PostConstruct
    public void init() {
        System.out.println("init");
    }

    public String sayHello() {
        return "Hello " + name;
    }

    public void print() {
        this.printer.print(sayHello());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }
}
