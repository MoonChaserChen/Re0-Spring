package ink.akira.spring;

import org.junit.Test;
import org.springframework.web.context.WebApplicationContext;

public class CommonTest {
    @Test
    public void test() {
        System.out.println(WebApplicationContext.class.getName());
    }
}
