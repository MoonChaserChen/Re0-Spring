# 类型为List的属性只添加一个
## 1)类
```java
public abstract class AbstractResourceBasedMessageSource extends AbstractMessageSource {

    private final Set<String> basenameSet = new LinkedHashSet<>(4);

    public void setBasename(String basename) {
        setBasenames(basename);
    }

    public void setBasenames(String... basenames) {
        this.basenameSet.clear();
        addBasenames(basenames);
    }
    
    // ------------------
    // other code ignored
    // ------------------
}
```
## 2)xml配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
        <property name="basename" value="message"/>
    </bean>
</beans>
```