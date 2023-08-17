package ink.akira.spring.context;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Locale;

public class MessageResourceTest {
    @Test
    public void test() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-bean3.xml");
        ContextDemoService demoService = context.getBean("demoService", ContextDemoService.class);
        demoService.checkParam(null);
    }

    @Test
    public void test1() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-bean3.xml");
        ContextDemoService demoService = context.getBean("demoService", ContextDemoService.class);
        demoService.checkParam(null, Locale.ENGLISH);
    }
}
