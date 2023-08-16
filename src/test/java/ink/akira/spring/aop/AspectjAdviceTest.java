package ink.akira.spring.aop;

import ink.akira.spring.Pet;
import org.aopalliance.aop.Advice;
import org.junit.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import org.springframework.aop.interceptor.DebugInterceptor;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

public class AspectjAdviceTest {

    @Test
    public void test() {
        // 为 DemoService 创建 ProxyFactory
        DemoService demoService = new DemoService();
        ProxyFactory factory = new ProxyFactory(demoService);
        // 创建Pointcut、Advice、Advisor 并加入到 ProxyFactory
        factory.addAdvisor(new DefaultPointcutAdvisor(new NameMatchMethodPointcut().addMethodName("hello"), new DebugInterceptor()));

        // 获取代理对象并执行
        DemoService proxyedDemoService = (DemoService) factory.getProxy();
        proxyedDemoService.hello();
    }

    @Test
    public void test1() {
        // 为 DemoService 创建 ProxyFactory
        DemoService demoService = new DemoService();
        ProxyFactory factory = new ProxyFactory(demoService);
        // 创建Pointcut、Advice、Advisor 并加入到 ProxyFactory
        factory.addAdvisor(new DefaultPointcutAdvisor(new NameMatchMethodPointcut().addMethodName("hello"), new PerformanceMonitorInterceptor()));

        // 获取代理对象并执行
        DemoService proxyedDemoService = (DemoService) factory.getProxy();
        proxyedDemoService.hello();
    }

    @Test
    public void test2() {
        // 为 DemoService 创建 ProxyFactory
        DemoService demoService = new DemoService();
        ProxyFactory factory = new ProxyFactory(demoService);
        // 创建Pointcut、Advice、Advisor 并加入到 ProxyFactory
        Pointcut pointcut = new NameMatchMethodPointcut().addMethodName("hello");
        Advice advice = new MethodBeforeAdviceInterceptor((method, args, target) -> new MyAdvice().print());
        factory.addAdvisor(new DefaultPointcutAdvisor(pointcut, advice));

        // 获取代理对象并执行
        DemoService proxyedDemoService = (DemoService) factory.getProxy();
        proxyedDemoService.hello();
    }

    @Test
    public void test3() {
        // 创建 DefaultListableBeanFactory，并利用 XmlBeanDefinitionReader 从 spring-bean.xml 中加载Bean
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(new ClassPathResource("spring-bean1.xml"));

        // 从容器中获取
        DemoService proxyedDemoService = beanFactory.getBean("demoServiceFactory", DemoService.class);
        proxyedDemoService.hello();
    }

    @Test
    public void test4() {
        // 创建 DefaultListableBeanFactory，并利用 XmlBeanDefinitionReader 从 spring-bean.xml 中加载Bean
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(new ClassPathResource("spring-bean2.xml"));

        // 从容器中获取
        DemoService proxyedDemoService = beanFactory.getBean("demoServiceFactory", DemoService.class);
        proxyedDemoService.hello();
    }
}
