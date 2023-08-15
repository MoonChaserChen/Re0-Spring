package ink.akira.spring.bean;

import ink.akira.spring.Pet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

public class BeanFactoryTest {
    @Test
    public void testCommon() {
        String beanName = "myPet";
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 创建简单 BeanDefinition
        // 设置 className, 属性Pet.id
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName("ink.akira.spring.Pet");
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("id", 1));
        propertyValues.addPropertyValue(new PropertyValue("name", "Tom"));
        propertyValues.addPropertyValue(new PropertyValue("age", 18));
        beanDefinition.setPropertyValues(propertyValues);
        beanFactory.registerBeanDefinition(beanName, beanDefinition);

        // 从容器中获取
        Pet myPet = beanFactory.getBean(beanName, Pet.class);
        System.out.println(myPet); // Pet(id=1, name=Tom, age=18)
    }

    @Test
    public void testParentBeanDefinition() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 创建 parentBeanDefinition
        GenericBeanDefinition parent = new GenericBeanDefinition();
        parent.setBeanClassName("ink.akira.spring.bean.BeanFactoryTest.A");
        parent.setPropertyValues(new MutablePropertyValues().add("id", 1).add("name", "Tom"));
        // 创建 childBeanDefinition
        GenericBeanDefinition child = new GenericBeanDefinition();
        child.setBeanClassName("ink.akira.spring.bean.BeanFactoryTest.B");
        child.setPropertyValues(new MutablePropertyValues().add("name", "Jerry"));
        child.setParentName("parent");

        beanFactory.registerBeanDefinition("parent", parent);
        beanFactory.registerBeanDefinition("child", child);

        // 从容器中获取
        B b = beanFactory.getBean("child", B.class);
        System.out.println(b); // BeanFactoryTest.B(id=1, name=Jerry)
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class A {
        private int id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class B {
        private int id;
        private String name;
    }
}
