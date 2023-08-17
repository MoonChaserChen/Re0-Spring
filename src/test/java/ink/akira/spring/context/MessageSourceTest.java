package ink.akira.spring.context;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Locale;

public class MessageSourceTest {
    @Test
    public void test() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-bean3.xml");
        String chineseGreeting = context.getMessage("greeting", new Object[]{"汤姆"}, Locale.getDefault());
        System.out.println(chineseGreeting);
        String enGreeting = context.getMessage("greeting", new Object[]{"Tom"}, Locale.ENGLISH);
        System.out.println(enGreeting);
    }

    @Test
    public void test1() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-bean3.xml");
        String message = context.getMessage("argument.required", new Object[]{"checkParam"}, Locale.getDefault());
        System.out.println(message);
    }
}
